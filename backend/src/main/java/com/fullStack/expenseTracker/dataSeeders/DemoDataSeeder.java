package com.fullStack.expenseTracker.dataSeeders;

import com.fullStack.expenseTracker.enums.ERole;
import com.fullStack.expenseTracker.models.*;
import com.fullStack.expenseTracker.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DemoDataSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDemoData() {
        if (userRepository.existsByEmail("demo@mywallet.com")) {
            log.info("Demo user already exists, skipping demo seeder");
            return;
        }

        log.info("Seeding demo user and sample data...");

        // Create demo user
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElse(null);

        if (userRole == null) {
            log.warn("ROLE_USER not found, skipping demo seeder");
            return;
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User demoUser = new User(
                "DemoUser",
                "demo@mywallet.com",
                passwordEncoder.encode("Demo@1234"),
                null, null, true, roles
        );
        userRepository.save(demoUser);

        // Seed transactions across multiple days
        LocalDate today = LocalDate.now();

        // Expense transactions (categories 1-12)
        addTransaction(demoUser, 1, "Morning coffee", 4.50, today);
        addTransaction(demoUser, 1, "Lunch with team", 18.00, today.minusDays(1));
        addTransaction(demoUser, 1, "Dinner delivery", 32.50, today.minusDays(3));
        addTransaction(demoUser, 2, "Uber to office", 12.00, today);
        addTransaction(demoUser, 2, "Gas refill", 45.00, today.minusDays(5));
        addTransaction(demoUser, 3, "Amazon order", 89.99, today.minusDays(2));
        addTransaction(demoUser, 3, "New headphones", 149.00, today.minusDays(7));
        addTransaction(demoUser, 4, "Movie tickets", 28.00, today.minusDays(4));
        addTransaction(demoUser, 5, "Electric bill", 120.00, today.minusDays(10));
        addTransaction(demoUser, 5, "Internet bill", 59.99, today.minusDays(10));
        addTransaction(demoUser, 6, "Pharmacy", 25.50, today.minusDays(6));
        addTransaction(demoUser, 7, "Udemy course", 12.99, today.minusDays(8));
        addTransaction(demoUser, 8, "Weekly groceries", 95.00, today.minusDays(3));
        addTransaction(demoUser, 8, "Fruits & veggies", 35.00, today.minusDays(9));
        addTransaction(demoUser, 9, "Monthly rent", 1200.00, today.minusDays(15));
        addTransaction(demoUser, 10, "Netflix", 15.99, today.minusDays(12));
        addTransaction(demoUser, 10, "Spotify", 9.99, today.minusDays(12));
        addTransaction(demoUser, 11, "Weekend getaway", 250.00, today.minusDays(6));
        addTransaction(demoUser, 12, "ATM fee", 3.00, today.minusDays(11));

        // Income transactions (categories 13-18)
        addTransaction(demoUser, 13, "Monthly salary", 5000.00, today.minusDays(15));
        addTransaction(demoUser, 14, "Freelance project", 850.00, today.minusDays(4));
        addTransaction(demoUser, 15, "Stock dividend", 120.00, today.minusDays(8));
        addTransaction(demoUser, 16, "Birthday gift", 200.00, today.minusDays(2));
        addTransaction(demoUser, 17, "Amazon refund", 45.00, today.minusDays(1));
        addTransaction(demoUser, 18, "Cashback reward", 15.00, today.minusDays(9));

        // Create budget for current month
        Budget budget = new Budget(
                demoUser.getId(),
                2500.00,
                today.getMonthValue(),
                today.getYear()
        );
        budgetRepository.save(budget);

        log.info("Demo data seeded: 25 transactions + budget for demo@mywallet.com");
    }

    private void addTransaction(User user, int categoryId, String description, double amount, LocalDate date) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            transactionRepository.save(new Transaction(user, category, description, amount, date));
        }
    }
}
