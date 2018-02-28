package seneca.structgen.sa.adaptive;

import org.apache.log4j.Logger;
import seneca.structgen.annealinglog.CommonAnnealingLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * An annealing engine implementation based on the paper by Vincent A. Cicirello (On the Design of
 * an Adaptive Simulated Annealing Algorithm).
 *
 * @author maclean
 */
public class AdaptiveAnnealingEngine implements AnnealingEngineI {

    public final static int DEFAULT_EVALSMAX = 1000;
    private final Random rand = new Random();
    private final ArrayList<TemperatureListener> temperatureListeners;
    private int evalSMax;
    private double currentTemperature = 0d;
    private AnnealerAdapterI annealerAdapter;
    private boolean stopRunning = false;
    private boolean maxFitnessReached = false;
    private CommonAnnealingLog annealingLog = null;
    int iteration = 1;
    boolean isFinished = false;
    private Logger logger = null;
    private DecimalFormat formatter = new DecimalFormat("#.####");

    public AdaptiveAnnealingEngine(MoleculeAnnealerAdapter annealerAdapter, int evalSMax, CommonAnnealingLog annealLog, Logger logger) {
        this.annealerAdapter = annealerAdapter;
        this.evalSMax = evalSMax;
        this.temperatureListeners = new ArrayList<TemperatureListener>();
        this.annealingLog = annealLog;
        this.logger = logger;
    }

    
    public void addTemperatureListener(TemperatureListener listener) {
        this.temperatureListeners.add(listener);
    }

    /*
    * This implementation is near-identical to the pseudocode of Figure 2.
    *
    */
    public void run() {
        System.out.println("Running ASA");
        annealerAdapter.initialState();

        double t = 0.5;
        double acceptRate = 0.5;
        for (int i = 1; i < evalSMax; i++) {
            iteration++;
            if (annealerAdapter.isCancelled()) {
                return;
            } else {
                annealerAdapter.nextState();
            }

            // the normal annealing process
            if (annealerAdapter.costDecreasing()) {
                annealerAdapter.accept();
                acceptRate = (1.0 / 500.0) * (499.0 * (acceptRate + 1));
            } else {
                double r = rand.nextDouble();
                if (r < Math.pow(Math.E, (annealerAdapter.costDifference() / t))) {
                    annealerAdapter.accept();
                    acceptRate = (1.0 / 500.0) * (499.0 * (acceptRate + 1));
                } else {
                    annealerAdapter.reject();
                    acceptRate = (1.0 / 500.0) * (499.0 * (acceptRate));
                }
            }

            // calculate the lambda rate
            double lamRate = 0.0;
            if (i / evalSMax < 0.15) {
                lamRate = 0.44 + 0.56 * Math.pow(560, -i / evalSMax / 0.15);
            } else if (i / evalSMax >= 0.15 && i / evalSMax < 0.65) {
                lamRate = 0.44;
            } else if (0.65 <= i / evalSMax) {
                lamRate = 0.44 * Math.pow(440, -(i / evalSMax - 0.65) / 0.15);
            }

            // use the lambda to adjust the temperature
            if (acceptRate > lamRate) {
                t = 0.999 * t;
            } else {
                t = t / 0.999;
            }
            fireTemperatureEvent(t);
            if (stopRunning || maxFitnessReached) {
                isFinished = true;
                break;
            }
            if (iteration == evalSMax) {
                isFinished = true;
            }
            updateLog();
        }

    }

    private void fireTemperatureEvent(double t) {
        for (TemperatureListener listener : this.temperatureListeners) {
            listener.temperatureChange(t);
            currentTemperature = t;
        }
    }

    
    public void setAnnealerAdapter(AnnealerAdapterI adapter) {
        this.annealerAdapter = adapter;
    }

    
    public void setShouldStop(boolean value) {
        stopRunning = value;
    }

    
    public long getIterations() {
        return iteration;
    }

    
    public boolean isFinished() {
        return isFinished;
    }

    private double lastRecordedBestScore = 0d;
    private boolean firstEntry = true;

    void updateLog() {
        if (firstEntry) {
            lastRecordedBestScore = annealerAdapter.getBestAnnealScore();
            annealingLog.addEntry((double) iteration, (double) currentTemperature, annealerAdapter.getBestAnnealScore());
            firstEntry = false;
        } else if (annealerAdapter.getBestAnnealScore() > lastRecordedBestScore) {
            lastRecordedBestScore = annealerAdapter.getBestAnnealScore();
            if (lastRecordedBestScore == 1.0) {
                maxFitnessReached = true;
            }
            annealingLog.addEntry((double) iteration, (double) currentTemperature, lastRecordedBestScore);
            logger.info(iteration + ";" + formatter.format(currentTemperature) + ";" + formatter.format(lastRecordedBestScore) + annealerAdapter.getBestScoreSummary().allJudgeScores);
            // System.out.println("UPDATE LOG: " + iteration + " - " + currentTemperature + " - " + annealerAdapter.getBestAnnealScore());
        }
    }

    
    public CommonAnnealingLog getUpdatedAnnealingLog() {
        return annealingLog;
    }
}
