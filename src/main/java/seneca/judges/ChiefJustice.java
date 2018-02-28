/*
 *  ChiefJustice.java
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
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.judges;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.Utilities;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Administers and controls all the Judges involved in a CASE run
 *
 * @author steinbeck @created September 10, 2001
 */
public class ChiefJustice implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChiefJustice.class);
    List judges = null;
    boolean isInitialized = false;
    boolean propsCalculated = false;
    private boolean perceptionNeeded = false;
    private List<Future<JudgeResult>> judgeResults = null;
    DecimalFormat df = new DecimalFormat("###.##");
    ;

    /**
     * Constructor for the ChiefJustice object
     */
    public ChiefJustice() {
        this(new ArrayList());
        initJudges();
    }

    /**
     * Constructor for the ChiefJustice object
     *
     * @param judges Description of Parameter
     */
    public ChiefJustice(List judges) {
        this.judges = judges;
        perceptionNeeded = isMoleculePerceptionNeeded();
    }

    private boolean isMoleculePerceptionNeeded() {
        for (int f = 0; f < judges.size(); f++) {
            Judge judge = (Judge) judges.get(f);
            if ((judge.getName().equals("NMRShiftDbJudge") && judge.getEnabled()) ||
                    (judge.getName().equals("NPLikenessJudge") && judge.getEnabled())) {
                return true;
            }
        }
        return false;
    }

    /**
     * initializes the judges
     */
    public void initJudges() {
        for (int f = 0; f < judges.size(); f++) {
            if (((Judge) judges.get(f)).getEnabled()) {
                ((Judge) judges.get(f)).init();
            }
        }
        isInitialized = true;
        for (int f = 0; f < judges.size(); f++) {
            if (((Judge) judges.get(f)).getEnabled()
                    && ((Judge) judges.get(f)).hasMaxScore()) {
                ((Judge) judges.get(f)).calcMaxScore();
            }
        }
    }

    /**
     * Sets the Judges attribute of the ChiefJustice object
     *
     * @param judges The new Judges value
     */
    public void setJudges(List judges) {
        this.judges = judges;
    }

    public ScoreSummary getScore(IAtomContainer molecule) throws Exception {
        if (!isInitialized) {
            initJudges();
        }
        double score = 0;
        String description = "";
        Judge judge = null;
        JudgeResult judgeResult = null;
        double maxScore = 0;
        propsCalculated = false;
        String allScores = "";
        //System.out.println("--------------------------------------------------");
        for (int f = 0; f < judges.size(); f++) {
            judge = (Judge) judges.get(f);
            if (judge.getEnabled()) {
                judgeResult = judge.evaluate(molecule);
                maxScore += judgeResult.maxScore;
                score += judgeResult.score;
                //System.out.println(judge.getName() + " " + score);
                description += judgeResult.scoreDescription + "\n";
                allScores += ";" + judgeResult.score;
            }
        }
        if (score < 0) {
            score = 0;
        }
        ScoreSummary scoreSummary = new ScoreSummary(score, description);
        scoreSummary.maxScore = maxScore;
        scoreSummary.allJudgeScores = allScores;
        scoreSummary.costValue = score / maxScore;
        return scoreSummary;
    }

    public ScoreSummary getScoreByThreading(IAtomContainer molecule) {
        if (!isInitialized) {
            initJudges();
        }

        Judge judge = null;
        judgeResults = new ArrayList<Future<JudgeResult>>();
        propsCalculated = false;

        ExecutorService executor = Executors.newFixedThreadPool(judges.size());
        for (int f = 0; f < judges.size(); f++) {
            judge = (Judge) judges.get(f);
            if (judge.getEnabled()) {
                try {
                    IAtomContainer clone = molecule.clone();
                    if (perceptionNeeded) {
                        Utilities.calculateProperties(clone);
                    }
                    //  clone.setProperties(molecule.getProperties());
                    judge.setAtomContainer(clone);
                } catch (Exception e) {
                    logger.error(e);
                }
                Future<JudgeResult> submit = executor.submit(judge);
                judgeResults.add(submit);
            }
        }
        executor.shutdown();
//        try {
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
//        } catch (InterruptedException e) {
//            logger.error("Problem awaiting termination in chief justice");
//            e.printStackTrace();
//        }
        return extractJudgeResults();
    }

    private ScoreSummary extractJudgeResults() {

        double score = 0;
        double maxScore = 0;
        double npCost = Double.NaN;
        String description = "";
        String allScores = "";
        JudgeResult judgeResult = null;
        for (Future<JudgeResult> result : judgeResults) {
            try {
                judgeResult = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                logger.error(e);
            }
            if (judgeResult.maxScore != 1.0) {
                maxScore += judgeResult.maxScore;
                score += judgeResult.score;
            } else {
                npCost = judgeResult.score;
            }
            description += judgeResult.scoreDescription + "\n";
            allScores += ";" + judgeResult.score;

        }
        if (score < 0) {
            score = 0;
        }
        ScoreSummary scoreSummary = new ScoreSummary(score, description);
        scoreSummary.allJudgeScores = allScores;
        double otherJudgesCost = score / maxScore;
        if (npCost != npCost) {
            scoreSummary.costValue = otherJudgesCost;
        } else {
            scoreSummary.costValue = calcTotalCost(otherJudgesCost, npCost);
//            if (npCost < 0.0 || otherJudgesCost < 0.0) {
//                System.out.println("other: " + otherJudgesCost + " np: " + npCost + " totalled: " + scoreSummary.costValue);
//            }

        }
        scoreSummary.maxScore = maxScore;

        return scoreSummary;
    }

    private double calcTotalCost(double otherJudgesCost, double npCost) {
        if (otherJudgesCost == otherJudgesCost) {
            return npCost == npCost ? (otherJudgesCost * (1 + npCost)) / 2 : otherJudgesCost;
        } else {
            return npCost == npCost ? npCost : 0d;
        }
    }

    private ScoreSummary extractResults() {

        double individualSummedUpScore = 0;
        double maxScore = 0;
        double combinedScores = 0d;
        double combinedMaxScore = 0d;
        String description = "";
        String allScores = "";
        List<JudgeResult> toCombine = new ArrayList<JudgeResult>();
        JudgeResult judgeResult = null;
        for (Future<JudgeResult> result : judgeResults) {
            try {
                judgeResult = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                logger.error(e);
            }
            if (judgeResult.shouldCombined == false) {
                maxScore += judgeResult.maxScore;
                individualSummedUpScore += judgeResult.score;
            } else {
                toCombine.add(judgeResult);
                combinedScores += judgeResult.score;
                combinedMaxScore += judgeResult.maxScore;
            }

            description += judgeResult.scoreDescription + "\n";
            allScores += ";" + df.format(judgeResult.score);

        }
        double combinedCost = combineScores(toCombine);
        if (individualSummedUpScore < 0) {
            individualSummedUpScore = 0;
        }
        ScoreSummary scoreSummary = new ScoreSummary(individualSummedUpScore + combinedScores, description);
        scoreSummary.allJudgeScores = allScores;
        double costValue = individualSummedUpScore / maxScore;
        scoreSummary.costValue = calcTotalCost(costValue, combinedCost);
        scoreSummary.maxScore = maxScore + combinedMaxScore;

        return scoreSummary;
    }

    private double combineScores(List<JudgeResult> judgeResults) {

        int size = judgeResults.size();
        double cost = 0d;
        JudgeResult np = null;
        JudgeResult nmrshiftdb = null;
        JudgeResult antiBredt = null;
        switch (size) {
            case 0:
                return cost;
            case 1:
                if (judgeResults.get(0).index == 1) {
                    return judgeResults.get(0).score;
                }
                cost = judgeResults.get(0).score / judgeResults.get(0).maxScore;
                return cost;
            case 2:

                for (JudgeResult result : judgeResults) {
                    if (result.index == 0) {
                        nmrshiftdb = result;
                    } else if (result.index == 1) {
                        np = result;
                    } else if (result.index == 2) {
                        antiBredt = result;
                    }
                }
                cost = ((nmrshiftdb.score / nmrshiftdb.maxScore) * (1 + (np.score * 0.5))) / 2;
                return cost;
            default:
                return cost;
        }
    }


    private void updateLog() {

    }

    /**
     * Gets the Judges attribute of the ChiefJustice object
     *
     * @return The Judges value
     */
    public List getJudges() {
        return judges;
    }

    /**
     * Adds a feature to the Judge attribute of the ChiefJustice object
     *
     * @param judge The feature to be added to the Judge attribute
     */
    public void addJudge(Judge judge) {
        this.judges.add(judge);

    }

    /**
     * Description of the Method
     *
     * @param judge Description of Parameter
     */
    public void removeJudge(Judge judge) {
        this.judges.remove(judge);
    }

    /**
     * Description of the Method
     *
     * @return Description of the Returned Value
     */
    
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("Error cloning justice");
        }
        return o;
    }
}
