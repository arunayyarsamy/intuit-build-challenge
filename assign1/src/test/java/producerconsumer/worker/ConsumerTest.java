package producerconsumer.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import producerconsumer.buffer.Buffer;
import producerconsumer.model.DataItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerTest {

    @Mock
    private Buffer<DataItem<Integer>> buffer;

    @Test
    void testConsumerRunsSuccessfully() throws Exception {
        // Setup mock behavior: return 1, 2, and then Poison Pill
        when(buffer.take())
                .thenReturn(DataItem.normal(1))
                .thenReturn(DataItem.normal(2))
                .thenReturn(DataItem.poison(2));

        List<Integer> destination = new ArrayList<>();
        Consumer<Integer> consumer = new Consumer<>(buffer, destination, 0);

        consumer.run();

        // Verify destination contains items
        assertEquals(2, destination.size());
        assertEquals(1, destination.get(0));
        assertEquals(2, destination.get(1));

        // Verify buffer interactions
        verify(buffer, times(3)).take();
    }
}
