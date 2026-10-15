# Java Multithreading: Synchronization

> Interview-oriented notes with explanations, examples, practice
> exercises, and key takeaways.

## Learning objectives

By the end of this topic, you should be able to:

-   Explain race conditions and critical sections.
-   Explain why shared mutable state needs coordination.
-   Use `synchronized` methods and synchronized blocks.
-   Distinguish instance locks from class-level locks.
-   Explain monitor ownership and what happens when a thread enters a
    synchronized region.
-   Understand how `join()` helps the main thread wait for worker
    threads.
-   Recognize common synchronization mistakes and explain when
    alternatives such as `AtomicInteger` or `ReentrantLock` may be
    useful.

------------------------------------------------------------------------

## 1. Why synchronization is needed

When multiple threads access and modify the same mutable data at the
same time, the program can produce incorrect results.

Consider:

``` java
private int stock = 1;

public void purchase() {
    if (stock > 0) {
        stock--;
        System.out.println("Purchase successful");
    }
}
```

Two threads could both read `stock == 1` before either thread decrements
it. Both may then report a successful purchase, even though only one
product was available.

This is a **race condition**: the result depends on the timing or
interleaving of concurrent operations.

### Shared state

A value is shared when multiple threads can access the same underlying
state. Examples include:

-   An instance field shared by threads using the same object.
-   A static field shared across instances of a class.
-   A mutable collection accessed by multiple threads.
-   A shared buffer between producer and consumer threads.

### Critical section

A **critical section** is a section of code that accesses shared state
and must be coordinated to preserve correctness.

For inventory, the check and update must be treated as one coordinated
operation:

``` java
if (stock > 0) {
    stock--;
}
```

Protecting only `stock--` is not enough: the check and decrement belong
together.

------------------------------------------------------------------------

## 2. What does `synchronized` do?

Java's `synchronized` keyword uses an object's intrinsic lock, also
called its **monitor**, to coordinate threads.

Only one thread at a time can own a particular monitor. A thread
attempting to enter a synchronized region guarded by a monitor owned by
another thread must wait until that monitor becomes available.

A synchronized region also establishes memory-visibility guarantees:
releasing a monitor happens-before a subsequent acquisition of that same
monitor. This helps a thread observe changes made by another thread
under the same lock.

**Important:** `synchronized` protects an operation only when the
relevant concurrent accesses use the same lock consistently. It does not
automatically make every access to a field thread-safe.

------------------------------------------------------------------------

## 3. Synchronized instance methods

Syntax:

``` java
public synchronized void purchase() {
    // Critical section
}
```

For an instance method, Java acquires the monitor of the current object
(`this`) before entering the method and releases it when the method
exits, including when it exits by throwing an exception.

For example:

``` java
ProductInventory inventory = new ProductInventory(1);

Thread t1 = new Thread(inventory::purchase);
Thread t2 = new Thread(inventory::purchase);

t1.start();
t2.start();
```

Both threads call `purchase()` on the same object, so they compete for
the same monitor. One enters first; the other cannot enter that
synchronized method on that object until the lock is released.

### Key points

-   The lock belongs to the object, not to the method itself.
-   Two threads calling synchronized instance methods on the same object
    must coordinate through that object's monitor.
-   Two threads calling synchronized instance methods on different
    objects can run those methods concurrently because the objects have
    different monitors.
-   A synchronized method does not prevent unsynchronized code from
    accessing the same fields.

------------------------------------------------------------------------

## 4. Synchronized static methods

Syntax:

``` java
public static synchronized void incrementTotalOrders() {
    totalOrders++;
}
```

A static synchronized method acquires the monitor of the class object.
For `ProductInventory`, that monitor is `ProductInventory.class`.

### Instance lock versus class lock

  ----------------------------------------------------------------------------
  Feature                 Synchronized instance   Synchronized static method
                          method                  
  ----------------------- ----------------------- ----------------------------
  Monitor acquired        Current instance        Class object
                          (`this`)                (`ProductInventory.class`)

  Lock shared by          Calls using the same    Calls using the same class
                          object                  object

  Different instances     Can have different      Still use the same class
                          locks                   lock

  Common use              Protecting per-object   Protecting class-level
                          state                   shared state
  ----------------------------------------------------------------------------

An instance lock and a class lock are different locks. Holding one does
not automatically mean a thread holds the other.

### Example

``` java
public synchronized void purchase() {
    // Locks this inventory object
}

private static synchronized void incrementTotalOrders() {
    // Locks ProductInventory.class
}
```

If different inventory objects are used concurrently, their `purchase()`
methods can run at the same time. The static synchronized increment
method serializes increments across those objects.

------------------------------------------------------------------------

## 5. Synchronized blocks

A synchronized block lets you choose the monitor and restrict the scope
of locking.

Syntax:

``` java
synchronized (lock) {
    // Critical section guarded by lock
}
```

Example:

``` java
public void updateStock(int quantity) {
    synchronized (this) {
        stock += quantity;
    }
}
```

This block uses the current object as its lock. All code that must
coordinate access to the same stock should use the same locking
strategy.

A separate private lock is often useful when you do not want external
code to synchronize on the object:

``` java
private final Object stockLock = new Object();

public void updateStock(int quantity) {
    synchronized (stockLock) {
        stock += quantity;
    }
}
```

### Method versus block

  -----------------------------------------------------------------------
  Synchronized method                 Synchronized block
  ----------------------------------- -----------------------------------
  Locks for the whole method body     Locks only for the block

  Instance method uses `this`         You choose the lock object

  Static method uses the class object You choose the lock object

  Simple when the whole method must   Useful when only a specific region
  be protected                        needs protection
  -----------------------------------------------------------------------

Do not choose a different lock for each method that modifies the same
shared state unless the design explicitly coordinates those locks.

------------------------------------------------------------------------

## 6. Worked example: `ProductInventory`

This example protects the stock check and decrement using an instance
monitor. It also protects the shared static order counter using a
class-level monitor.

``` java
package synchronizedDemo.synchronized01.practice;

public class ProductInventory {

    private int stock;
    private static int totalOrders = 0;

    public ProductInventory(int stock) {
        this.stock = stock;
    }

    public synchronized void purchase() {
        if (stock > 0) {
            stock--;

            incrementTotalOrders();

            System.out.println(
                Thread.currentThread().getName()
                    + " purchased product. Remaining stock: " + stock
            );
        } else {
            System.out.println(
                Thread.currentThread().getName() + ": Product out of stock"
            );
        }
    }

    private static synchronized void incrementTotalOrders() {
        totalOrders++;
    }

    public synchronized int getStock() {
        return stock;
    }

    public static synchronized int getTotalOrders() {
        return totalOrders;
    }

    public static void main(String[] args) throws InterruptedException {
        ProductInventory inventory = new ProductInventory(1);

        Runnable task = inventory::purchase;

        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Final stock: " + inventory.getStock());
        System.out.println("Successful orders: " + getTotalOrders());
    }
}
```

### What should happen?

With initial stock `1` and both threads using the same inventory object:

-   One thread purchases the product.
-   The stock becomes `0`.
-   The other thread reports that the product is out of stock.
-   The final stock is `0`.
-   The total successful order count is `1`.

The thread that wins the race to acquire the lock is not guaranteed;
either thread may purchase the product.

### Why use `join()`?

`join()` makes the calling thread wait until the target thread
terminates. In this example, the main thread waits for both purchase
threads before printing the final values.

Without those joins, the main thread could print before the purchase
attempts have completed.

### Important design notes

-   `purchase()` locks one inventory instance.
-   `incrementTotalOrders()` locks the class object, so concurrent
    increments across different inventory instances are serialized.
-   The getters above use matching synchronization for the fields they
    read.
-   If a `setStock()` method is added, it must follow the same locking
    strategy as other stock operations.
-   In a real application, the global static order counter may be better
    represented by a separate service or order repository, depending on
    the design.

------------------------------------------------------------------------

## 7. Why `totalOrders++` can be unsafe

The expression:

``` java
totalOrders++;
```

is not an atomic operation. Conceptually, it involves reading a value,
adding one, and writing the result.

Suppose `totalOrders` is `0` and two threads increment it concurrently
without coordination:

1.  Thread A reads `0`.
2.  Thread B reads `0`.
3.  Thread A writes `1`.
4.  Thread B writes `1`.

The final result is `1`, even though two increments were attempted. This
is a **lost update**.

Ways to coordinate increments include:

### Option A: Synchronize the increment

``` java
private static synchronized void incrementTotalOrders() {
    totalOrders++;
}
```

This is suitable for learning monitor-based synchronization.

### Option B: Use `AtomicInteger`

``` java
import java.util.concurrent.atomic.AtomicInteger;

private static final AtomicInteger totalOrders = new AtomicInteger();

private static void incrementTotalOrders() {
    totalOrders.incrementAndGet();
}
```

`incrementAndGet()` performs an atomic increment. An atomic variable is
useful for an independent counter, but it does not automatically make a
larger multi-step operation involving several fields atomic.

------------------------------------------------------------------------

## 8. Common mistakes

### Mistake 1: Synchronizing on different objects

``` java
synchronized (new Object()) {
    // ...
}
```

Creating a new lock object for each call does not coordinate those
calls, because each thread can acquire a different monitor.

### Mistake 2: Assuming synchronized protects every access

If `purchase()` is synchronized but `setStock()` modifies `stock`
without using the same lock, the setter can race with the purchase
operation.

### Mistake 3: Protecting only part of a compound operation

The stock check and decrement must be coordinated together. Protecting
only the decrement leaves a gap in which two threads can both pass the
check.

### Mistake 4: Confusing instance and static synchronization

A synchronized instance method and a static synchronized method use
different monitors. One does not automatically protect the other.

### Mistake 5: Using `if` instead of `while` with `wait()`

When coordinating threads with `wait()`, recheck the condition in a
`while` loop after waking. A wake-up does not guarantee the condition is
now true.

### Mistake 6: Calling `wait()` or `notify()` without owning the monitor

Calling these methods without owning the relevant object's monitor
throws `IllegalMonitorStateException`.

### Mistake 7: Assuming synchronization guarantees execution order

Synchronization provides mutual exclusion and memory-visibility
guarantees for the same monitor. It does not guarantee that a particular
thread acquires the lock first.

------------------------------------------------------------------------

## 9. `wait()`, `notify()`, and `notifyAll()` --- introduction

These methods are used for thread coordination and are especially
important in producer--consumer problems.

-   `wait()` causes the current thread to wait and releases the monitor
    on which it waits.
-   `notify()` wakes one thread waiting on that object's monitor.
-   `notifyAll()` wakes all threads waiting on that object's monitor;
    they must compete to reacquire the monitor before proceeding.

They must be called while owning the monitor of the object on which they
are invoked.

Typical pattern:

``` java
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }

    // Perform the operation once the condition is satisfied.
    lock.notifyAll();
}
```

Use `while`, not `if`, to recheck the condition after waking. The exact
producer--consumer implementation is a separate practice exercise.

------------------------------------------------------------------------

## 10. When to consider other concurrency tools

`synchronized` is a fundamental tool, but Java provides other options:

-   **`volatile`:** Provides visibility and ordering guarantees for a
    variable, but does not make compound operations such as `count++`
    atomic.
-   **`AtomicInteger`:** Provides atomic operations on an integer
    counter.
-   **`ReentrantLock`:** Offers explicit lock/unlock operations and
    features such as timed or interruptible lock acquisition. Unlocking
    should generally be done in a `finally` block.
-   **Concurrent collections:** Provide thread-safe collection
    operations for common use cases.
-   **Blocking queues:** Often simplify producer--consumer designs by
    providing built-in thread-safe handoff and blocking behavior.

Choose the tool based on the required correctness guarantees and
coordination pattern.

------------------------------------------------------------------------

## 11. Practice exercises

Mark each exercise as complete when you can implement it and explain the
result.

-   [ ] **Exercise 1 --- Race condition:** Remove `synchronized` from
    `purchase()`. Explain why incorrect results are possible, even if
    they do not appear on every run.
-   [ ] **Exercise 2 --- Same object:** Run many threads against one
    inventory object. Confirm that successful purchases never exceed the
    initial stock.
-   [ ] **Exercise 3 --- Different objects:** Create two inventory
    instances and call `purchase()` concurrently. Explain why their
    instance locks do not block each other.
-   [ ] **Exercise 4 --- Shared static counter:** Make the counter
    increment safe across multiple inventory instances.
-   [ ] **Exercise 5 --- Synchronized block:** Rewrite the stock
    operation using a private lock object and use the same lock
    consistently.
-   [ ] **Exercise 6 --- Consistent access:** Ensure that all methods
    reading or writing stock follow a compatible synchronization
    strategy.
-   [ ] **Exercise 7 --- Producer--consumer:** Implement a one-slot
    buffer using `synchronized`, `wait()`, and `notifyAll()`.
-   [ ] **Exercise 8 --- Bounded buffer:** Extend the buffer to hold
    multiple items and coordinate multiple producers and consumers.

------------------------------------------------------------------------

## 12. Interview questions

Try answering these without looking at the notes.

1.  What is a race condition?
2.  What is a critical section?
3.  What does `synchronized` guarantee?
4.  What is an intrinsic lock or monitor?
5.  What object is locked by a synchronized instance method?
6.  What object is locked by a static synchronized method?
7.  Can two threads execute a synchronized instance method at the same
    time on different objects?
8.  Can a synchronized method call another synchronized method on the
    same object?
9.  Why is `count++` not thread-safe by itself?
10. Does `synchronized` guarantee which thread executes first?
11. What is the difference between a synchronized method and a
    synchronized block?
12. Why must all relevant accesses use a consistent locking strategy?
13. What is the difference between `wait()` and `sleep()`?
14. Why should `wait()` usually be called inside a `while` loop?
15. What is the difference between `notify()` and `notifyAll()`?
16. What can cause `IllegalMonitorStateException`?
17. What is the difference between `synchronized` and `volatile`?
18. When might `AtomicInteger` be preferable to synchronization?
19. What is a deadlock?
20. How does `join()` differ from `wait()`?

------------------------------------------------------------------------

## 13. Quick revision sheet

  -----------------------------------------------------------------------
  Term                                Remember
  ----------------------------------- -----------------------------------
  Race condition                      Result depends on concurrent
                                      timing/interleaving

  Critical section                    Code that needs coordinated access
                                      to shared state

  `synchronized` instance method      Acquires `this` monitor

  `static synchronized` method        Acquires the class object's monitor

  Synchronized block                  Acquires the monitor of the
                                      specified object

  Monitor                             Intrinsic lock associated with an
                                      object

  `wait()`                            Waits and releases the monitor
                                      being waited on

  `notify()`                          Wakes one waiter on that monitor

  `notifyAll()`                       Wakes all waiters on that monitor

  `join()`                            Waits for another thread to
                                      terminate

  `volatile`                          Visibility/order guarantees, not
                                      atomic compound updates

  `AtomicInteger`                     Atomic operations on an integer

  Deadlock                            Threads wait indefinitely for
                                      locks/resources held by one another
  -----------------------------------------------------------------------

## Progress tracker

-   [x] Basic synchronized instance method
-   [x] Instance monitor versus class monitor
-   [x] Race-condition concept
-   [x] `join()` and final result observation
-   [ ] Race condition demonstrated in code
-   [ ] Synchronized blocks practised
-   [ ] Consistent locking across all accesses
-   [ ] Producer--consumer implementation
-   [ ] Deadlock practice
-   [ ] `wait()`, `notify()`, and `notifyAll()` practice
-   [ ] Interview questions answered independently

**Next topic:** Producer--consumer using a one-slot shared buffer,
followed by a bounded buffer with multiple producers and consumers.
