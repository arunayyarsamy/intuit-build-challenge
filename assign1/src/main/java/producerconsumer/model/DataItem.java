package producerconsumer.model;

/**
 * Represents a data item flowing through the system.
 * It can hold a value or represent a poison pill for termination.
 *
 * @param <T> The type of the value held.
 */
public class DataItem<T> {

    private final T value;
    private final boolean isPoisonPill;
    private final int verificationCount;

    private DataItem(T value, boolean isPoisonPill, int verificationCount) {
        this.value = value;
        this.isPoisonPill = isPoisonPill;
        this.verificationCount = verificationCount;
    }

    /**
     * Creates a normal data item with a value.
     *
     * @param value The value to wrap.
     * @param <T>   The type of the value.
     * @return A new DataItem containing the value.
     */
    public static <T> DataItem<T> normal(T value) {
        return new DataItem<>(value, false, 0);
    }

    /**
     * Creates a poison pill item used to signal termination.
     *
     * @param verifiedCount The total count of items successfully produced.
     * @param <T>           The type of the value (will be null for poison pill).
     * @return A new DataItem representing a poison pill.
     */
    public static <T> DataItem<T> poison(int verifiedCount) {
        return new DataItem<>(null, true, verifiedCount);
    }

    /**
     * @return The value held by this item. May be null if it is a poison pill.
     */
    public T getValue() {
        return value;
    }

    /**
     * @return True if this item is a poison pill, false otherwise.
     */
    public boolean isPoisonPill() {
        return isPoisonPill;
    }

    public int getVerificationCount() {
        return verificationCount;
    }

    @Override
    public String toString() {
        if (isPoisonPill) {
            return "DataItem{POISON_PILL}";
        }
        return "DataItem{value=" + value + "}";
    }
}
