package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer;

/*
 * ============================================================================
 * CLASS: Producer
 * ============================================================================
 *
 * PURPOSE:
 * Represents the Producer thread in the Producer-Consumer problem.
 *
 * The Producer is responsible for producing data and placing it into the
 * shared buffer by calling the produce() method of SharedBuffer.
 *
 * IMPLEMENTATION:
 * This class implements the Runnable interface, which allows its task
 * to be executed by a Java Thread.
 *
 * HOW IT WORKS:
 *
 *   1. The Producer receives a SharedBuffer object through its constructor.
 *
 *   2. The run() method is executed when the associated thread starts.
 *
 *   3. The Producer calls sharedBuffer.produce() to produce data.
 *
 *   4. If the buffer is full, the Producer waits inside produce() until
 *      the buffer becomes available.
 *
 *   5. If the thread is interrupted while waiting, the InterruptedException
 *      is caught and the thread's interrupted status is restored.
 *
 * IMPORTANT:
 * The Producer does not create its own buffer. It shares the same
 * SharedBuffer instance with the Consumer.
 *
 * This is essential because both threads must operate on the same shared
 * state for synchronization to work correctly.
 * ============================================================================
 */
public class Producer implements Runnable {

    /*
     * ------------------------------------------------------------------------
     * SHARED BUFFER REFERENCE
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Holds a reference to the SharedBuffer object into which the Producer
     * places data.
     *
     * WHY IS THIS REQUIRED?
     * The Producer needs access to the shared buffer to call produce().
     *
     * SHARING:
     * The Main class passes the SharedBuffer instance to the Producer.
     * The Consumer receives that same instance.
     *
     * As a result, both threads coordinate using the same object's monitor
     * when they call its synchronized methods.
     */
    private SharedBuffer sharedBuffer;


    /*
     * ------------------------------------------------------------------------
     * CONSTRUCTOR: Producer(SharedBuffer sharedBuffer)
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Initializes the Producer with the SharedBuffer object it will use.
     *
     * PARAMETER:
     * sharedBuffer - The shared buffer used to exchange data between
     *                the Producer and Consumer.
     *
     * The constructor stores the supplied reference in the instance variable
     * so that the run() method can access the buffer later.
     */
    public Producer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }


    /*
     * =========================================================================
     * METHOD: run()
     * =========================================================================
     *
     * PURPOSE:
     * Contains the task performed by the Producer thread.
     *
     * THREAD EXECUTION:
     * When start() is called on the Thread associated with this Producer,
     * the JVM executes this run() method on that thread.
     *
     * EXECUTION FLOW:
     *
     *   1. The Producer thread enters run().
     *
     *   2. It calls sharedBuffer.produce().
     *
     *   3. The produce() method acquires the SharedBuffer object's monitor
     *      because it is declared synchronized.
     *
     *   4. If the buffer is full, produce() calls wait(), which releases
     *      the monitor and suspends the Producer until it can resume.
     *
     *   5. If production completes, control returns to run().
     *
     * EXCEPTION HANDLING:
     *
     * The produce() method declares InterruptedException because wait()
     * can throw this exception when the thread is interrupted while waiting.
     *
     * Since Runnable.run() cannot declare InterruptedException, the exception
     * must be handled inside this method.
     *
     * Thread.currentThread().interrupt() restores the current thread's
     * interrupted status so that higher-level code can detect the interruption.
     *
     * IMPORTANT:
     * Restoring the interrupted status does not restart the thread or
     * automatically retry the production operation.
     * =========================================================================
     */
    @Override
    public void run() {

        /*
         * Call the shared buffer's production method.
         *
         * The actual buffer synchronization and data production logic
         * are implemented inside SharedBuffer.produce().
         */
        try {
            sharedBuffer.produce();

        } catch (InterruptedException e) {

            /*
             * Restore the interrupted status of the current Producer thread.
             *
             * Calling wait() can clear the interrupted status when it throws
             * InterruptedException. Restoring the status preserves the
             * interruption signal for code that may inspect it later.
             */
            Thread.currentThread().interrupt();
        }
    }
}

