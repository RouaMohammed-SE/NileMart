package org.route.repository;

import jakarta.annotation.PostConstruct;
import org.route.model.Category;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {
    private final List<Category> categories = new ArrayList<>();

    @PostConstruct
    void init() {
        categories.add(new Category("Electronics", "electronics", "", 1, true));
        categories.add(new Category("Mobile Phones", "mobile-phones", "electronics", 1, true));
        categories.add(new Category("Laptops", "laptops", "electronics", 2, true));
        categories.add(new Category("Accessories", "electronics-accessories", "electronics", 3, true));
        categories.add(new Category("Home & Kitchen", "home-kitchen", "", 2, true));
        categories.add(new Category("Cookware", "cookware", "home-kitchen", 1, true));
        categories.add(new Category("Lighting", "lighting", "home-kitchen", 2, true));
        categories.add(new Category("Fashion", "fashion", "", 3, false));
        categories.add(new Category("Men's Clothing", "mens-clothing", "fashion", 1, true));
    }

    @Override
    public List<Category> findAll() {
        return List.copyOf(categories);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categories.stream().filter(c -> c.getSlug().equals(slug)).findFirst();
    }

    @Override
    public void save(Category category) {
        categories.add(category);
    }
}
