public class Worker {
    private final Plant plant;

    public Worker(Plant plant) {
        this.plant = plant;
    }

    public void processEntireOrange(Orange o) {
        do {
            processOrange(o);
        } while (o.getState() != Orange.State.Bottled);
        plant.completeOrange(o);
    }

    public void processOrange(Orange o) {
        o.runProcess();
    }
}
