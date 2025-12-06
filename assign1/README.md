# Java Producer-Consumer Challenge

## Overview
This project implements a robust **Producer-Consumer** pattern in Java, demonstrating advanced thread synchronization and concurrency primitives. It is designed with a unidirectional data pipeline:

`SourceContainer → Producer → Buffer → Consumer → DestinationContainer`

For a detailed technical deep-dive, please refer to [docs/Architecture.md](docs/Architecture.md).

## Key Features
- **Two Synchronization Modes**:
  - **BlockingQueue**: High-performance, production-ready implementation using `java.util.concurrent`.
  - **Wait/Notify**: Manual monitor synchronization to demonstrate low-level concurrency mastery.
- **Graceful Termination**: Uses the **Poison Pill** pattern to ensure all threads shut down cleanly without abrupt interruption.
- **Thread Safety**: Fully synchronized data transfer and state management.
- **Granular Unit Tests**: Comprehensive test suite using JUnit 5 and Mockito.

## Project Structure
The source code follows a clean package structure under `producerconsumer`:
- `buffer`: Buffer interfaces and implementations.
- `worker`: Producer and Consumer runnables.
- `model`: Data carriers (`DataItem`).
- `config`: Configuration classes.

## How to Run
All commands should be run from the `assign1` directory.

### 1. Build & Test
This project uses Maven for dependency management and building.

```bash
# Compile and run unit tests
mvn clean test

# Package the application
mvn package
```

### 2. Run Application
Run the application using the `exec-maven-plugin` (if configured) or directly with Java from the `target/classes` or `out` directory.

**Using Java:**
```bash
# Ensure classes are compiled
mvn compile

# Run in default BlockingQueue mode
java -cp target/classes producerconsumer.ProducerConsumerApplication

# Run in Wait/Notify mode
java -cp target/classes producerconsumer.ProducerConsumerApplication WAIT_NOTIFY
```

## Sample Output
```
Starting Producer-Consumer Application
Mode: BLOCKING_QUEUE
Items: 50
[Producer] Producing: 1
...
[Consumer] Consumed: 50
[Producer] Sending poison pill...
[Consumer] Received poison pill. Terminating.

Application Finished.
Duration: 615 ms

--- Verification ---
SUCCESS: Source and Destination content match perfectly.
```
