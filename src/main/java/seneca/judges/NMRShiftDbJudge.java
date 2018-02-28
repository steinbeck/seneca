/* HOSECodeJudge.java
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.HOSECodeGenerator;
import seneca.core.Utilities;
import seneca.predictor.nmrshiftdb.lucene.HOSEIndex;

import java.util.List;

/**
 * This Judge assigns a score to a structure depending on the the deviation of the experimental 13C
 * carbon spectrum from a back-calculated one. The prediction of spectrum is done by table look up
 * of "C13 environments: shift" pairs in NMRShiftDB knowledge-base.
 */
public class NMRShiftDbJudge extends AtomCenteredFragmentJudge {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(NMRShiftDbJudge.class);
    // protected transient HOSECodeGenerator hcg;
    private HOSECodeGenerator hoseGenerator = null;
    //protected transient HOSETable hoseTable = null;
    protected HOSEIndex hoseTable = null;

    public NMRShiftDbJudge() {
        super("NMRShiftDbJudge");
        hasMaxScore = true;
        hoseGenerator = new HOSECodeGenerator();
    }

    public void setScore(int s) {
    }


    
    public void init() {
        // hcg = new HOSECodeGenerator();
        //  hoseTable = HOSETable.getInstance();
        hoseTable = HOSEIndex.getInstance();
    }

    /**
     * The methods evaluates a given structure by recalculating the carbon shift for each carbon
     * atom using a one-sphere HOSE Code method and calculating the deviation from the
     * experimental carbon spectrum. The deviation is normalized to 100 using confidence limit
     * given by the HOSE code table, i.e. a deviation of excatly the size of the confidence limit
     * is score zero, no deviation is core 100.
     *
     * @param ac The bond matrix to judge
     * @return A JudgeResult containing the score for this structure
     */
    
    public JudgeResult evaluate(IAtomContainer ac)
            throws Exception {
        scoreSum = 0;
        debug = false;
        String hoseCode = "";
        double shift = 0.0;
        double confidenceLimit = 0.0;
        double deviation = 0.0;
        double mediumDeviation = 0.0;
        //double[] currentCarbonShifts = new double[carbonShifts.length];
        int carbonCount = 0;
        Utilities.calculateProperties(ac);
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (ac.getAtom(f).getSymbol().equals("C")) {
                try {
                    hoseCode = getHose(ac.getAtom(f), ac);
                    List<Double> shiftAndConfidenceLimit = hoseTable.getShiftAndConfidenceLimit(hoseCode);

                    if (shiftAndConfidenceLimit.size() == 2) {
                        shift = shiftAndConfidenceLimit.get(0);
                        confidenceLimit = shiftAndConfidenceLimit.get(1);
                    }
                    // currentCarbonShifts[carbonCount] = shift;
                    deviation = Math.abs(shift - carbonShifts[carbonCount]);
                    carbonCount++;
                    mediumDeviation += deviation;
                    if (deviation < confidenceLimit) {
                        scoreSum += score;
                    }
                } catch (Exception exc) {
                    logger.error(exc.getMessage());
                    continue;
                }
            }
        }
        // double wcc = 0d;
//        if (currentCarbonShifts.length == carbonShifts.length) {
//            System.out.println("checking wcc");
//         //   wcc = ModifiedWeightedCrossCorrelation.wcc(carbonShifts, currentCarbonShifts, 3.0);
//            // System.out.println("wcc: " + wcc);
//        }
        String message = ", NMRShiftDBJudge: " + scoreSum + "/" + maxScore
                + ", Carbon shift medium deveation: "
                + (mediumDeviation / carbonCount) + "\n";
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }

    private String getHose(IAtom atom, IAtomContainer molecule) throws CDKException {
        String hoseCode = hoseGenerator.getHOSECode(molecule, atom, 4, true);
        String[] split = hoseCode.split(";");
        return split[1];
    }


    
    public JudgeResult call() throws Exception {
        scoreSum = 0;
        debug = false;
        String hoseCode = "";
        double shift = 0.0;
        double confidenceLimit = 0.0;
        double deviation = 0.0;
        double mediumDeviation = 0.0;
        int carbonCount = 0;
        Utilities.calculateProperties(ac);
        //double[] currentCarbonShifts = new double[carbonShifts.length];
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (ac.getAtom(f).getSymbol().equals("C")) {
                try {
                    hoseCode = getHose(ac.getAtom(f), ac);
                    List<Double> shiftAndConfidenceLimit = hoseTable.getShiftAndConfidenceLimit(hoseCode);

                    if (shiftAndConfidenceLimit.size() == 2) {
                        shift = shiftAndConfidenceLimit.get(0);
                        confidenceLimit = shiftAndConfidenceLimit.get(1);
                    }
                    //currentCarbonShifts[carbonCount] = shift;
                    deviation = Math.abs(shift - carbonShifts[carbonCount]);
                    carbonCount++;
                    mediumDeviation += deviation;
                    if (deviation <= confidenceLimit) {
                        scoreSum += score;
                    }
                } catch (Exception exc) {
                    logger.error(exc.getMessage());
                    logger.error(exc.getStackTrace());
                    exc.printStackTrace();
                    continue;
                }
            }
        }
//        double wcc = 0d;
//        wcc = ModifiedWeightedCrossCorrelation.wcc(carbonShifts, currentCarbonShifts, 3.0);
        if (!ConnectivityChecker.isConnected(ac)) {
            scoreSum -= 500;
            // wcc -= 0.5d;
        }


        String message = ", NMRShiftDBJudge: " + scoreSum + "/" + maxScore
                + ", Carbon shift medium deveation: "
                + (mediumDeviation / carbonCount) + "\n";
        return new JudgeResult(maxScore, scoreSum, 0, message);
//        String message = ", NMRShiftDBJudge: " + wcc * 100d + "/" + 100
//                + ", Carbon shift medium deveation: "
//                + (mediumDeviation / carbonCount) + " - wcc " + wcc + "\n";
//        return new JudgeResult(100, wcc * 100d, 0, message);
    }
}
