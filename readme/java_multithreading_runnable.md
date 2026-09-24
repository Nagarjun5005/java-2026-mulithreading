# Java Multithreading — Creating Threads Using Runnable

## 1. Overview

Java provides another way to create a thread by implementing the `Runnable` interface.

The key idea is to **separate the task from the thread that executes the task**.

```text
Runnable → What work should be done?

Thread   → Which thread executes that work?
```

A class implementing `Runnable` represents the **task/work**. It does not itself create a new thread.

---

## 2. Creating a Runnable Task

Example:

```java
package thread.creation.runnable.theory;

public class RunnableTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Running the thread using Runnable");
    }
}
```

### Understanding the code

```java
public class RunnableTask implements Runnable
```

The class implements the `Runnable` interface.

`Runnable` requires us to implement:

```java
void run();
```

The `run()` method contains the work that we want a thread to perform.

### Important

Creating a class that implements `Runnable` **does not create a thread**.

For example:

```java
RunnableTask runnableTask = new RunnableTask();
```

This only creates a normal Java object representing the task.

```text
RunnableTask object
        ↓
Represents the task
        ↓
No new thread yet
```

---

## 3. Creating a Thread Using the Runnable Task

After creating the task, we create a `Thread` and pass the `Runnable` object to it.

```java
RunnableTask runnableTask = new RunnableTask();

Thread th = new Thread(runnableTask);
```

The relationship is:

```text
RunnableTask
     |
     | task
     ↓
   Thread
```

The `Thread` now knows which `Runnable` task it should execute.

However, the thread has not started yet.

---

## 4. Starting the Thread

We start the thread using:

```java
th.start();
```

The complete flow is:

```text
RunnableTask runnableTask = new RunnableTask();
        ↓
Create the task
        ↓
Thread th = new Thread(runnableTask);
        ↓
Associate task with Thread
        ↓
th.start();
        ↓
New thread starts
        ↓
run() executes
        ↓
Task is performed
```

---

# 5. Complete Example

## RunnableTask.java

```java
package thread.creation.runnable.theory;

public class RunnableTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Running the thread using Runnable");
    }
}
```

## Main.java

```java
package thread.creation.runnable.theory;

public class Main {

    public static void main(String[] args) {

        // Create the Runnable object.
        // This represents the task.
        // No new thread is created here.
        RunnableTask runnableTask = new RunnableTask();

        // Create a Thread and pass the Runnable task to it.
        // The Thread now knows which task it should execute.
        Thread th = new Thread(runnableTask);

        // Start the new thread.
        // The new thread will execute RunnableTask.run().
        th.start();
    }
}
```

---

# 6. Step-by-Step Execution

## Step 1 — Create the Runnable object

```java
RunnableTask runnableTask = new RunnableTask();
```

At this point:

```text
Main Thread
    |
    ↓
Creates RunnableTask object
    |
    ↓
RunnableTask represents the task
```

No new thread has been created.

---

## Step 2 — Create the Thread

```java
Thread th = new Thread(runnableTask);
```

Now a `Thread` object is created.

The Runnable task is passed to the Thread.

```text
RunnableTask
     |
     | contains
     ↓
    run()
     |
     | given to
     ↓
  Thread
```

The thread still has not started executing.

---

## Step 3 — Start the Thread

```java
th.start();
```

Now the JVM starts the new thread.

The new thread executes the `run()` method:

```java
public void run() {
    System.out.println("Running the thread using Runnable");
}
```

Conceptually:

```text
Main Thread
     |
     | start()
     ↓
New Thread
     |
     ↓
RunnableTask.run()
     |
     ↓
Task executes
```

---

# 7. Runnable Is the Task, Not the Thread

This is the most important concept in this section.

Consider:

```java
RunnableTask task = new RunnableTask();
```

This means:

> Create the task.

It does not mean:

> Create a new thread.

Then:

```java
Thread thread = new Thread(task);
```

means:

> Create a Thread and associate the task with it.

Finally:

```java
thread.start();
```

means:

> Start the new thread and execute the task.

Therefore:

```text
Runnable → Task / Work

Thread → Execution mechanism

start() → Starts the thread
```

---

# 8. `start()` vs `run()`

This is a very important interview concept.

## Using `start()`

```java
Thread thread = new Thread(task);

thread.start();
```

`start()` starts a new thread.

The new thread will execute the `run()` method.

```text
Main Thread
     |
     | start()
     ↓
New Thread
     |
     ↓
run()
```

---

## Calling `run()` Directly

Consider:

```java
RunnableTask task = new RunnableTask();

task.run();
```

This does **not** create a new thread.

It is simply a normal method call.

```text
Main Thread
     |
     ↓
task.run()
     |
     ↓
Task executes on Main Thread
```

Therefore:

```text
start() → New thread

run()   → Normal method call
```

---

# 9. Why Separate Runnable and Thread?

When extending `Thread`, the class represents both the task and the thread:

```java
class EmailThread extends Thread {

    @Override
    public void run() {
        System.out.println("Sending email");
    }
}
```

Conceptually:

```text
EmailThread
    |
    ├── Task
    |
    └── Thread
```

With `Runnable`:

```java
class EmailTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Sending email");
    }
}
```

Now the responsibilities are separated:

```text
EmailTask
    ↓
Task

Thread
    ↓
Execution mechanism
```

The separation can be summarized as:

> `Runnable` defines **what should be done**, while `Thread` defines **the execution mechanism**.

---

# 10. Java Multiple Inheritance Reason

Another important reason for using `Runnable` is Java's class inheritance rule.

Java does not allow a class to extend multiple classes.

This is not allowed:

```java
class EmailService extends NotificationService, Thread {
}
```

A class can extend only one class.

However, a class can extend one class and implement interfaces:

```java
class EmailService extends NotificationService implements Runnable {

    @Override
    public void run() {
        System.out.println("Sending email");
    }
}
```

This is possible because `Runnable` is an interface.

```text
Class
  |
  ├── extends ONE class
  |
  └── implements MULTIPLE interfaces
```

Therefore, if a class already needs to extend another class, implementing `Runnable` allows it to define a task that can be executed by a Thread.

---

# 11. Complete Mental Model

```text
                 RunnableTask
                 ┌─────────────┐
                 │             │
                 │   run()     │
                 │             │
                 │    TASK     │
                 └──────┬──────┘
                        │
                        │ passed to
                        ↓
                 ┌─────────────┐
                 │   Thread    │
                 │             │
                 │  Execution  │
                 │  Mechanism  │
                 └──────┬──────┘
                        │
                      start()
                        │
                        ↓
                   NEW THREAD
                        │
                        ↓
                      run()
                        │
                        ↓
                  Task executes
```

---

# 12. Practical Example — E-commerce

Imagine an e-commerce application.

When a customer places an order, the application may need to send an order confirmation email.

The task can be represented using `Runnable`:

```java
public class EmailTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Sending order confirmation email");
    }
}
```

Then:

```java
EmailTask task = new EmailTask();

Thread emailThread = new Thread(task);

emailThread.start();
```

The important separation is:

```text
EmailTask
    ↓
What should happen?
    ↓
Send confirmation email


Thread
    ↓
Execute the task
```

This example is only for understanding thread creation. In production applications, tasks are commonly managed using higher-level concurrency mechanisms such as thread pools.

---

# 13. Runnable as a Functional Interface

`Runnable` has one abstract method:

```java
void run();
```

Because of this, it can also be used with a lambda expression.

Example:

```java
Runnable task = () -> {
    System.out.println("Running the task");
};

Thread thread = new Thread(task);

thread.start();
```

This represents the same basic concept:

```text
Runnable
    ↓
Task
    ↓
Thread
    ↓
start()
    ↓
Execution
```

---

# 14. Key Differences

| Concept | Purpose |
|---|---|
| `Runnable` | Represents the task/work |
| `run()` | Contains the task logic |
| `Thread` | Represents the execution mechanism |
| `new Thread(task)` | Associates a task with a thread |
| `start()` | Starts a new thread |
| `run()` called directly | Normal method call; no new thread |

---

# 15. Interview Questions

## Q1. What is Runnable?

`Runnable` is an interface used to represent a task that can be executed by a thread.

---

## Q2. Does implementing Runnable create a thread?

No.

Implementing `Runnable` only defines the task.

A `Thread` must be created and started separately.

---

## Q3. How do you create a thread using Runnable?

```java
Runnable task = new RunnableTask();

Thread thread = new Thread(task);

thread.start();
```

---

## Q4. What is the purpose of the `run()` method?

The `run()` method contains the work that the thread should execute.

---

## Q5. What is the difference between `start()` and `run()`?

`start()` starts a new thread, while directly calling `run()` is simply a normal method call on the current thread.

---

## Q6. Why can Runnable be useful when a class already extends another class?

Because Java does not support multiple class inheritance.

A class can extend one class and still implement `Runnable`.

Example:

```java
class EmailService extends NotificationService implements Runnable {
}
```

---

## Q7. Is Runnable a thread?

No.

`Runnable` represents a task.

`Thread` is the mechanism used to execute that task.

---

# 16. Quick Revision

Remember these three lines:

```java
RunnableTask task = new RunnableTask();

Thread thread = new Thread(task);

thread.start();
```

Meaning:

```text
RunnableTask
    ↓
WHAT should be done?

Thread
    ↓
Executes the task

start()
    ↓
Starts the new thread
```

## Golden Rule

> **Runnable defines the task. Thread executes the task. `start()` starts the new thread.**
