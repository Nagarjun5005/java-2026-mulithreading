package thread.lifecycle;


/**
 * This class demonstrates the basic Java Thread Life Cycle.
 *
 * The program demonstrates these Thread states:
 *
 *     NEW
 *       ↓
 *     RUNNABLE
 *       ↓
 *     TIMED_WAITING
 *       ↓
 *     RUNNABLE
 *       ↓
 *     TERMINATED
 *
 *
 * It also demonstrates:
 *
 *     - Thread creation
 *     - Thread naming
 *     - start()
 *     - getState()
 *     - isAlive()
 *     - sleep()
 *     - join()
 *     - currentThread()
 */
public class ThreadLifeCycle {


    public static void main(String[] args) throws InterruptedException {


        /*
         * =========================================================
         * STEP 1 — CREATE THE TASK
         * =========================================================
         *
         * Runnable represents the WORK/TASK that the thread
         * will execute.
         *
         * Here we are using a lambda because Runnable is a
         * functional interface.
         *
         * IMPORTANT:
         *
         * Creating a Runnable does NOT create a Thread.
         *
         * We are only defining:
         *
         *     "What work should the thread perform?"
         */
        Runnable task = () -> {


            /*
             * Thread.currentThread()
             *
             * Returns the thread that is currently executing
             * this code.
             *
             * getName()
             *
             * Returns the name of that thread.
             *
             * Since this code will eventually be executed by
             * "worker-thread", the output will be something like:
             *
             *     worker thread started/running worker-thread
             */
            System.out.println(
                    "worker thread started/running " +
                            Thread.currentThread().getName()
            );


            try {


                /*
                 * =================================================
                 * TIMED_WAITING
                 * =================================================
                 *
                 * sleep(3000) tells the CURRENT THREAD:
                 *
                 *     "Pause execution for approximately 3 seconds."
                 *
                 * During this time the worker thread enters:
                 *
                 *     TIMED_WAITING
                 *
                 * The state transition is:
                 *
                 *     RUNNABLE
                 *          ↓
                 *     Thread.sleep(3000)
                 *          ↓
                 *     TIMED_WAITING
                 *
                 * After the 3 seconds are over, the thread can
                 * become RUNNABLE again.
                 */
                Thread.sleep(3000);


                /*
                 * The worker thread has finished sleeping.
                 *
                 * It can now continue executing the remaining
                 * code in the run() method.
                 */
                System.out.println(
                        "worker thread finished sleeping"
                );


            } catch (InterruptedException e) {


                /*
                 * sleep() can throw InterruptedException if
                 * another thread interrupts this thread.
                 *
                 * We restore the interrupted status of the
                 * current thread using:
                 *
                 *     Thread.currentThread().interrupt();
                 *
                 * We will study interruption in more detail
                 * separately.
                 */
                Thread.currentThread().interrupt();
            }

        };


        /*
         * =========================================================
         * STEP 2 — CREATE THE THREAD
         * =========================================================
         *
         * We now create a Thread object and give it:
         *
         *     1. The Runnable task
         *     2. A thread name
         *
         * This uses the constructor:
         *
         *     Thread(Runnable task, String name)
         *
         *
         * At this point:
         *
         *     - Thread object exists
         *     - Task is associated with it
         *     - Thread has a name
         *     - Thread has NOT started
         *
         * Therefore the state is:
         *
         *     NEW
         */
        Thread workerThread =
                new Thread(task, "worker-thread");


        /*
         * getState()
         *
         * Returns the current state of the Thread.
         *
         * start() has not been called yet.
         *
         * Therefore:
         *
         *     NEW
         *
         * Expected output:
         *
         *     After thread creation, the thread status is NEW
         */
        System.out.println(
                "After thread creation, the thread status is " +
                        workerThread.getState()
        );


        /*
         * isAlive()
         *
         * Returns true if the thread has been started and
         * has not yet terminated.
         *
         * The thread is still NEW.
         *
         * Therefore:
         *
         *     false
         */
        System.out.println(
                "is Thread Alive --> " +
                        workerThread.isAlive()
        );


        /*
         * =========================================================
         * STEP 3 — START THE THREAD
         * =========================================================
         *
         * start() starts a NEW execution path.
         *
         * The state changes conceptually:
         *
         *     NEW
         *      ↓
         *   start()
         *      ↓
         *   RUNNABLE
         *
         *
         * IMPORTANT:
         *
         * start() does NOT mean that the thread is guaranteed
         * to immediately execute on the CPU.
         *
         * The thread becomes eligible to run and the scheduler
         * decides when it gets CPU execution time.
         *
         *
         * Eventually, the worker thread will execute:
         *
         *     task.run()
         */
        workerThread.start();


        /*
         * The worker thread has now been started.
         *
         * Its state will normally be RUNNABLE at this point.
         *
         * However, because thread scheduling is unpredictable,
         * we should not assume that getState() will always show
         * exactly RUNNABLE at this exact moment.
         *
         * The worker thread may already have started executing.
         */
        System.out.println(
                "After starting the thread, thread state " +
                        workerThread.getState()
        );


        /*
         * Since start() has been called and the worker thread
         * has not completed yet, isAlive() should normally be:
         *
         *     true
         *
         * The thread is now alive.
         */
        System.out.println(
                "is worker thread alive after starting ---> " +
                        workerThread.isAlive()
        );


        /*
         * =========================================================
         * STEP 4 — GIVE THE WORKER TIME TO ENTER sleep()
         * =========================================================
         *
         * This sleep() is executed by the MAIN THREAD.
         *
         * We are NOT putting the worker thread to sleep here.
         *
         * Why 500 milliseconds?
         *
         * We want to give the worker thread enough time to reach:
         *
         *     Thread.sleep(3000)
         *
         * inside the Runnable.
         *
         * This makes it easier for us to observe the worker thread
         * in the TIMED_WAITING state.
         */
        Thread.sleep(500);


        /*
         * =========================================================
         * STEP 5 — OBSERVE TIMED_WAITING
         * =========================================================
         *
         * At this point, the worker thread should normally be
         * inside:
         *
         *     Thread.sleep(3000)
         *
         * Therefore its state should be:
         *
         *     TIMED_WAITING
         *
         *
         * The worker thread's flow is:
         *
         *     RUNNABLE
         *         ↓
         *     sleep(3000)
         *         ↓
         *     TIMED_WAITING
         */
        System.out.println(
                "while the thread is sleeping " +
                        workerThread.getState()
        );


        /*
         * =========================================================
         * STEP 6 — JOIN THE WORKER THREAD
         * =========================================================
         *
         * join() is executed by the MAIN THREAD.
         *
         * It means:
         *
         *     "Main thread, wait until workerThread finishes."
         *
         *
         * Therefore the MAIN THREAD enters:
         *
         *     WAITING
         *
         * while the WORKER THREAD continues its execution.
         *
         *
         * Conceptually:
         *
         *     MAIN THREAD
         *          ↓
         *       join()
         *          ↓
         *       WAITING
         *
         *
         *     WORKER THREAD
         *          ↓
         *     finishes sleep
         *          ↓
         *     continues run()
         *          ↓
         *     TERMINATED
         *
         *
         * Once the worker thread terminates, the main thread
         * is allowed to continue.
         */
        workerThread.join();


        /*
         * =========================================================
         * STEP 7 — WORKER THREAD TERMINATED
         * =========================================================
         *
         * join() has returned.
         *
         * This means the worker thread has finished executing
         * its run() method.
         *
         * Therefore:
         *
         *     workerThread.getState()
         *
         * should return:
         *
         *     TERMINATED
         *
         *
         * The worker thread's life cycle is now:
         *
         *     NEW
         *       ↓
         *     RUNNABLE
         *       ↓
         *     TIMED_WAITING
         *       ↓
         *     RUNNABLE
         *       ↓
         *     TERMINATED
         */
        System.out.println(
                "After worker thread completes " +
                        workerThread.getState()
        );


        /*
         * isAlive()
         *
         * The worker thread has already completed its execution.
         *
         * Therefore:
         *
         *     false
         *
         *
         * Remember:
         *
         *     NEW         → isAlive() = false
         *     RUNNABLE    → isAlive() = true
         *     TERMINATED  → isAlive() = false
         */
        System.out.println(
                "After worker thread is completed, " +
                        "is thread alive? " +
                        workerThread.isAlive()
        );
    }
}