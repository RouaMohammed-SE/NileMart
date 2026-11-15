package org.route.repository;

import jakarta.annotation.PostConstruct;
import org.route.model.Brand;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryBrandRepository implements BrandRepository {
    private final List<Brand> brands = new ArrayList<>();

    @PostConstruct
    void init() {
        brands.add(new Brand("Samsung", "samsung", true));
        brands.add(new Brand("Asus", "asus", true));
        brands.add(new Brand("Anker", "anker", true));
        brands.add(new Brand("Tefal", "tefal", true));
        brands.add(new Brand("Old Brand", "old-brand", false));
    }

    @Override
    public List<Brand> findAll() {
        return List.copyOf(brands);
    }

    @Override
    public Optional<Brand> findBySlug(String slug) {
        return brands.stream().filter(b -> b.getSlug().equals(slug)).findFirst();
    }

    @Override
    public void save(Brand brand) {
        brands.add(brand);
    }
}
