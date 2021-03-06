/*
 *  SAStochasticGenerator.java
 *
 *  Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  accompanying this package.
 */
package seneca.structgen.sa.regular;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import seneca.judges.Judge;
import seneca.judges.ScoreSummary;
import seneca.structgen.StructureGenerator;
import seneca.structgen.StructureGeneratorResult;
import seneca.structgen.StructureGeneratorStatus;
import seneca.structgen.annealinglog.CommonAnnealingLog;

import java.text.DecimalFormat;

/**
 * A Stochatic Structure Generator whose convergence is driven by Simulated Annealing as suggested
 * in Faulon, J.-L. Stochastic Generator of Chemical Structure. 2. Using Simulated Annealing To
 * Search the Space of Constitutional Isomers. Journal of Chemical Information and Computer Sciences
 * 1996, 36, 731-740.
 *
 * @author steinbeck @created September 10, 2001
 */
public class SAStochasticGenerator extends StructureGenerator {

    private static final long serialVersionUID = 1L;
    protected ScoreSummary bestScore = null;
    protected ScoreSummary recentScore = null;
    protected ScoreSummary lastScore = null;
    protected long iteration = 0;
    RandomGenerator randomGent = null;
    ConvergenceAnnealingEngine annealingEngine = null;
    CommonAnnealingLog annealingLog = null;
    int alStepsize = 0;
    IAtomContainer lastStructure = null;
    IAtomContainer bestStructure = null;
    int lastAnnealingCounter = 0;
    private Thread structGenThread = null;
    boolean debug = false;
    private boolean hasStarted = false;
    private boolean updatedWithZero = false;
    private DecimalFormat formatter = new DecimalFormat("#.####");

    public SAStochasticGenerator() {
        super();
        name = "SAStochasticGenerator";
        annealingLog = new CommonAnnealingLog();
        annealingLog.addEntry(0d, 0d, 0d);
        alStepsize = annealingLog.getIterationAxisStepsize();
    }

    /**
     * Creates a summary on the current status of the calculation and returns is
     */
    
    public Object getStatus() throws java.io.IOException {
        StructureGeneratorStatus sgs = new StructureGeneratorStatus();
        Double[] entry;
        sgs.annealingLog = new CommonAnnealingLog();
        if (!updatedWithZero) {
            sgs.annealingLog.addEntry(0d, 0d, 0d);
            updatedWithZero = true;
        }
        int currentCount = this.annealingLog.getTotalEntriesCountIn(0);
        for (int f = lastAnnealingCounter; f < currentCount; f++) {
            entry = this.annealingLog.getEntry(0, f);
            sgs.annealingLog.addEntry(entry[0], entry[1], entry[2]);
        }
        if (lastAnnealingCounter != 0 && lastAnnealingCounter == currentCount) {
            entry = this.annealingLog.getEntry(0, lastAnnealingCounter - 1);
            sgs.annealingLog.addEntry(entry[0], entry[1], entry[2]);
        }
        lastAnnealingCounter = currentCount;

        sgs.molecularFormula = molecularFormula;
        sgs.datasetName = this.datasetName;

        if (annealingEngine != null) {
            sgs.iteration = annealingEngine.getIterations();
            sgs.bestStructure = bestStructure;
            sgs.bestEvaluation = bestScore;
            if (getStructGenThread() == null && !hasStarted) {
                sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.IDLE];
            } else {
                sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.RUNNING];
            }
            if (stopRunning) {
                sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.STOPPED];
            } else if (hasStarted && annealingEngine.isFinished()) {
                sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.FINISHED];
            }
        }
        sgs.timeTaken = timeTakenSoFar();

        return sgs;
    }


    public Thread getStructGenThread() {
        return structGenThread;
    }

    
    public void start() {

        if (structGenThread == null) {
            structGenThread = new Thread(this, getName());
            hasStarted = true;
            structGenThread.start();
            structGenLogger.info("started structure generation");
        }
    }

    public void stop() {
        if (running) {
            this.stopRunning = true;
            structGenLogger.info("stopped structure generation");
        }
    }

    
    public void run() {

        Thread.currentThread();
        try {
            execute();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        structGenThread = null;

    }

    
    public void execute() throws Exception {
        long start = System.currentTimeMillis();
        startTime = start;
        running = true;
        IAtomContainer result = null;
        IAtomContainer startMolecule = null;
        lastScore = new ScoreSummary(0, "nop");
        bestScore = new ScoreSummary(0, "nop");
        initiateProgressLogger();

        extractAnnealingEngine();
        startMolecule = generateSingleRandomStructure();

        randomGent = new RandomGenerator(startMolecule);
        annealingEngine.initAnnealing(randomGent, chiefJustice);
        //    calculateFinalMaxScore();
        structGenLogger.info("Running Simulated annealing.");
        do {
            try {
                if (!stopRunning) {

                    result = randomGent.proposeStructure();
                    Thread.yield();

                    recentScore = chiefJustice.getScoreByThreading(result);
                    Thread.yield();
                    if (annealingEngine.isAccepted(recentScore.score,
                            lastScore.score)) {
                        randomGent.acceptStructure();
                        lastScore = recentScore;
                    }
                    Thread.yield();
                    if (recentScore.score > bestScore.score) {
                        try {
                            bestScore = recentScore;
                            bestStructure = (IAtomContainer) result.clone();
                            bestStructure.setProperty("Score", decimalFormat.format(bestScore.costValue));
                            structureGeneratorResult.structures.push(bestStructure);
                            annealingEngine.setConvergenceCounter(0);

                        } catch (CloneNotSupportedException ex) {
                            structGenLogger.error(ex.getMessage());
                        }
                    }
                    if (bestScore.costValue == 1.0) {
                        annealingEngine.setMaxFitnessReached(true);
                        structGenLogger.info("Maximum fitness reached");
                    }
                    annealingEngine.cool();
                    updateLog();
                    Thread.yield();
                }
                if (stopRunning || annealingEngine.isMaxFitnessReached()) {
                    Thread.currentThread().interrupt();
                    break;
                }

            } catch (Exception ex) {
                structGenLogger.error(ex.getMessage());
            }
        } while (!annealingEngine.isFinished());
        running = false;
        long end = System.currentTimeMillis();
        System.out.println("Finished evolving in : " + (end - start) / 1000 + " seconds");
        structGenLogger.info("Finished evolving in : " + (end - start) / 1000 + " seconds");
    }

    private void initiateProgressLogger() {

        String judges = "";
        System.out.println(chiefJustice);
        for (Object obj : chiefJustice.getJudges()) {
            Judge judge = (Judge) obj;
            judges += ";" + judge.getName();
            System.out.println(judge.getName());
        }
        System.out.println(judges);
        annealingEvolutionLogger.info("Iteration;" + "Temperature;" + "Cost" + judges);
    }

//
//    private void calculateFinalMaxScore() {
//        try {
//            this.maxScore = chiefJustice.getScore(atomContainer).maxScore;
//        } catch (Exception ex) {
//            structGenLogger.error(ex.getMessage());
//        }
//    }

    protected void mutate() {
    }

    void extractAnnealingEngine() {
        Object obj;
        for (int f = 0; f < annealingOptions.size(); f++) {
            obj = annealingOptions.get(f);
            if (obj instanceof AnnealingEngine) {
                annealingEngine = (ConvergenceAnnealingEngine) obj;

                return;
            }
        }
        System.out.println("AnnealingEngine not found");
        structGenLogger.warn("AnnealingEngine not found");
    }

    void updateLog() {
        annealingLog.addEntry((double) annealingEngine.getIterations(), (double) annealingEngine.getTemperature(), (double) (bestScore.costValue));
        annealingEvolutionLogger.info(annealingEngine.getIterations() + ";" + formatter.format(annealingEngine.getTemperature()) + ";" + formatter.format(bestScore.costValue) + bestScore.allJudgeScores);
    }

    
    public StructureGeneratorResult call() throws Exception {
        long start = System.currentTimeMillis();
        startTime = start;
        running = true;
        IAtomContainer result = null;
        IAtomContainer startMolecule = null;
        lastScore = new ScoreSummary(0, "nop");
        bestScore = new ScoreSummary(0, "nop");
        initiateProgressLogger();

        extractAnnealingEngine();
        startMolecule = generateSingleRandomStructure();

        randomGent = new RandomGenerator(startMolecule);
        annealingEngine.initAnnealing(randomGent, chiefJustice);
        //calculateFinalMaxScore();
        structGenLogger.info("Running Simulated annealing.");
        do {
            try {
                if (!stopRunning) {

                    result = randomGent.proposeStructure();
                    Thread.yield();

                    recentScore = chiefJustice.getScoreByThreading(result);
                    Thread.yield();
                    if (annealingEngine.isAccepted(recentScore.score,
                            lastScore.score)) {
                        randomGent.acceptStructure();
                        lastScore = recentScore;
                    }
                    Thread.yield();
                    if (recentScore.score > bestScore.score) {
                        try {
                            bestScore = recentScore;
                            bestStructure = (IAtomContainer) result.clone();
                            annealingEngine.setConvergenceCounter(0);
                        } catch (CloneNotSupportedException ex) {
                            structGenLogger.error(ex.getMessage());
                        }
                    }
                    if (recentScore.score >= bestScore.score) {
                        System.out.println("best score: " + recentScore.score + " cost : " + recentScore.costValue + " desc : " + recentScore.description);
                        bestScore = recentScore;
                        IAtomContainer bestMolecule = (IAtomContainer) result.clone();
//                        Double cost = recentScore.costValue;
//                        bestScore.costValue = cost;
                        bestMolecule.setProperty("Score", decimalFormat.format(bestScore.costValue));
                        structureGeneratorResult.structures.push(bestMolecule);
                        if (bestScore.costValue == 1.0) {
                            annealingEngine.setMaxFitnessReached(true);
                            structGenLogger.info("Maximum fitness reached");
                        }

                    }
                    annealingEngine.cool();
                    updateLog();
                    Thread.yield();
                }
                if (stopRunning || annealingEngine.isMaxFitnessReached()) {
                    Thread.currentThread().interrupt();
                    break;
                }

            } catch (Exception ex) {
                structGenLogger.error(ex.getMessage());
            }
        } while (!annealingEngine.isFinished());
        running = false;
        long end = System.currentTimeMillis();
        System.out.println("Finished evolving in : " + (end - start) / 1000 + " seconds");
        structGenLogger.info("Finished evolving in : " + (end - start) / 1000 + " seconds");
        return structureGeneratorResult;
    }
}
