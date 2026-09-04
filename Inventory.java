import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Inventory {

    private List<Item> items = new ArrayList<>();

    public static final String[] CATEGORIES = { "Clothing", "Electronics", "Entertainment" };

    // Validation: is this a real category?
    public boolean isValidCategory(String category) {
        for (String c : CATEGORIES) {
            if (c.equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    // Returns the properly-capitalized version of a category the user typed,
    // e.g. "electronics" -> "Electronics"
    public String normalizeCategory(String category) {
        for (String c : CATEGORIES) {
            if (c.equalsIgnoreCase(category)) {
                return c;
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