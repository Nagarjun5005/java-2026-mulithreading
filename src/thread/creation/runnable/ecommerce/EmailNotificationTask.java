package thread.creation.runnable.ecommerce;

/**
 * This class represents a TASK that needs to be executed.
 *
 * In this example, the task is:
 *
 *     "Send an email notification to the customer"
 *
 * IMPORTANT:
 *
 * This class implements Runnable.
 *
 * Runnable represents the WORK/TASK.
 * It does NOT represent the actual Thread.
 *
 * Think of it as:
 *
 *     Runnable → WHAT should be done?
 *     Thread   → WHICH thread will execute it?
 */
public class EmailNotificationTask implements Runnable {

    /*
     * The Order object contains the information required
     * to send the email notification.
     *
     * For example:
     *
     *     order.getCustomerName()
     *     order.getOrderId()
     *
     * This variable is shared with this Runnable task.
     */
    private Order order;


    /**
     * Constructor
     *
     * We receive the Order object when creating the task.
     *
     * Example:
     *
     *     Order order = new Order(...);
     *
     *     EmailNotificationTask task =
     *             new EmailNotificationTask(order);
     *
     * The task now has access to the order information.
     */
    public EmailNotificationTask(Order order) {
        this.order = order;
    }


    /**
     * run() contains the actual WORK/TASK that should be performed.
     *
     * IMPORTANT:
     *
     * The run() method itself does NOT create a new thread.
     *
     * The Thread will execute this method when we call:
     *
     *     thread.start();
     *
     * Execution flow:
     *
     *     EmailNotificationTask
     *             ↓
     *          run()
     *             ↓
     *     Send email notification
     */
    @Override
    public void run() {

        /*
         * This represents the actual task.
         *
         * When this Runnable is given to a Thread and the Thread
         * is started, this code will execute on the new thread.
         *
         * Example:
         *
         *     Thread emailThread = new Thread(task);
         *     emailThread.start();
         *
         * Then:
         *
         *     emailThread
         *          ↓
         *     task.run()
         *          ↓
         *     Email notification
         */
        System.out.println(
                "Email Notification sent " + order.getCustomerName()
        );
    }
}