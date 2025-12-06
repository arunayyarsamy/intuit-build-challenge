package producerconsumer.worker;

import producerconsumer.buffer.Buffer;
import producerconsumer.model.DataItem;

import java.util.List;

/**
 * Producer thread that simulates generating data and putting it into the shared
 * buffer.
 *
 * @param <T> The type of data being produced.
 */
public class Producer<T> implements Runnable {

    private final Buffer<DataItem<T>> buffer;
    private final List<T> sourceData;
    private final long delayMillis;
    private final int maxRetries;
    private int successfulProductionCount = 0;

    /**
     * @param buffer      The shared buffer to write to.
     * @param sourceData  The list of items to produce.
     * @param delayMillis Artificial delay to simulate work (in milliseconds).
     * @param maxRetries  Maximum number of retries for item processing.
     */
    public Producer(Buffer<DataItem<T>> buffer, List<T> sourceData, long delayMillis, int maxRetries) {
        this.buffer = buffer;
        this.sourceData = sourceData;
        this.delayMillis = delayMillis;
        this.maxRetries = maxRetries;
    }

    @Override
    public void run() {
        try {
            for (T item : sourceData) {
                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }

                int attempts = 0;
                boolean success = false;
                while (!success && attempts <= maxRetries) {
                    try {
                        attempts++;
                        System.out.println("[Producer] Producing: " + item + " (Attempt " + attempts + ")");
                        buffer.put(DataItem.normal(item));
                        success = true;
                        successfulProductionCount++; // Increment on successful production
                    } catch (RuntimeException e) {
                        System.err.println("[Producer] Failed to produce " + item + ": " + e.getMessage());
                        if (attempts > maxRetries) {
                            System.err.println("[Producer] Max retries reached for " + item + ". Skipping.");
                        }
                    }
                }
            }
            // Signal completion
            System.out.println("[Producer] Sending poison pill with count: " + successfulProductionCount);
            buffer.put(DataItem.poison(successfulProductionCount)); // Pass the count to the poison pill
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Producer] Interrupted");
        }
    }
}
