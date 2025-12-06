package producerconsumer;

import producerconsumer.buffer.BlockingQueueBuffer;
import producerconsumer.buffer.Buffer;
import producerconsumer.buffer.WaitNotifyBuffer;
import producerconsumer.config.AppConfig;
import producerconsumer.model.DataItem;
import producerconsumer.worker.Consumer;
import producerconsumer.worker.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main application class for the Producer-Consumer challenge.
 */
public class ProducerConsumerApplication {

    public static void main(String[] args) {
        // --- 1. Configuration ---
        // You can change the mode here to AppConfig.BufferMode.WAIT_NOTIFY to test
        // manual synchronization
        AppConfig config = new AppConfig(
                10, // Buffer Capacity
                50, // Total items to produce
                AppConfig.BufferMode.BLOCKING_QUEUE,
                5, // Producer delay (ms)
                10, // Consumer delay (ms)
                3 // Producer retries
        );

        // Parse args if provided for mode switching
        if (args.length > 0 && args[0].equalsIgnoreCase("WAIT_NOTIFY")) {
            config = new AppConfig(10, 50, AppConfig.BufferMode.WAIT_NOTIFY, 5, 10, 3);
        }

        System.out.println("Starting Producer-Consumer Application");
        System.out.println("Mode: " + config.getBufferMode());
        System.out.println("Items: " + config.getItemCount());

        runApplication(config);
    }

    private static void runApplication(AppConfig config) {
        // --- 2. Setup Data ---
        List<Integer> sourceData = IntStream.rangeClosed(1, config.getItemCount())
                .boxed()
                .collect(Collectors.toList());

        List<Integer> destinationData = Collections.synchronizedList(new ArrayList<>());

        // --- 3. Initialize Buffer ---
        Buffer<DataItem<Integer>> buffer;
        if (config.getBufferMode() == AppConfig.BufferMode.WAIT_NOTIFY) {
            buffer = new WaitNotifyBuffer<>(config.getBufferCapacity());
        } else {
            buffer = new BlockingQueueBuffer<>(config.getBufferCapacity());
        }

        // --- 4. Create Workers ---
        Producer<Integer> producer = new Producer<>(buffer, sourceData, config.getProducerDelayMillis(),
                config.getProducerRetries());
        Consumer<Integer> consumer = new Consumer<>(buffer, destinationData, config.getConsumerDelayMillis());

        // --- 5. Execute ---
        ExecutorService executor = Executors.newFixedThreadPool(2);
        long startTime = System.currentTimeMillis();

        executor.submit(producer);
        executor.submit(consumer);

        // --- 6. Shutdown & Await ---
        executor.shutdown();
        try {
            // Wait for a reasonable amount of time based on delays + buffer
            boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
            if (!finished) {
                System.err.println("Timed out waiting for tasks to finish.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nApplication Finished.");
        System.out.println("Duration: " + (endTime - startTime) + " ms");

        // --- 7. Verification ---
        verify(sourceData, destinationData);
    }

    private static void verify(List<Integer> source, List<Integer> destination) {
        System.out.println("\n--- Verification ---");
        System.out.println("Expected items: " + source.size());
        System.out.println("Consumed items: " + destination.size());

        if (source.size() != destination.size()) {
            System.err.println("FAILURE: Item count mismatch!");
            return;
        }

        if (source.equals(destination)) {
            System.out.println("SUCCESS: Source and Destination content match perfectly.");
        } else {
            System.err.println("FAILURE: Content mismatch!");
        }
    }
}
