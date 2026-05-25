package com.huseyin.personalfinanceapi.category.seed;

import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.category.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sistem ilk başlatıldığında built-in (sistem) kategorileri
 * yükler. Built-in kategoriler {@code user_id} null ile saklanır ve tüm kullanıcılar
 * tarafından okunabilir; ancak değiştirilemez veya silinemez.
 *
 */
@Component
@Order(1)
public class BuiltInCategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public BuiltInCategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Expense Categories
        seedExpense("Food & Drink");
        seedExpense("Shopping");
        seedExpense("Transportation");
        seedExpense("Bills & Utilities");
        seedExpense("Rent");
        seedExpense("Health");
        seedExpense("Education");
        seedExpense("Entertainment");
        seedExpense("Clothing");
        seedExpense("Other Expense");

        // Income Categories
        seedIncome("Salary");
        seedIncome("Interest"); // TransactionValidator.INTEREST_CATEGORY_NAME
        seedIncome("Bonus");
        seedIncome("Gift");
        seedIncome("Rental Income");
        seedIncome("Other Income");

    }
    private void seedExpense(String name) { upsertBuiltIn(name, Category.CategoryType.EXPENSE); }
    private void seedIncome(String name)  { upsertBuiltIn(name, Category.CategoryType.INCOME); }


    private void upsertBuiltIn(String name, Category.CategoryType type) {
        // avoiding duplicate categorY creation between built-in categories
        if (categoryRepository.existsByNameIgnoreCaseAndBuiltInTrue(name)) {
            return;
        }
        Category c = new Category();
        c.setName(name);
        c.setType(type);
        c.setBuiltIn(true);
        c.setUser(null); // built-in
        c.setActive(true);
        categoryRepository.save(c);
    }

    /** All built-in category definitions for reference */
    public static List<Map.Entry<String, Category.CategoryType>> builtInDefinitions() {
        List<Map.Entry<String, Category.CategoryType>> definitions = new ArrayList<>();

        // Add Expenses
        definitions.add(Map.entry("Food & Drink", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Shopping", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Transportation", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Bills & Utilities", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Rent", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Health", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Education", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Entertainment", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Clothing", Category.CategoryType.EXPENSE));
        definitions.add(Map.entry("Other Expense", Category.CategoryType.EXPENSE));

        // Add Incomes
        definitions.add(Map.entry("Salary", Category.CategoryType.INCOME));
        definitions.add(Map.entry("Interest", Category.CategoryType.INCOME));
        definitions.add(Map.entry("Bonus", Category.CategoryType.INCOME));
        definitions.add(Map.entry("Gift", Category.CategoryType.INCOME));
        definitions.add(Map.entry("Rental Income", Category.CategoryType.INCOME));
        definitions.add(Map.entry("Other Income", Category.CategoryType.INCOME));


        return definitions;
    }
}
