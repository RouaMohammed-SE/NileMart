package org.route.controller;


import jakarta.validation.Valid;
import org.route.model.Product;
import org.route.service.BrandService;
import org.route.service.CategoryService;
import org.route.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @GetMapping("/products")
    public String getProducts(Model model) {
        model.addAttribute("rows", productService.getAvailableProductRows());
        return "products";
    }

    @GetMapping({"/products/new", "/new"})
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        addFormOptions(model);
        return "productForm";
    }

    @PostMapping("/products")
    public String addProduct(@ModelAttribute("product") @Valid Product product,
                             BindingResult result, Model model) {
        productService.validate(product, result);
        if (result.hasErrors()) {
            addFormOptions(model);
            return "productForm";
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("categories", categoryService.findActiveSubCategories());
        model.addAttribute("brands", brandService.findActive());
    }
}
