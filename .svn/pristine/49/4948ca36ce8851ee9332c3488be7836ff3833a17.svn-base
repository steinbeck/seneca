/* JudgeResult.java
 *
 * Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license.
 * The intent of this license is to state the conditions under which this Package 
 * may be copied, such that the Copyright Holder maintains some semblance
 * of artistic control over the development of the package, while giving the 
 * users of the package the right to use and distribute the Package in a
 * more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * The complete text of the license can be found in a file called LICENSE 
 * accompanying this package.
 */

package seneca.judges;

import java.io.Serializable;

/**
 * Instances of this class are returned by Judges. In addition to the score the
 * do also return the maximum possible score in this run and the property value
 * based on which the score has been calculated
 */

public class JudgeResult implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The score calulated for the current structure *
     */
    public double score;

    /**
     * The maximum reachable score - This is certainly not always defined. *
     */
    public double maxScore;

    /**
     * The score is often based on particular property, e. g. the Wiener Number
     * of the current structure. This field allows passing back a value for this
     * property
     */
    public long propertyValue;

    /**
     * A Human-readable description like '10 of 20 HMBC signals satisfied'. This
     * can for instance be used as the title for a structure drawing.
     */
    public String scoreDescription;

    /*
    variable to let know chiefjustice to combine results from different judges
     */
    public boolean shouldCombined = false;
    /*
    Index to specify judges NMRShiftDB = 0, NPLikeness = 1, AntiBredt = 2 ..etc.
     */

    public int index = 0;

    public JudgeResult() {

    }

    public JudgeResult(double maxScore, double score, long proptertyValue,
                       String scoreDescription) {
        this.maxScore = maxScore;
        this.score = score;
        this.propertyValue = proptertyValue;
        this.scoreDescription = scoreDescription;
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
}