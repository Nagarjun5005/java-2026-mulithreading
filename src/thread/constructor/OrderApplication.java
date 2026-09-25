package thread.constructor;


/**
 * This class demonstrates:
 *
 * 1. Creating a Runnable task
 * 2. Creating a Thread using a Runnable
 * 3. Using the Thread constructor
 * 4. Giving a Thread a name
 * 5. Starting the Thread
 * 6. Checking which Thread is currently executing
 *
 *
 * IMPORTANT CONCEPT:
 *
 * In this example, the TASK and THREAD are separate.
 *
 *      EmailNotificationTask
 *              ↓
 *          Runnable
 *              ↓
 *          Represents the TASK
 *
 *              +
 *
 *          Thread
 *              ↓
 *       Executes the TASK
 *
 *
 * The basic flow is:
 *
 *      Create Task
 *          ↓
 *      Create Thread(task, name)
 *          ↓
 *      start()
 *          ↓
 *      Thread executes run()
 */
public class OrderApplication {

    public static void main(String[] args) {

        /*
         * main() is executed by the MAIN THREAD.
         *
         * Thread.currentThread()
         *      ↓
         * Returns the thread that is currently executing this code.
         *
         * getName()
         *      ↓
         * Returns the name of that thread.
         *
         * The main thread normally has the name:
         *
         *      main
         */
        System.out.println(
                "Running on: " +
                        Thread.currentThread().getName()
        );


        /*
         * This code is currently being executed by
         * the MAIN THREAD.
         */
        System.out.println("Processing order...");


        /*
         * Create an Order object.
         *
         * This is a normal Java object.
         *
         * No new Thread is created here.
         */
        Order order = new Order(1, "Nagarjun");


        /*
         * Simulate saving the order.
         *
         * This code is still being executed by the
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
         *      WHAT work should be performed?
         *
         * Answer:
         *
         *      Send an email notification.
         *
         *
         * IMPORTANT:
         *
         * Creating the Runnable object does NOT create
         * a new Thread.
         *
         * At this point we only have:
         *
         *      Runnable Task
         *
         *      No new Thread yet.
         */
        EmailNotificationTask emailNotificationTask =
                new EmailNotificationTask(order);


        /*
         * Create a Thread using the Runnable task.
         *
         * This is an important Thread constructor:
         *
         *      Thread(Runnable task, String name)
         *
         *
         * First argument:
         *
         *      emailNotificationTask
         *
         *      ↓
         *      The WORK/TASK that the Thread should execute.
         *
         *
         * Second argument:
         *
         *      "Email-Thread"
         *
         *      ↓
         *      The name given to this Thread.
         *
         *
         * Therefore:
         *
         *      Thread
         *       ├── Task → emailNotificationTask
         *       └── Name → Email-Thread
         *
         *
         * IMPORTANT:
         *
         * Creating the Thread object does NOT start
         * the Thread.
         *
         * The Thread is only created at this point.
         */
        Thread emailThread =
                new Thread(
                        emailNotificationTask,
                        "Email-Thread"
                );


        /*
         * Start the Thread.
         *
         * start() tells the JVM to begin execution
         * of the new Thread.
         *
         *
         * The new Thread will eventually execute:
         *
         *      emailNotificationTask.run()
         *
         *
         * The execution flow becomes:
         *
         *      emailThread.start()
         *              ↓
         *      New Thread starts
         *              ↓
         *      run() executes
         *              ↓
         *      Email notification is sent
         *
         *
         * The new Thread has the name:
         *
         *      Email-Thread
         */
        emailThread.start();


        /*
         * This statement is executed by the MAIN THREAD.
         *
         * IMPORTANT:
         *
         * Calling start() does not mean that the main
         * thread must wait for Email-Thread to finish.
         *
         * Both threads can now execute independently.
         *
         * Therefore, the exact output order can vary.
         *
         * For example:
         *
         *      Order processing completed!
         *      Email Notification sent Nagarjun
         *
         * OR:
         *
         *      Email Notification sent Nagarjun
         *      Order processing completed!
         *
         *
         * This happens because the JVM/OS scheduler determines
         * when the runnable thread gets CPU execution time.
         */
        System.out.println("Order processing completed!");
    }
}