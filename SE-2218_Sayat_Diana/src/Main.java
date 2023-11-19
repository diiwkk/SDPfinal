import java.util.*;

// Strategy Pattern
interface ShippingStrategy {
    double calculateShippingCost(double weight);
}

class StandardShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateShippingCost(double weight) {
        return weight * 0.5;
    }
}

class ExpressShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateShippingCost(double weight) {
        return weight * 1.5;
    }
}

// Singleton Pattern
class ShoppingCart {
    private static ShoppingCart instance;
    private Map<Product, Integer> products;
    private List<ProductObserver> productObservers;

    private ShoppingCart() {
        this.products = new HashMap<>();
        this.productObservers = new ArrayList<>();
    }

    public static synchronized ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addProduct(Product product) {
        products.put(product, products.getOrDefault(product, 0) + 1);
    }

    public void checkout(ShippingStrategy shippingStrategy) {
        double totalWeight = calculateTotalWeight();
        double shippingCost = shippingStrategy.calculateShippingCost(totalWeight);

        System.out.println("Items in the cart:");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(product.getName() + " - $" + product.getPrice() + " x " + quantity);
        }

        System.out.println("Total weight: " + totalWeight + " kg");
        System.out.println("Shipping cost: $" + shippingCost);
        System.out.println("Total cost: $" + (calculateTotalCost() + shippingCost));
    }

    public void addProductObserver(ProductObserver observer) {
        productObservers.add(observer);
    }

    public void notifyProductObservers(Product product) {
        for (ProductObserver observer : productObservers) {
            observer.update(product);
        }
    }

    private double calculateTotalWeight() {
        return products.entrySet().stream().mapToDouble(entry -> entry.getKey().getWeight() * entry.getValue()).sum();
    }

    private double calculateTotalCost() {
        return products.entrySet().stream().mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue()).sum();
    }

    public void clearCart() {
        products.clear();
    }
}

// Decorator Pattern
abstract class ProductDecorator implements Product {
    private Product decoratedProduct;

    ProductDecorator(Product decoratedProduct) {
        this.decoratedProduct = decoratedProduct;
    }

    @Override
    public String getName() {
        return decoratedProduct.getName();
    }

    @Override
    public double getPrice() {
        return decoratedProduct.getPrice();
    }

    @Override
    public double getWeight() {
        return decoratedProduct.getWeight();
    }
}

// Adapter Pattern
interface PaymentAdapter {
    void processPayment(double amount);
}

class CreditCardPaymentAdapter implements PaymentAdapter {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}

// Factory Pattern
interface Product {
    String getName();
    double getPrice();
    double getWeight();
}

class Laptop implements Product {
    @Override
    public String getName() {
        return "Laptop";
    }

    @Override
    public double getPrice() {
        return 800.0;
    }

    @Override
    public double getWeight() {
        return 2.5;
    }
}

class Smartphone implements Product {
    @Override
    public String getName() {
        return "Smartphone";
    }

    @Override
    public double getPrice() {
        return 400.0;
    }

    @Override
    public double getWeight() {
        return 0.5;
    }
}

class TV implements Product {
    @Override
    public String getName() {
        return "TV";
    }

    @Override
    public double getPrice() {
        return 1000.0;
    }

    @Override
    public double getWeight() {
        return 10.0;
    }
}
class ProductFactory {
    public static Product getProduct(int productId) {
        switch (productId) {
            case 1:
                return new Laptop();
            case 2:
                return new Smartphone();
            case 3:
                return new TV();
            default:
                return null;
        }
    }
}


// Observer Pattern
interface ProductObserver {
    void update(Product product);
}

class EmailProductObserver implements ProductObserver {
    @Override
    public void update(Product product) {
        System.out.println("Sending product update email for: " + product.getName());
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Singleton Pattern
        ShoppingCart shoppingCart = ShoppingCart.getInstance();

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Add a product to the cart");
            System.out.println("2. View cart and checkout");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProductToCart(scanner, shoppingCart);
                    break;
                case 2:
                    checkout(scanner, shoppingCart);
                    break;
                case 3:
                    System.out.println("Exiting the application. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void addProductToCart(Scanner scanner, ShoppingCart shoppingCart) {
        System.out.println("Select a product by entering its number:");
        System.out.println("1. Laptop");
        System.out.println("2. Smartphone");
        System.out.println("3. TV");

        int productNumber = scanner.nextInt();
        scanner.nextLine();
        Product selectedProduct = ProductFactory.getProduct(productNumber);

        if (selectedProduct != null) {
            System.out.println("Selected product: " + selectedProduct.getName());
            System.out.println("Price: $" + selectedProduct.getPrice());

            System.out.println("Enter your email or card number for payment: ");
            String paymentDetails = scanner.nextLine();

            // Adapter Pattern
            PaymentAdapter paymentAdapter = new CreditCardPaymentAdapter();
            paymentAdapter.processPayment(selectedProduct.getPrice());

            // Observer Pattern
            ProductObserver emailObserver = new EmailProductObserver();
            shoppingCart.addProductObserver(emailObserver);

            // Notify observers about the product
            shoppingCart.notifyProductObservers(selectedProduct);

            // Singleton Pattern
            shoppingCart.addProduct(selectedProduct);
        } else {
            System.out.println("Invalid product number. Please select a valid product.");
        }
    }

    private static void checkout(Scanner scanner, ShoppingCart shoppingCart) {
        // Strategy Pattern
        ShippingStrategy shippingStrategy = promptShippingStrategy(scanner);

        // Checkout
        shoppingCart.checkout(shippingStrategy);

        System.out.println("Enter delivery address: ");
        String deliveryAddress = scanner.nextLine();
        System.out.println("Your order will be delivered to: " + deliveryAddress);

        // Clear the cart after checkout
        shoppingCart.clearCart();
    }

    private static ShippingStrategy promptShippingStrategy(Scanner scanner) {
        System.out.println("Select shipping strategy (1 for Standard, 2 for Express): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        return (choice == 1) ? new StandardShippingStrategy() : new ExpressShippingStrategy();
    }
}
