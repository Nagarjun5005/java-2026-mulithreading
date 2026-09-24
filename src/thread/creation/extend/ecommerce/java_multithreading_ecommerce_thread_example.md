# Java Multithreading — Thread Creation Using `Thread`
## Complete E-Commerce Example

> **Interview focus:** Understand how a Java thread is created by extending `Thread`, what happens when `start()` is called, how the JVM/OS/CPU fit into the picture, and why `start()` and `run()` are different.

## 1. Real-World Scenario

Imagine an **e-commerce application**. A customer places an order and the application needs to:
1. Process/save the order.
2. Send an order confirmation email.
3. Continue with other application work.

For learning, we will execute the email notification in a separate thread.

> In a real production application, you would generally use a thread pool, `ExecutorService`, asynchronous processing, or a messaging system rather than manually creating a new thread for every request. This example is specifically for understanding Java thread creation.

## 2. Example Project

```text
ECommerceApplication
        |
        +-- Order
        |
        +-- EmailNotificationThread
```

## 3. `Order.java`

```java
public class Order {

    private int orderId;
    private String customerName;

    public Order(int orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }
}
```

This class represents an order.

```java
Order order = new Order(101, "Nagarjun");
```

Conceptually, the object is created in the heap:

```text
Heap
 |
 +-- Order object
       +-- orderId = 101
       +-- customerName = "Nagarjun"
```

## 4. `EmailNotificationThread.java`

```java
public class EmailNotificationThread extends Thread {

    private Order order;

    public EmailNotificationThread(Order order) {
        this.order = order;
    }

    @Override
    public void run() {

        System.out.println(
            "Sending email for order: " + order.getOrderId()
        );

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(
            "Email sent to customer: " + order.getCustomerName()
        );
    }
}
```

`EmailNotificationThread` extends `Thread`.

The overridden `run()` method defines the task that the thread should execute.

## 5. `ECommerceApplication.java`

```java
public class ECommerceApplication {

    public static void main(String[] args) {

        System.out.println("Processing order...");

        Order order = new Order(101, "Nagarjun");

        System.out.println("Order saved successfully.");

        EmailNotificationThread emailThread =
                new EmailNotificationThread(order);

        emailThread.start();

        System.out.println("Order processing completed.");
    }
}
```

## 6. Compilation

```text
ECommerceApplication.java
Order.java
EmailNotificationThread.java
        |
       javac
        |
        v
.class bytecode files
```

The `.class` files contain Java bytecode.

> The CPU does not directly execute Java bytecode. The JVM executes the bytecode, using interpretation and JIT compilation as appropriate, ultimately resulting in native instructions executed by the CPU.

## 7. Running the Application

When we execute:

```bash
java ECommerceApplication
```

the operating system manages the running Java application as a process, and the JVM runs within that process.

```text
Operating System
        |
        v
Java Process
        |
        v
       JVM
```

The JVM loads the required classes and starts the application's main thread.

## 8. Main Thread

The JVM creates the initial **main thread**.

```text
Java Process
     |
    JVM
     |
     +-- Main Thread
```

The main thread executes:

```java
public static void main(String[] args)
```

## 9. Creating the Order

```java
Order order = new Order(101, "Nagarjun");
```

The main thread creates an `Order` object in the heap.

```text
Main Thread
     |
     v
new Order(...)
     |
     v
Heap
     |
     +-- Order object
```

## 10. Creating the Thread Object

```java
EmailNotificationThread emailThread =
        new EmailNotificationThread(order);
```

This creates a **Thread object**.

> **Important:** A Thread object has been created, but a new thread of execution has **not** started yet.

```text
Main Thread
     |
     +-- creates EmailNotificationThread object
                         |
                         v
                       Heap
```

## 11. Calling `start()`

```java
emailThread.start();
```

`start()` requests the JVM/runtime to start a **new thread of execution**.

Before:

```text
Java Process
     |
     +-- Main Thread
```

After:

```text
Java Process
     |
     +-- Main Thread
     |
     +-- Email Notification Thread
```

Now there are two execution paths.

## 12. Main Thread vs Email Thread

```text
                 Java Process
                      |
             +--------+--------+
             |                 |
        Main Thread       Email Thread
             |                 |
          main()              run()
             |                 |
       Continue work       Send email
```

The main thread continues executing `main()`, while the newly started thread executes `run()`.

The exact ordering between them is not guaranteed because runnable threads are scheduled independently.

## 13. What Happens to `run()`?

The new thread eventually executes:

```java
@Override
public void run() {
    // send email
}
```

Flow:

```text
emailThread.start()
        |
        v
New thread starts
        |
        v
run()
        |
        v
Send email
```

## 14. Possible Output

You might see:

```text
Processing order...
Order saved successfully.
Order processing completed.
Sending email for order: 101
Email sent to customer: Nagarjun
```

Or:

```text
Processing order...
Order saved successfully.
Sending email for order: 101
Order processing completed.
Email sent to customer: Nagarjun
```

The exact ordering between the main thread and email thread is not guaranteed.

## 15. Thread Stacks

Each thread has its own stack.

```text
Java Process
|
+-- Main Thread
|      |
|      +-- Main Thread Stack
|             +-- main() frame
|
+-- Email Thread
       |
       +-- Email Thread Stack
              +-- run() frame
```

## 16. How Does the Email Thread Access the Order?

The `Order` object is passed to the thread:

```java
new EmailNotificationThread(order);
```

The object is in shared heap memory, so the email thread can access it.

```text
                    Heap
                      |
                      v
                Order object
                orderId = 101
                      ^
                      |
                Email Thread
                  accesses
                 the object
```

## 17. Operating System and CPU

The OS schedules runnable threads onto available CPU cores.

```text
Java Process
     |
     +-- Main Thread
     |
     +-- Email Thread
             |
             v
       OS Scheduler
             |
             v
            CPU
```

On a multi-core machine, different threads may execute simultaneously on different CPU cores.

## 18. `start()` vs `run()`

### `start()`

```java
emailThread.start();
```

```text
Main Thread
     |
   start()
     |
     v
New Thread
     |
    run()
     |
     v
Send Email
```

A new execution path is created.

### Direct `run()`

```java
emailThread.run();
```

```text
Main Thread
     |
    run()
     |
     v
Send Email
```

No new thread is created by this call. `run()` is simply called like a normal method.

### Golden Rule

> **`start()` → starts a new execution path.**  
> **`run()` → defines the task.**  
> **Direct `run()` → no new thread.**

## 19. Complete End-to-End Picture

```text
E-Commerce Source Code
        |
        | javac
        v
Java Bytecode
        |
        | java ECommerceApplication
        v
Operating System
        |
        v
Java Process
        |
       JVM
        |
        v
Main Thread
        |
        v
      main()
        |
   +----+--------------------+
   |                         |
new Order()       new EmailNotificationThread()
   |                         |
   v                         v
 Heap                  Thread object created
                             |
                             v
                    emailThread.start()
                             |
                             v
                      New Thread Starts
                             |
                             v
                            run()
                             |
                             v
                       Send Email
                             |
                             v
                       OS Scheduler
                             |
                             v
                            CPU
```

## 20. Why Use a Separate Thread?

Without a separate thread:

```text
Main Thread
    |
    v
Process Order
    |
    v
Send Email
    |
    v
Wait for Email Operation
    |
    v
Continue
```

With another thread:

```text
                 Main Thread
                     |
                Process Order
                     |
                  start()
                     |
              +------+------+
              |             |
              v             v
          Continue      Email Thread
          other work        |
                            v
                        Send Email
```

This is the basic motivation for multiple threads: different pieces of work can have different execution paths and can make progress concurrently.

# 21. Interview Questions

### Q1. Does `new EmailNotificationThread()` start a thread?

**Answer:** No. It creates a Thread object. The thread of execution starts when `start()` is called.

### Q2. What happens when `start()` is called?

**Answer:** `start()` requests the JVM/runtime to start a new thread of execution. The new thread becomes eligible for scheduling and, when it runs, invokes its `run()` method.

### Q3. Which thread executes `main()`?

**Answer:** The JVM's main thread.

### Q4. Which thread executes `run()` after `start()`?

**Answer:** The newly started thread.

### Q5. Does calling `run()` create a new thread?

**Answer:** No. It is a normal method call executed by the current thread.

### Q6. Why do we override `run()`?

**Answer:** To define the task/work that the thread should execute.

### Q7. What is the difference between a Thread object and a thread of execution?

**Answer:** A Thread object is a Java object representing/managing a thread. Creating the object with `new` does not start a new execution path. Calling `start()` initiates the new thread of execution.

### Q8. Can the main thread and email thread access the same Order object?

**Answer:** Yes. Objects are generally stored in shared heap memory, so multiple threads can access the same object. If shared mutable state is modified concurrently, synchronization or another concurrency mechanism may be required.

# 22. Quick Revision

```text
new EmailNotificationThread()
        |
        v
Thread object created
        |
        v
     start()
        |
        v
New thread starts
        |
        v
     run()
        |
        v
Send email
```

### Interview Mental Model

When you see:

```java
EmailNotificationThread emailThread =
        new EmailNotificationThread(order);

emailThread.start();
```

Think:

```text
new
 ↓
Thread object created
 ↓
start()
 ↓
New execution path
 ↓
run()
 ↓
Task executes
```

### Remember

- **`new` → creates the Thread object**
- **`start()` → starts a new execution path**
- **`run()` → defines the task**
- **Direct `run()` → no new thread**
- **Main thread → executes `main()`**
- **Worker thread → executes `run()`**
- **Heap → shared between threads**
- **Stack → each thread has its own stack**

# 23. Production Caveat

The example intentionally demonstrates manual thread creation.

In a production e-commerce backend, you would generally avoid creating a new thread for every email:

```java
new EmailNotificationThread(order).start();
```

Instead, applications commonly use:

```text
ExecutorService / Thread Pool
        OR
Spring @Async
        OR
Message Queue
        |
        +-- RabbitMQ
        +-- Kafka
        etc.
```

Those mechanisms will be covered separately when studying thread pools and higher-level concurrency APIs.

The goal of this example is to understand the fundamental Java flow:

```text
Process
   ↓
JVM
   ↓
Main Thread
   ↓
new Thread object
   ↓
start()
   ↓
New Thread
   ↓
run()
   ↓
Task Execution
```
