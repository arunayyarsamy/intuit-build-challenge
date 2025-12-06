package producerconsumer.buffer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Buffer implementation using Java's built-in
 * {@link java.util.concurrent.BlockingQueue}.
 * High-performance, production-ready implementation.
 *
 * @param <T> The type of items stored.
 */
public class BlockingQueueBuffer<T> implements Buffer<T> {

    private final BlockingQueue<T> queue;

    /**
     * @param capacity The maximum number of items the buffer can hold.
     */
    public BlockingQueueBuffer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void put(T item) throws InterruptedException {
        queue.put(item);
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }
}
