package producerconsumer.test;

import producerconsumer.buffer.BlockingQueueBuffer;
import producerconsumer.buffer.Buffer;
import producerconsumer.buffer.WaitNotifyBuffer;
import producerconsumer.config.AppConfig;
import producerconsumer.model.DataItem;
import producerconsumer.worker.Consumer;
import producerconsumer.worker.Producer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * JUnit 5 test for Producer-Consumer implementation.
 */
public class ProducerConsumerTest {

    @Test
    void testBlockingQueueMode() {
        assertTrue(runTest("BlockingQueue Mode", AppConfig.BufferMode.BLOCKING_QUEUE, 100, 10));
    }

    @Test
    void testWaitNotifyMode() {
        assertTrue(runTest("WaitNotify Mode", AppConfig.BufferMode.WAIT_NOTIFY, 100, 10));
    }

    @Test
    void testEmptyInput() {
        assertTrue(runTest("Empty Input", AppConfig.BufferMode.BLOCKING_QUEUE, 0, 10));
    }

    @Test
    void testSingleItem() {
        assertTrue(runTest("Single Item", AppConfig.BufferMode.WAIT_NOTIFY, 1, 10));
    }

    private boolean runTest(String testName, AppConfig.BufferMode mode, int itemCount, int capacity) {
        System.out.print("Test [" + testName + "]: ");
        try {
            // Setup
            List<Integer> sourceData = IntStream.rangeClosed(1, itemCount)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> destinationData = Collections.synchronizedList(new ArrayList<>());

            Buffer<DataItem<Integer>> buffer;
            if (mode == AppConfig.BufferMode.WAIT_NOTIFY) {
                buffer = new WaitNotifyBuffer<>(capacity);
            } else {
                buffer = new BlockingQueueBuffer<>(capacity);
            }

            // Execution
            Producer<Integer> producer = new Producer<>(buffer, sourceData, 0, 3); // 0ms delay for speed, 3 retries
            Consumer<Integer> consumer = new Consumer<>(buffer, destinationData, 0);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(producer);
            executor.submit(consumer);

            executor.shutdown();
            boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);

            if (!finished) {
                System.out.println("FAILED (Timeout)");
                executor.shutdownNow();
                return false;
            }

            // Verification
            if (sourceData.size() != destinationData.size()) {
                System.out.println("FAILED (Size Mismatch: expected " + sourceData.size() + ", got "
                        + destinationData.size() + ")");
                return false;
            }

            if (!sourceData.equals(destinationData)) {
                System.out.println("FAILED (Content Mismatch)");
                return false;
            }

            System.out.println("PASSED");
            return true;

        } catch (Exception e) {
            System.out.println("FAILED (Exception: " + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
    }
}
