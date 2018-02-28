package seneca.judges;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.predictor.RingStrainDetector;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 11/05/2013
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class RingStrainJudge extends Judge {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AntiBredtJudge.class);
    private RingStrainDetector detector;

    public RingStrainJudge() {
        super("RingStrainJudge");
    }

    
    public JudgeResult evaluate(IAtomContainer ac) throws Exception {
        scoreSum = 0;
        boolean isStrained = false;
        if (detector.isStrained(ac)) {
            isStrained = true;
            scoreSum = -200;
            //System.out.println("score sum: " + scoreSum + " , atom count: " + atomCount);
        }
//        String desc = "Anti bredt: " + scoreSum + "/"
//                + maxScore;
        JudgeResult judgeResult = new JudgeResult(maxScore, scoreSum, 0, "; Strained: " + isStrained + " ; ");
        judgeResult.index = 2;
        return judgeResult;
    }

    
    public void init() {
        detector = new RingStrainDetector();
    }

    
    public void calcMaxScore() {
        maxScore = 0;
    }

    
    public JudgeResult call() throws Exception {
        scoreSum = 0;
        boolean isStrained = false;
        if (detector.isStrained(ac)) {
            isStrained = true;
            scoreSum = -200;
            //System.out.println("score sum: " + scoreSum + " , atom count: " + atomCount);
        }
//        String desc = "Anti bredt: " + scoreSum + "/"
//                + maxScore;
        return new JudgeResult(maxScore, scoreSum, 0, "; Strained: " + isStrained + " ; ");
    }
}

