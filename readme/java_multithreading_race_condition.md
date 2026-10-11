# Java Multithreading — Race Conditions & Shared Mutable State

## Overview

When multiple threads execute concurrently, they may access the same data.

If that data is:

- **Shared** between threads
- **Mutable** (can be changed)
- Accessed or modified concurrently
- Not properly coordinated

then the application can experience a **race condition**.

A race condition can produce incorrect, inconsistent, or unpredictable results.

---

## 1. The Core Problem

```text
Multiple Threads
       ↓
Shared Mutable Data
       ↓
Concurrent Access / Modification
       ↓
Unexpected Interleaving
       ↓
Race Condition
       ↓
Incorrect Result
```

The problem is not simply that multiple threads exist.

The problem is that multiple threads can interact with the **same changing data** without appropriate coordination.

---

## 2. What Is a Race Condition?

A **race condition** occurs when multiple threads concurrently access shared mutable data and the result depends on the timing or interleaving of their execution.

In simple terms:

> The result depends on which thread gets to execute certain operations first.

---

## 3. Shared Data

Data is **shared** when multiple threads can access the same data.

Example:

```java
private static int count = 0;
```

If two threads execute code that accesses this static variable:

```text
Thread 1 ──────┐
               │
               ├──→ count
               │
Thread 2 ──────┘
```

both threads are accessing the same variable.

Therefore, `count` is shared.

---

## 4. Mutable Data

Data is **mutable** when its value can be changed.

For example:

```java
count++;
```

changes the value of `count`.

So:

```java
private static int count = 0;
```

is mutable because its value can become:

```text
0 → 1 → 2 → 3 → ...
```

---

## 5. Shared Mutable State

This is an important concept in multithreading.

```text
Shared
   +
Mutable
   ↓
Potential concurrency problem
```

Example:

```java
private static int count = 0;
```

If multiple threads modify it:

```java
count++;
```

we need to think about thread safety.

---

## 6. Shared Mutable Objects

The concept is not limited to primitive variables.

An object can also be shared and mutable.

Example:

```java
class Counter {

    int count = 0;

    void increment() {
        count++;
    }
}
```

If the same object is shared:

```java
Counter counter = new Counter();
```

and multiple threads call:

```java
counter.increment();
```

then the `Counter` object is shared mutable state.

```text
             Counter Object
             count = 0
                  │
          ┌───────┴───────┐
          ↓               ↓
      Thread 1         Thread 2
          │               │
          └───────┬───────┘
                  ↓
          modifies same object
```

Race conditions can happen with shared mutable objects as well as shared mutable primitive variables.

---

## 7. Concurrent Thread Access

Multiple threads can execute at overlapping times.

For example:

```text
Thread 1:
    READ
    ADD
    WRITE

Thread 2:
    READ
    ADD
    WRITE
```

The execution may actually happen like:

```text
Thread 1 → READ
Thread 2 → READ
Thread 1 → ADD
Thread 2 → ADD
Thread 1 → WRITE
Thread 2 → WRITE
```

This is **interleaving**.

The exact order is influenced by thread scheduling.

---

## 8. Why `count++` Is Not One Simple Operation

Consider:

```java
count++;
```

It looks like one statement.

Conceptually, it involves:

```text
1. READ count
2. ADD 1
3. WRITE count
```

So:

```text
count++
   ↓
READ
   ↓
MODIFY
   ↓
WRITE
```

Because multiple threads can interleave these steps, updates can be lost.

---

## 9. Lost Update

A **lost update** happens when one thread's modification is overwritten by another thread's modification.

Suppose:

```text
count = 100
```

Two threads execute `count++`.

Possible execution:

```text
Thread 1                 Thread 2

READ 100
                         READ 100

ADD 1
                         ADD 1

WRITE 101
                         WRITE 101
```

Expected:

```text
102
```

Actual:

```text
101
```

One increment was lost.

This is a common manifestation of a race condition.

---

## 10. Practical Race Condition Example

```java
public class RaceExampleCounter {

    private static int count = 0;

    public static void main(String[] args)
            throws InterruptedException {

        Runnable task = () -> {

            for (int i = 0; i < 1_000_000; i++) {
                count++;
            }
        };

        Thread thread1 =
                new Thread(task, "thread-1");

        Thread thread2 =
                new Thread(task, "thread-2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Expected Counter: 2000000");
        System.out.println("Actual Counter: " + count);
    }
}
```

---

## 11. Expected Result

Each thread performs:

```text
1,000,000 increments
```

Therefore:

```text
Thread 1 → 1,000,000
Thread 2 → 1,000,000

Expected = 2,000,000
```

---

## 12. Actual Result

Because both threads modify the same shared variable without synchronization, updates can be lost.

Example:

```text
Expected Counter: 2000000
Actual Counter: 1068345
```

The exact result is not guaranteed.

Another execution could produce a different number.

It could even produce:

```text
Actual Counter: 2000000
```

That does **not** prove the program is thread-safe.

---

## 13. Why Can We Sometimes Get 2000000?

Race conditions are **timing-dependent**.

One execution might look like:

```text
Thread 1                 Thread 2

READ 100
ADD 1
WRITE 101

                         READ 101
                         ADD 1
                         WRITE 102
```

No update is lost in this particular interleaving.

Another execution:

```text
Thread 1                 Thread 2

READ 100
                         READ 100

ADD 1                    ADD 1

WRITE 101
                         WRITE 101
```

Now an update is lost.

Therefore:

> A race condition does not have to produce an incorrect result on every execution.

This is one reason race-condition bugs can be difficult to reproduce.

---

## 14. Thread Scheduling

The exact execution order of threads is not something application code should normally assume.

For example:

```text
T1 → T1 → T1 → T2 → T1 → T2
```

or:

```text
T2 → T1 → T2 → T2 → T1
```

or:

```text
T1 → T2 → T1 → T2 → T1
```

Different interleavings can produce different results when shared mutable state is accessed without proper coordination.

---

## 15. Why `join()` Does NOT Fix the Race Condition

The example uses:

```java
thread1.join();
thread2.join();
```

`join()` means:

> The thread that calls `join()` waits until the target thread finishes.

It ensures that the main thread does not print the final result before both worker threads finish.

But it does **not** make this safe:

```java
count++;
```

So:

```text
join()
  ↓
Makes the calling thread wait
```

while:

```text
synchronization
  ↓
Controls concurrent access to shared data
```

These solve different problems.

---

## 16. Race Condition vs Thread Safety

### Race Condition

A problem caused by unsafe concurrent access to shared state.

### Thread Safety

A property of code where concurrent execution does not produce incorrect behavior.

```text
Race Condition
      ↓
Problem
      ↓
Need safe coordination
      ↓
Thread-safe solution
```

Later we will learn mechanisms such as:

```text
synchronized
locks
atomic classes
concurrent collections
```

---

## 17. Backend Example — Inventory

Imagine an e-commerce application:

```text
Product ID: 101
Stock: 1
```

Two HTTP requests arrive nearly simultaneously:

```text
Request A → Thread A
Request B → Thread B
```

Both execute:

```java
if (stock > 0) {
    stock--;
    createOrder();
}
```

Possible execution:

```text
Thread A                    Thread B

stock > 0 ✓
                            stock > 0 ✓

stock--
                            stock--

createOrder()
                            createOrder()
```

Now the system may process two purchases for one available item.

Possible consequences:

```text
❌ Overselling
❌ Duplicate processing
❌ Incorrect inventory
❌ Inconsistent application state
```

---

## 18. Important Distinction

Do not say:

> "A race condition happens whenever two threads access the same variable."

That is too broad.

Multiple threads can safely read immutable data concurrently.

The bigger concern is:

```text
Shared
   +
Mutable
   +
Concurrent access/modification
   +
No appropriate coordination
   ↓
Potential race condition
```

---

## 19. Shared Immutable Data

Suppose:

```java
String productName = "Laptop";
```

and nobody modifies it.

Multiple threads can read the same value:

```text
Thread 1 ──→ "Laptop"
Thread 2 ──→ "Laptop"
Thread 3 ──→ "Laptop"
```

Concurrent reads of immutable data are generally not the same kind of problem as concurrent modification of shared mutable state.

The important concern is **modification**.

---

## 20. Common Examples of Potentially Unsafe Operations

Be careful with operations such as:

```java
counter++;
```

```java
counter--;
```

```java
balance = balance - amount;
```

```java
stock = stock - quantity;
```

```java
total = total + price;
```

They often follow:

```text
READ
 ↓
MODIFY
 ↓
WRITE
```

If multiple threads perform these operations on shared mutable state, race conditions may occur.

---

## 21. How to Identify a Potential Race Condition

When reviewing multithreaded code, ask:

### 1. Is the data shared?

Can multiple threads access the same variable/object?

### 2. Is the data mutable?

Can its value/state change?

### 3. Can multiple threads modify it concurrently?

If yes, there is potential for a race condition.

### 4. Is there appropriate coordination?

Examples include:

```text
synchronization
atomic operations
locks
thread-safe collections
database concurrency controls
```

---

## 22. Race Condition Mental Model

```text
                 Shared Mutable State
                         │
                         ▼
                 Multiple Threads
                         │
                         ▼
                Concurrent Access
                         │
                         ▼
                 Operation Interleaving
                         │
                         ▼
                  Race Condition
                         │
                         ▼
                   Lost Updates
                         │
                         ▼
                  Incorrect Result
```

---

## 23. Key Terms

| Term | Meaning |
|---|---|
| **Shared data** | Data accessible by multiple threads |
| **Mutable data** | Data whose value/state can change |
| **Shared mutable state** | Changing data accessed by multiple threads |
| **Concurrent access** | Multiple threads access data during overlapping execution |
| **Interleaving** | Operations from different threads execute in mixed order |
| **Race condition** | Result depends on timing/interleaving of concurrent execution |
| **Lost update** | One thread's update is overwritten by another |
| **Thread safety** | Code behaves correctly under concurrent execution |

---

## 24. Interview Answer

### What is a race condition?

> A race condition occurs when multiple threads concurrently access shared mutable data, and the result depends on the timing or interleaving of their execution. This can lead to incorrect or inconsistent results.

Example:

> If two threads execute `counter++` concurrently, both may read the same value before either writes the updated value, causing a lost update.

---

## 25. Key Takeaways

```text
1. Multiple threads can execute concurrently.

2. Threads can share access to the same object or variable.

3. Shared data is not automatically unsafe.

4. Shared MUTABLE data is where concurrency problems become important.

5. count++ is conceptually READ → MODIFY → WRITE.

6. Multiple threads can interleave those operations.

7. This can cause lost updates.

8. A race condition is timing/interleaving dependent.

9. Getting the expected result once does NOT prove thread safety.

10. join() waits for a thread to finish.
    It does NOT protect shared mutable data.

11. Race conditions are problems that synchronization
    and other concurrency mechanisms are designed to control.
```

---

## 26. Current Learning Position

We have now established the **problem**:

```text
Shared Mutable State
        ↓
Concurrent Thread Access
        ↓
Race Condition
        ↓
Incorrect Result
```

### Next Topic

The natural next topic is:

# `synchronized`

We will learn:

1. What `synchronized` actually does
2. `synchronized` method
3. `synchronized` block
4. Monitor / intrinsic lock
5. Critical section
6. How `synchronized` prevents the race condition
7. Connection between `synchronized` and the `BLOCKED` state

The goal is not to memorize the keyword:

```java
synchronized
```

but to understand **what happens to the threads and the shared data when synchronization is applied**.
