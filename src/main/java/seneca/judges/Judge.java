/*
 *  Judge.java
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

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Base-class for evaluating the score of a particular structure with respect to agreement to a
 * given parameter. This class is to be subclassed and customized.
 *
 * @author steinbeck @created October 5, 2001
 */
public abstract class Judge implements Serializable, Cloneable, Callable<JudgeResult> {

    private static final long serialVersionUID = 1L;
    public static final Logger logger = Logger.getLogger(Judge.class);

    /*
     * A name identifying the scope of the Judge
     */
    public String name = "nop";

    /*
     * Important property of a Judge. Tells a score summarizing entity if it is possible to
     * calculate a maximum score for this judge This is important, for example, for letting a
     * Simulated Annealing run converge to a maximum achivable score.
     */
    boolean hasMaxScore = false;
    boolean ringSetRequired = false;
    int[][] connectionTable;
    double score, maxScore, scoreSum;
    String resultString;
    int multiplicator;
    transient JudgeListener judgeListener = null;
    protected IAtomContainer ac = null;
    protected int atomCount = 0;


    /*
     * Should this Judge be used during the evaluation process?
     */
    private boolean enabled = false;
    private boolean initialized = false;
    static boolean debug = false;
    static boolean report = true;

    public Judge(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMultiplicator(int multiplicator) {
        this.multiplicator = multiplicator;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        fireChanged();
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void setJudgeListener(JudgeListener jl) {
        this.judgeListener = jl;
    }

    public void setRingSetRequired(boolean ringSetRequired) {
        this.ringSetRequired = ringSetRequired;
    }

    public void setHasMaxScore(boolean hasMaxScore) {
        this.hasMaxScore = hasMaxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getMultiplicator() {
        return this.multiplicator;
    }

    public String getName() {
        return name;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public boolean isRingSetRequired() {
        return this.ringSetRequired;
    }

    public boolean hasMaxScore() {
        return this.hasMaxScore;
    }

    public double getMaxScore() {
        return this.maxScore;
    }

    public void setAtomCount(int atomCount) {
        this.atomCount = atomCount;
    }


    public abstract JudgeResult evaluate(IAtomContainer ac)
            throws Exception;

    public abstract void init();

    public abstract void calcMaxScore();

    public void fireChanged() {
        if (judgeListener != null) {
            judgeListener.judgeDataChanged();
        }
    }

    
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("MyObject can't clone");
        }
        return o;
    }

    public synchronized void setAtomContainer(IAtomContainer container) {
        this.ac = container;
    }
}
