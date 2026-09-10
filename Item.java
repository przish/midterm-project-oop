public abstract class Item {

    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_QUANTITY = 10_000;
    public static final double MAX_PRICE = 500_000.0;

    private String id;
    private String name;
    private int quantity;
    private double price;

    public Item(String id, String name, int quantity, double price) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty.");
        }
        if (id.trim().startsWith("-")) {
            throw new IllegalArgumentException("Item ID cannot be negative.");
        }
        this.id = id.trim();
        setName(name);
        setQuantity(quantity);
        setPrice(price);
    }

    // getters dito

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // setters d2

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        String trimmed = name.trim();
        if (!trimmed.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Item name must contain at least one letter.");
        }
        if (!trimmed.matches("^[a-zA-Z0-9\\s.,'\"()&/+\\-%#:]+$")) {
            throw new IllegalArgumentException("Item name contains invalid characters.");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Item name cannot exceed " + MAX_NAME_LENGTH + " characters.");
        }
        this.name = trimmed;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Quantity cannot exceed " + MAX_QUANTITY + ".");
        }
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (Double.isInfinite(price) || Double.isNaN(price) || price > MAX_PRICE) {
            throw new IllegalArgumentException("Price cannot exceed " + MAX_PRICE + ".");
        }
        this.price = price;
    }

    // abstraction

    public abstract String getCategory();
}