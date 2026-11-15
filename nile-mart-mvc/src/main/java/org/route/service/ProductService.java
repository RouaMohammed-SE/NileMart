package org.route.service;


import org.route.model.Product;
import org.route.model.ProductRow;
import org.route.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductService(ProductRepository productRepository,
                          CategoryService categoryService,
                          BrandService brandService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    public List<Product> getAvailableProducts() {
        List<Product> availableProducts = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            if (p.getInStock() > 0)
                availableProducts.add(p);
        }
        return availableProducts;
    }

    /** Available products with the category and brand names looked up for display. */
    public List<ProductRow> getAvailableProductRows() {
        return getAvailableProducts().stream()
                .map(p -> new ProductRow(p.getSku(), p.getName(), p.getPrice(), p.getInStock(),
                        categoryService.nameOf(p.getCategory()),
                        brandService.nameOf(p.getBrandSlug())))
                .toList();
    }

    public void validate(Product product, Errors errors) {
        if (!errors.hasFieldErrors("sku") && isProductExist(product.getSku())) {
            errors.rejectValue("sku", "product.sku.duplicate", "sku is already exist");
        }
        if (!errors.hasFieldErrors("category") && !categoryService.isSubCategory(product.getCategory())) {
            errors.rejectValue("category", "product.category.invalid",
                    "Category must be an existing sub-category");
        }
        String brandSlug = product.getBrandSlug();
        if (brandSlug != null && !brandSlug.isBlank() && !brandService.isActiveBrand(brandSlug)) {
            errors.rejectValue("brandSlug", "product.brand.invalid",
                    "Brand does not exist or is inactive");
        }
    }

    public void saveProduct(Product product) {
        if (product.getBrandSlug() != null && product.getBrandSlug().isBlank()) {
            product.setBrandSlug(null);
        }
        productRepository.save(product);
    }

    public boolean isProductExist(String sku) {
        return productRepository.findBySku(sku) != null;
    }
}
