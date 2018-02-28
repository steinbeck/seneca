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
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.BremserOneSphereHOSECodePredictor;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 * This Judge assigns a score to a structure depending on the the deviation of the experimental 13C
 * carbon spectrum from a backcalculated one. Currently the backcalculation is very rudimentary
 * (based on a one-sphere HOSE code prediction), so that the role of this judge can only be to
 * assure that the carbon atom environment is in the correct range with respect to hybridization
 * state and hetero attachments
 */
public class HOSECodeJudge extends AtomCenteredFragmentJudge {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HOSECodeJudge.class);
    protected transient HOSECodeGenerator hcg;
    protected transient BremserOneSphereHOSECodePredictor predictor;

    public HOSECodeJudge() {
        super("HOSECodeJudge");
        hasMaxScore = true;
    }

    public void setScore(int s) {
    }

    
    public void init() {
        hcg = new HOSECodeGenerator();
        predictor = new BremserOneSphereHOSECodePredictor();

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
        int carbonCount = 0;
        for (int f = 0; f < ac.getAtomCount(); f++) {

            if (ac.getAtom(f).getSymbol().equals("C")) {
                try {
                    hoseCode = hcg.makeBremserCompliant(hcg.getHOSECode(ac, ac.getAtom(f), 1));
                    shift = predictor.predict(hoseCode);
                    confidenceLimit = predictor.getConfidenceLimit(hoseCode);
                    //logger.info("HOSE "+ hoseCode+"; shift: " + shift + " ,ConfidenceLimit: " + confidenceLimit);

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
        String message = ", HOSECodeJudge: " + scoreSum + "/" + maxScore
                + ", Carbon shift medium deveation: "
                + (mediumDeviation / carbonCount) + "\n";
        return new JudgeResult(maxScore, scoreSum, 0, message);
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
        // System.out.println("HOSE judge");
        for (int f = 0; f < ac.getAtomCount(); f++) {

            if (ac.getAtom(f).getSymbol().equals("C")) {
                try {
                    hoseCode = hcg.makeBremserCompliant(hcg.getHOSECode(ac, ac.getAtom(f), 1));
                    shift = predictor.predict(hoseCode);
                    confidenceLimit = predictor.getConfidenceLimit(hoseCode);
                    //logger.info("HOSE "+ hoseCode+"; shift: " + shift + " ,ConfidenceLimit: " + confidenceLimit);

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
        if (!ConnectivityChecker.isConnected(ac)) {
            scoreSum -= 500;
            // wcc -= 0.5d;
        }
        String message = ", HOSECodeJudge: " + scoreSum + "/" + maxScore
                + ", Carbon shift medium deveation: "
                + (mediumDeviation / carbonCount) + "\n";
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }


}
