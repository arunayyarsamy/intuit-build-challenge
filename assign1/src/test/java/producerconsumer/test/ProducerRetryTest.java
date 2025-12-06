package producerconsumer.test;

import producerconsumer.buffer.Buffer;
import producerconsumer.model.DataItem;
import producerconsumer.worker.Consumer;
import producerconsumer.worker.Producer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JUnit 5 test to demonstrate Producer retry logic using a "Flaky" Buffer.
 */
public class ProducerRetryTest {

    /**
     * Test where Producer succeeds after retrying.
     */
    @Test
    void testRetrySuccess() {
        System.out.println("\n--- Test: Success After Retry ---");
        int failuresToSimulate = 2;
        int maxRetries = 3;

        FlakyBuffer flakyBuffer = new FlakyBuffer(failuresToSimulate);
        List<Integer> source = Collections.singletonList(1);
        List<Integer> dest = Collections.synchronizedList(new ArrayList<>());

        // Setup Producer with 3 retries
        Producer<Integer> producer = new Producer<>(flakyBuffer, source, 0, maxRetries);
        Consumer<Integer> consumer = new Consumer<>(flakyBuffer, dest, 0);

        runParams(producer, consumer);

        System.out.println("Attempts made: " + flakyBuffer.getAttemptCount());

        // Expected: 1 item produced eventually
        assertEquals(1, dest.size(), "Should have produced 1 item eventually");
        assertEquals(1, dest.get(0), "Item value should match");
    }

    /**
     * Test where Producer runs out of retries.
     */
    @Test
    void testRetryExhaustion() {
        System.out.println("\n--- Test: Retry Exhaustion ---");
        int failuresToSimulate = 5; // More than max retries
        int maxRetries = 3;

        FlakyBuffer flakyBuffer = new FlakyBuffer(failuresToSimulate);
        List<Integer> source = Collections.singletonList(99);
        List<Integer> dest = Collections.synchronizedList(new ArrayList<>());

        // Setup Producer with 3 retries
        Producer<Integer> producer = new Producer<>(flakyBuffer, source, 0, maxRetries);
        Consumer<Integer> consumer = new Consumer<>(flakyBuffer, dest, 0);

        runParams(producer, consumer);

        System.out.println("Attempts made: " + flakyBuffer.getAttemptCount());

        // Expected: 0 items produced (gave up)
        assertTrue(dest.isEmpty(), "Should give up after max retries");
    }

    private void runParams(Producer<Integer> p, Consumer<Integer> c) {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(p);
        exec.submit(c);
        exec.shutdown();
        try {
            exec.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * A Buffer implementation that fails N times before succeeding.
     */
    static class FlakyBuffer implements Buffer<DataItem<Integer>> {
        private final AtomicInteger failureCount;
        private final AtomicInteger attempts = new AtomicInteger(0);
        // Minimal backing storage
        private DataItem<Integer> storedItem = null;

        public FlakyBuffer(int failuresToSimulate) {
            this.failureCount = new AtomicInteger(failuresToSimulate);
        }

        public int getAttemptCount() {
            return attempts.get();
        }

        @Override
        public synchronized void put(DataItem<Integer> item) throws InterruptedException {
            if (item.isPoisonPill()) {
                // poison pill always succeeds so consumer can stop
                while (storedItem != null) {
                    wait();
                }
                storedItem = item;
                notifyAll();
                return;
            }

            attempts.incrementAndGet();
            if (failureCount.get() > 0) {
                failureCount.decrementAndGet();
                System.out.println("  [FlakyBuffer] Simulating CRASH/FAILURE!");
                throw new RuntimeException("Simulated Network Error");
            }

            // Success
            System.out.println("  [FlakyBuffer] Success!");
            while (storedItem != null) {
                wait();
            }
            storedItem = item;
            notifyAll();
        }

        @Override
        public synchronized DataItem<Integer> take() throws InterruptedException {
            while (storedItem == null) {
                wait();
            }
            DataItem<Integer> result = storedItem;
            storedItem = null;
            notifyAll(); // allow put to proceed if waiting
            return result;
        }
    }
}
