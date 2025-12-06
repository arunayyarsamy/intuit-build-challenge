package producerconsumer.buffer;

/**
 * Interface defining a blocking buffer for thread-safe data transfer.
 *
 * @param <T> The type of items stored in the buffer.
 */
public interface Buffer<T> {

    /**
     * Inserts an item into the buffer, waiting if necessary for space to become
     * available.
     *
     * @param item The item to insert.
     * @throws InterruptedException if data transfer is interrupted.
     */
    void put(T item) throws InterruptedException;

    /**
     * Retrieves and removes the head of this buffer, waiting if necessary until an
     * element becomes available.
     *
     * @return The head of the buffer.
     * @throws InterruptedException if data transfer is interrupted.
     */
    T take() throws InterruptedException;
}
