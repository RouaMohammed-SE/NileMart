package org.route.repository;

import org.route.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();

    Optional<Category> findBySlug(String slug);

    void save(Category category);
}
