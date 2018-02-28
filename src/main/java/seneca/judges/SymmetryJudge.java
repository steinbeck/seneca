/* SymmetryJudge.java
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

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.exception.JudgeEvaluationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on inspection of e.g. a 13C-NMR spectrum certain bond orders for each
 * node can be favoured by assigning positive scores for them and negative (or
 * no) scores for unwanted bond orders
 */

public class SymmetryJudge extends Judge {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SymmetryJudge.class);
    protected List symmetryClassesList;
    protected SymmetryClass[] symmetryClasses;

    public SymmetryJudge() {
        super("SymmetryJudge");
        symmetryClassesList = new ArrayList();
        score = 500;
        hasMaxScore = true;

    }

    public int getSymmetryClassCount() {
        return symmetryClassesList.size();
    }

    public void addSymmetryClass(boolean[] symmetricAtoms) {
        symmetryClassesList.add(new SymmetryClass(symmetricAtoms));
        init();
    }

    public void clearSymmetryClasses() {
        symmetryClassesList.clear();
    }

    public boolean[] getSymmetryClass(int n) {
        if (n < symmetryClassesList.size()) {
            return ((SymmetryClass) symmetryClassesList.get(n)).symmetricAtoms;
        }
        return null;
    }

    public int getClassMember(int classNumber, int memberNumber) {
        if (symmetryClasses.length > classNumber) {
            int counter = 0;
            for (int f = 0; f < symmetryClasses[classNumber].symmetricAtoms.length; f++) {
                if (symmetryClasses[classNumber].symmetricAtoms[f])
                    counter++;
                if (counter == memberNumber)
                    return f;
            }
        }
        return -1;
    }

    public void setScore(int s) {
        score = s;
    }

    
    public void init() {
        symmetryClasses = new SymmetryClass[symmetryClassesList.size()];

		/*
         * I copy the List into an array to have greater speed in the compute
		 * server
		 */
        for (int f = 0; f < symmetryClassesList.size(); f++) {
            symmetryClasses[f] = (SymmetryClass) symmetryClassesList
                    .get(f);
        }
    }

    
    public void calcMaxScore() {
        maxScore = 0;

        for (int f = 0; f < symmetryClassesList.size(); f++) {
            maxScore += (faculty(symmetryClasses[f].symmetricAtomsCount) / 2);
        }
    }
    
    public static int faculty(int i) {
                       if (i > 1) {
                                  return i * faculty(i - 1);
                           }
                       return 1;
                }

    
    public JudgeResult evaluate(IAtomContainer ac)
            throws JudgeEvaluationException {
        int scoreSum = 0;
        long[] mm;
        try {
            mm = MorganNumbersTools.getMorganNumbers(ac);
        } catch (Exception exc) {
            String s = "An Exception occured while trying to calculate Morgan Numbers ";
            s += "for the current structure. This should never happen of course :-)";
            logger.warn(s);
            throw new JudgeEvaluationException(s);
        }

		/*
         * we cycle through all the symmetry classes. Within each class, we
		 * check if each possible pair of atoms in the class has the same Morgan
		 * number.
		 */

        for (int f = 0; f < symmetryClasses.length; f++) {
            for (int g = 0; g < symmetryClasses[f].symmetricAtoms.length; g++) {
                if (g < symmetryClasses[f].symmetricAtoms.length - 1
                        && symmetryClasses[f].symmetricAtoms[g]) {
                    for (int h = g + 1; h < symmetryClasses[f].symmetricAtoms.length; h++) {
                        if (symmetryClasses[f].symmetricAtoms[h]) {
                            if (mm[g] == mm[h]) {
                                scoreSum += score;
                            }
                        }
                    }
                }
            }
        }
        String message = "" + scoreSum / score
                + " symmetry classes are completly filled.";
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }

    
    public JudgeResult call() throws Exception {
        int scoreSum = 0;
        long[] mm;
        try {
            mm = MorganNumbersTools.getMorganNumbers(ac);
        } catch (Exception exc) {
            String s = "An Exception occured while trying to calculate Morgan Numbers ";
            s += "for the current structure. This should never happen of course :-)";
            logger.warn(s);
            throw new JudgeEvaluationException(s);
        }

		/*
         * we cycle through all the symmetry classes. Within each class, we
		 * check if each possible pair of atoms in the class has the same Morgan
		 * number.
		 */

        for (int f = 0; f < symmetryClasses.length; f++) {
            for (int g = 0; g < symmetryClasses[f].symmetricAtoms.length; g++) {
                if (g < symmetryClasses[f].symmetricAtoms.length - 1
                        && symmetryClasses[f].symmetricAtoms[g]) {
                    for (int h = g + 1; h < symmetryClasses[f].symmetricAtoms.length; h++) {
                        if (symmetryClasses[f].symmetricAtoms[h]) {
                            if (mm[g] == mm[h]) {
                                scoreSum += score;
                            }
                        }
                    }
                }
            }
        }
        String message = "" + scoreSum / score
                + " symmetry classes are completly filled.";
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }

    class SymmetryClass implements java.io.Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        boolean[] symmetricAtoms;
        int symmetricAtomsCount = 0;

        SymmetryClass(int N) {
            symmetricAtoms = new boolean[N];
        }

        SymmetryClass(boolean[] symmetricAtoms) {
            this.symmetricAtoms = symmetricAtoms;
            symmetricAtomsCount = 0;
            for (int f = 0; f < symmetricAtoms.length; f++) {
                if (symmetricAtoms[f])
                    symmetricAtomsCount++;
            }
        }

    }

}
