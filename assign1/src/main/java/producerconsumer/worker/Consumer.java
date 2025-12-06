package producerconsumer.worker;

import producerconsumer.buffer.Buffer;
import producerconsumer.model.DataItem;

import java.util.List;

/**
 * Consumer thread that reads from the shared buffer and stores valid items.
 * Terminate when a poison pill is received.
 *
 * @param <T> The type of data being consumed.
 */
public class Consumer<T> implements Runnable {

    private final Buffer<DataItem<T>> buffer;
    private final List<T> destination;
    private final long delayMillis;

    /**
     * @param buffer      The shared buffer to read from.
     * @param destination The container to store consumed items.
     * @param delayMillis Artificial delay to simulate work (in milliseconds).
     */
    public Consumer(Buffer<DataItem<T>> buffer, List<T> destination, long delayMillis) {
        this.buffer = buffer;
        this.destination = destination;
        this.delayMillis = delayMillis;
    }

    @Override
    public void run() {
        try {
            int consumedCount = 0;
            while (true) {
                DataItem<T> item = buffer.take();

                if (item.isPoisonPill()) {
                    System.out.println("[Consumer] Received poison pill. Terminating.");
                    System.out.println("[Consumer] Verifying data integrity...");
                    int expected = item.getVerificationCount();
                    if (consumedCount == expected) {
                        System.out.println("[Consumer] SUCCESS: Integrity Verified. Presumed Count: " + expected
                                + ", Actual: " + consumedCount);
                    } else {
                        System.err.println("[Consumer] FAILURE: Data Loss Detected! Presumed Count: " + expected
                                + ", Actual: " + consumedCount);
                    }
                    break;
                }

                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }

                System.out.println("[Consumer] Consumed: " + item.getValue());
                consumedCount++;
                // Synchronized block to ensure thread-safe addition if destination is not
                // thread-safe
                synchronized (destination) {
                    destination.add(item.getValue());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Consumer] Interrupted");
        }
    }
}
