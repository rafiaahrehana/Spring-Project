-- =============================================================
-- StartupHub — PostgreSQL Row-Level Security Setup
-- Run as a superuser ONCE after schema creation
-- =============================================================
-- Creates:
--   1. Application DB role (app_user) used by Spring Boot
--   2. RLS policies on every tenant-scoped table
--   3. current_setting('app.company_id') as the tenant discriminant
--
-- Spring Boot sets this per-connection via:
--   SET LOCAL app.company_id = '<companyId>';
-- wrapped in TenantAwareJpaInterceptor.
-- =============================================================

-- ── Application Role ──────────────────────────────────────────
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user LOGIN PASSWORD 'change_in_production';
    END IF;
END $$;

GRANT CONNECT ON DATABASE startuphub TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO app_user;

-- ── Helper function ───────────────────────────────────────────
CREATE OR REPLACE FUNCTION current_company_id() RETURNS BIGINT AS $$
BEGIN
    RETURN NULLIF(current_setting('app.company_id', TRUE), '')::BIGINT;
EXCEPTION WHEN OTHERS THEN
    RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE;

-- ── Enable RLS on every tenant table ─────────────────────────
-- Note: Platform tables (users, companies, service_categories,
--       feature_flags) are NOT RLS-scoped here because they are
--       either global or the tenant root itself.

DO $$
DECLARE
    t TEXT;
    tenant_tables TEXT[] := ARRAY[
        'employees','departments','clients',
        'workflow_templates','workflow_stages',
        'hub_services','wallets','wallet_transactions',
        'job_postings','job_applications',
        'service_requests','tasks','request_comments','request_status_history',
        'notifications','invoices','payments',
        'attendances','leave_requests','leave_balances',
        'payrolls','assets','expenses','timesheets',
        'leads','lead_activities','client_notes',
        'performance_reviews','employment_letters','announcements',
        'service_reviews'
    ];
BEGIN
    FOREACH t IN ARRAY tenant_tables LOOP
        EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE %I FORCE ROW LEVEL SECURITY', t);
    END LOOP;
END $$;

-- ── Per-table RLS policies ────────────────────────────────────
-- Pattern: allow all operations when company_id matches session variable.
-- USING clause filters SELECTs; WITH CHECK clause filters INSERT/UPDATE.

DO $$
DECLARE
    t TEXT;
    tenant_tables TEXT[] := ARRAY[
        'employees','departments','clients',
        'workflow_templates','workflow_stages',
        'hub_services','wallets','wallet_transactions',
        'job_postings','job_applications',
        'service_requests','tasks','request_comments','request_status_history',
        'notifications','invoices','payments',
        'attendances','leave_requests','leave_balances',
        'payrolls','assets','expenses','timesheets',
        'leads','lead_activities','client_notes',
        'performance_reviews','employment_letters','announcements',
        'service_reviews'
    ];
BEGIN
    FOREACH t IN ARRAY tenant_tables LOOP
        -- Drop existing policies to allow idempotent re-runs
        EXECUTE format('DROP POLICY IF EXISTS tenant_isolation ON %I', t);

        EXECUTE format($$
            CREATE POLICY tenant_isolation ON %I
            AS PERMISSIVE
            FOR ALL
            TO app_user
            USING (company_id = current_company_id())
            WITH CHECK (company_id = current_company_id())
        $$, t);
    END LOOP;
END $$;

-- ── request_status_history uses company_id column directly ───
-- (does not extend BaseEntity — no soft-delete, company_id is plain BIGINT)
DROP POLICY IF EXISTS tenant_isolation ON request_status_history;
CREATE POLICY tenant_isolation ON request_status_history
    AS PERMISSIVE FOR ALL TO app_user
    USING (company_id = current_company_id())
    WITH CHECK (company_id = current_company_id());

-- ── wallet_transactions company_id is via FK ─────────────────
-- Already covered by the loop above.

-- ── Superuser bypass (for migrations and admin tasks) ─────────
-- Superuser role bypasses RLS by default in PostgreSQL.
-- app_user is NOT a superuser, so RLS applies to all Spring Boot queries.

-- ── Verification ─────────────────────────────────────────────
SELECT
    schemaname,
    tablename,
    policyname,
    permissive,
    roles,
    cmd,
    qual
FROM pg_policies
WHERE schemaname = 'public'
  AND policyname = 'tenant_isolation'
ORDER BY tablename;
