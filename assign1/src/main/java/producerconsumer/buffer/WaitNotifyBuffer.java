package producerconsumer.buffer;

import java.util.Queue;
import java.util.ArrayDeque;

/**
 * Buffer implementation using manual synchronization with {@code wait()} and
 * {@code notifyAll()}.
 * This implementation demonstrates low-level thread coordination.
 *
 * @param <T> The type of items stored.
 */
public class WaitNotifyBuffer<T> implements Buffer<T> {

    private final Queue<T> queue;
    private final int capacity;

    /**
     * @param capacity The maximum number of items the buffer can hold.
     */
    public WaitNotifyBuffer(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    @Override
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }

    @Override
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }
}
