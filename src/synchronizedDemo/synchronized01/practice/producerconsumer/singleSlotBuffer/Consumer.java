package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer;

/*
 * ============================================================================
 * CLASS: Consumer
 * ============================================================================
 *
 * PURPOSE:
 * Represents the Consumer thread in the Producer-Consumer problem.
 *
 * The Consumer retrieves data from the shared buffer by calling the
 * consume() method of SharedBuffer.
 *
 * IMPLEMENTATION:
 * This class implements the Runnable interface, allowing its task to be
 * executed by a Java Thread.
 *
 * HOW IT WORKS:
 *
 *   1. The Consumer receives a SharedBuffer object through its constructor.
 *
 *   2. The run() method executes when the associated thread starts.
 *
 *   3. The Consumer calls sharedBuffer.consume() to retrieve data.
 *
 *   4. If the buffer is empty, the Consumer waits inside consume() until
 *      the Producer produces data.
 *
 *   5. If the Consumer is interrupted while waiting, it catches the
 *      InterruptedException and restores its interrupted status.
 *
 * IMPORTANT:
 * The Consumer must use the same SharedBuffer instance as the Producer.
 * This allows both threads to coordinate access to the same data and
 * synchronize using the same object's monitor.
 * ============================================================================
 */
public class Consumer implements Runnable {

    /*
     * ------------------------------------------------------------------------
     * INSTANCE VARIABLE: sharedBuffer
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Holds a reference to the SharedBuffer object from which the Consumer
     * retrieves data.
     *
     * INITIALIZATION:
     * The Main class passes the SharedBuffer instance to the Consumer
     * through its constructor.
     *
     * SHARED ACCESS:
     * The Producer and Consumer reference the same SharedBuffer object.
     *
     * Consequently, when either thread calls a synchronized method on that
     * object, it must acquire the same object's monitor.
     */
    private SharedBuffer sharedBuffer;


    /*
     * ------------------------------------------------------------------------
     * CONSTRUCTOR: Consumer(SharedBuffer sharedBuffer)
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Initializes the Consumer with the buffer it will use.
     *
     * PARAMETER:
     * sharedBuffer - The shared buffer containing data produced by the
     *                Producer and consumed by this Consumer.
     *
     * The constructor stores the supplied object reference in the instance
     * variable so that run() can access the buffer.
     */
    public Consumer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }


    /*
     * =========================================================================
     * METHOD: run()
     * =========================================================================
     *
     * PURPOSE:
     * Defines the task performed by the Consumer thread.
     *
     * THREAD EXECUTION:
     * When start() is called on the Thread associated with this Consumer,
     * Java executes this run() method on that thread.
     *
     * EXECUTION FLOW:
     *
     *   1. The Consumer thread enters run().
     *
     *   2. It calls sharedBuffer.consume().
     *
     *   3. The consume() method acquires the SharedBuffer object's monitor
     *      because it is declared synchronized.
     *
     *   4. If the buffer is empty, consume() calls wait().
     *      This releases the monitor and suspends the Consumer until it
     *      can resume.
     *
     *   5. Once data is available, the Consumer retrieves it, marks the
     *      buffer as empty, and returns from consume().
     *
     * EXCEPTION HANDLING:
     *
     * The consume() method declares InterruptedException because wait()
     * can throw this exception if the Consumer is interrupted while waiting.
     *
     * Runnable.run() cannot declare InterruptedException, so the exception
     * is handled inside this method.
     *
     * Thread.currentThread().interrupt() restores the interrupted status
     * of the current Consumer thread.
     *
     * IMPORTANT:
     * Restoring the interrupted status does not restart the thread or
     * automatically retry the consumption operation.
     * =========================================================================
     */
    @Override
    public void run() {

        /*
         * Request data from the shared buffer.
         *
         * The consume() method handles the actual consumption logic,
         * synchronization, and waiting when the buffer is empty.
         */
        try {
            sharedBuffer.consume();

        } catch (InterruptedException e) {

            /*
             * Restore the interrupted status of the current Consumer thread.
             *
             * When wait() throws InterruptedException, the interrupted status
             * is cleared. Calling interrupt() restores the interruption signal
             * so that other code can detect it if necessary.
             */
            Thread.currentThread().interrupt();
        }
    }
}

