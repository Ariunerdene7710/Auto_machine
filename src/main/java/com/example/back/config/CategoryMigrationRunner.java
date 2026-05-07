package com.example.back.config;

import com.example.back.entity.Category;
import com.example.back.repository.CategoryRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CategoryMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryRepository categoryRepository;

    public CategoryMigrationRunner(JdbcTemplate jdbcTemplate, CategoryRepository categoryRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!hasColumn("machines", "category") || !hasColumn("machines", "category_id")) {
            return;
        }

        List<LegacyMachineCategory> legacyCategories = jdbcTemplate.query(
                "SELECT id, category FROM machines " +
                        "WHERE category_id IS NULL AND category IS NOT NULL AND TRIM(category) <> ''",
                this::mapLegacyCategory
        );

        for (LegacyMachineCategory legacyCategory : legacyCategories) {
            Category category = categoryRepository.findByNameIgnoreCase(legacyCategory.categoryName())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(legacyCategory.categoryName());
                        newCategory.setCreatedAt(LocalDateTime.now());
                        newCategory.setUpdatedAt(LocalDateTime.now());
                        return categoryRepository.save(newCategory);
                    });

            jdbcTemplate.update(
                    "UPDATE machines SET category_id = ? WHERE id = ?",
                    category.getId(),
                    legacyCategory.machineId()
            );
        }
    }

    private boolean hasColumn(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }

    private LegacyMachineCategory mapLegacyCategory(ResultSet rs, int rowNum) throws SQLException {
        return new LegacyMachineCategory(
                rs.getLong("id"),
                rs.getString("category").trim()
        );
    }

    private record LegacyMachineCategory(Long machineId, String categoryName) {
    }
}
