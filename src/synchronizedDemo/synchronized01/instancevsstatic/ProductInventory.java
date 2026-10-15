package synchronizedDemo.synchronized01.instancevsstatic;

/**
 * Demonstrates INSTANCE synchronization vs STATIC synchronization.
 *
 * INSTANCE synchronization:
 *     Protects operations using a particular object's monitor.
 *
 * STATIC synchronization:
 *     Protects operations using the class object's monitor.
 *
 * In this example:
 *     inventory1 and inventory2 have separate stock values.
 *     totalOrders is shared across all ProductInventory objects.
 */
public class ProductInventory {

    /**
     * STATIC SHARED DATA
     *
     * Because totalOrders is static, there is only one
     * totalOrders variable shared by all ProductInventory objects.
     *
     * inventory1 and inventory2 both access this same variable.
     *
     * It is mutable because its value changes when an order
     * is successfully placed.
     *
     * Multiple threads can update it, so we protect increments
     * using a static synchronized method.
     */
    private static int totalOrders = 0;


    /**
     * INSTANCE DATA
     *
     * Because stock is not static, every ProductInventory
     * object has its own stock variable.
     *
     * Example:
     *
     * inventory1 -> stock = 5
     * inventory2 -> stock = 5
     *
     * Updating inventory1.stock does not directly change
     * inventory2.stock.
     */
    private int stock;


    /**
     * CONSTRUCTOR
     *
     * The constructor initializes the stock for each object.
     *
     * this.stock refers to the instance variable belonging
     * to the object currently being created.
     *
     * The parameter stock contains the value passed by the caller.
     */
    public ProductInventory(int stock) {
        this.stock = stock;
    }


    /**
     * INSTANCE SYNCHRONIZED METHOD
     *
     * The synchronized keyword on an instance method means
     * Java acquires the intrinsic monitor of the current object:
     *
     *     synchronized (this)
     *
     * If Thread-1 calls inventory1.purchase() and Thread-2
     * also calls inventory1.purchase(), they compete for
     * inventory1's monitor.
     *
     * Only one can execute this synchronized method on
     * inventory1 at a time.
     *
     * However, a thread calling inventory2.purchase() uses
     * inventory2's monitor, which is a different monitor.
     */
    public synchronized void purchase() {

        /**
         * Check whether this inventory object has stock available.
         *
         * Because the method is synchronized, other threads
         * cannot enter this same object's synchronized purchase()
         * method while the current thread holds its monitor.
         */
        if (stock > 0) {

            /**
             * Decrement this object's stock.
             *
             * For inventory1, this changes inventory1.stock.
             * For inventory2, this changes inventory2.stock.
             */
            stock--;

            /**
             * Display the current thread and remaining stock.
             *
             * Thread.currentThread() returns the thread that is
             * currently executing this code.
             *
             * getName() returns that thread's name.
             *
             * Output order may vary because threads execute concurrently.
             */
            System.out.println(
                    Thread.currentThread().getName()
                            + " Purchased Product --> Remaining Stock "
                            + stock
            );

            /**
             * Call the static synchronized method to update
             * the shared totalOrders variable.
             *
             * The current thread already holds this inventory
             * object's monitor. The static method additionally
             * requires the class object's monitor.
             */
            incrementTotalOrders();

        } else {

            /**
             * If stock is zero, the purchase cannot succeed.
             *
             * totalOrders is not incremented because no purchase
             * was completed.
             */
            System.out.println(
                    Thread.currentThread().getName()
                            + " -> Product out of stock"
            );
        }

        /*
         * When purchase() finishes, its instance monitor is released.
         *
         * Java releases the monitor automatically, including if
         * the method exits because an exception is thrown.
         */
    }


    /**
     * STATIC SYNCHRONIZED METHOD
     *
     * This method updates the single shared totalOrders variable.
     *
     * Because the method is static, it does not lock "this".
     *
     * Instead, Java acquires the intrinsic monitor associated with:
     *
     *     ProductInventory.class
     *
     * All threads calling this method compete for that same
     * class monitor, regardless of which inventory object
     * they were working with.
     *
     * This protects totalOrders++ from concurrent lost updates
     * among calls that use this same class monitor.
     */
    private static synchronized void incrementTotalOrders() {

        // Increment the shared total order count.
        totalOrders++;

        // Display the updated count.
        System.out.println(
                "Total orders: " + totalOrders
        );

        /*
         * The class monitor is released when this method returns.
         *
         * The caller's instance monitor remains held until
         * purchase() finishes.
         */
    }


    /**
     * STATIC GETTER
     *
     * Returns the shared totalOrders value.
     *
     * This getter is not synchronized. In this example, it is
     * called after all worker threads have finished using join().
     *
     * The joins establish the necessary happens-before
     * relationship for the main thread to observe their completed
     * actions.
     */
    public static int getTotalOrders() {
        return totalOrders;
    }


    /**
     * STATIC SETTER
     *
     * Allows the shared totalOrders value to be changed.
     *
     * Note: this setter is not synchronized. If it were called
     * concurrently with increments, it could cause race conditions.
     * It is not needed by this demonstration.
     */
    public static void setTotalOrders(int totalOrders) {
        ProductInventory.totalOrders = totalOrders;
    }


    /**
     * INSTANCE GETTER
     *
     * Returns the stock of this particular inventory object.
     *
     * This getter is not synchronized. In this example, it is
     * called after join() confirms that all worker threads have
     * completed.
     */
    public int getStock() {
        return stock;
    }


    /**
     * INSTANCE SETTER
     *
     * Changes the stock of this particular object.
     *
     * This setter is not synchronized. If it is used concurrently
     * with purchase(), it can bypass the protection provided by
     * the synchronized purchase() method.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }


    /**
     * MAIN METHOD
     *
     * Creates two inventory objects and three worker threads.
     *
     * The goal is to demonstrate:
     *
     * 1. Two threads competing for the same object's monitor.
     * 2. A thread using a different object's monitor.
     * 3. All successful purchases updating the same static counter.
     */
    public static void main(String[] args) throws InterruptedException {

        /**
         * Create two separate ProductInventory objects.
         *
         * Each object starts with stock = 5.
         *
         * inventory1.stock = 5
         * inventory2.stock = 5
         *
         * Both objects share the same static totalOrders variable.
         */
        ProductInventory inventory1 = new ProductInventory(5);
        ProductInventory inventory2 = new ProductInventory(5);


        /**
         * TASK 1
         *
         * Runnable represents work to execute; it does not
         * itself create a thread.
         *
         * This task attempts five purchases against inventory1.
         */
        Runnable task1 = () -> {
            for (int i = 0; i < 5; i++) {
                inventory1.purchase();
            }
        };


        /**
         * TASK 2
         *
         * This task also attempts five purchases against inventory1.
         *
         * When task1 and task2 run on separate threads, they compete
         * for inventory1's monitor because purchase() is an
         * instance synchronized method.
         *
         * Since inventory1 starts with only five units, at most
         * five purchases across these two tasks can succeed.
         */
        Runnable task2 = () -> {
            for (int i = 0; i < 5; i++) {
                inventory1.purchase();
            }
        };


        /**
         * TASK 3
         *
         * This task attempts five purchases against inventory2.
         *
         * inventory2 is a different object, so purchase() uses
         * inventory2's monitor rather than inventory1's monitor.
         *
         * Therefore, instance synchronization on inventory1
         * does not itself block this task from entering
         * inventory2.purchase().
         *
         * The static synchronized method still uses the same
         * ProductInventory.class monitor across both objects.
         */
        Runnable task3 = () -> {
            for (int i = 0; i < 5; i++) {
                inventory2.purchase();
            }
        };


        /**
         * CREATE THREADS
         *
         * Each Thread is associated with its own Runnable task.
         *
         * Thread-1 -> task1 -> inventory1
         * Thread-2 -> task2 -> inventory1
         * Thread-3 -> task3 -> inventory2
         *
         * At this point, the Thread objects have been created
         * but have not started executing.
         */
        Thread thread1 = new Thread(task1, "Thread-1");
        Thread thread2 = new Thread(task2, "Thread-2");
        Thread thread3 = new Thread(task3, "Thread-3");


        /**
         * START THREADS
         *
         * start() initiates each thread's independent execution.
         *
         * The scheduler determines when each thread runs.
         * We cannot assume Thread-1 always runs first.
         */
        thread1.start();
        thread2.start();
        thread3.start();


        /**
         * WAIT FOR THREAD COMPLETION
         *
         * join() makes the main thread wait for each worker
         * thread to finish.
         *
         * It does not protect shared variables by itself.
         * Synchronization is provided by the synchronized methods.
         *
         * Waiting for all three threads ensures that the results
         * are printed after their purchase tasks finish.
         */
        thread1.join();
        thread2.join();
        thread3.join();


        /**
         * FINAL RESULTS
         *
         * inventory1:
         *     Two tasks attempted 10 purchases against stock = 5.
         *     Five purchases succeed; final stock = 0.
         *
         * inventory2:
         *     One task attempted five purchases against stock = 5.
         *     Five purchases succeed; final stock = 0.
         *
         * totalOrders:
         *     Shared across both inventory objects.
         *     Five successful purchases from inventory1 and five
         *     from inventory2 produce a total of 10.
         *
         * The order of earlier log messages may vary, but the
         * final values should be the same for this program.
         */
        System.out.println(
                "\nInventory 1 remaining stock: "
                        + inventory1.getStock()
        );

        System.out.println(
                "Inventory 2 remaining stock: "
                        + inventory2.getStock()
        );

        System.out.println(
                "Total orders: "
                        + ProductInventory.getTotalOrders()
        );
    }
}

