package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class);
             var in = new Scanner(System.in)) {

            ProductService productService = context.getBean(ProductService.class);
            CheckoutService checkout = context.getBean(CheckoutService.class);
            Map<Integer, ShoppingCart> carts = new LinkedHashMap<>();
            int nextCartNumber = 1;

            while (true) {
                System.out.println("""
                        ========== NILEMART ============
                        1. Show products
                        2. Create a cart
                        3. Add a product to a cart
                        4. Show a cart
                        5. Check out a cart
                        0. Exit""");
                System.out.print("> ");
                if (!in.hasNextLine()) break;
                String choice = in.nextLine().trim();
                if (choice.equals("0")) break;

                try {
                    switch (choice) {
                        case "1" -> showProducts(productService);
                        case "2" -> {
                            String name = ask(in, "Customer name: ");
                            String phone = ask(in, "Phone: ");
                            String email = ask(in, "Email (optional): ");
                            ShoppingCart cart = context.getBean(ShoppingCart.class);
                            cart.setCartNumber(nextCartNumber++);
                            cart.setCustomer(new Customer(name, phone, email.isBlank() ? null : email));
                            carts.put(cart.getCartNumber(), cart);
                            System.out.println("Cart #" + cart.getCartNumber() + " created for " + name);
                        }
                        case "3" -> {
                            ShoppingCart cart = findCart(carts, in);
                            String sku = ask(in, "Product SKU: ");
                            int qty = askInt(in, "Quantity: ");
                            cart.addItem(sku, qty);
                            System.out.println("Added. Cart #" + cart.getCartNumber() + " subtotal: "
                                    + money(cart.subtotal()));
                        }
                        case "4" -> showCart(findCart(carts, in), productService);
                        case "5" -> {
                            ShoppingCart cart = findCart(carts, in);
                            Governorate governorate = askGovernorate(in);
                            Order order = checkout.placeOrder(cart, governorate);
                            System.out.println("Order " + order.orderNumber() + " placed. Subtotal "
                                    + money(order.subtotal()) + " | VAT " + money(order.vat())
                                    + " | Shipping " + money(order.shippingFee())
                                    + " | Total " + money(order.total()));
                        }
                        default -> System.out.println("Unknown option.");
                    }
                } catch (StoreException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        }
    }

    private static String money(BigDecimal value) {
        return String.format(Locale.US, "%,.2f EGP", value);
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static int askInt(Scanner in, String prompt) {
        try {
            return Integer.parseInt(ask(in, prompt));
        } catch (NumberFormatException exception) {
            throw new StoreException("Please enter a whole number.");
        }
    }

    private static ShoppingCart findCart(Map<Integer, ShoppingCart> carts, Scanner in) {
        ShoppingCart cart = carts.get(askInt(in, "Cart number: "));
        if (cart == null) throw new StoreException("Cart not found.");
        return cart;
    }

    private static Governorate askGovernorate(Scanner in) {
        Governorate[] all = Governorate.values();
        for (int i = 0; i < all.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, all[i]);
        }
        int number = askInt(in, "Governorate number: ");
        if (number < 1 || number > all.length) throw new StoreException("Invalid governorate.");
        return all[number - 1];
    }

    private static void showProducts(ProductService service) {
        System.out.printf("%-14s %-28s %-15s %12s %6s%n", "SKU", "Name", "Category", "Price", "Stock");
        for (Product product : service.findAll()) {
            System.out.printf(Locale.US, "%-14s %-28s %-15s %,12.2f %6d%n",
                    product.getSku(), product.getName(), product.getCategory(),
                    product.getPrice(), product.getStockQuantity());
        }
    }

    private static void showCart(ShoppingCart cart, ProductService service) {
        System.out.println("Cart #" + cart.getCartNumber() + " - " + cart.getCustomer().fullName());
        if (cart.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        cart.getLines().forEach((sku, quantity) -> System.out.println(
                "  " + quantity + " x " + service.findBySku(sku).getName()
                        + " = " + money(service.getPrice(sku, quantity))));
        System.out.println("Subtotal: " + money(cart.subtotal()));
    }
}
