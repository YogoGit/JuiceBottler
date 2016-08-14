public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;

    private static final int NUM_PLANTS = 1;

    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
           plants[i] = new Plant(i + 1);
           plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
           p.stopPlant();
           p.waitToStop();
        }

        // Summarize the results
        int oranges = 0;
        int bottles = 0;
        int waste = 0;
        for (Plant p : plants) {
            oranges += p.getOranges();
            bottles += p.getBottles();
            waste += p.getWaste();
        }
        System.out.println("Total processed = " + oranges);
        System.out.println("Created " + bottles +
                           ", wasted " + waste + " oranges");
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

    private static final int NUM_WORKERS = 3;

    private final Thread[] workers;
    private final int plantNum;
    private volatile boolean timeToWork;
    private volatile int orangesProcessed;

    Plant(int plantNum) {
        this.plantNum = plantNum;
        workers = new Thread[NUM_WORKERS];
        orangesProcessed = 0;
    }

    public void startPlant() {
        timeToWork = true;
        for (int i = 0; i < NUM_WORKERS; i++) {
            workers[i] = new Thread(this, "PlantNum[" + plantNum + "][" + (i + 1) + "]");
            workers[i].start();
        }
    }

    public void stopPlant() {
        timeToWork = false;
    }

    public void waitToStop() {
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                System.err.println(worker.getName() + " stop malfunction");
            }
        }
    }

    // This is run once per worker
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
