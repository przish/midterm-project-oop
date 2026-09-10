import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Inventory {

    private List<Item> items = new ArrayList<>();

    public static final String[] CATEGORIES = { "Clothing", "Electronics", "Entertainment" };

    // Validation: is this a real category? (Accepts category name or 1-based index: 1, 2, 3)
    public boolean isValidCategory(String category) {
        if (category == null) {
            return false;
        }
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (category.equals(String.valueOf(i + 1)) || CATEGORIES[i].equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    // Returns the properly-capitalized version of a category the user typed or selected,
    // e.g. "1" -> "Clothing", "electronics" -> "Electronics"
    public String normalizeCategory(String category) {
        if (category == null) {
            return category;
        }
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (category.equals(String.valueOf(i + 1)) || CATEGORIES[i].equalsIgnoreCase(category)) {
                return CATEGORIES[i];
            }
        }
        return category;
    }

    // Validation: does this ID already belong to an item?
    public boolean idExists(String id) {
        return findById(id) != null;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item findById(String id) {
        for (Item item : items) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    // Normalizes text by trimming and collapsing multiple spaces into a single space
    public static String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }

    // Searches for items by ID first, then by Name with whitespace normalized.
    // e.g., searching for "Pen 15" matches "Pen  15", returning any items matching that name even if they have different IDs.
    public List<Item> search(String query) {
        List<Item> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String trimmedQuery = query.trim();
        String normalizedQuery = normalizeWhitespace(trimmedQuery);

        // 1. Check exact match by ID first
        Item byId = findById(trimmedQuery);
        if (byId != null) {
            results.add(byId);
            return results;
        }

        // 2. Search by Name with normalized whitespace (case-insensitive)
        for (Item item : items) {
            String normalizedItemName = normalizeWhitespace(item.getName());
            if (normalizedItemName.equalsIgnoreCase(normalizedQuery)) {
                results.add(item);
            }
        }

        return results;
    }

    public boolean removeItem(String id) {
        Item item = findById(id);
        if (item == null) {
            return false;
        }
        items.remove(item);
        return true;
    }

    public List<Item> getByCategory(String category) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<Item> getAll() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<Item> getLowStockItems() {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() <= 5) {
                result.add(item);
            }
        }
        return result;
    }

    public List<Item> sortItems(String sortBy, boolean ascending) {
        List<Item> sorted = new ArrayList<>(items);
        Comparator<Item> comparator;

        if (sortBy.equalsIgnoreCase("quantity")) {
            comparator = Comparator.comparingInt(Item::getQuantity);
        } else {
            comparator = Comparator.comparingDouble(Item::getPrice);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);
        return sorted;
    }
}