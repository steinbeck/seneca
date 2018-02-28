package seneca.structgen.ea;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.judges.ChiefJustice;
import seneca.judges.ScoreSummary;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 28/02/2013
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class FitnessCalculator implements Callable<Individual> {

    private Individual individual = null;
    private ChiefJustice justice = null;

    public FitnessCalculator(ChiefJustice justice, Individual individual) {
        this.justice = justice;
        this.individual = individual;
    }

    
    public Individual call() throws Exception {
        ScoreSummary summary = this.justice.getScoreByThreading((IAtomContainer) individual.getMolecule().clone());
        individual.setScoreSummary(summary);
        individual.setFitness(summary.costValue);
        return individual;
    }

    public synchronized void setIndividual(Individual individual) {
        this.individual = individual;
    }
}
