package seneca.structgen.ea;

import seneca.judges.ChiefJustice;

public interface EvolvingEngine {

    public void run();

    public void stopEvolving();

    public boolean isFinished();

    public int getGenerations();

    public void initAnnealing(Population population, ChiefJustice justice);
}