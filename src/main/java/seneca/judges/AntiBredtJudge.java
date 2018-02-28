package seneca.judges;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.predictor.AntiBredtDetector;


/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 15/02/2013
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
public class AntiBredtJudge extends Judge {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AntiBredtJudge.class);
    private AntiBredtDetector detector;

    public AntiBredtJudge() {
        super("AntiBredtJudge");
    }

    
    public JudgeResult evaluate(IAtomContainer ac) throws Exception {
        scoreSum = 0;
        boolean isAnti = false;
        if (detector.isAntiBredt(ac)) {
            isAnti = true;
            scoreSum = -500;
            //System.out.println("score sum: " + scoreSum + " , atom count: " + atomCount);
        }
//        String desc = "Anti bredt: " + scoreSum + "/"
//                + maxScore;
        JudgeResult judgeResult = new JudgeResult(maxScore, scoreSum, 0, "; Antibredt: " + isAnti + " ; ");
        judgeResult.index = 2;
        return judgeResult;
    }

    
    public void init() {
        detector = new AntiBredtDetector();
    }

    
    public void calcMaxScore() {
        maxScore = 0;
    }

    
    public JudgeResult call() throws Exception {
        scoreSum = 0;
        boolean isAnti = false;
        if (detector.isAntiBredt(ac)) {
            isAnti = true;
            scoreSum = -500;
            //System.out.println("score sum: " + scoreSum + " , atom count: " + atomCount);
        }
//        String desc = "Anti bredt: " + scoreSum + "/"
//                + maxScore;
        return new JudgeResult(maxScore, scoreSum, 0, "; Antibredt: " + isAnti + " ; ");
    }
}
