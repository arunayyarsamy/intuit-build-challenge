package producerconsumer.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import producerconsumer.buffer.Buffer;
import producerconsumer.model.DataItem;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerTest {

    @Mock
    private Buffer<DataItem<Integer>> buffer;

    @Test
    void testProducerRunsSuccessfully() throws Exception {
        List<Integer> data = Arrays.asList(1, 2, 3);
        Producer<Integer> producer = new Producer<>(buffer, data, 0, 3);

        producer.run();

        // Verify that buffer.put was called for each item + poison pill
        verify(buffer, times(3)).put(argThat(item -> !item.isPoisonPill()));
        verify(buffer, times(1)).put(argThat(item -> item.isPoisonPill()));
    }

    @Test
    void testProducerRetriesOnRuntimeException() throws Exception {
        // Producer catches RuntimeException and retries
        // Let's force an exception on first put
        doThrow(new RuntimeException("Simulated error"))
                .doNothing()
                .when(buffer).put(any());

        List<Integer> data = Arrays.asList(1);
        Producer<Integer> producer = new Producer<>(buffer, data, 0, 3);

        producer.run();

        // Verify it retried (called put at least twice)
        verify(buffer, atLeast(2)).put(any());
    }

    @Test
    void testProducerStopsOnInterruption() throws Exception {
        // Producer catches InterruptedException and stops
        doThrow(new InterruptedException("Simulated interruption"))
                .when(buffer).put(any());

        List<Integer> data = Arrays.asList(1, 2);
        Producer<Integer> producer = new Producer<>(buffer, data, 0, 3);

        producer.run();

        // Verify it called put once (and failed) and then stopped (did not try to put
        // 2)
        verify(buffer, times(1)).put(any());
        // Verify it did NOT send poison pill (because it exited loop early)
        verify(buffer, never()).put(argThat(item -> item.isPoisonPill()));
    }
}
