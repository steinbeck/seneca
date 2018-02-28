/* ScoreSummary.java
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

public class ScoreSummary implements java.io.Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ScoreSummary(double score, String description) {
        this.score = score;
        this.description = description;
    }

    public ScoreSummary() {
    }

    public double score = 0d;
    public double maxScore = -1d;
    public double costValue = 0d;
    public String description = "";
    public String allJudgeScores = "";

    
    public String toString() {
        String s = description + "Overall Score:" + score;
        if (maxScore > 0) {
            s += "/" + maxScore;
        }
        return s;
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
