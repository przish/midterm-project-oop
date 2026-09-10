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
        boolean isValid = true;
        int choice = 0;
        while (isValid) {
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 9) {
                    isValid = false;
                } else {
                    System.out.print("[ ! ] Invalid choice. Please enter a number from 1 to 9: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("[ ! ] Invalid input. Please enter a number from 1 to 9: ");
            }
        }
        return choice;
    }

    // Required text field (ID): cannot be blank and cannot be negative
    private static String readId(String prompt) {
        boolean isReading = true;
        String input = "";
        while (isReading) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[ ! ] This field cannot be empty. Please try again.");
            } else if (input.startsWith("-")) {
                System.out.println("[ ! ] ID cannot be negative. Please try again.");
            } else {
                isReading = false;
            }
        }
        return input;
    }

    private static String readNonEmptyString(String prompt) {
        return readId(prompt);
    }

    // Item name: required, must contain at least one letter,
    // cannot be solely symbols/numbers/gibberish, and must contain valid characters
    private static String readItemName(String prompt) {
        boolean isReading = true;
        String input = "";
        while (isReading) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("[ ! ] This field cannot be empty. Please try again.");
                continue;
            }

            if (!input.matches(".*[a-zA-Z].*")) {
                System.out.println(
                        "[ ! ] Item name must contain at least one letter and cannot be solely symbols or numbers.");
                continue;
            }

            if (!input.matches("^[a-zA-Z0-9\\s.,'\"()&/+\\-%#:]+$")) {
                System.out.println(
                        "[ ! ] Item name contains invalid characters. Only letters, numbers, spaces, and standard punctuation are allowed.");
                continue;
            }

            if (input.length() > Item.MAX_NAME_LENGTH) {
                System.out.printf("[ ! ] Item name cannot exceed %d characters. Please try again.%n",
                        Item.MAX_NAME_LENGTH);
                continue;
            }

            isReading = false;
        }
        return input;
    }

    // Quantity: must be a whole number, cannot be negative, and cannot exceed MAX_QUANTITY
    private static int readQuantity(String prompt) {
        boolean isReading = true;
        int value = 0;
        while (isReading) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                if (input.contains(",")) {
                    if (!input.matches("^-?[0-9]{1,3}(,[0-9]{3})+$")) {
                        System.out.println(
                                "[ ! ] Invalid quantity format. Please enter a whole number (e.g., 10 or 1,000).");
                        continue;
                    }
                } else {
                    if (!input.matches("^-?[0-9]+$")) {
                        System.out.println("[ ! ] Invalid number. Please enter a whole number.");
                        continue;
                    }
                }

                long rawValue;
                try {
                    rawValue = Long.parseLong(input.replace(",", ""));
                } catch (NumberFormatException e) {
                    System.out.printf("[ ! ] Quantity cannot exceed %,d (number is too large). Please try again.%n",
                            Item.MAX_QUANTITY);
                    continue;
                }

                if (rawValue < 0) {
                    System.out.println("[ ! ] Quantity cannot be negative. Please try again.");
                    continue;
                }
                if (rawValue > Item.MAX_QUANTITY) {
                    System.out.printf("[ ! ] Quantity cannot exceed %,d. Please try again.%n", Item.MAX_QUANTITY);
                    continue;
                }
                value = (int) rawValue;
                isReading = false;
            } catch (NumberFormatException e) {
                System.out.println("[ ! ] Invalid number. Please enter a whole number.");
            }
        }
        return value;
    }

    // Price: must be a number, greater than zero, and cannot exceed MAX_PRICE.
    // Supports standard comma thousands separators (e.g., 15,999 or 15,999.50),
    // but rejects invalid comma placements (e.g., 159,99).
    private static double readPrice(String prompt) {
        boolean isReading = true;
        double value = 0.0;
        while (isReading) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                if (input.contains(",")) {
                    if (!input.matches("^-?[0-9]{1,3}(,[0-9]{3})+(\\.[0-9]+)?$")) {
                        System.out.println(
                                "[ ! ] Invalid price format. Please enter a valid price (e.g., 250 or 15,999).");
                        continue;
                    }
                } else {
                    if (!input.matches("^-?[0-9]+(\\.[0-9]+)?$")) {
                        System.out.println("[ ! ] Invalid number. Please enter a valid price.");
                        continue;
                    }
                }

                try {
                    value = Double.parseDouble(input.replace(",", ""));
                } catch (NumberFormatException e) {
                    System.out.printf("[ ! ] Price is too large. Maximum allowed is %,.2f.%n", Item.MAX_PRICE);
                    continue;
                }

                if (value <= 0) {
                    System.out.println("[ ! ] Price must be greater than zero. Please try again.");
                    continue;
                }
                if (Double.isInfinite(value) || Double.isNaN(value) || value > Item.MAX_PRICE) {
                    System.out.printf("[ ! ] Price cannot exceed %,.2f. Please try again.%n", Item.MAX_PRICE);
                    continue;
                }
                isReading = false;
            } catch (NumberFormatException e) {
                System.out.println("[ ! ] Invalid number. Please enter a valid price.");
            }
        }
        return value;
    }

    // Category: required, and must be one of the three that exist.
    // Displays numeric choices and accepts either number (1-3) or name.
    // Returns null (after printing the required message) if the category
    // doesn't exist, or the properly-capitalized category name if it does.
    private static String readCategoryOrNull(String prompt) {
        System.out.println("Categories:");
        System.out.println(" 1 - Clothing");
        System.out.println(" 2 - Electronics");
        System.out.println(" 3 - Entertainment");
        boolean isReading = true;
        String input = "";
        while (isReading) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                isReading = false;
            } else {
                System.out.println("[ ! ] Category cannot be empty. Please try again.");
            }
        }

        if (!inventory.isValidCategory(input)) {
            System.out.println("\n[ ! ] Category " + input + " does not exist!");
            return null;
        }
        return inventory.normalizeCategory(input);
    }

    private static int readUpdateFieldChoice() {
        boolean isChoosing = true;
        int choice = 0;
        while (isChoosing) {
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equalsIgnoreCase("quantity")) {
                choice = 1;
                isChoosing = false;
            } else if (input.equals("2") || input.equalsIgnoreCase("price")) {
                choice = 2;
                isChoosing = false;
            } else {
                System.out.print("[ ! ] Invalid choice. Enter 1 for Quantity or 2 for Price: ");
            }
        }
        return choice;
    }

    private static String readSortBy() {
        boolean isChoosing = true;
        String sortBy = "";
        while (isChoosing) {
            System.out.print("Sort by (1 - Quantity, 2 - Price): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equalsIgnoreCase("quantity")) {
                sortBy = "Quantity";
                isChoosing = false;
            } else if (input.equals("2") || input.equalsIgnoreCase("price")) {
                sortBy = "Price";
                isChoosing = false;
            } else {
                System.out.println("[ ! ] Invalid choice. Please enter 1 for Quantity or 2 for Price.");
            }
        }
        return sortBy;
    }

    private static boolean readSortOrder() {
        boolean isChoosing = true;
        boolean ascending = true;
        while (isChoosing) {
            System.out.print("Order (1 - Ascending, 2 - Descending): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equalsIgnoreCase("ascending")) {
                ascending = true;
                isChoosing = false;
            } else if (input.equals("2") || input.equalsIgnoreCase("descending")) {
                ascending = false;
                isChoosing = false;
            } else {
                System.out.println("[ ! ] Invalid choice. Please enter 1 for Ascending or 2 for Descending.");
            }
        }
        return ascending;
    }

    // ================= MENU ACTIONS =================

    private static void addItem() {
        System.out.println("\n----- ADD ITEM -----");
        String category = readCategoryOrNull("Enter Category (1-3): ");
        if (category == null) {
            return;
        }

        String id = readNonEmptyString("Enter ID: ");
        boolean isDuplicate = inventory.idExists(id);
        while (isDuplicate) {
            System.out.println("[ ! ] Item ID " + id + " already exists. Please enter a different ID.");
            id = readNonEmptyString("Enter ID: ");
            isDuplicate = inventory.idExists(id);
        }

        String name = readItemName("Enter Name: ");
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

        System.out.print("\nUpdate (1 - Quantity, 2 - Price): ");
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
        String category = readCategoryOrNull("Enter Category (1-3): ");
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
        String query = readNonEmptyString("Enter ID or Name: ");
        List<Item> results = inventory.search(query);

        if (results.isEmpty()) {
            System.out.println("\n[ ! ] Item not found!");
            return;
        }

        for (Item item : results) {
            System.out.println();
            System.out.println("ID:       " + item.getId());
            System.out.println("Name:     " + item.getName());
            System.out.println("Quantity: " + formatQuantity(item.getQuantity()));
            System.out.println("Price:    " + formatPrice(item.getPrice()));
            System.out.println("Category: " + item.getCategory());
        }
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
                    item.getId(), item.getName(), formatQuantity(item.getQuantity()), formatPrice(item.getPrice()),
                    item.getCategory());
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