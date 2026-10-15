package synchronizedDemo.synchronized01.practice.producerconsumer.singleSlotBuffer;

/*
 * ============================================================================
 * CLASS: SharedBuffer
 * ============================================================================
 *
 * PURPOSE:
 * This class represents a shared, single-slot buffer used by a Producer
 * thread and a Consumer thread.
 *
 * The Producer places data into the buffer, and the Consumer retrieves it.
 * Both threads access the same SharedBuffer object.
 *
 * PROBLEM BEING SOLVED:
 * Without proper synchronization, the Producer and Consumer could access
 * or modify shared data at the wrong time.
 *
 * For example:
 *   - The Consumer might try to consume data when the buffer is empty.
 *   - The Producer might overwrite data that has not yet been consumed.
 *
 * SOLUTION:
 * We use synchronized methods, wait(), notifyAll(), and a boolean flag
 * to coordinate access to the shared buffer.
 *
 * BUFFER RULES:
 *   1. The Producer can produce data only when the buffer is empty.
 *   2. The Consumer can consume data only when the buffer is full.
 *   3. If either thread cannot proceed, it waits until the buffer state
 *      changes.
 *
 * NOTE:
 * This implementation demonstrates a single-slot buffer. The Producer
 * currently produces the fixed value 10, and each method call handles
 * only one production or consumption operation.
 * ============================================================================
 */

public class SharedBuffer {

    /*
     * ------------------------------------------------------------------------
     * SHARED VARIABLE: data
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Stores the actual value placed in the buffer by the Producer.
     *
     * ACCESS:
     * Both the Producer and Consumer access this variable through the
     * synchronized methods of this class.
     *
     * In this example, the Producer assigns the value 10.
     *
     * IMPORTANT:
     * The data variable alone does not tell us whether the buffer contains
     * valid data. The available flag represents the buffer's logical state.
     */
    private int data;


    /*
     * ------------------------------------------------------------------------
     * SHARED VARIABLE: available
     * ------------------------------------------------------------------------
     *
     * PURPOSE:
     * Indicates whether the buffer currently contains data.
     *
     * STATES:
     *   false -> Buffer is EMPTY; the Producer may produce data.
     *   true  -> Buffer is FULL; the Consumer may consume data.
     *
     * INITIAL STATE:
     * The buffer starts empty, so available is initialized to false.
     *
     * ROLE IN THREAD COORDINATION:
     *   - The Producer waits while available is true.
     *   - The Consumer waits while available is false.
     *
     * This flag prevents the Producer from overwriting unconsumed data
     * and prevents the Consumer from consuming data before it is produced.
     */
    private boolean available = false;


    /*
     * =========================================================================
     * METHOD: produce()
     * =========================================================================
     *
     * PURPOSE:
     * Places data into the shared buffer.
     *
     * SYNCHRONIZATION:
     * The synchronized keyword requires the calling thread to acquire
     * this SharedBuffer object's monitor before entering the method.
     *
     * Only one thread at a time can hold this object's monitor.
     * This protects the shared buffer state while the method executes.
     *
     * EXECUTION FLOW:
     *
     *   1. Acquire the SharedBuffer object's monitor.
     *
     *   2. Check whether the buffer is already full.
     *
     *   3. If the buffer is full, call wait().
     *      - wait() releases the object's monitor.
     *      - The Producer suspends until it can resume.
     *      - Releasing the monitor allows the Consumer to enter consume().
     *
     *   4. Once the buffer is empty, assign the value 10 to data.
     *
     *   5. Set available to true to indicate that the buffer is full.
     *
     *   6. Print the produced value.
     *
     *   7. Call notifyAll() to wake threads waiting on this object's monitor.
     *
     *   8. Exit the method and release the monitor.
     *
     * IMPORTANT CONCURRENCY CONCEPTS:
     *
     * - wait() must be called while holding the monitor of the object on
     *   which it is invoked. Here, that object is this SharedBuffer instance.
     *
     * - wait() releases the monitor, allowing another thread to enter a
     *   synchronized method on the same object.
     *
     * - notifyAll() wakes waiting threads but does not immediately release
     *   the monitor. A notified thread must reacquire the monitor before
     *   continuing.
     *
     * - The while loop rechecks the condition after the thread resumes.
     *
     * EXCEPTION:
     * InterruptedException is propagated to the caller if the thread is
     * interrupted while waiting.
     * =========================================================================
     */
    public synchronized void produce() throws InterruptedException {

        /*
         * If the buffer is full, the Producer cannot write new data.
         *
         * The while loop repeatedly checks the condition because the buffer
         * state must be verified again whenever the Producer resumes.
         */
        while (available) {

            /*
             * Suspend the Producer and release this object's monitor.
             *
             * This allows the Consumer to acquire the monitor and consume
             * the data already present in the buffer.
             */
            wait();
        }

        /*
         * The buffer is empty, so the Producer can safely place data in it.
         *
         * This example produces the fixed value 10.
         */
        data = 10;

        /*
         * Mark the buffer as full.
         *
         * The Consumer uses this flag to determine whether data is available.
         */
        available = true;

        /*
         * Display the value that has been produced.
         */
        System.out.println("Produced: " + data);

        /*
         * Notify all threads waiting on this object's monitor.
         *
         * A waiting Consumer can now compete to reacquire the monitor and
         * check whether it can consume the produced data.
         *
         * notifyAll() does not release the monitor immediately. The Producer
         * continues until it exits this synchronized method.
         */
        notifyAll();
    }


    /*
     * =========================================================================
     * METHOD: consume()
     * =========================================================================
     *
     * PURPOSE:
     * Retrieves data from the shared buffer and returns the consumed value.
     *
     * SYNCHRONIZATION:
     * The synchronized keyword requires the Consumer to acquire this
     * SharedBuffer object's monitor before entering the method.
     *
     * EXECUTION FLOW:
     *
     *   1. Acquire the SharedBuffer object's monitor.
     *
     *   2. Check whether the buffer is empty.
     *
     *   3. If the buffer is empty, call wait().
     *      - wait() releases the object's monitor.
     *      - The Consumer suspends until it can resume.
     *      - The Producer can acquire the monitor and produce data.
     *
     *   4. Once data is available, copy data into consumedData.
     *
     *   5. Set available to false to indicate that the buffer is empty.
     *
     *   6. Print the consumed value.
     *
     *   7. Call notifyAll() to wake threads waiting on this object's monitor.
     *
     *   8. Return the consumed value.
     *
     * IMPORTANT:
     *
     * The Consumer must not read data until available is true.
     * Otherwise, it could attempt to consume data from an empty buffer.
     *
     * The while loop ensures that the condition is checked again after
     * the Consumer resumes from wait().
     * =========================================================================
     */
    public synchronized int consume() throws InterruptedException {

        /*
         * If the buffer is empty, the Consumer has nothing to consume.
         *
         * The Consumer must wait until the Producer places data in the buffer.
         */
        while (!available) {

            /*
             * Suspend the Consumer and release this object's monitor.
             *
             * Releasing the monitor allows the Producer to enter produce(),
             * place data in the buffer, and notify waiting threads.
             */
            wait();
        }

        /*
         * The buffer contains data, so copy its value into a local variable.
         *
         * This local variable represents the value being consumed.
         */
        int consumedData = data;

        /*
         * Mark the buffer as empty because its data has now been consumed.
         *
         * This allows the Producer to produce another item if it calls
         * produce() again.
         */
        available = false;

        /*
         * Display the value consumed by the Consumer.
         */
        System.out.println("Consumed: " + consumedData);

        /*
         * Notify all threads waiting on this object's monitor.
         *
         * For example, a waiting Producer can wake up and check whether
         * the buffer is now empty.
         *
         * The Producer must reacquire the monitor before continuing.
         */
        notifyAll();

        /*
         * Return the consumed value to the calling thread.
         */
        return consumedData;
    }

}

