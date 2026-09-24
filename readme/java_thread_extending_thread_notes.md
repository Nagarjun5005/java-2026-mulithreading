# Java Multithreading --- Creating a Thread by Extending `Thread`

## 1. Example Program

``` java
package thread;

public class ExtendedThread extends Thread {

    @Override
    public void run() {
        System.out.println("Task is running");
    }

    public static void main(String[] args) {
        ExtendedThread th = new ExtendedThread();
        th.start();
    }
}
```

------------------------------------------------------------------------

## 2. What Happens When the Program Runs?

### Step 1 --- Compilation

The Java source file:

``` text
ExtendedThread.java
```

is compiled using:

``` bash
javac ExtendedThread.java
```

This produces:

``` text
ExtendedThread.class
```

The `.class` file contains Java bytecode.

> The CPU does not directly execute Java bytecode. The JVM executes the
> bytecode, using interpretation and JIT compilation as appropriate,
> ultimately resulting in native instructions executed by the CPU.

------------------------------------------------------------------------

## 3. JVM Starts the Application

When we run:

``` bash
java thread.ExtendedThread
```

the operating system manages the running Java application as a process,
and the JVM runs within that process.

Conceptually:

``` text
Operating System
       |
       v
Java Process
       |
       v
      JVM
```

The JVM loads the required class and starts the application's main
thread.

------------------------------------------------------------------------

## 4. The Main Thread Starts

The JVM creates the initial **main thread**, which executes:

``` java
public static void main(String[] args)
```

Initially:

``` text
Java Process
    |
   JVM
    |
    +-- Main Thread
```

The main thread begins executing the statements inside `main()`.

------------------------------------------------------------------------

## 5. `new ExtendedThread()` Does NOT Start a Thread

When this line executes:

``` java
ExtendedThread th = new ExtendedThread();
```

a `Thread` object is created.

Because:

``` java
class ExtendedThread extends Thread
```

`ExtendedThread` inherits the behavior of `Thread`.

However, **a new thread of execution has not started yet**.

Conceptually:

``` text
Main Thread
    |
    +-- creates ExtendedThread object
                    |
                    v
                  Heap
```

`th` is a reference to that `ExtendedThread` object.

------------------------------------------------------------------------

## 6. What Does `run()` Represent?

The class overrides:

``` java
@Override
public void run() {
    System.out.println("Task is running");
}
```

The `run()` method defines the work that the thread should perform.

Think:

``` text
Thread
   |
   +-- run()
        |
        +-- Task to execute
```

Overriding `run()` tells the thread what work it should perform when it
executes.

------------------------------------------------------------------------

## 7. What Happens When `start()` Is Called?

The important line is:

``` java
th.start();
```

`start()` is what requests the JVM/runtime to start a **new thread of
execution**.

Before `start()`:

``` text
Java Process
    |
    +-- Main Thread
```

After `start()`:

``` text
Java Process
    |
    +-- Main Thread
    |
    +-- New Worker Thread
```

The new thread is scheduled for execution, and when it begins execution,
the `run()` method is invoked.

Conceptually:

``` text
main()
  |
  +-- th.start()
          |
          v
     New Thread
          |
          v
        run()
          |
          v
System.out.println("Task is running");
```

------------------------------------------------------------------------

## 8. Main Thread vs Worker Thread

After `start()` is called, there are two execution paths:

``` text
              Java Process
                   |
          +--------+--------+
          |                 |
     Main Thread        Worker Thread
          |                 |
       main()              run()
          |                 |
      continues         executes task
```

The main thread does not execute `run()`.

The newly started thread executes `run()`.

The exact ordering between the threads is controlled by scheduling, so
the main thread and worker thread can execute concurrently.

------------------------------------------------------------------------

## 9. Thread Stack

Each thread has its own stack.

After the worker thread begins executing `run()`:

``` text
Java Process
|
+-- Main Thread
|      |
|      +-- Main Thread Stack
|             |
|             +-- main() frame
|
+-- Worker Thread
       |
       +-- Worker Thread Stack
              |
              +-- run() frame
```

The worker thread's stack contains the execution information for its
current method invocation.

------------------------------------------------------------------------

## 10. CPU and Operating System

The operating system schedules runnable threads onto available CPU
cores.

Conceptually:

``` text
Java Process
    |
    +-- Main Thread
    |
    +-- Worker Thread
            |
            v
      OS Scheduler
            |
            v
          CPU
```

On a multi-core system, different threads may execute simultaneously on
different CPU cores.

------------------------------------------------------------------------

## 11. `start()` vs `run()`

This is one of the most important interview concepts.

### `start()`

``` java
th.start();
```

-   Starts a new thread of execution.
-   The new thread eventually invokes `run()`.
-   The calling thread does not execute the `run()` method itself.

``` text
Main Thread
     |
   start()
     |
     v
New Thread
     |
    run()
```

### `run()`

``` java
th.run();
```

Calling `run()` directly is just a normal method call.

It does **not** create a new thread.

``` text
Main Thread
     |
    run()
     |
     v
Task executes on Main Thread
```

### Easy rule

> `start()` → new execution path\
> `run()` → normal method call

------------------------------------------------------------------------

## 12. Complete Execution Flow

``` text
ExtendedThread.java
        |
        | javac
        v
ExtendedThread.class
        |
        | java thread.ExtendedThread
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
        | executes main()
        |
        +-- new ExtendedThread()
        |       |
        |       +-- Thread object created
        |
        +-- th.start()
                |
                v
          New Worker Thread
                |
                v
              run()
                |
                v
        "Task is running"
```

------------------------------------------------------------------------

# Interview Questions

## Q1. What are the steps involved when `start()` is called?

**Answer:**

`start()` requests the JVM/runtime to start a new thread of execution.
The new thread becomes runnable and is scheduled for execution. When it
starts running, it invokes the thread's `run()` method.

------------------------------------------------------------------------

## Q2. Does `new ExtendedThread()` create a running thread?

**Answer:**

No. It creates a `Thread` object. The new thread of execution begins
only when `start()` is called.

------------------------------------------------------------------------

## Q3. What is the difference between `start()` and `run()`?

**Answer:**

`start()` starts a new thread of execution, while calling `run()`
directly is simply a normal method call executed by the current thread.

------------------------------------------------------------------------

## Q4. Which thread executes `main()`?

**Answer:**

The JVM's main thread executes the `main()` method.

------------------------------------------------------------------------

## Q5. Which thread executes `run()` after `start()`?

**Answer:**

The newly started thread executes the overridden `run()` method.

------------------------------------------------------------------------

## Q6. Does calling `run()` create a new thread?

**Answer:**

No. Calling `run()` directly does not create a new thread. It executes
like a normal method call on the current thread.

------------------------------------------------------------------------

## Q7. Why do we override `run()`?

**Answer:**

We override `run()` to define the task/work that the thread should
execute.

------------------------------------------------------------------------

## Q8. Is `ExtendedThread` itself the thread?

**Answer:**

`ExtendedThread` is a class that extends `Thread`. An instance of it
represents a thread and can be used to start that thread's execution.

------------------------------------------------------------------------

## Q9. What happens after `start()` is called?

**Answer:**

A new thread of execution is started. The new thread becomes eligible
for scheduling, and when it runs, the JVM invokes its `run()` method.

------------------------------------------------------------------------

# Quick Revision

``` text
new ExtendedThread()
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
Task executes
```

### Remember

> **`new` creates the Thread object.**

> **`start()` starts a new execution path.**

> **`run()` contains the task.**

> **Calling `run()` directly does not create a new thread.**

> **The main thread executes `main()`, while the newly started thread
> executes `run()`.**
