# Java Multithreading — Thread Constructors and Naming Threads

## 1. Overview

When creating threads in Java, we can use different `Thread` constructors.

We can also give meaningful names to threads.

Thread naming is especially useful when debugging multithreaded applications because logs can show which thread is executing a particular piece of code.

---

## 2. Thread Constructors

### Constructor 1 — Empty Thread

```java
Thread thread = new Thread();
```

Creates a `Thread` object without assigning a `Runnable` task.

It does not start the thread.

---

### Constructor 2 — Thread with Runnable

```java
RunnableTask task = new RunnableTask();

Thread thread = new Thread(task);
```

The `Runnable` represents the task, while the `Thread` represents the execution mechanism.

```text
RunnableTask
     ↓
    TASK
     ↓
Thread
     ↓
Execution mechanism
```

The thread starts only when:

```java
thread.start();
```

is called.

---

### Constructor 3 — Runnable + Thread Name

A thread can be created with both a task and a name:

```java
RunnableTask task = new RunnableTask();

Thread thread = new Thread(task, "Email-Thread");
```

Here:

```text
First argument  → Runnable task
Second argument → Thread name
```

So:

```text
Thread
 ├── Task → RunnableTask
 └── Name → Email-Thread
```

---

## 3. Thread Naming

If we don't explicitly give a thread a name, Java assigns a default name such as:

```text
Thread-0
Thread-1
Thread-2
```

We can give threads meaningful names:

```java
Thread emailThread =
        new Thread(emailNotificationTask, "Email-Thread");
```

Then:

```java
System.out.println(emailThread.getName());
```

prints:

```text
Email-Thread
```

---

## 4. Why Name Threads?

Thread names are useful when debugging and analyzing application logs.

For example:

```text
main
Email-Thread
Payment-Thread
Order-Thread
Report-Thread
```

If a log message is associated with `Payment-Thread`, it becomes easier to understand which thread produced it.

Meaningful names are particularly useful in backend applications where many threads may execute concurrently.

---

## 5. Setting a Thread Name

There are two common ways.

### Method 1 — During Construction

```java
Thread thread =
        new Thread(task, "Email-Thread");
```

### Method 2 — After Construction

```java
Thread thread = new Thread(task);

thread.setName("Email-Thread");
```

Then:

```java
System.out.println(thread.getName());
```

prints:

```text
Email-Thread
```

---

## 6. Getting the Thread Name

Use:

```java
thread.getName();
```

Example:

```java
Thread thread =
        new Thread(task, "Email-Thread");

System.out.println(thread.getName());
```

Output:

```text
Email-Thread
```

---

## 7. Getting the Currently Executing Thread

Java provides:

```java
Thread.currentThread()
```

This returns the thread that is currently executing the code.

To get its name:

```java
System.out.println(
        Thread.currentThread().getName()
);
```

If this code is inside `main()`:

```text
main
```

If it is executing inside our email thread:

```text
Email-Thread
```

---

## 8. `getName()` vs `currentThread().getName()`

### `thread.getName()`

```java
thread.getName();
```

Means:

> Get the name of this particular Thread object.

### `Thread.currentThread().getName()`

```java
Thread.currentThread().getName();
```

Means:

> Get the name of the thread that is currently executing this code.

---

## 9. E-commerce Example

### EmailNotificationTask.java

```java
package thread.creation.runnable.ecommerce;

public class EmailNotificationTask implements Runnable {

    private Order order;

    public EmailNotificationTask(Order order) {
        this.order = order;
    }

    @Override
    public void run() {

        System.out.println(
                "Running on: " +
                Thread.currentThread().getName()
        );

        System.out.println(
                "Email Notification sent " +
                order.getCustomerName()
        );
    }
}
```

### OrderApplication.java

```java
package thread.creation.runnable.ecommerce;

public class OrderApplication {

    public static void main(String[] args) {

        System.out.println(
                "Running on: " +
                Thread.currentThread().getName()
        );

        System.out.println("Processing order...");

        Order order = new Order(1, "Nagarjun");

        System.out.println("Order saved successfully.");

        EmailNotificationTask emailNotificationTask =
                new EmailNotificationTask(order);

        // First argument  → Runnable task
        // Second argument → Thread name
        Thread emailThread =
                new Thread(
                        emailNotificationTask,
                        "Email-Thread"
                );

        emailThread.start();

        System.out.println("Order processing completed!");
    }
}
```

---

## 10. Possible Output

One possible output:

```text
Running on: main
Processing order...
Order saved successfully.
Order processing completed!
Running on: Email-Thread
Email Notification sent Nagarjun
```

Another possible output:

```text
Running on: main
Processing order...
Order saved successfully.
Running on: Email-Thread
Email Notification sent Nagarjun
Order processing completed!
```

The ordering can vary because the main thread and email thread execute independently after `start()`.

---

## 11. Complete Execution Flow

```text
                     Main Thread
                         |
                         ↓
                  main() executes
                         |
                         ↓
                   Create Order
                         |
                         ↓
              Create Runnable Task
                         |
                         ↓
             new Thread(task, name)
                         |
                         ↓
                  Thread Object
                         |
                       start()
                         |
              ┌──────────┴──────────┐
              ↓                     ↓
         Main Thread           Email-Thread
              |                     |
              ↓                     ↓
   Order processing          run() executes
      continues                    |
                                    ↓
                          Send Email Notification
```

---

## 12. Important Thread Methods

| Method | Purpose |
|---|---|
| `getName()` | Gets the name of a Thread |
| `setName()` | Changes the name of a Thread |
| `currentThread()` | Gets the currently executing Thread |
| `start()` | Starts the Thread |

Examples:

```java
thread.getName();

thread.setName("Email-Thread");

Thread.currentThread();

thread.start();
```

---

## 13. Important Concepts

### `new Thread()`

Creates a Thread object. It does not automatically start execution.

### `new Thread(task)`

Creates a Thread and associates a Runnable task with it.

### `new Thread(task, "Email-Thread")`

Creates a Thread, associates a Runnable task, and gives the Thread a name.

### `start()`

Starts the Thread.

### `run()`

Contains the work that the Thread executes.

### `getName()`

Gets the Thread's name.

### `setName()`

Changes the Thread's name.

### `Thread.currentThread()`

Gets the Thread that is currently executing the code.

---

## 14. Quick Revision

```java
RunnableTask task = new RunnableTask();

Thread thread =
        new Thread(task, "Email-Thread");

thread.start();
```

Think:

```text
RunnableTask
     ↓
WHAT work?

Thread
     ↓
WHO executes it?

"Email-Thread"
     ↓
WHAT is the thread called?

start()
     ↓
START execution
```

Inside the task:

```java
Thread.currentThread().getName();
```

means:

> Which thread is executing this code right now?

---

## 15. Interview Questions

### Q1. How can you name a thread?

```java
Thread thread = new Thread(task, "Email-Thread");
```

or:

```java
thread.setName("Email-Thread");
```

### Q2. How do you get a thread's name?

```java
thread.getName();
```

### Q3. How do you get the currently executing thread?

```java
Thread.currentThread();
```

To get its name:

```java
Thread.currentThread().getName();
```

### Q4. Why are thread names useful?

Thread names make logs and debugging easier because they help identify which thread is executing a particular piece of code.

### Q5. Does creating a Thread object start the thread?

No.

```java
Thread thread = new Thread(task);
```

only creates the Thread object.

The thread starts when:

```java
thread.start();
```

is called.

---

## Golden Rule

> **Create the task → create the Thread → optionally give it a name → call `start()` → the Thread executes `run()`.**
