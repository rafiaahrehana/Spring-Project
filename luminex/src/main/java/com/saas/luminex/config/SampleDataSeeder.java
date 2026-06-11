package com.saas.luminex.config;

import com.saas.luminex.entity.*;
import com.saas.luminex.enums.*;
import com.saas.luminex.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Loads rich sample data for dev/demo environments.
 * Run with: spring.profiles.active=dev
 *
 * Seeded data mirrors the original LumiNex db.json structure.
 */
@Component
@Profile("dev")
@Order(2)   // runs after DataSeeder (Order 1)
@RequiredArgsConstructor
@Slf4j
public class SampleDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceRequestRepository requestRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 2) {
            log.info("Sample data already loaded, skipping.");
            return;
        }
        seedUsers();
        List<Category> categories = seedCategories();
        List<com.saas.luminex.entity.Service> services = seedServices(categories);
        seedSubscriptions();
        seedKnowledgeBase();
        seedRequestsAndPayments(services);
        log.info("✅ Sample data loaded successfully.");
    }

    // ─── Users ────────────────────────────────────────────────────────────────

    private void seedUsers() {
        String pw = passwordEncoder.encode("password123");

        List<User> users = List.of(
            User.builder().name("Badrul Islam").email("badrul@gmail.com")
                    .password(pw).role(Role.CLIENT).phone("01700000001")
                    .companyName("BD Textiles Ltd").isActive(true).build(),
            User.builder().name("Sajin Ahmed").email("sajin@gmail.com")
                    .password(pw).role(Role.CLIENT).phone("01700000002")
                    .companyName("Sajin Enterprises").isActive(true).build(),
            User.builder().name("Abdul Hamid").email("hamid@gmail.com")
                    .password(pw).role(Role.CLIENT).phone("01700000003")
                    .companyName("Hamid & Co.").isActive(true).build(),
            User.builder().name("Fahad Aziz").email("fahad@gmail.com")
                    .password(pw).role(Role.CLIENT).phone("01700000004")
                    .companyName("Aziz Holdings").isActive(true).build(),
            User.builder().name("Shaharan Hossain").email("shaharan@gmail.com")
                    .password(pw).role(Role.CLIENT).phone("01700000005")
                    .companyName("Shaharan Corp").isActive(true).build(),
            User.builder().name("Emon Hossain").email("emon@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000001")
                    .isActive(true).build(),
            User.builder().name("Sadiya Rahman").email("sadiya@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000002")
                    .isActive(true).build(),
            User.builder().name("Mahbub Sheikh").email("mahbub@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000003")
                    .isActive(true).build(),
            User.builder().name("Israt Jahan Jui").email("jui@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000004")
                    .isActive(true).build(),
            User.builder().name("Tanvir Hossain").email("tanvir@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000005")
                    .isActive(true).build(),
            User.builder().name("Rafiaah").email("rafiaah@gmail.com")
                    .password(pw).role(Role.EMPLOYEE).phone("01800000006")
                    .isActive(true).build()
        );

        users.forEach(u -> {
            if (!userRepository.existsByEmail(u.getEmail())) userRepository.save(u);
        });
        log.info("  → {} users seeded", users.size());
    }

    // ─── Categories ───────────────────────────────────────────────────────────

    private List<Category> seedCategories() {
        List<Category> categories = List.of(
            Category.builder().name("Web Development").icon("💻").color("#6366f1").isActive(true).build(),
            Category.builder().name("Digital Marketing").icon("📣").color("#ec4899").isActive(true).build(),
            Category.builder().name("Graphic Design").icon("🎨").color("#f59e0b").isActive(true).build(),
            Category.builder().name("IT Support").icon("🛠").color("#10b981").isActive(true).build(),
            Category.builder().name("Legal Services").icon("⚖️").color("#3b82f6").isActive(true).build(),
            Category.builder().name("Accounting").icon("📊").color("#8b5cf6").isActive(true).build()
        );

        List<Category> saved = categories.stream()
                .filter(c -> !categoryRepository.existsByName(c.getName()))
                .map(categoryRepository::save)
                .toList();

        log.info("  → {} categories seeded", saved.size());
        return categoryRepository.findAll();
    }

    // ─── Services ─────────────────────────────────────────────────────────────

    private List<com.saas.luminex.entity.Service> seedServices(List<Category> cats) {
        Category webDev     = find(cats, "Web Development");
        Category marketing  = find(cats, "Digital Marketing");
        Category design     = find(cats, "Graphic Design");
        Category itSupport  = find(cats, "IT Support");
        Category legal      = find(cats, "Legal Services");
        Category accounting = find(cats, "Accounting");

        List<com.saas.luminex.entity.Service> services = List.of(
            svc("Website Redesign", "Complete redesign of your business website",
                    new BigDecimal("15000"), PriceType.FIXED, 14, webDev),
            svc("E-Commerce Development", "Full-featured online store with payment integration",
                    new BigDecimal("30000"), PriceType.FIXED, 30, webDev),
            svc("Landing Page", "High-converting single-page design and development",
                    new BigDecimal("8000"), PriceType.FIXED, 7, webDev),
            svc("SEO Optimization", "Improve your Google ranking with on-page & off-page SEO",
                    new BigDecimal("5000"), PriceType.MONTHLY, null, marketing),
            svc("Social Media Management", "Full management of Facebook, Instagram & LinkedIn",
                    new BigDecimal("8000"), PriceType.MONTHLY, null, marketing),
            svc("Google Ads Campaign", "Setup and management of paid Google advertising",
                    new BigDecimal("6000"), PriceType.MONTHLY, null, marketing),
            svc("Logo Design", "Professional brand logo with 3 revision rounds",
                    new BigDecimal("3000"), PriceType.FIXED, 5, design),
            svc("Brand Identity Package", "Logo, color palette, fonts and brand guidelines",
                    new BigDecimal("10000"), PriceType.FIXED, 10, design),
            svc("IT Infrastructure Setup", "Server, network and workstation setup for your office",
                    new BigDecimal("20000"), PriceType.FIXED, 7, itSupport),
            svc("Monthly IT Support", "Dedicated IT helpdesk support for your team",
                    new BigDecimal("5000"), PriceType.MONTHLY, null, itSupport),
            svc("Business Registration", "Complete company registration with RJSC",
                    new BigDecimal("12000"), PriceType.FIXED, 21, legal),
            svc("Tax Filing", "Annual corporate tax return preparation and filing",
                    new BigDecimal("8000"), PriceType.FIXED, 30, accounting)
        );

        List<com.saas.luminex.entity.Service> saved = services.stream()
                .map(serviceRepository::save)
                .toList();

        log.info("  → {} services seeded", saved.size());
        return saved;
    }

    // ─── Subscriptions ────────────────────────────────────────────────────────

    private void seedSubscriptions() {
        if (subscriptionRepository.count() > 0) return;

        List<Subscription> plans = List.of(
            Subscription.builder().name("Starter").price(new BigDecimal("999"))
                .featuresJson("[\"1 Active Project\",\"Email Support\",\"Basic Reports\",\"5 GB Storage\"]")
                .recommended(false).isActive(true).build(),
            Subscription.builder().name("Professional").price(new BigDecimal("2999"))
                .featuresJson("[\"5 Active Projects\",\"Priority Support\",\"Advanced Reports\",\"50 GB Storage\",\"Team Access\"]")
                .recommended(true).isActive(true).build(),
            Subscription.builder().name("Enterprise").price(new BigDecimal("7999"))
                .featuresJson("[\"Unlimited Projects\",\"Dedicated Manager\",\"Custom Reports\",\"500 GB Storage\",\"API Access\",\"SLA Guarantee\"]")
                .recommended(false).isActive(true).build()
        );
        subscriptionRepository.saveAll(plans);
        log.info("  → {} subscription plans seeded", plans.size());
    }

    // ─── Knowledge Base ───────────────────────────────────────────────────────

    private void seedKnowledgeBase() {
        if (knowledgeBaseRepository.count() > 0) return;

        User admin = userRepository.findByEmail("admin@luminex.com").orElse(null);

        List<KnowledgeBase> articles = List.of(
            KnowledgeBase.builder().title("How to Submit a Service Request")
                .category("Getting Started")
                .content("Log in as a CLIENT, go to My Requests, click New Request, select a service category and service, set priority, add notes and submit.")
                .createdBy(admin).build(),
            KnowledgeBase.builder().title("Understanding Request Status")
                .category("Getting Started")
                .content("PENDING: awaiting review. IN_REVIEW: admin reviewing. IN_PROGRESS: employee working. ON_HOLD: paused. COMPLETED: done. CANCELLED: closed.")
                .createdBy(admin).build(),
            KnowledgeBase.builder().title("Payment Methods Accepted")
                .category("Billing")
                .content("We accept bKash, Nagad, Rocket, debit/credit cards and bank transfers. All transactions are secured.")
                .createdBy(admin).build(),
            KnowledgeBase.builder().title("How to Add an Employee")
                .category("Admin Guide")
                .content("Go to Admin > Users > Employees, click Add Employee, fill in name, email and temporary password. The employee can change their password after first login.")
                .createdBy(admin).build(),
            KnowledgeBase.builder().title("Updating Task Progress")
                .category("Employee Guide")
                .content("Navigate to My Tasks, click on a task, drag the progress slider or type a percentage and click Save. Update worked hours for accurate billing.")
                .createdBy(admin).build()
        );
        knowledgeBaseRepository.saveAll(articles);
        log.info("  → {} knowledge base articles seeded", articles.size());
    }

    // ─── Service Requests & Payments ─────────────────────────────────────────

    private void seedRequestsAndPayments(List<com.saas.luminex.entity.Service> services) {
        if (requestRepository.count() > 0) return;

        User badrul   = get("badrul@gmail.com");
        User sajin    = get("sajin@gmail.com");
        User hamid    = get("hamid@gmail.com");
        User fahad    = get("fahad@gmail.com");
        User shaharan = get("shaharan@gmail.com");
        User emon     = get("emon@gmail.com");
        User sadiya   = get("sadiya@gmail.com");
        User mahbub   = get("mahbub@gmail.com");

        com.saas.luminex.entity.Service website   = services.get(0);
        com.saas.luminex.entity.Service ecommerce = services.get(1);
        com.saas.luminex.entity.Service seo       = services.get(3);
        com.saas.luminex.entity.Service logo      = services.get(6);
        com.saas.luminex.entity.Service itSetup   = services.get(8);
        com.saas.luminex.entity.Service tax       = services.get(11);

        List<ServiceRequest> requests = List.of(
            req(badrul, website, emon, RequestStatus.COMPLETED, Priority.HIGH, 100, 28,
                    "Client wants mobile-first design", "Please focus on responsive layout"),
            req(sajin, ecommerce, sadiya, RequestStatus.IN_PROGRESS, Priority.HIGH, 60, 45,
                    "Integrate bKash & Nagad payment gateways", "Need launch by month end"),
            req(hamid, seo, mahbub, RequestStatus.IN_PROGRESS, Priority.NORMAL, 40, 12,
                    "Focus on local Dhaka keywords", null),
            req(fahad, logo, emon, RequestStatus.PENDING, Priority.NORMAL, 0, 0,
                    null, "Please use blue and gold colors"),
            req(shaharan, itSetup, null, RequestStatus.PENDING, Priority.URGENT, 0, 0,
                    null, "New office opening next week"),
            req(badrul, tax, mahbub, RequestStatus.COMPLETED, Priority.NORMAL, 100, 16,
                    "FY 2023-24 filing complete", null)
        );

        List<ServiceRequest> saved = requestRepository.saveAll(requests);
        log.info("  → {} service requests seeded", saved.size());

        // Payments for completed requests
        List<Payment> payments = List.of(
            Payment.builder().client(badrul).serviceRequest(saved.get(0))
                    .amount(website.getPrice()).method(PaymentMethod.BKASH)
                    .status(PaymentStatus.PAID).transactionId("BK-20240301-001")
                    .description("Website Redesign payment").build(),
            Payment.builder().client(badrul).serviceRequest(saved.get(5))
                    .amount(tax.getPrice()).method(PaymentMethod.BANK_TRANSFER)
                    .status(PaymentStatus.PAID).transactionId("BT-20240410-001")
                    .description("Tax Filing FY23-24").build(),
            Payment.builder().client(sajin).serviceRequest(saved.get(1))
                    .amount(ecommerce.getPrice().multiply(new BigDecimal("0.5")))
                    .method(PaymentMethod.NAGAD).status(PaymentStatus.PENDING)
                    .description("E-Commerce 50% advance").build()
        );
        paymentRepository.saveAll(payments);
        log.info("  → {} payments seeded", payments.size());

        // Sample notifications
        List<Notification> notifications = List.of(
            Notification.builder().user(badrul).title("Request Completed")
                    .message("Your Website Redesign project is complete. Please proceed to payment.")
                    .type(NotificationType.REQUEST_COMPLETED).isRead(true).build(),
            Notification.builder().user(sajin).title("Request Update")
                    .message("Your E-Commerce project is 60% complete. Great progress!")
                    .type(NotificationType.REQUEST_UPDATED).isRead(false).build(),
            Notification.builder().user(emon).title("New Task Assigned")
                    .message("You have been assigned to: Logo Design for Fahad Aziz")
                    .type(NotificationType.REQUEST_ASSIGNED).isRead(false).build()
        );
        notificationRepository.saveAll(notifications);
        log.info("  → {} notifications seeded", notifications.size());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ServiceRequest req(User client, com.saas.luminex.entity.Service service,
                                User employee, RequestStatus status, Priority priority,
                                int progress, int workedHours,
                                String adminNotes, String clientNotes) {
        return ServiceRequest.builder()
                .client(client).service(service).assignedEmployee(employee)
                .status(status).priority(priority).progress(progress)
                .workedHours(workedHours).adminNotes(adminNotes).clientNotes(clientNotes)
                .build();
    }

    private com.saas.luminex.entity.Service svc(String name, String desc,
            BigDecimal price, PriceType priceType, Integer days, Category cat) {
        return com.saas.luminex.entity.Service.builder()
                .name(name).description(desc).price(price)
                .priceType(priceType).deliveryDays(days)
                .category(cat).isActive(true).build();
    }

    private Category find(List<Category> cats, String name) {
        return cats.stream().filter(c -> c.getName().equals(name)).findFirst()
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }

    private User get(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }
}
