package gpGUI;

/**
 * Represents an item in a combo box.
 */
public class ComboItem {
    private String key;
    private int value;

    /**
     * Constructs a new ComboItem with the specified key and value.
     *
     * @param key   the key associated with the item
     * @param value the value associated with the item
     */
    public ComboItem(String key, int value) {
        this.key = key;
        this.value = value;
    }

    //Getters
    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key;
    }
}
