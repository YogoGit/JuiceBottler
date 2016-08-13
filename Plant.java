public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;

    public static void main(String[] args) {
        // Startup a single plant
        Plant p = new Plant(1);
        p.startPlant();

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        p.stopPlant();
        p.waitToStop();

        // Summarize the results
        System.out.println("Total processed = " + p.getOranges());
        System.out.println("Created " + p.getBottles() +
                           ", wasted " + p.getWaste() + " oranges");
    }

    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    public final int ORANGES_PER_BOTTLE = 3;

    private final Thread thread;
    private volatile boolean timeToWork;
    private volatile int orangesProcessed;

    Plant(int plantNum) {
        thread = new Thread(this, "PlantNum[" + plantNum + "]");
        orangesProcessed = 0;
    }

    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    public void stopPlant() {
        timeToWork = false;
    }

    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " stop malfunction");
        }
    }

    public void run() {
        System.out.print(Thread.currentThread().getName() + " Processing oranges");
        Worker worker = new Worker(this);
        while (timeToWork) {
            worker.processEntireOrange(new Orange());
            System.out.print(".");
        }
        System.out.println("");
    }

    public void completeOrange(Orange o) {
        // Do one final check on the orange
        if (o.getState() == Orange.State.Bottled) {
           o.runProcess();
           orangesProcessed++;
        }
    }

    public int getOranges() {
        return orangesProcessed;
    }

    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}
