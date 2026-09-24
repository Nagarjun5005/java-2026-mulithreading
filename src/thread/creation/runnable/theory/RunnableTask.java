package thread.creation.runnable.theory;

/**
 * RunnableTask demonstrates how to define a task using the Runnable interface.
 *
 * IMPORTANT CONCEPT:
 *
 * Runnable does NOT create a thread.
 *
 * Runnable represents the TASK / WORK that we want a thread to execute.
 *
 * Think of it as:
 *
 *      Runnable  →  WHAT work should be done?
 *      Thread    →  WHICH thread will execute the work?
 */
public class RunnableTask implements Runnable {

    /**
     * The run() method contains the actual task that should be executed
     * by a thread.
     *
     * IMPORTANT:
     *
     * Calling run() directly:
     *
     *      task.run();
     *
     * does NOT create a new thread.
     *
     * The run() method executes on the current thread.
     *
     * A new thread is created when we use:
     *
     *      Thread thread = new Thread(task);
     *      thread.start();
     *
     * The start() method will eventually cause this run() method
     * to execute on the newly created thread.
     */
    @Override
    public void run() {

        // This is the actual work/task performed by the thread.
        System.out.println("Running the thread using Runnable");
    }
}