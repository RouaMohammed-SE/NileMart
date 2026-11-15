package org.route.service;

import org.route.model.Brand;
import org.route.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    /** Active brands only: the ones offered in new choices such as the product form. */
    public List<Brand> findActive() {
        return brandRepository.findAll().stream().filter(Brand::isActive).toList();
    }

    public Optional<Brand> findBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }

    public boolean isActiveBrand(String slug) {
        return findBySlug(slug).map(Brand::isActive).orElse(false);
    }

    /** Brand name for display; "—" when there is no brand. */
    public String nameOf(String slug) {
        if (slug == null || slug.isBlank()) {
            return "—";
        }
        return findBySlug(slug).map(Brand::getName).orElse("—");
    }

    /** Business rules that need more than one field or the stored data. */
    public void validate(Brand brand, Errors errors) {
        if (!errors.hasFieldErrors("name") && brand.getName() != null) {
            boolean nameTaken = brandRepository.findAll().stream()
                    .anyMatch(b -> b.getName().equalsIgnoreCase(brand.getName()));
            if (nameTaken) {
                errors.rejectValue("name", "brand.name.duplicate", "Brand name already exists");
            }
        }
        if (!errors.hasFieldErrors("slug") && brand.getSlug() != null
                && brandRepository.findBySlug(brand.getSlug()).isPresent()) {
            errors.rejectValue("slug", "brand.slug.duplicate", "Slug already exists");
        }
    }

    public void save(Brand brand) {
        brandRepository.save(brand);
    }
}
