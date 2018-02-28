package seneca.judges;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 08/03/2013
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class AtomCenteredFragmentJudge extends Judge {
    private static final long serialVersionUID = 1L;
    public static final Logger logger = Logger.getLogger(AtomCenteredFragmentJudge.class);

    protected double[] carbonShifts;
    protected int score = 100;


    public AtomCenteredFragmentJudge(String name) {
        super(name);
    }

    
    public void calcMaxScore() {
        maxScore = carbonShifts.length * score;
    }

    /**
     * Here we assign the values for the experimental carbon shifts
     *
     * @param shifts An array of experimental carbon shifts
     */
    public void setCarbonShifts(double[] shifts) {
        logger.info("CARBON SHIFTS: " + shifts);
        this.carbonShifts = shifts;
    }


}
