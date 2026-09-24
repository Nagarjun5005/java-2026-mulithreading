package thread.creation.runnable.theory;

public class Main {

    public static void main(String[] args) {

        /*
         * STEP 1: Create the Runnable object
         *
         * RunnableTask contains the TASK that we want to execute.
         *
         * At this point:
         *
         *     - RunnableTask object is created
         *     - NO new thread is created
         *     - main thread is still the only executing thread
         *
         * Think:
         *
         *     RunnableTask = WHAT should be done?
         */
        RunnableTask runnableTask = new RunnableTask();


        /*
         * STEP 2: Create a Thread object and give it the Runnable task
         *
         * Here we are creating a Thread object and passing our
         * RunnableTask to it.
         *
         * The Thread now knows:
         *
         *     "When you start me, execute this Runnable's run() method."
         *
         * IMPORTANT:
         *
         * Creating the Thread object does NOT start the thread.
         *
         * At this point:
         *
         *     Main Thread
         *          +
         *     Thread object (not running yet)
         */
        Thread th = new Thread(runnableTask);


        /*
         * STEP 3: Start the thread
         *
         * start() tells the JVM that this Thread should begin execution.
         *
         * A new thread is started.
         *
         * The new thread will execute:
         *
         *     RunnableTask.run()
         *
         * So now we have:
         *
         *     Main Thread
         *          |
         *          |---- executes main()
         *
         *     Worker Thread
         *          |
         *          |---- executes RunnableTask.run()
         */
        th.start();
    }
}