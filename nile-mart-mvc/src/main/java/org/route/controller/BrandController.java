package org.route.controller;

import jakarta.validation.Valid;
import org.route.model.Brand;
import org.route.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/brands")
    public String list(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brands";
    }

    @GetMapping("/brands/new")
    public String showForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brandForm";
    }

    @PostMapping("/brands")
    public String save(@ModelAttribute("brand") @Valid Brand brand, BindingResult result) {
        brandService.validate(brand, result);
        if (result.hasErrors()) {
            return "brandForm";
        }
        brandService.save(brand);
        return "redirect:/brands";
    }
}
