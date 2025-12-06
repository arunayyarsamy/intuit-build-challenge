# Architecture Overview

This document provides a detailed explanation of the system architecture for the Java Producer–Consumer Challenge.  
The architecture is designed to demonstrate clean object-oriented structure, concurrency primitives, safe thread communication, and extensibility.

---

# 1. System Architecture

The system implements a unidirectional data pipeline:

```
SourceContainer → Producer → Buffer → Consumer → DestinationContainer
```

Each stage is decoupled and communicates only through defined abstractions. The buffer acts as the synchronization point between multiple threads.

---

# 2. Architectural Goals

The architecture is driven by the following goals:

### ✔️ Modularity  
Each component has a single responsibility and is replaceable without affecting others.

### ✔️ Extensibility  
Two synchronization mechanisms are supported:
- High-level `BlockingQueue`
- Low-level `wait/notify`

Both implementations plug into the same `Buffer<T>` interface.

### ✔️ Concurrency Safety  
Thread lifecycle, blocking behavior, and shutdown are predictable and correct.

### ✔️ Demonstrate Depth  
The architecture shows understanding of:
- Blocking queues  
- Wait/Notify  
- Poison pill termination  
- ExecutorService  
- Abstractions & decoupling  

---

# 3. Component Architecture

## 3.1 Model Layer

### **DataItem<T>**
Represents the item flowing through the system.

Responsibilities:
- Wrap normal values  
- Represent a poison pill  
- Allow consumers to detect termination  

Key characteristics:
- Immutable  
- Simple data carrier  
- Used by both Producer and Consumer  

---

## 3.2 Buffer Layer

### Buffer<T> (Interface)
Defines a blocking buffer used for handoff between threads.

```java
void put(T item) throws InterruptedException;
T take() throws InterruptedException;
```

This interface isolates worker logic from synchronization policy.

---

### BlockingQueueBuffer<T>
Implementation using Java’s built-in concurrency utilities.

Internals:
- Backed by an `ArrayBlockingQueue`  
- Provides reliable blocking semantics  
- Zero manual locking  

Used as:
- The default, production-ready implementation  

---

### WaitNotifyBuffer<T>
Manual implementation using classic monitor synchronization.

Internals:
- Backed by `ArrayDeque`  
- Uses `synchronized`, `wait()`, `notifyAll()`  
- Demonstrates understanding of low-level thread coordination  

Used for:
- Demonstrating deeper concurrency knowledge  

---

## 3.3 Worker Layer

### Producer
Reads from a source container and pushes items into the buffer.

Responsibilities:
- Sequentially produce items  
- Insert “poison pill” when complete  
- Optional artificial delay  
- Handles thread interruption gracefully  

Key behavior:
```java
buffer.put(DataItem.normal(value));
...
buffer.put(DataItem.poison());
```

---

### Consumer
Reads from the buffer and writes items into the destination container.

Responsibilities:
- Continuously consume until poison pill  
- Thread-safe append to the destination  
- Optional delay  
- Predictable shutdown  

Key behavior:
```java
while (!item.isPoisonPill()) {
    destination.add(item.getValue());
}
```

---

## 3.4 Configuration Layer

### AppConfig
Holds all tunable parameters:

- Buffer capacity  
- Number of items  
- Buffer mode (BlockingQueue / WaitNotify)  
- Worker delays  
- Future extensibility  

### BufferMode (Enum)
```
BLOCKING_QUEUE
WAIT_NOTIFY
```

---

## 3.5 Application Layer

### ProducerConsumerApplication

Responsibilities:
- Initialize configuration  
- Create and populate source container  
- Instantiate buffer using selected mode  
- Start Producer & Consumer via ExecutorService  
- Await thread completion  
- Provide final verification  

Lifecycle:
1. Build configuration  
2. Initialize containers  
3. Select buffer implementation  
4. Execute Producer and Consumer  
5. Await termination  
6. Perform correctness validation  

Executor choice:
```java
ExecutorService executor = Executors.newFixedThreadPool(2);
```

---

# 4. Concurrency Architecture

## 4.1 Synchronization Models

### A. BlockingQueue Model
- Best for real-world applications  
- Avoids manual locking  
- Guarantees thread safety  

Pros:
- Fewer bugs  
- Simpler  
- Highly efficient  

---

### B. Wait/Notify Model
- Manual control over monitor lock  
- Requires thorough understanding of `synchronized`  
- Explicit blocking behavior  

Pros:
- Deep understanding of Java concurrency  
- Useful in interview settings  

Cons:
- More error-prone  
- Harder to maintain  

---

# 5. Termination Architecture: Poison Pill Pattern

To stop the consumer safely, the system uses a **poison pill**:

```
Producer → puts poison pill → Consumer detects pill → shuts down gracefully
```

Why we avoid:
- Stopping threads manually  
- Using flags shared across threads  
- Thread interruption as a control mechanism  

Poison pill is the cleanest, most scalable solution.

---

# 6. Sequence Diagram (ASCII)

```
Producer                         Buffer                       Consumer
    |                              |                            |
    |-- put(DataItem) -----------> |                            |
    |                              | -- take() ---------------> |
    |                              |                            |
    |-- put(poison pill) --------> |                            |
    |                              | -- take() ---------------> |
    |                              |                            |
    |                              | <-- detects poison --------|
    |                              |                            |
```

---

# 7. Design Rationale Summary

- The architecture is **clean**, **modular**.
- Abstractions allow multiple synchronization strategies  
- Proper thread-handling with predictable shutdown  
- Safe, extensible, real-world concurrency foundation  
