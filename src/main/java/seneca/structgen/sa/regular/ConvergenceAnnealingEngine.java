/*
 *  $RCSfile: ConvergenceAnnealingEngine.java,v $
 *  $Author: steinbeck $
 *  $Date: 2004/02/16 09:50:54 $
 *  $Revision: 1.10 $
 *
 *  Copyright (C) 1997 - 2001  Dr. Christoph Steinbeck
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
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.structgen.sa.regular;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import seneca.judges.ChiefJustice;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The AnnealingEngine controls the course of the tempererature during a Simulated Annealing run
 * according to the data stored in a given AnnealingSchedule configuration object.
 *
 * @author steinbeck @created July 7, 2001
 */
public class ConvergenceAnnealingEngine implements AnnealingEngine,
        java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    /**
     * The maximum number of steps for each Plateau (Markov Chain)
     */
    long maxPlateauSteps = 1500;
    /**
     * The number of steps for each Plateau (Markov Chain)
     */
    long plateauStepCounter = 0;
    /**
     * Number of uphill made since last reset of this counter
     */
    long uphill = 0;
    /**
     * Number of uphill moves to be made before leaving this Plateau
     */
    long maxUphillSteps = 150;
    /**
     * The best score so far. Used for resetting the convergenceCounter
     */
    long bestScore = 0;
    /**
     * Counter that counts the unsuccessful steps (steps without a change in the score)
     */
    long convergenceCounter = 0;
    /**
     * Value for the convergenceCounter to be reached as a stop criterion
     */
    long convergenceStopCount = 40000;
    /**
     * The starting temperature (multiplied with k for convenience)
     */
    double start_kT = 0;
    /**
     * The current temperature (multiplied with k for convenience)
     */
    double current_kT = 0;
    /**
     * The cooling rate. Factor with which to multiply the current temperature in order to
     * calculate the new one.
     */
    double coolingRate = 0.95;
    /**
     * The counter for the annealing steps (surprise!)
     */
    long annealingStepCounter;
    /**
     * False as long as the calculation is not converged
     */
    boolean isConverged = false;
    /**
     * The initial acceptance probability used for initializing the schedule
     */
    double initialAcceptanceProbability = 0.8;
    /**
     * A central scoring facility for the initialization
     */
    ChiefJustice chiefJustice = null;
    /**
     * A source of structures for the initialization
     */
    RandomGenerator randomGent = null;
    /**
     * The number of cycles used in initialization
     */
    int initCycles = 500;
    /**
     * The number of total iterations done so far
     */
    long iterations = 0;
    long reportsteps = 1000;
    boolean debug = false;
    boolean report = true;
    private boolean maxFitnessReached = false;


    public ConvergenceAnnealingEngine() {
    }

    public boolean isMaxFitnessReached() {
        return maxFitnessReached;
    }

    public void setMaxFitnessReached(boolean value) {
        this.maxFitnessReached = value;
    }

    public void setInitialAcceptanceProbability(
            double initialAcceptanceProbability) {
        this.initialAcceptanceProbability = initialAcceptanceProbability;
    }

    public void setRandomGent(RandomGenerator randomGent) {
        this.randomGent = randomGent;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    
    public void setConvergenceCounter(long convergenceCounter) {
        this.convergenceCounter = convergenceCounter;
    }

    public long getConvergenceCounter() {
        return convergenceCounter;
    }

    public void setChiefJustice(ChiefJustice chiefJustice) {
        this.chiefJustice = chiefJustice;
    }

    public void setMaxPlateauSteps(long mps) {
        this.maxPlateauSteps = mps;
    }

    public void setMaxUphillSteps(long mus) {
        this.maxUphillSteps = mus;
    }

    public void setConvergenceStopCount(long csc) {
        this.convergenceStopCount = csc;
    }

    public void setStart_kT(double skt) {
        this.start_kT = skt;
    }

    public void setCurrent_kT(double ckt) {
        this.current_kT = ckt;
    }

    public void setCoolingRate(double cr) {
        this.coolingRate = cr;
    }

    public RandomGenerator getRandomGent() {
        return randomGent;
    }

    public ChiefJustice getChiefJustice() {
        return chiefJustice;
    }

    
    public long getIterations() {
        return iterations;
    }

    public double getInitialAcceptanceProbability() {
        return initialAcceptanceProbability;
    }

    public double getCoolingRate() {
        return coolingRate;
    }

    
    public double getTemperature() {
        if (debug) {
            System.out.println("ConvergenceAnnealingEngine->getCurrent_kT");
        }
        return current_kT;
    }

    
    public boolean isFinished() {
        return isConverged;
    }

    public long getMaxPlateauSteps() {
        return this.maxPlateauSteps;
    }

    public long getMaxUphillSteps() {
        return this.maxUphillSteps;
    }

    
    public boolean isAccepted(double recentScore, double lastScore) {
        double deltaE = lastScore - recentScore;
        double rnd = Math.random();
        double exp = (deltaE / current_kT);
        if (deltaE <= 0) {
            // log("Accepted better or equal result: deltaE = " + deltaE);
            uphill++;
            return true;
        } else {
            if (rnd < Math.exp(-exp)) {
                // log("Accepted stochastic update: rnd = " + rnd);
                // log("Math.exp(-exp) = " + Math.exp(-exp));
                return true;
            } else {
                return false;
            }
        }
    }

    
    public void cool() {
        if (plateauStepCounter > maxPlateauSteps || uphill > maxUphillSteps) {
            current_kT *= coolingRate;
            plateauStepCounter = 0;
            uphill = 0;
        } else if (((double) iterations / (double) reportsteps) == (int) ((double) iterations / (double) reportsteps)) {
            log("iterations: " + iterations + "; convergenceCounter: "
                    + convergenceCounter + "; current_kT: " + current_kT);
        }
        plateauStepCounter++;
        convergenceCounter++;
        if (convergenceCounter > convergenceStopCount || current_kT < 1.0 || maxFitnessReached) {
            log("Convergence criterion reached!");
            log("convergenceCounter: " + convergenceCounter + "/"
                    + convergenceStopCount);
            log("current_kT: " + current_kT);
            log("iterations: " + iterations);
            isConverged = true;
        }
        iterations++;
    }

    
    public void initAnnealing(RandomGenerator randomGent,
                              ChiefJustice chiefJustice) {
        IAtomContainer mol = null;
        double lastScore = 0d;
        double recentScore = 0d;
        double changes = 0d;
        for (int f = 0; f < initCycles; f++) {
            try {
                mol = (IAtomContainer) randomGent.proposeStructure();

                randomGent.acceptStructure();

                recentScore = chiefJustice.getScore(mol).score; // gets max score at this point.
                changes += Math.abs(lastScore - recentScore);
                lastScore = recentScore;
            } catch (Exception ex) {
                Logger.getLogger(ConvergenceAnnealingEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        changes = (changes / initCycles);
        setStart_kT(-changes / Math.log(getInitialAcceptanceProbability()));
        setCurrent_kT(start_kT);
        log("Starting temperature set to : " + start_kT);
        log("ConvergenceStopCount is : " + convergenceStopCount);
        log("maxUphillSteps: "+ maxUphillSteps);
        log("maxPlateauSteps: "+ maxPlateauSteps);
        log("convergenceStopCount: "+ convergenceStopCount);
     
    }

    private void log(String s) {
    		System.out.println(s);
    }
    
    
    
}
