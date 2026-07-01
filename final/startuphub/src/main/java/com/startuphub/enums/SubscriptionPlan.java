package com.startuphub.enums;

/**
 * Subscription plans available on the platform.
 *
 * FREE        — 3 service requests/month, 2 team members, 14-day trial
 * STARTER     — BDT 2,000/month, 15 requests, 5 team members
 * PRO         — BDT 5,000/month, 50 requests, unlimited team members
 * ENTERPRISE  — BDT 15,000/month, unlimited everything, priority support
 *
 * Limits are enforced in SubscriptionPlanService (Phase 3).
 */
public enum SubscriptionPlan {
    FREE,
    STARTER,
    PRO,
    ENTERPRISE
}
