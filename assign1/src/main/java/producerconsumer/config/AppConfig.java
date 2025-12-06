package producerconsumer.config;

/**
 * Configuration for the Producer-Consumer application.
 */
public class AppConfig {

    private final int bufferCapacity;
    private final int itemCount;
    private final BufferMode bufferMode;
    private final int producerDelayMillis;
    private final int consumerDelayMillis;
    private final int producerRetries;

    public AppConfig(int bufferCapacity, int itemCount, BufferMode bufferMode, int producerDelayMillis,
            int consumerDelayMillis, int producerRetries) {
        this.bufferCapacity = bufferCapacity;
        this.itemCount = itemCount;
        this.bufferMode = bufferMode;
        this.producerDelayMillis = producerDelayMillis;
        this.consumerDelayMillis = consumerDelayMillis;
        this.producerRetries = producerRetries;
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BufferMode getBufferMode() {
        return bufferMode;
    }

    public int getProducerDelayMillis() {
        return producerDelayMillis;
    }

    public int getConsumerDelayMillis() {
        return consumerDelayMillis;
    }

    public int getProducerRetries() {
        return producerRetries;
    }

    /**
     * Enum for buffer implementation modes.
     */
    public enum BufferMode {
        BLOCKING_QUEUE,
        WAIT_NOTIFY
    }
}
