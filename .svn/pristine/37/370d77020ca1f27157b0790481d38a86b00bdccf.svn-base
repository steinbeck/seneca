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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.Utilities;
import uk.ac.ebi.cheminformatics.nplikeness.scorer.NPScoreCalculator;

import java.text.DecimalFormat;

public class NPLikenessJudge extends Judge {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //    /*
//    max -min NP value to normalize = 3.5 - (-3.5) == 7.0
//    */
//    private double max_min = 7.0;
//    private double min = -3.5;
     /*
    max -min NP value to normalize = 3.72 - (-3.1) == 6.82 for height-3
    */
    private double max_min = 6.82;
    private double min = -3.1;
    public int npScore = 0; // Score for optimum fit of exp. with calc. shift

    private static final Logger logger = Logger.getLogger(NPLikenessJudge.class);
    protected transient NPScoreCalculator npLikenessCalculator = null;
    DecimalFormat df = null;

    public NPLikenessJudge() {
        super("NPLikenessJudge");
        hasMaxScore = true;
        df = new DecimalFormat("###.##");
    }

    public void setScore(int s) {
    }

    
    public void init() {
        npLikenessCalculator = new NPScoreCalculator();
    }

    
    public void calcMaxScore() {
//        maxScore = 3.5 * 100 * atomCount;
//        maxScore = 1.0 * 100 * atomCount;
        maxScore = 1.0;
    }

    
    public JudgeResult evaluate(IAtomContainer ac) throws Exception {
        scoreSum = 0;
        debug = false;
        String uuid_score = "";
        IAtomContainer clone = (IAtomContainer) Utilities.cloneObject(ac);
        uuid_score = npLikenessCalculator.curateAndScore(clone);

        if (!uuid_score.isEmpty()) {
            String[] uuid_score_values = uuid_score.split("\\|");
            double score = Double.parseDouble(uuid_score_values[1]);
            double normalizedScore = (score - min) / max_min;
            scoreSum = normalizedScore * 100 * clone.getAtomCount();
            new AtomContainer();
            logger.info("score: " + score + " ;score sum: " + scoreSum + " norm score: " + normalizedScore);

//            if (score >= 1.0) {
//                scoreSum = maxScore;
//            } else {
//                scoreSum = score * 100 * moleculeWithout_H.getAtomCount();
//            }
            // scoreSum = score * 100 * moleculeWithout_H.getAtomCount();
            //   logger.info("score: " + score + " ;score sum: " + scoreSum);
        } else {
            scoreSum = 0d;
        }
        String message = "NP-Judge Score: " + df.format(scoreSum) + "/" + maxScore + "\n";
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }

    
    public JudgeResult call() throws Exception {
        scoreSum = 0;
        debug = false;
        String uuid_score = "";
        uuid_score = npLikenessCalculator.curateAndScoreIgnoringH(ac);

        if (!uuid_score.isEmpty()) {
            String[] uuid_score_values = uuid_score.split("\\|");
            double score = Double.parseDouble(uuid_score_values[1]);
            double normalizedScore = (score - min) / max_min;
            //   scoreSum = normalizedScore * 100 * atomCount;
            scoreSum = normalizedScore;
            //       logger.info("score: " + score + " ;score sum: " + scoreSum + " norm score: " + normalizedScore);

//            if (score >= 1.0) {
//                scoreSum = maxScore;
//            } else {
//                scoreSum = score * 100 * moleculeWithout_H.getAtomCount();
//            }
            // scoreSum = score * 100 * moleculeWithout_H.getAtomCount();
            //   logger.info("score: " + score + " ;score sum: " + scoreSum);
        } else {
            scoreSum = 0d;
        }
        String message = "NP-Judge Score: " + df.format(scoreSum) + "/" + maxScore + "\n";
//        JudgeResult result = new JudgeResult(maxScore, scoreSum, 0, message);
//        result.shouldCombined = true;
//        result.index = 1;
        return new JudgeResult(maxScore, scoreSum, 0, message);
    }
}
