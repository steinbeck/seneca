/*
 *  TwoDSpectrumJudge.java
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
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Description of the Class
 *
 * @author steinbeck @created October 6, 2001
 */
public abstract class TwoDSpectrumJudge extends Judge implements Serializable,
        Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(TwoDSpectrumJudge.class);
    /**
     * Description of the Field
     */
    public boolean[][][] assignment = null;

    /**
     * Description of the Field
     */
    public int scores[] = new int[7];
    /**
     * Description of the Field
     */
    public List rules = new ArrayList();
    /**
     * Description of the Field
     */
    protected int numberOf2DSignals = 0;
    private int cutOff;
    List sphere = null;

    /**
     * Constructor for the TwoDSpectrumJudge object
     *
     * @param name Description of Parameter
     */
    public TwoDSpectrumJudge(String name) {
        super(name);
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
        }
        hasMaxScore = true;
        sphere = new ArrayList();
        // debug = true;
    }

    /**
     * Sets the Scores attribute of the TwoDSpectrumJudge object
     *
     * @param scores The new Scores value
     */
    public void setScores(int[] scores) {
        this.scores = scores;
    }

    /**
     * Sets the Score attribute of the TwoDSpectrumJudge object
     *
     * @param score    The new Score value
     * @param position The new Score value
     */
    public void setScore(int score, int position) {
        if (position >= scores.length || position < 0) {
            return;
        }
        scores[position] = score;
    }

    /**
     * Sets the CutOff attribute of the TwoDSpectrumJudge object
     *
     * @param cutOff The new CutOff value
     */
    public void setCutOff(int cutOff) {
        this.cutOff = cutOff;
    }

    /**
     * Gets the CutOff attribute of the TwoDSpectrumJudge object
     *
     * @return The CutOff value
     */
    public int getCutOff() {
        return cutOff;
    }

    /**
     * must be called before this Judge is passed to the Structure Generators Judges List, in
     * order for it to function properly
     */
    
    public void init() {
        for (int f = 0; f < assignment.length; f++) {
            for (int g = 0; g < assignment.length; g++) {
                for (int h = 0; h < assignment.length; h++) {
                    if (assignment[f][g][h]) {
                        rules.add(new TwoDRule(f, h));
                    }
                }
            }
        }
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > 0) {
                cutOff = i + 1;
            }
        }
        logger.info("CutOff for pathlength search set to " + cutOff + " in " + getName());
    }

    /**
     * Description of the Method
     */
    
    public void calcMaxScore() {
        int max = 0;
        for (int f = 0; f < scores.length; f++) {
            if (scores[f] > max) {
                max = scores[f];
            }
        }
        numberOf2DSignals = 0;

        for (int f = 0; f < assignment.length; f++) {
            for (int g = 0; g < assignment.length; g++) {
                for (int h = 0; h < assignment.length; h++) {
                    if (assignment[f][g][h]) {
                        numberOf2DSignals += 1;
                    }
                }
            }
        }
        maxScore = (double) numberOf2DSignals * max;
        logger.info("MaxScore in " + getName() + " set to " + maxScore);
    }

    /**
     * Description of the Method
     *
     * @param ac Description of Parameter
     * @return Description of the Returned Value
     */
    //
    public JudgeResult evaluate(IAtomContainer ac) {

        if (assignment == null) {
            resultString = "No signals available for " + name;
            return new JudgeResult(0, 0, 0, resultString);
        }
        scoreSum = 0;
        int satisfiedSignals = 0;
        int plength = 0;
        TwoDRule rule;

        logger.info("TwoDSpectrumJudge->evaluate()->rules.size(): " + rules.size());
        logger.info(ac);

        for (int f = 0; f < rules.size(); f++) {
            rule = (TwoDRule) rules.get(f);
            sphere.clear();
            logger.info("TwoDSpectrumJudge->evaluate()->rule.atom1: " + rule.atom1);
            logger.info("TwoDSpectrumJudge->evaluate()->rule.atom2: " + rule.atom2);
            sphere.add(ac.getAtom(rule.atom1));
            try {
                plength = PathTools.breadthFirstTargetSearch(ac, sphere,
                        ac.getAtom(rule.atom2), 0, cutOff);
            } catch (ConcurrentModificationException cme) {
                logger.error(cme.getMessage());
            }
            logger.info("TwoDSpectrumJudge->evaluate()->plength: " + plength);
            if (plength > 0) {
                scoreSum += scores[plength - 1];
                if (scores[plength - 1] > 0) {
                    satisfiedSignals++;
                }
            }

        }
        resultString = satisfiedSignals + "/" + numberOf2DSignals
                + " Signals satisfied in " + name + ". Score " + scoreSum + "/"
                + maxScore;
        logger.info(resultString);
        return new JudgeResult(maxScore, scoreSum, satisfiedSignals,
                resultString);

    }

    
    public JudgeResult call() throws Exception {
        if (assignment == null) {
            resultString = "No signals available for " + name;
            return new JudgeResult(0, 0, 0, resultString);
        }
        scoreSum = 0;
        int satisfiedSignals = 0;
        int plength = 0;
        TwoDRule rule;

        logger.info("TwoDSpectrumJudge->evaluate()->rules.size(): " + rules.size());
        logger.info(ac);

        for (int f = 0; f < rules.size(); f++) {
            rule = (TwoDRule) rules.get(f);
            sphere.clear();
            logger.info("TwoDSpectrumJudge->evaluate()->rule.atom1: " + rule.atom1);
            logger.info("TwoDSpectrumJudge->evaluate()->rule.atom2: " + rule.atom2);
            sphere.add(ac.getAtom(rule.atom1));
            try {
                plength = PathTools.breadthFirstTargetSearch(ac, sphere,
                        ac.getAtom(rule.atom2), 0, cutOff);
            } catch (ConcurrentModificationException cme) {
                logger.error(cme.getMessage());
            }
            logger.info("TwoDSpectrumJudge->evaluate()->plength: " + plength);
            if (plength > 0) {
                scoreSum += scores[plength - 1];
                if (scores[plength - 1] > 0) {
                    satisfiedSignals++;
                }
            }

        }
        resultString = satisfiedSignals + "/" + numberOf2DSignals
                + " Signals satisfied in " + name + ". Score " + scoreSum + "/"
                + maxScore;
        logger.info(resultString);
        return new JudgeResult(maxScore, scoreSum, satisfiedSignals,
                resultString);

    }

    /**
     * Description of the Method
     *
     * @return Description of the Returned Value
     */
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (name == null || assignment == null) {
            return "";
        }
        sb.append("Configuration of TwoDSpectrumJudge " + name + ":\n");
        sb.append("Listing relation of nuclei by number...\n");
        for (int f = 0; f < assignment.length; f++) {
            for (int g = 0; g < assignment.length; g++) {
                for (int h = 0; h < assignment.length; h++) {
                    if (assignment[f][g][h]) {
                        sb.append(f + "-" + h + "\n");
                    }
                }
            }
        }
        sb.append("End of listing");
        return sb.toString();
    }
}
