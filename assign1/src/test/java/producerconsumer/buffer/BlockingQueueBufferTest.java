package producerconsumer.buffer;

import org.junit.jupiter.api.Test;
import producerconsumer.model.DataItem;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class BlockingQueueBufferTest {

    @Test
    void testAddAndRemove() throws InterruptedException {
        BlockingQueueBuffer<DataItem<Integer>> buffer = new BlockingQueueBuffer<>(5);
        DataItem<Integer> item = DataItem.normal(1);

        buffer.put(item);
        assertEquals(item, buffer.take());
    }

    @Test
    void testBlockingBehavior() {
        BlockingQueueBuffer<DataItem<Integer>> buffer = new BlockingQueueBuffer<>(1);
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            buffer.put(DataItem.normal(1));
            // Now full. Next add should block.
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100); // Wait a bit then remove
                    buffer.take();
                } catch (InterruptedException e) {
                }
            });
            t.start();

            // This line will block until the thread removes an item
            buffer.put(DataItem.normal(2));
        });
    }
}
