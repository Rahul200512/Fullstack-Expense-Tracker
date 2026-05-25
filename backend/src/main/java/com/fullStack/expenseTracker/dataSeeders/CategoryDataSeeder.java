package com.fullStack.expenseTracker.dataSeeders;

import com.fullStack.expenseTracker.enums.ETransactionType;
import com.fullStack.expenseTracker.models.Category;
import com.fullStack.expenseTracker.models.TransactionType;
import com.fullStack.expenseTracker.repository.CategoryRepository;
import com.fullStack.expenseTracker.repository.TransactionTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class CategoryDataSeeder {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    @Transactional
    public void loadCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seeder");
            return;
        }

        TransactionType expenseType = transactionTypeRepository
                .findByTransactionTypeName(ETransactionType.TYPE_EXPENSE);

        TransactionType incomeType = transactionTypeRepository
                .findByTransactionTypeName(ETransactionType.TYPE_INCOME);

        if (expenseType == null || incomeType == null) {
            log.warn("Transaction types not found, skipping category seeder");
            return;
        }

        log.info("Seeding {} expense and {} income categories", 12, 6);

        List<String> expenseCategories = List.of(
                "Food & Dining", "Transport", "Shopping", "Entertainment",
                "Bills & Utilities", "Health", "Education", "Groceries",
                "Rent", "Subscriptions", "Travel", "Other Expense"
        );

        List<String> incomeCategories = List.of(
                "Salary", "Freelance", "Investment", "Gift",
                "Refund", "Other Income"
        );

        for (String name : expenseCategories) {
            categoryRepository.save(new Category(name, expenseType, true));
        }

        for (String name : incomeCategories) {
            categoryRepository.save(new Category(name, incomeType, true));
        }
    }
}
