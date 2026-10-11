# Java Multithreading — Thread Life Cycle

## 1. Overview

A Java thread goes through different states during its lifetime.

Java officially defines **6 thread states**:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

You can observe a thread's current state using:

```java
thread.getState()
```

---

## 2. Thread Life Cycle

A simple thread can follow:

```text
NEW
 ↓
RUNNABLE
 ↓
TERMINATED
```

A thread can also move between states:

```text
NEW
 ↓
RUNNABLE
 ↓
TIMED_WAITING
 ↓
RUNNABLE
 ↓
TERMINATED
```

Or:

```text
NEW
 ↓
RUNNABLE
 ↓
BLOCKED
 ↓
RUNNABLE
 ↓
TERMINATED
```

A thread does **not** have to visit every state.

---

# 3. Thread States

## 3.1 NEW

A thread is in `NEW` when the `Thread` object has been created but `start()` has not been called.

```java
Thread workerThread = new Thread(task, "worker-thread");

System.out.println(workerThread.getState());
```

Output:

```text
NEW
```

Also:

```java
workerThread.isAlive(); // false
```

---

## 3.2 RUNNABLE

After:

```java
workerThread.start();
```

the thread becomes eligible to execute and enters `RUNNABLE`.

```text
NEW
 ↓
start()
 ↓
RUNNABLE
```

### Important interview point

Java does **not** have a separate `RUNNING` state.

`RUNNABLE` represents a thread that is ready to run or currently running on the CPU. The scheduler determines when it actually gets CPU time.

---

## 3.3 BLOCKED

A thread enters `BLOCKED` when it is waiting to acquire a monitor lock, commonly when entering a `synchronized` section whose lock is already owned by another thread.

Conceptually:

```text
Thread A → owns lock
Thread B → wants same lock
Thread B → BLOCKED
```

Important:

> `BLOCKED` means waiting to acquire a monitor lock.

---

## 3.4 WAITING

A thread enters `WAITING` when it waits indefinitely for another thread or action.

A common example:

```java
workerThread.join();
```

without a timeout.

If the main thread calls this:

```text
MAIN THREAD
    ↓
workerThread.join()
    ↓
WAITING
```

The main thread waits until the worker finishes.

Other APIs can also produce `WAITING`, such as:

```java
Object.wait();
```

---

## 3.5 TIMED_WAITING

A thread enters `TIMED_WAITING` when it waits for a specified amount of time.

Examples:

```java
Thread.sleep(3000);
```

```java
workerThread.join(3000);
```

Conceptually:

```text
RUNNABLE
   ↓
sleep(3000)
   ↓
TIMED_WAITING
   ↓
timeout expires
   ↓
RUNNABLE
```

`sleep()` does **not** terminate the thread.

---

## 3.6 TERMINATED

A thread enters `TERMINATED` after its `run()` method completes.

```text
NEW
 ↓
RUNNABLE
 ↓
run() completes
 ↓
TERMINATED
```

A terminated thread cannot be restarted.

Calling `start()` again on the same thread throws:

```text
IllegalThreadStateException
```

To run the task again, create a new `Thread` object.

---

# 4. `getState()`

Use:

```java
thread.getState();
```

to obtain the thread's current Java state.

Possible states:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

### Scheduling caveat

Thread states can change very quickly.

Therefore:

```java
workerThread.start();
System.out.println(workerThread.getState());
```

does **not guarantee** that the output will always be `RUNNABLE`. The worker may already have progressed further before the main thread checks its state.

---

# 5. `isAlive()`

`isAlive()` answers:

> Has this thread started and not yet finished?

Typical relationship:

```text
NEW            → false
RUNNABLE       → true
BLOCKED        → true
WAITING        → true
TIMED_WAITING  → true
TERMINATED     → false
```

Example:

```java
workerThread.getState(); // TIMED_WAITING
workerThread.isAlive(); // true
```

---

# 6. `getState()` vs `isAlive()`

| Method | What it tells you |
|---|---|
| `getState()` | Exact Java thread state |
| `isAlive()` | Whether the thread has started and has not yet terminated |

`getState()` gives more detailed information.

---

# 7. `sleep()` vs `join()`

## `Thread.sleep()`

```java
Thread.sleep(3000);
```

The **current thread** sleeps.

If the worker executes it:

```text
WORKER THREAD
     ↓
TIMED_WAITING
```

The main thread is not automatically affected.

---

## `join()`

```java
workerThread.join();
```

The thread that **calls** `join()` waits for the target thread to finish.

If main calls it:

```text
MAIN THREAD
    ↓
WAITING
```

while:

```text
WORKER THREAD
    ↓
continues execution
```

When the worker terminates, `join()` returns and main continues.

---

# 8. Practical Thread Life Cycle Program

```java
package thread.lifecycle;

public class ThreadLifeCycle {

    public static void main(String[] args) throws InterruptedException {

        // Runnable represents the TASK / WORK.
        // It does not create a thread by itself.
        Runnable task = () -> {

            System.out.println(
                    "Worker thread started: "
                    + Thread.currentThread().getName()
            );

            try {

                // Worker enters TIMED_WAITING.
                Thread.sleep(3000);

                System.out.println(
                        "Worker thread finished sleeping"
                );

            } catch (InterruptedException e) {

                // Restore the interrupted status.
                Thread.currentThread().interrupt();
            }
        };

        // Creating a Thread object does NOT start the thread.
        // The thread is currently NEW.
        Thread workerThread =
                new Thread(task, "worker-thread");

        System.out.println(
                "After thread creation: "
                + workerThread.getState()
        );

        System.out.println(
                "Is worker thread alive? "
                + workerThread.isAlive()
        );

        // start() creates a new execution path.
        // The worker becomes eligible to run.
        workerThread.start();

        System.out.println(
                "After starting thread: "
                + workerThread.getState()
        );

        System.out.println(
                "Is worker thread alive after starting? "
                + workerThread.isAlive()
        );

        // This sleep belongs to the MAIN thread.
        // It gives the worker time to reach its own sleep().
        Thread.sleep(500);

        System.out.println(
                "While worker thread is sleeping: "
                + workerThread.getState()
        );

        // Main waits for workerThread to finish.
        // Main → WAITING
        // Worker → continues execution
        workerThread.join();

        // Worker has completed run(), so it is TERMINATED.
        System.out.println(
                "After worker thread completes: "
                + workerThread.getState()
        );

        System.out.println(
                "Is worker thread alive after completion? "
                + workerThread.isAlive()
        );
    }
}
```

---

# 9. Execution Flow

### Step 1 — Create task

```java
Runnable task = () -> {
    ...
};
```

Creates a task. No thread is created yet.

### Step 2 — Create Thread

```java
Thread workerThread =
        new Thread(task, "worker-thread");
```

State:

```text
NEW
```

### Step 3 — Start Thread

```java
workerThread.start();
```

The worker becomes eligible for execution:

```text
RUNNABLE
```

### Step 4 — Worker sleeps

```java
Thread.sleep(3000);
```

Worker state:

```text
TIMED_WAITING
```

### Step 5 — Main joins worker

```java
workerThread.join();
```

Main waits:

```text
MAIN → WAITING
WORKER → continues
```

### Step 6 — Worker finishes

```text
TIMED_WAITING
      ↓
RUNNABLE
      ↓
run() ends
      ↓
TERMINATED
```

### Step 7 — Main continues

When the worker terminates:

```java
workerThread.join();
```

returns and the main thread continues.

---

# 10. Complete Mental Model

```text
                    Thread object created
                            │
                            ▼
                          NEW
                            │
                         start()
                            │
                            ▼
                        RUNNABLE
                       /    |    \
                      /     |     \
                     ▼      ▼      ▼
                BLOCKED  WAITING  TIMED_WAITING
                   │        │          │
                   └────────┴──────────┘
                            │
                            ▼
                        RUNNABLE
                            │
                       run() ends
                            │
                            ▼
                       TERMINATED
```

---

# 11. Quick State Definitions

| State | Simple Meaning |
|---|---|
| `NEW` | Thread created but not started |
| `RUNNABLE` | Ready to run or currently running |
| `BLOCKED` | Waiting to acquire a monitor lock |
| `WAITING` | Waiting indefinitely |
| `TIMED_WAITING` | Waiting for a specified amount of time |
| `TERMINATED` | Thread execution has finished |

---

# 12. Interview Questions

### Q1. What are the states of a Java thread?

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

### Q2. Does Java have a `RUNNING` thread state?

No. Java's official state is `RUNNABLE`, which covers both ready-to-run and currently-running threads.

### Q3. What happens after `start()`?

The thread becomes eligible for execution and enters `RUNNABLE`. The scheduler determines when it actually executes.

### Q4. What is the difference between `BLOCKED` and `WAITING`?

```text
BLOCKED → waiting to acquire a monitor lock
WAITING → waiting indefinitely for another thread/action
```

### Q5. What is the difference between `WAITING` and `TIMED_WAITING`?

```text
WAITING       → no specified timeout
TIMED_WAITING → specified waiting duration
```

Examples:

```java
thread.join();      // WAITING
thread.join(3000);  // TIMED_WAITING
Thread.sleep(3000); // TIMED_WAITING
```

### Q6. Does `sleep()` terminate a thread?

No. It temporarily moves the current thread to `TIMED_WAITING`.

### Q7. What happens when `run()` completes?

The thread enters:

```text
TERMINATED
```

### Q8. Can a terminated thread be restarted?

No. Calling `start()` again throws `IllegalThreadStateException`.

### Q9. What does `isAlive()` tell us?

Whether the thread has started and has not yet terminated.

### Q10. What does `join()` do?

It makes the thread that calls `join()` wait until the target thread completes.

---

# 13. Key Rules

```text
1. new Thread(...)        → NEW

2. start()                → RUNNABLE

3. Lock unavailable       → BLOCKED

4. join() / wait()        → WAITING

5. sleep(timeout)         → TIMED_WAITING

6. run() finishes         → TERMINATED

7. sleep() affects the
   CURRENT thread

8. join() makes the
   CALLING thread wait

9. Java has no official
   RUNNING state

10. A TERMINATED thread
    cannot be restarted
```

---

# 14. One-Line Mental Model

> **NEW → start() → RUNNABLE → temporary waiting/blocking states → RUNNABLE → TERMINATED**

Most important distinction:

```text
BLOCKED        → waiting for a LOCK
WAITING        → waiting indefinitely
TIMED_WAITING  → waiting for a limited TIME
TERMINATED     → execution is FINISHED
```
