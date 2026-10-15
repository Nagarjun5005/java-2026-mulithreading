package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer;

/*
 * ============================================================================
 * CLASS: Main
 * ============================================================================
 *
 * PURPOSE:
 * This is the entry point of the Producer-Consumer application.
 *
 * The Main class creates the shared buffer, initializes the Producer and
 * Consumer tasks, creates their corresponding Thread objects, and starts
 * their execution.
 *
 * RESPONSIBILITIES:
 *
 *   1. Create a single SharedBuffer object.
 *
 *   2. Create a Producer and pass the shared buffer to it.
 *
 *   3. Create a Consumer and pass the same shared buffer to it.
 *
 *   4. Create separate Thread objects for the Producer and Consumer.
 *
 *   5. Start both threads so that they can execute concurrently.
 *
 *   6. Use join() to wait for both threads to finish.
 *
 * IMPORTANT CONCURRENCY CONCEPTS:
 *
 * - Shared State:
 *   Both threads access the same SharedBuffer instance.
 *
 * - Thread Creation:
 *   Each Thread receives a Runnable implementation that defines its task.
 *
 * - Concurrent Execution:
 *   After start() is called, the JVM schedules the threads for execution.
 *   Their execution order is not guaranteed.
 *
 * - Thread Coordination:
 *   The SharedBuffer class coordinates the Producer and Consumer using
 *   synchronized, wait(), and notifyAll().
 *
 * - Thread Completion:
 *   The join() method allows the main thread to wait for another thread
 *   to finish execution.
 * ============================================================================
 */
public class Main {

    /*
     * =========================================================================
     * METHOD: main()
     * =========================================================================
     *
     * PURPOSE:
     * The main() method is the starting point of the Java application.
     *
     * PARAMETER:
     * String[] args - Accepts command-line arguments.
     *
     * THROWS:
     * InterruptedException - Can be thrown if the main thread is interrupted
     * while waiting for another thread using join().
     * =========================================================================
     */
    public static void main(String[] args) throws InterruptedException {

        /*
         * STEP 1: CREATE THE SHARED BUFFER
         * ---------------------------------------------------------------------
         *
         * Create one SharedBuffer object.
         *
         * This object stores the shared data and manages synchronization
         * between the Producer and Consumer.
         *
         * Both threads must use this same object for the synchronization
         * mechanism to coordinate their access correctly.
         */
        SharedBuffer sharedBuffer = new SharedBuffer();


        /*
         * STEP 2: CREATE THE PRODUCER TASK
         * ---------------------------------------------------------------------
         *
         * Create a Producer object and pass the shared buffer to it.
         *
         * The Producer will use this buffer to produce data by calling
         * sharedBuffer.produce().
         *
         * At this stage, the Producer task is created but is not executing
         * on a separate thread.
         */
        Producer producer = new Producer(sharedBuffer);


        /*
         * STEP 3: CREATE THE CONSUMER TASK
         * ---------------------------------------------------------------------
         *
         * Create a Consumer object and pass the same shared buffer to it.
         *
         * The Consumer will use this buffer to retrieve data by calling
         * sharedBuffer.consume().
         *
         * IMPORTANT:
         * The Consumer receives the exact same SharedBuffer instance
         * that was passed to the Producer.
         *
         * Therefore, both threads access the same data and use the same
         * object's monitor when entering synchronized methods.
         */
        Consumer consumer = new Consumer(sharedBuffer);


        /*
         * STEP 4: CREATE THE PRODUCER THREAD
         * ---------------------------------------------------------------------
         *
         * Create a Thread object and provide the Producer as its Runnable task.
         *
         * "Producer Thread" is the name assigned to this thread.
         *
         * Creating a Thread does not start its execution.
         *
         * The Producer's run() method will execute when start() is called.
         */
        Thread thread1 = new Thread(producer, "Producer Thread");


        /*
         * STEP 5: CREATE THE CONSUMER THREAD
         * ---------------------------------------------------------------------
         *
         * Create a separate Thread object and provide the Consumer as its
         * Runnable task.
         *
         * "Consumer Thread" is the name assigned to this thread.
         *
         * The Consumer's run() method will execute when start() is called.
         */
        Thread thread2 = new Thread(consumer, "Consumer Thread");


        /*
         * STEP 6: START THE PRODUCER THREAD
         * ---------------------------------------------------------------------
         *
         * start() asks the JVM to schedule the thread for execution.
         *
         * The JVM executes the Producer's run() method on the new thread.
         *
         * The Producer then calls sharedBuffer.produce().
         *
         * IMPORTANT:
         * Calling start() does not guarantee that the Producer will execute
         * before the Consumer. Thread scheduling is nondeterministic.
         */
        thread1.start();


        /*
         * STEP 7: START THE CONSUMER THREAD
         * ---------------------------------------------------------------------
         *
         * Start the Consumer thread.
         *
         * The JVM executes the Consumer's run() method on this separate thread.
         *
         * The Consumer then calls sharedBuffer.consume().
         *
         * POSSIBLE SCENARIO:
         * If the Consumer reaches consume() before the Producer has produced
         * data, it finds available == false and waits.
         *
         * The Producer can then acquire the buffer's monitor, produce data,
         * and call notifyAll().
         *
         * The Consumer can resume after it is notified and reacquires
         * the monitor, provided the buffer condition allows it to proceed.
         */
        thread2.start();


        /*
         * STEP 8: WAIT FOR THE PRODUCER TO FINISH
         * ---------------------------------------------------------------------
         *
         * join() makes the main thread wait until thread1 terminates.
         *
         * The Producer thread can continue executing independently while
         * the main thread is waiting.
         *
         * If the Producer has already finished, join() returns immediately.
         *
         * NOTE:
         * join() does not stop the Producer or Consumer. It only coordinates
         * when the main thread can continue.
         */
        thread1.join();


        /*
         * STEP 9: WAIT FOR THE CONSUMER TO FINISH
         * ---------------------------------------------------------------------
         *
         * join() makes the main thread wait until thread2 terminates.
         *
         * This ensures that the main thread does not finish its main()
         * method until both the Producer and Consumer have completed.
         *
         * Once both join() calls return, both worker threads have terminated.
         */
        thread2.join();
    }
}
