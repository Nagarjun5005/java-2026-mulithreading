package synchronizedDemo.synchronized01.block;

/**
 * Demonstrates how a synchronized block can be used
 * to protect shared mutable data from a race condition.
 *
 * KEY CONCEPT:
 *
 * Multiple threads can execute the same method at the same time.
 * If those threads modify the same shared variable, a race condition
 * can occur.
 *
 * synchronized block prevents multiple threads from entering
 * the protected section at the same time when they use the same lock.
 */
public class CounterWithBlock {

    /**
     * Shared mutable data.
     *
     * "static" means there is only ONE counter associated
     * with this class, which means both threads access
     * the same variable.
     *
     * The variable is mutable because its value changes:
     *
     * counter = counter + 1
     *
     * Because multiple threads modify this variable,
     * it is vulnerable to a race condition.
     */
    private static int counter = 0;


    /**
     * This object is used as the LOCK.
     *
     * Every Java object has an intrinsic lock / monitor
     * associated with it.
     *
     * When we write:
     *
     *     synchronized (lock)
     *
     * we are telling Java:
     *
     * "A thread must acquire the monitor associated
     *  with the lock object before entering this block."
     *
     * The 'final' keyword means that the lock reference
     * cannot be changed to point to another object.
     *
     * Both threads must use this SAME lock object
     * for synchronization to work.
     */
    private static final Object lock = new Object();


    /**
     * Increments the shared counter.
     *
     * Notice that the entire method is NOT synchronized.
     *
     * Only the code that actually accesses the shared
     * variable is protected.
     *
     * This is the main advantage of a synchronized block:
     *
     *     We can protect only the critical section
     *     instead of synchronizing the entire method.
     */
    private static void increment() {

        /**
         * SYNCHRONIZED BLOCK
         *
         * The object inside synchronized(...) determines
         * which monitor/lock will be used.
         *
         * Here:
         *
         *     lock
         *       ↓
         *     lock's monitor
         *
         * A thread must acquire this monitor before
         * entering the block.
         *
         * If another thread already owns this monitor,
         * the second thread has to wait until the monitor
         * becomes available.
         */
        synchronized (lock) {

            /**
             * CRITICAL SECTION
             *
             * This is the section that accesses and modifies
             * shared mutable data.
             *
             * counter++ looks like one operation,
             * but conceptually it involves:
             *
             *     1. READ counter
             *     2. ADD 1
             *     3. WRITE counter
             *
             * Without synchronization, two threads could
             * perform these operations at the same time
             * and lose an update.
             *
             * With synchronized(lock), only one thread
             * at a time can execute this section
             * when competing for the same lock.
             */
            counter++;
        }

        /*
         * When the thread leaves the synchronized block,
         * the monitor is automatically released.
         *
         * The programmer does NOT need to manually unlock it.
         */
    }


    /**
     * Main thread starts two worker threads.
     */
    public static void main(String[] args) throws InterruptedException {

        /**
         * Runnable represents the TASK/WORK.
         *
         * Runnable itself does NOT create a thread.
         *
         * Both Thread objects below will execute
         * this same task.
         */
        Runnable task = () -> {

            /*
             * Each thread calls increment() one million times.
             *
             * Thread-1:
             *     1,000,000 increments
             *
             * Thread-2:
             *     1,000,000 increments
             *
             * Total expected:
             *
             *     1,000,000 + 1,000,000
             *     = 2,000,000
             */
            for (int i = 0; i < 1_000_000; i++) {
                increment();
            }
        };


        /**
         * Create the first Thread object.
         *
         * At this point the thread has been CREATED,
         * but it has not started executing yet.
         *
         * Its initial state is NEW.
         */
        Thread th1 = new Thread(task, "Thread-1");


        /**
         * Create the second Thread object.
         *
         * This thread also executes the same Runnable task.
         */
        Thread th2 = new Thread(task, "Thread-2");


        /**
         * start() starts a new execution path.
         *
         * Thread-1 begins executing the task.
         *
         * IMPORTANT:
         *
         * start() does NOT mean:
         *
         *     "execute the run() method like a normal method call"
         *
         * Instead, it causes the new thread to begin execution,
         * and the Runnable's run() method is executed by that thread.
         */
        th1.start();


        /**
         * Start Thread-2.
         *
         * Now both Thread-1 and Thread-2 can execute
         * concurrently.
         *
         * Both threads will eventually call:
         *
         *     increment()
         *
         * and both will compete for:
         *
         *     lock
         */
        th2.start();


        /**
         * join()
         *
         * The main thread waits for Thread-1 to finish.
         *
         * This does NOT synchronize counter++.
         *
         * The synchronization of counter++ is handled by:
         *
         *     synchronized (lock)
         *
         * join() only ensures that the main thread
         * doesn't print the result before Thread-1 finishes.
         */
        th1.join();


        /**
         * The main thread also waits for Thread-2
         * to finish.
         *
         * Only after both worker threads complete
         * will the main thread continue.
         */
        th2.join();


        /**
         * At this point:
         *
         * Thread-1 → completed 1,000,000 increments
         * Thread-2 → completed 1,000,000 increments
         *
         * Because the increment operation was protected
         * by the same lock, the expected result is:
         *
         *     2,000,000
         */
        System.out.println("Expected output: 2000000");


        /**
         * Print the actual value.
         *
         * Because counter++ was protected by the
         * synchronized block, the result should be:
         *
         *     2000000
         */
        System.out.println("Actual output: " + counter);
    }
}


/*
 * ============================================================
 *                    EXECUTION FLOW
 * ============================================================
 *
 *
 *                     Main Thread
 *                         |
 *                         |
 *                 Creates Runnable
 *                         |
 *                         ↓
 *                 Creates Thread-1
 *                 Creates Thread-2
 *                         |
 *                         ↓
 *                    start() both
 *                         |
 *              ┌──────────┴──────────┐
 *              ↓                     ↓
 *          Thread-1               Thread-2
 *              |                     |
 *              ↓                     ↓
 *         increment()           increment()
 *              |                     |
 *              ↓                     ↓
 *       synchronized(lock)    synchronized(lock)
 *              |                     |
 *              ↓                     |
 *         Gets monitor              |
 *              |                     |
 *              ↓                     |
 *          counter++                 |
 *              |                     |
 *              ↓                     |
 *       Releases monitor            |
 *                                    ↓
 *                             Gets monitor
 *                                    |
 *                                    ↓
 *                                counter++
 *                                    |
 *                                    ↓
 *                             Releases monitor
 *
 *
 * ============================================================
 *                    IMPORTANT CONCEPTS
 * ============================================================
 *
 * 1. counter
 *    ↓
 *    Shared mutable data.
 *
 *
 * 2. lock
 *    ↓
 *    Object used for synchronization.
 *
 *
 * 3. Monitor / Intrinsic Lock
 *    ↓
 *    Every Java object has an intrinsic monitor.
 *    synchronized(lock) uses the monitor associated
 *    with the lock object.
 *
 *
 * 4. synchronized block
 *    ↓
 *    Protects only the required section of code.
 *
 *
 * 5. Critical section
 *    ↓
 *    The section of code that accesses/modifies
 *    shared mutable data.
 *
 *
 * 6. Mutual exclusion
 *    ↓
 *    Only one thread at a time can execute the
 *    synchronized section when competing for the
 *    same monitor.
 *
 *
 * 7. join()
 *    ↓
 *    Makes the calling thread wait for another
 *    thread to finish.
 *
 *    join() does NOT prevent race conditions.
 *
 *
 * ============================================================
 *                    GOLDEN RULE
 * ============================================================
 *
 * synchronized(lock)
 *
 * means:
 *
 * "Before entering this block, acquire the monitor
 *  associated with 'lock'. If another thread already
 *  owns that monitor, wait until it is released."
 *
 *
 * Therefore:
 *
 * Shared mutable data
 *        +
 * Multiple threads
 *        +
 * No synchronization
 *        ↓
 * Potential race condition
 *
 *
 * Shared mutable data
 *        +
 * Multiple threads
 *        +
 * synchronized(lock)
 *        ↓
 * Protected critical section
 */