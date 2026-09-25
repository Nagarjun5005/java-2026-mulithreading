package thread.constructor;


/**
 * This class represents the TASK that will be executed by a Thread.
 *
 * IMPORTANT:
 *
 * EmailNotificationTask implements Runnable.
 *
 * Runnable represents:
 *
 *      WHAT work should be performed?
 *
 * Thread represents:
 *
 *      WHICH execution mechanism will perform that work?
 *
 *
 * The relationship is:
 *
 *      Runnable Task
 *           ↓
 *      Thread
 *           ↓
 *        start()
 *           ↓
 *        run()
 *
 *
 * This class itself is NOT a Thread.
 */
public class EmailNotificationTask implements Runnable {


    /*
     * The Order object contains the information required
     * by this task.
     *
     * For example:
     *
     *      Order ID
     *      Customer Name
     *
     * The task uses this object when sending the notification.
     */
    private Order order;


    /**
     * Constructor of EmailNotificationTask.
     *
     * The Order object is passed into the task when the task
     * is created.
     *
     * Example:
     *
     *      Order order = new Order(1, "Nagarjun");
     *
     *      EmailNotificationTask task =
     *              new EmailNotificationTask(order);
     *
     *
     * IMPORTANT:
     *
     * Creating this EmailNotificationTask object does NOT
     * create a new Thread.
     *
     * It only creates the TASK.
     */
    public EmailNotificationTask(Order order) {
        this.order = order;
    }


    /**
     * run() contains the actual work that this Runnable performs.
     *
     * The Thread will execute this method after:
     *
     *      thread.start();
     *
     *
     * IMPORTANT:
     *
     * Calling run() directly:
     *
     *      task.run();
     *
     * does NOT create a new Thread.
     *
     * It would simply execute this method on the current thread.
     *
     *
     * When using:
     *
     *      Thread thread = new Thread(task);
     *      thread.start();
     *
     * the new Thread will execute this run() method.
     */
    @Override
    public void run() {

        /*
         * Thread.currentThread()
         *
         * Returns the Thread that is currently executing this code.
         *
         * getName()
         *
         * Returns the name of that Thread.
         *
         *
         * For example, if we create the Thread as:
         *
         *      Thread emailThread =
         *              new Thread(task, "Email-Thread");
         *
         * then this will print:
         *
         *      Running on: Email-Thread
         */
        System.out.println(
                "Running on: " +
                        Thread.currentThread().getName()
        );


        /*
         * This represents the actual task performed by the
         * Runnable.
         *
         * The new Thread executes this code when its start()
         * method is called.
         */
        System.out.println(
                "Email Notification sent " +
                        order.getCustomerName()
        );
    }
}