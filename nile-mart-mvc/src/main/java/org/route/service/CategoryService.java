package org.route.service;

import org.route.model.Category;
import org.route.model.CategoryRow;
import org.route.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final Comparator<Category> BY_ORDER =
            Comparator.comparing(Category::getDisplayOrder).thenComparing(Category::getName);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    private static boolean isTopLevel(Category category) {
        return category.getParentSlug() == null || category.getParentSlug().isBlank();
    }

    /** All top-level categories sorted by display order. */
    public List<Category> findTopLevel() {
        return categoryRepository.findAll().stream()
                .filter(CategoryService::isTopLevel)
                .sorted(BY_ORDER)
                .toList();
    }

    /** Top-level categories offered in the parent drop-down (active only). */
    public List<Category> findActiveTopLevel() {
        return findTopLevel().stream().filter(Category::isActive).toList();
    }

    public List<Category> findSubCategories(String parentSlug) {
        return categoryRepository.findAll().stream()
                .filter(c -> !isTopLevel(c) && c.getParentSlug().equals(parentSlug))
                .sorted(BY_ORDER)
                .toList();
    }

    /** Sub-categories offered in the product form (active only), grouped by parent. */
    public List<Category> findActiveSubCategories() {
        List<Category> result = new ArrayList<>();
        for (Category top : findTopLevel()) {
            for (Category sub : findSubCategories(top.getSlug())) {
                if (sub.isActive()) {
                    result.add(sub);
                }
            }
        }
        return result;
    }

    public boolean isSubCategory(String slug) {
        return slug != null && findBySlug(slug).map(c -> !isTopLevel(c)).orElse(false);
    }

    /** Category name for display; falls back to the slug if the category is unknown. */
    public String nameOf(String slug) {
        return findBySlug(slug).map(Category::getName).orElse(slug);
    }

    /** Each top-level category followed by its sub-categories, both sorted by display order. */
    public List<CategoryRow> getCategoryRows() {
        List<CategoryRow> rows = new ArrayList<>();
        for (Category top : findTopLevel()) {
            rows.add(new CategoryRow(top.getName(), top.getSlug(), "—",
                    top.getDisplayOrder(), top.isActive()));
            for (Category sub : findSubCategories(top.getSlug())) {
                rows.add(new CategoryRow("— " + sub.getName(), sub.getSlug(), top.getName(),
                        sub.getDisplayOrder(), sub.isActive()));
            }
        }
        return rows;
    }

    /** Business rules: parent (CAT-01), name unique within the parent, slug unique. */
    public void validate(Category category, Errors errors) {
        String parentSlug = category.getParentSlug();
        boolean hasParent = parentSlug != null && !parentSlug.isBlank();
        Category parent = null;
        boolean parentOk = true;

        if (hasParent) {
            Optional<Category> found = findBySlug(parentSlug);
            if (found.isEmpty()) {
                errors.rejectValue("parentSlug", "category.parent.missing",
                        "Parent category does not exist");
                parentOk = false;
            } else if (!isTopLevel(found.get())) {
                errors.rejectValue("parentSlug", "category.parent.level",
                        "Parent must be a top-level category");
                parentOk = false;
            } else {
                parent = found.get();
            }
        }

        if (parentOk && !errors.hasFieldErrors("name") && category.getName() != null) {
            String expectedParent = hasParent ? parentSlug : "";
            boolean nameTaken = categoryRepository.findAll().stream()
                    .anyMatch(c -> (isTopLevel(c) ? "" : c.getParentSlug()).equals(expectedParent)
                            && c.getName().equalsIgnoreCase(category.getName()));
            if (nameTaken) {
                String where = parent != null ? parent.getName() : "the top level";
                errors.rejectValue("name", "category.name.duplicate",
                        "A category with this name already exists under " + where);
            }
        }

        if (!errors.hasFieldErrors("slug") && category.getSlug() != null
                && findBySlug(category.getSlug()).isPresent()) {
            errors.rejectValue("slug", "category.slug.duplicate", "Slug already exists");
        }
    }

    public void save(Category category) {
        if (category.getParentSlug() == null || category.getParentSlug().isBlank()) {
            category.setParentSlug("");
        }
        categoryRepository.save(category);
    }
}
