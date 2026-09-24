package thread.creation.runnable.ecommerce;


/**
 * This class represents the main application that processes an order.
 *
 * This example demonstrates how a Runnable task can be given to a Thread.
 *
 * The overall flow is:
 *
 *     Order created
 *          ↓
 *     Order saved
 *          ↓
 *     Create Runnable task
 *          ↓
 *     Give task to Thread
 *          ↓
 *     Start Thread
 *          ↓
 *     EmailNotificationTask.run()
 *
 */
public class OrderApplication {

    public static void main(String[] args) {

        /*
         * The main() method is executed by the MAIN THREAD.
         *
         * When the Java application starts, the JVM creates
         * the main thread to execute main().
         *
         * At this point:
         *
         *     Main Thread
         *          ↓
         *     executes main()
         */
        System.out.println("Processing order...");


        /*
         * Create an Order object.
         *
         * This is just a normal Java object.
         *
         * It contains information about the order,
         * such as:
         *
         *     Order ID     → 1
         *     Customer    → Nagarjun
         *
         * No new thread is created here.
         */
        Order order = new Order(1, "Nagarjun");


        /*
         * Simulate saving the order.
         *
         * This code is currently being executed by the
         * MAIN THREAD.
         */
        System.out.println("Order saved successfully.");


        /*
         * Create the Runnable task.
         *
         * EmailNotificationTask implements Runnable.
         *
         * Therefore, this object represents:
         *
         *     "The task of sending an email notification."
         *
         * IMPORTANT:
         *
         * Creating this object does NOT create a new thread.
         *
         * We are only creating the TASK.
         *
         * Think:
         *
         *     Runnable → WHAT should be done?
         *
         * Answer:
         *
         *     Send an email notification.
         */
        EmailNotificationTask emailNotificationTask =
                new EmailNotificationTask(order);


        /*
         * Create a Thread and give it the Runnable task.
         *
         * Here:
         *
         *     emailNotificationTask → TASK
         *
         *     emailThread          → THREAD
         *
         * The Thread now knows which Runnable task
         * it should execute.
         *
         * IMPORTANT:
         *
         * Creating the Thread object does NOT start
         * the thread yet.
         *
         * We have:
         *
         *     Main Thread
         *          +
         *     Email Thread (not started yet)
         */
        Thread emailThread = new Thread(emailNotificationTask);


        /*
         * Start the email thread.
         *
         * start() tells the JVM to start a NEW thread.
         *
         * The new thread will eventually execute:
         *
         *     emailNotificationTask.run()
         *
         * Therefore:
         *
         *     emailThread.start()
         *              ↓
         *        New Thread starts
         *              ↓
         *        run() executes
         *              ↓
         *        Email notification is sent
         *
         * IMPORTANT:
         *
         * We use start(), NOT run().
         *
         * Calling:
         *
         *     emailNotificationTask.run();
         *
         * directly would simply execute run() on the
         * current thread (main thread).
         */
        emailThread.start();


        /*
         * This statement belongs to the MAIN THREAD.
         *
         * An important point:
         *
         * The main thread does NOT necessarily wait for
         * emailThread to finish before executing this line.
         *
         * Therefore, the output order can vary.
         *
         * For example, we may see:
         *
         *     Processing order...
         *     Order saved successfully.
         *     order processing completed!
         *     Email Notification sent Nagarjun
         *
         * OR:
         *
         *     Processing order...
         *     Order saved successfully.
         *     Email Notification sent Nagarjun
         *     order processing completed!
         *
         * The exact order depends on thread scheduling.
         */
        System.out.println("order processing completed!");
    }
}