package producerconsumer.buffer;

import org.junit.jupiter.api.Test;
import producerconsumer.model.DataItem;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class WaitNotifyBufferTest {

    @Test
    void testAddAndRemove() throws InterruptedException {
        WaitNotifyBuffer<DataItem<Integer>> buffer = new WaitNotifyBuffer<>(5);
        DataItem<Integer> item = DataItem.normal(1);

        buffer.put(item);
        assertEquals(item, buffer.take());
    }

    @Test
    void testBlockingBehavior() {
        WaitNotifyBuffer<DataItem<Integer>> buffer = new WaitNotifyBuffer<>(1);
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
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

            // This line should block until thread removes item
            buffer.put(DataItem.normal(2));
        });
    }
}
