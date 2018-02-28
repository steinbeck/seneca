package seneca.structgen.sa.adaptive;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import seneca.judges.ChiefJustice;
import seneca.judges.ScoreSummary;
import seneca.structgen.sa.adaptive.MoleculeState.Acceptance;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoleculeAnnealerAdapter implements AnnealerAdapterI {

    private ChiefJustice chiefJudge;
    private final ArrayList<StateListener> stateListeners;
    private RandomGenerator randomGenerator;
    private double bestCost;
    private double currentCost;
    private double nextCost;
    private IAtomContainer bestMolecule;
    private IAtomContainer currentMolecule;
    private IAtomContainer nextMolecule;
    private int stepIndex;
    private int bestStepIndex;
    private boolean isCancelled;
    public ScoreSummary scoreSummary = null;
    private double maxScore;
    private double bestScoreCost;


    public MoleculeAnnealerAdapter(IAtomContainer startingMolecule, ChiefJustice judge) {
        this.chiefJudge = judge;
        this.stateListeners = new ArrayList<StateListener>();

        this.randomGenerator = new RandomGenerator(startingMolecule);

        this.currentMolecule = startingMolecule;
        this.nextMolecule = null;
        this.bestMolecule = currentMolecule;
        this.bestCost = this.currentCost = this.nextCost = 0.0;

        this.stepIndex = 0;
        this.bestStepIndex = 0;

        this.isCancelled = false;

        this.scoreSummary = new ScoreSummary();
        this.maxScore = assignMaxScore(startingMolecule);
        this.bestScoreCost = 0d;
    }

    private double assignMaxScore(IAtomContainer startingMolecule) {
        double max_Score = 0;
        try {
            max_Score = this.chiefJudge.getScoreByThreading(startingMolecule).maxScore;
        } catch (Exception ex) {
            Logger.getLogger(MoleculeAnnealerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return max_Score;
    }

    
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    
    public boolean isCancelled() {
        return this.isCancelled;
    }

    public IAtomContainer getBest() {
        return this.bestMolecule;
    }

    public int getBestStepIndex() {
        return this.bestStepIndex;
    }

    public IAtomContainer getCurrent() {
        return this.currentMolecule;
    }

    
    public void addStateListener(StateListener listener) {
        this.stateListeners.add(listener);
    }

    
    public boolean costDecreasing() {
//		System.out.println("current cost: "+ this.currentCost);
//		System.out.println("previous cost: "+ this.nextCost);
        return this.nextCost < this.currentCost;
    }

    
    public double costDifference() {
        return this.currentCost - this.nextCost;
    }

    private double cost(IAtomContainer mol) {

        this.scoreSummary = getScoreSummaryForThis(mol);
        this.bestScoreCost = this.scoreSummary.costValue;
        //    this.scoreSummary.costValue = this.bestScoreCost;

        try {
            return 1.0 - (this.bestScoreCost);
            // --> the score is in the range [0-1], so the cost must be 1-score.
        } catch (Exception e) {
            return -1.0;
        }
    }

    private ScoreSummary getScoreSummaryForThis(IAtomContainer molecule) {
        ScoreSummary summ = null;
        try {
            summ = this.chiefJudge.getScoreByThreading(molecule);
        } catch (Exception ex) {
            Logger.getLogger(MoleculeAnnealerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return summ;
    }

    
    public void initialState() {
        // bit pointless.
        this.currentMolecule = this.randomGenerator.getMolecule();
        this.currentCost = cost(this.currentMolecule);
        this.bestCost = this.currentCost;
    }

    
    public void nextState() {
        this.nextMolecule = this.randomGenerator.proposeStructure();
        this.nextCost = cost(this.nextMolecule);
        this.stepIndex++;
        fireStateEvent(new MoleculeState(currentMolecule, Acceptance.UNKNOWN, stepIndex));
    }

    
    public void accept() {
        this.currentMolecule = this.nextMolecule;
        this.currentCost = this.nextCost;
        if (this.currentCost < this.bestCost) {
            //System.out.println("bestMolecule > current, storing" + this.bestCost + " " + this.currentCost);
            this.bestMolecule = this.currentMolecule;
            this.bestCost = currentCost;
            this.bestStepIndex = this.stepIndex;
            fireStateEvent(new MoleculeState(currentMolecule, Acceptance.ACCEPT, stepIndex));
        } else {
            //System.out.println("bestMolecule !> current, !storing" + this.bestCost + " " + this.currentCost);
        }
        this.randomGenerator.acceptStructure();

    }

    
    public void reject() {
        fireStateEvent(new MoleculeState(nextMolecule, Acceptance.REJECT, stepIndex));
    }

    private void fireStateEvent(State state) {
        for (StateListener listener : this.stateListeners) {
            listener.stateChanged(state);
        }
    }

    
    public double getBestCost() {
        return bestCost;
    }

    
    public ScoreSummary getBestScoreSummary() {
        return scoreSummary;

    }

    
    public double getBestAnnealScore() {
        return this.bestScoreCost;
    }
}
