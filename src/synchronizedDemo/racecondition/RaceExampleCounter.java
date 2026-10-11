package synchronizedDemo.racecondition;

public class RaceExampleCounter {

    /*
     * ============================================================
     * SHARED MUTABLE STATE
     * ============================================================
     *
     * This variable is:
     *
     * 1. SHARED
     *    Both thread-1 and thread-2 can access the same 'count'.
     *
     * 2. MUTABLE
     *    Its value can be changed using:
     *
     *        count++;
     *
     * Because multiple threads can modify the same variable
     * concurrently, this is a potential source of a
     * RACE CONDITION.
     *
     * Initial value:
     *
     *        count = 0
     */
    private static int count = 0;


    public static void main(String[] args) throws InterruptedException {

        /*
         * ============================================================
         * CREATE THE TASK
         * ============================================================
         *
         * This Runnable represents the work that each thread
         * will perform.
         *
         * Both threads will execute the SAME task.
         *
         * Therefore, both threads will access the SAME static
         * 'count' variable.
         */
        Runnable task = () -> {

            /*
             * Each thread performs 1,000,000 increments.
             *
             * Thread-1 → 1,000,000 increments
             * Thread-2 → 1,000,000 increments
             *
             * Expected total:
             *
             * 1,000,000 + 1,000,000
             * = 2,000,000
             */
            for (int i = 0; i < 1_000_000; i++) {

                /*
                 * ====================================================
                 * RACE CONDITION HAPPENS HERE
                 * ====================================================
                 *
                 * It may look like:
                 *
                 *      count++;
                 *
                 * is one operation.
                 *
                 * Conceptually, however, it involves:
                 *
                 *      1. READ count
                 *      2. ADD 1
                 *      3. WRITE the new value
                 *
                 *
                 * Example:
                 *
                 * Suppose:
                 *
                 *      count = 100
                 *
                 * Thread-1:
                 *      READ  → 100
                 *
                 * Thread-2:
                 *      READ  → 100
                 *
                 * Thread-1:
                 *      ADD 1 → 101
                 *      WRITE → 101
                 *
                 * Thread-2:
                 *      ADD 1 → 101
                 *      WRITE → 101
                 *
                 * Expected:
                 *
                 *      102
                 *
                 * Actual:
                 *
                 *      101
                 *
                 * One increment has been LOST.
                 *
                 * This is called a:
                 *
                 *      LOST UPDATE
                 *
                 * And this is an example of a:
                 *
                 *      RACE CONDITION
                 */
                count++;
            }
        };


        /*
         * ============================================================
         * CREATE THREADS
         * ============================================================
         *
         * Both Thread objects use the SAME Runnable task.
         *
         * Therefore:
         *
         *      thread-1 → modifies count
         *      thread-2 → modifies count
         *
         * Both threads can execute count++ concurrently.
         */
        Thread thread1 = new Thread(task, "thread-1");
        Thread thread2 = new Thread(task, "thread-2");


        /*
         * ============================================================
         * START THREADS
         * ============================================================
         *
         * start() creates separate execution paths.
         *
         * After these calls:
         *
         *      thread-1 → starts executing task
         *      thread-2 → starts executing task
         *
         * The scheduler decides how their execution is interleaved.
         *
         * We do NOT control the exact order in which the threads
         * execute.
         */
        thread1.start();
        thread2.start();


        /*
         * ============================================================
         * WAIT FOR THREADS TO FINISH
         * ============================================================
         *
         * join() makes the MAIN thread wait until the specified
         * worker thread has completed.
         *
         * Important:
         *
         * join() DOES NOT make count thread-safe.
         *
         * It only ensures that main() does not print the result
         * before thread-1 and thread-2 finish their work.
         *
         * Without join():
         *
         *      Main could print count
         *      while the worker threads are still running.
         *
         * With join():
         *
         *      Thread-1 finishes
         *      Thread-2 finishes
         *              ↓
         *      Main continues
         */
        thread1.join();
        thread2.join();


        /*
         * ============================================================
         * EXPECTED RESULT
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
         * So we expect:
         *
         *      count = 2,000,000
         */
        System.out.println("Expected Counter: 2000000");


        /*
         * ============================================================
         * ACTUAL RESULT
         * ============================================================
         *
         * Because count++ is not protected from concurrent access,
         * some increments can be lost.
         *
         * Example result:
         *
         *      Actual Counter: 1068345
         *
         * This means many increments were overwritten by another
         * thread before they could be reflected in the final value.
         *
         * IMPORTANT:
         *
         * The exact incorrect result is NOT predictable.
         *
         * One execution might produce:
         *
         *      1068345
         *
         * Another execution might produce:
         *
         *      1500000
         *
         * Another execution might even produce:
         *
         *      2000000
         *
         * depending on thread scheduling and timing.
         *
         * Therefore, getting 2,000,000 does NOT prove that the code
         * is thread-safe.
         */
        System.out.println("Actual Counter: " + count);
    }
}


/*
 * ================================================================
 * OBSERVED OUTPUT
 * ================================================================
 *
 * Expected Counter: 2000000
 * Actual Counter: 1068345
 *
 *
 * WHY IS ACTUAL < EXPECTED?
 *
 *
 *          Shared Mutable Data
 *                  │
 *                  ▼
 *              count = 0
 *                  │
 *          ┌───────┴───────┐
 *          ▼               ▼
 *      thread-1         thread-2
 *          │               │
 *       count++         count++
 *          │               │
 *          └───────┬───────┘
 *                  ▼
 *         Concurrent Access
 *                  │
 *                  ▼
 *          Race Condition
 *                  │
 *                  ▼
 *           Lost Updates
 *                  │
 *                  ▼
 *          Actual < Expected
 *
 *
 * ================================================================
 *
 * KEY TAKEAWAY:
 *
 * The problem is NOT that multiple threads exist.
 *
 * The problem is that multiple threads are modifying the same
 * shared mutable data without proper synchronization.
 *
 *
 *        Multiple Threads
 *               +
 *        Shared Mutable Data
 *               +
 *        Concurrent Modification
 *               +
 *        No Coordination
 *               ↓
 *        RACE CONDITION
 *
 * The next step is to learn how 'synchronized' can control access
 * to this shared data and prevent this race condition.
 */




