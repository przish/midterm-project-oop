import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static Inventory inventory = new Inventory();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    updateItem();
                    break;
                case 3:
                    removeItem();
                    break;
                case 4:
                    displayByCategory();
                    break;
                case 5:
                    displayAll();
                    break;
                case 6:
                    searchItem();
                    break;
                case 7:
                    sortItems();
                    break;
                case 8:
                    displayLowStock();
                    break;
                case 9:
                    running = false;
                    System.out.println("\nExiting program. Goodbye!\n");
                    break;
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("                 MENU                   ");
        System.out.println("========================================");
        System.out.println(" 1 - Add Item");
        System.out.println(" 2 - Update Item");
        System.out.println(" 3 - Remove Item");
        System.out.println(" 4 - Display Items by Category");
        System.out.println(" 5 - Display All Items");
        System.out.println(" 6 - Search Item");
        System.out.println(" 7 - Sort Items");
        System.out.println(" 8 - Display Low Stock Items");
        System.out.println(" 9 - Exit");
        System.out.println("========================================");
        System.out.print("Enter choice: ");
    }

    // ================= INPUT VALIDATION HELPERS =================

    // Menu choice: must be a whole number from 1 to 9
    private static int readMenuChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 9) {
                    return choice;
                }
                System.out.print("[ ! ] Invalid choice. Please enter a number from 1 to 9: ");
            } catch (NumberFormatException e) {
                System.out.print("[ ! ] Invalid input. Please enter a number from 1 to 9: ");
            }
        }
    }

    // Required text field (ID, Name): cannot be blank
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[ ! ] This field cannot be empty. Please try again.");
        }
    }

    // Quantity: must be a whole number, and cannot be negative
    private static int readQuantity(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("[ ! ] Quantity cannot be negative. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[ ! ] Invalid number. Please enter a whole number.");
            }
        }
    }

    // Price: must be a number, and must be greater than zero
    private static double readPrice(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    System.out.println("[ ! ] Price must be greater than zero. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[ ! ] Invalid number. Please enter a valid price.");
            }
        }
    }

    // Category: required, and must be one of the three that exist.
    // Returns null (after printing the required message) if the category
    // doesn't exist, or the properly-capitalized category name if it does.
    private static String readCategoryOrNull(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                break;
            }
            System.out.println("[ ! ] Category cannot be empty. Please try again.");
        }

        if (!inventory.isValidCategory(input)) {
            System.out.println("\n[ ! ] Category " + input + " does not exist!");
            return null;
        }
        return inventory.normalizeCategory(input);
    }

    private static int readUpdateFieldChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equalsIgnoreCase("quantity")) {
                return 1;
            }
            if (input.equals("2") || input.equalsIgnoreCase("price")) {
                return 2;
            }
            System.out.print("[ ! ] Invalid choice. Enter 1 for Quantity or 2 for Price: ");
        }
    }

    private static String readSortBy() {
        while (true) {
            System.out.print("Sort by (Quantity/Price): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quantity") || input.equalsIgnoreCase("price")) {
                return input;
            }
            System.out.println("[ ! ] Invalid input. Please enter Quantity or Price.");
        }
    }

    private static boolean readSortOrder() {
        while (true) {
            System.out.print("Order (Ascending/Descending): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("ascending")) {
                return true;
            }
            if (input.equalsIgnoreCase("descending")) {
                return false;
            }
            System.out.println("[ ! ] Invalid input. Please enter Ascending or Descending.");
        }
    }

    // ================= MENU ACTIONS =================

    private static void addItem() {
        System.out.println("\n----- ADD ITEM -----");
        String category = readCategoryOrNull("Enter Category (Clothing/Electronics/Entertainment): ");
        if (category == null) {
            return;
        }

        String id = readNonEmptyString("Enter ID: ");
        while (inventory.idExists(id)) {
            System.out.println("[ ! ] Item ID " + id + " already exists. Please enter a different ID.");
            id = readNonEmptyString("Enter ID: ");
        }

        String name = readNonEmptyString("Enter Name: ");
        int quantity = readQuantity("Enter Quantity: ");
        double price = readPrice("Enter Price: ");

        Item item;
        if (category.equals("Clothing")) {
            item = new Clothing(id, name, quantity, price);
        } else if (category.equals("Electronics")) {
            item = new Electronics(id, name, quantity, price);
        } else {
            item = new Entertainment(id, name, quantity, price);
        }

        inventory.addItem(item);
        System.out.println("\nItem added successfully!");
    }

    private static void updateItem() {
        System.out.println("\n----- UPDATE ITEM -----");
        String id = readNonEmptyString("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("\n[ ! ] Item not found!");
            return;
        }

        System.out.print("\nUpdate (1) Quantity or (2) Price? ");
        int fieldChoice = readUpdateFieldChoice();

        if (fieldChoice == 1) {
            int oldQuantity = item.getQuantity();
            int newQuantity = readQuantity("Enter new Quantity: ");
            item.setQuantity(newQuantity);
            System.out.println("\nQuantity of Item " + item.getName()
                    + " is updated from " + oldQuantity + " to " + newQuantity);
        } else {
            double oldPrice = item.getPrice();
            double newPrice = readPrice("Enter new Price: ");
            item.setPrice(newPrice);
            System.out.println("\nPrice of Item " + item.getName()
                    + " is updated from " + formatPrice(oldPrice) + " to " + formatPrice(newPrice));
        }
    }

    private static void removeItem() {
        System.out.println("\n----- REMOVE ITEM -----");
        String id = readNonEmptyString("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("\n[ ! ] Item not found!");
            return;
        }

        inventory.removeItem(id);
        System.out.println("\nItem " + item.getName() + " has been removed from the inventory");
    }

    private static void displayByCategory() {
        System.out.println("\n----- DISPLAY ITEMS BY CATEGORY -----");
        String category = readCategoryOrNull("Enter Category (Clothing/Electronics/Entertainment): ");
        if (category == null) {
            return;
        }

        List<Item> items = inventory.getByCategory(category);
        if (items.isEmpty()) {
            System.out.println("\n[ ! ] No items found in the " + category + " category.");
            return;
        }

        System.out.println("\nCategory: " + category);
        printTable4(items);
    }

    private static void displayAll() {
        System.out.println("\n----- DISPLAY ALL ITEMS -----");
        if (inventory.isEmpty()) {
            System.out.println("\n[ ! ] Inventory is empty.");
            return;
        }
        printTable5(inventory.getAll());
    }

    private static void searchItem() {
        System.out.println("\n----- SEARCH ITEM -----");
        String id = readNonEmptyString("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("\n[ ! ] Item not found!");
            return;
        }

        System.out.println();
        System.out.println("ID:       " + item.getId());
        System.out.println("Name:     " + item.getName());
        System.out.println("Quantity: " + formatQuantity(item.getQuantity()));
        System.out.println("Price:    " + formatPrice(item.getPrice()));
        System.out.println("Category: " + item.getCategory());
    }

    private static void sortItems() {
        System.out.println("\n----- SORT ITEMS -----");
        if (inventory.isEmpty()) {
            System.out.println("\n[ ! ] Inventory is empty.");
            return;
        }

        String sortBy = readSortBy();
        boolean ascending = readSortOrder();

        List<Item> sorted = inventory.sortItems(sortBy, ascending);
        printTable5(sorted);
    }

    private static void displayLowStock() {
        System.out.println("\n----- DISPLAY LOW STOCK ITEMS (Quantity <= 5) -----");
        List<Item> lowStock = inventory.getLowStockItems();
        if (lowStock.isEmpty()) {
            System.out.println("\n[ ! ] No low stock items.");
            return;
        }
        printTable5(lowStock);
    }

    // ================= TABLE FORMATTING =================

    private static String formatQuantity(int quantity) {
        if (quantity <= 5) {
            return quantity + " [ ! ]";
        }
        return String.valueOf(quantity);
    }

    private static void printTable4(List<Item> items) {
        System.out.println();
        printSeparator(68);
        System.out.printf("%-12s %-26s %-14s %-12s%n", "ID", "Name", "Quantity", "Price");
        printSeparator(68);
        for (Item item : items) {
            System.out.printf("%-12s %-26s %-14s %-12s%n",
                    item.getId(), item.getName(), formatQuantity(item.getQuantity()), formatPrice(item.getPrice()));
        }
        printSeparator(68);

        boolean hasLowStock = false;
        for (Item item : items) {
            if (item.getQuantity() <= 5) {
                hasLowStock = true;
                break;
            }
        }
        if (hasLowStock) {
            System.out.println("[ ! ] Note: Items marked with [ ! ] have low stock (5 or below).");
        }
    }

    private static void printTable5(List<Item> items) {
        System.out.println();
        printSeparator(86);
        System.out.printf("%-12s %-26s %-14s %-14s %-16s%n", "ID", "Name", "Quantity", "Price", "Category");
        printSeparator(86);
        for (Item item : items) {
            System.out.printf("%-12s %-26s %-14s %-14s %-16s%n",
                    item.getId(), item.getName(), formatQuantity(item.getQuantity()), formatPrice(item.getPrice()), item.getCategory());
        }
        printSeparator(86);

        boolean hasLowStock = false;
        for (Item item : items) {
            if (item.getQuantity() <= 5) {
                hasLowStock = true;
                break;
            }
        }
        if (hasLowStock) {
            System.out.println("[ ! ] Note: Items marked with [ ! ] have low stock (5 or below).");
        }
    }

    private static void printSeparator(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }
        System.out.println(sb.toString());
    }

    private static String formatPrice(double price) {
        return String.format("%.2f", price);
    }
}