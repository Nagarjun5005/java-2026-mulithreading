package synchronizedDemo.synchronized01.method;

public class CounterWithSynchronized {

    /*
     * ============================================================
     * SHARED MUTABLE STATE
     * ============================================================
     *
     * 'counter' is:
     *
     * 1. SHARED
     *    Both thread-1 and thread-2 can access this variable.
     *
     * 2. MUTABLE
     *    Its value changes every time increment() is called.
     *
     * Initial value:
     *
     *      counter = 0
     *
     * Both threads will eventually modify this SAME variable.
     */
    private static int counter = 0;


    /*
     * ============================================================
     * SYNCHRONIZED METHOD
     * ============================================================
     *
     * 'synchronized' ensures that only ONE thread at a time can
     * execute this method using the same monitor lock.
     *
     * Since this is a STATIC synchronized method, the lock is
     * associated with the CLASS object:
     *
     *      CounterWithSynchronized.class
     *
     *
     * Conceptually:
     *
     * Thread-1
     *     ↓
     * acquire class lock
     *     ↓
     * enter increment()
     *     ↓
     * counter++
     *     ↓
     * exit increment()
     *     ↓
     * release class lock
     *
     *
     * Meanwhile, if Thread-2 tries to enter this method while
     * Thread-1 owns the lock:
     *
     * Thread-2
     *     ↓
     * tries to acquire class lock
     *     ↓
     * lock already occupied
     *     ↓
     * BLOCKED
     *
     * Once Thread-1 releases the lock:
     *
     * Thread-2
     *     ↓
     * acquires lock
     *     ↓
     * enters increment()
     *     ↓
     * counter++
     *
     *
     * ============================================================
     * CRITICAL SECTION
     * ============================================================
     *
     * The operation:
     *
     *      counter++;
     *
     * is the critical operation because it modifies shared mutable
     * state.
     *
     * Synchronization ensures that multiple threads cannot execute
     * this critical section concurrently through this method.
     */
    private static synchronized void increment() {

        /*
         * Without synchronization, this can suffer from a
         * race condition:
         *
         *      READ counter
         *          ↓
         *      ADD 1
         *          ↓
         *      WRITE counter
         *
         * Two threads could interleave these operations and lose
         * updates.
         *
         * With synchronized:
         *
         *      Thread-1 → lock → counter++ → unlock
         *
         *      Thread-2 → waits → lock → counter++ → unlock
         *
         * Therefore, the increment operation is protected from
         * concurrent execution by these threads.
         */
        counter++;
    }


    public static void main(String[] args)
            throws InterruptedException {


        /*
         * ============================================================
         * CREATE THE TASK
         * ============================================================
         *
         * Runnable represents the work that each thread will perform.
         *
         * Both threads will use this SAME Runnable task.
         *
         * Each thread calls increment() 1,000,000 times.
         *
         * Thread-1:
         *      1,000,000 calls
         *
         * Thread-2:
         *      1,000,000 calls
         *
         * Total:
         *
         *      2,000,000 calls
         */
        Runnable task = () -> {

            for (int i = 0; i < 1_000_000; i++) {

                /*
                 * Call the synchronized method.
                 *
                 * The thread must acquire the class monitor lock
                 * before it can execute increment().
                 *
                 * Only one thread can execute the synchronized
                 * method at a time using this same lock.
                 */
                increment();
            }
        };


        /*
         * ============================================================
         * CREATE TWO THREADS
         * ============================================================
         *
         * Both threads execute the same task.
         *
         * Therefore:
         *
         *      thread-1 → modifies counter
         *      thread-2 → modifies counter
         *
         * The shared counter is protected by increment().
         */
        Thread th1 = new Thread(task, "thread-1");

        Thread th2 = new Thread(task, "thread-2");


        /*
         * ============================================================
         * START THREADS
         * ============================================================
         *
         * start() creates separate execution paths.
         *
         * Both threads can now execute concurrently.
         *
         * However, whenever they reach increment():
         *
         *      Only ONE can enter the synchronized method
         *      at a time.
         *
         * The other thread must wait for the monitor lock.
         */
        th1.start();
        th2.start();


        /*
         * ============================================================
         * WAIT FOR BOTH THREADS
         * ============================================================
         *
         * join() makes the MAIN thread wait until the specified
         * worker thread has completed.
         *
         * Important:
         *
         * join() is NOT what makes counter thread-safe.
         *
         * synchronized provides mutual exclusion.
         *
         * join() only ensures that main() waits until both worker
         * threads finish before printing the final counter.
         *
         *
         * Main thread:
         *
         *      th1.join()
         *          ↓
         *      waits for thread-1
         *
         *      th2.join()
         *          ↓
         *      waits for thread-2
         *
         *      both finish
         *          ↓
         *      main continues
         */
        th1.join();
        th2.join();


        /*
         * ============================================================
         * EXPECTED OUTPUT
         * ============================================================
         *
         * Each thread performs:
         *
         *      1,000,000 increments
         *
         * Therefore:
         *
         *      1,000,000
         *    + 1,000,000
         *    ----------------
         *      2,000,000
         *
         * Because increment() is synchronized, the updates are
         * protected from the race condition.
         */
        System.out.println("Expected output " + 2_000_000);


        /*
         * ============================================================
         * ACTUAL OUTPUT
         * ============================================================
         *
         * Because both threads have completed and every increment()
         * call was protected by the synchronized method, the actual
         * result should be:
         *
         *      2,000,000
         *
         * Example:
         *
         * Expected output 2000000
         * Actual output--->2000000
         */
        System.out.println("Actual output--->" + counter);
    }
}


/*
 * ================================================================
 * COMPLETE EXECUTION MODEL
 * ================================================================
 *
 *
 *                    counter = 0
 *                         │
 *                         ▼
 *              ┌────────────────────┐
 *              │    Shared State    │
 *              │      counter       │
 *              └─────────┬──────────┘
 *                        │
 *                 ┌──────┴──────┐
 *                 ▼             ▼
 *             thread-1       thread-2
 *                 │             │
 *                 │             │
 *                 ▼             ▼
 *             increment()   increment()
 *                 │             │
 *                 ▼             ▼
 *              TRY LOCK      TRY LOCK
 *                 │             │
 *                 ▼             │
 *             GET LOCK          │
 *                 │             │
 *                 ▼             │
 *             counter++         │
 *                 │             │
 *                 ▼             │
 *            RELEASE LOCK       │
 *                               │
 *                               ▼
 *                            GET LOCK
 *                               │
 *                               ▼
 *                           counter++
 *                               │
 *                               ▼
 *                          RELEASE LOCK
 *
 *
 * ================================================================
 * IMPORTANT CONCEPTS
 * ================================================================
 *
 * 1. Race Condition
 *
 *    Multiple threads modify shared mutable data without
 *    proper coordination.
 *
 *
 * 2. Critical Section
 *
 *    The section of code that accesses/modifies shared state.
 *
 *    In this example:
 *
 *        counter++;
 *
 *
 * 3. synchronized
 *
 *    Provides mutual exclusion for the synchronized method
 *    using its associated monitor lock.
 *
 *
 * 4. Monitor Lock
 *
 *    A thread must acquire the appropriate monitor before it
 *    can enter the synchronized method.
 *
 *
 * 5. BLOCKED
 *
 *    If another thread already owns the monitor and a thread
 *    tries to enter the synchronized method, that thread can
 *    enter the BLOCKED state while waiting for the lock.
 *
 *
 * 6. join()
 *
 *    Makes the calling thread wait for another thread to finish.
 *
 *    It does NOT protect counter.
 *
 *
 * ================================================================
 *
 * WITHOUT synchronized:
 *
 *      Thread-1 ──────┐
 *                     ├──→ counter++ concurrently
 *      Thread-2 ──────┘
 *                         ↓
 *                   Race Condition
 *                         ↓
 *                   Lost Updates
 *                         ↓
 *                    Wrong Result
 *
 *
 * WITH synchronized:
 *
 *      Thread-1
 *          ↓
 *       GET LOCK
 *          ↓
 *       counter++
 *          ↓
 *     RELEASE LOCK
 *          ↓
 *
 *      Thread-2
 *          ↓
 *       GET LOCK
 *          ↓
 *       counter++
 *          ↓
 *     RELEASE LOCK
 *          ↓
 *
 *      Race condition prevented.
 *
 *
 * ================================================================
 * KEY TAKEAWAY
 * ================================================================
 *
 * synchronized does NOT mean:
 *
 *      "Only one thread exists."
 *
 * It means:
 *
 *      "Only one thread at a time can enter this synchronized
 *       region when competing for the same monitor lock."
 *
 *
 * The basic flow is:
 *
 *      Thread
 *        ↓
 *      acquire lock
 *        ↓
 *      enter synchronized code
 *        ↓
 *      execute critical section
 *        ↓
 *      release lock
 *
 * ================================================================
 */