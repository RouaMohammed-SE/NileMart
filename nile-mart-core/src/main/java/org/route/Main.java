package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ProductService productService = context.getBean(ProductService.class);
            productService.findAll().forEach(System.out::println);
        }
    }
}
