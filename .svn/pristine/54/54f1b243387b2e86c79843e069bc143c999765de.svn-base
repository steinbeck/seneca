/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.judges.ScoreSummary;

/**
 * @author kalai
 */
public class Individual {

    private long lifeTime = 0;
    private double fitness = 0d;
    private double selectionProbability = 0d;
    private int rank = 0;
    private int numberOfWins = 0;

    private int generation = 0;
    private boolean hasSeenGenerations = false;
    private boolean shouldRemoved = false;
    private boolean isBest = false;
    private Long hashCode = null;
    private IAtomContainer molecule = null;
    private ScoreSummary scoreSummary = null;

    public Individual(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public boolean isIsBest() {
        return isBest;
    }

    public void setIsBest(boolean isBest) {
        this.isBest = isBest;
    }

    public boolean ShouldRemoved() {
        return shouldRemoved;
    }

    public void setShouldRemoved(boolean shouldRemoved) {
        this.shouldRemoved = shouldRemoved;
    }

    public boolean hasSeenGenerations() {
        return hasSeenGenerations;
    }

    public void setHasSeenGenerations(boolean hasSeenGenerations) {
        this.hasSeenGenerations = hasSeenGenerations;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getSelectionProbability() {
        return selectionProbability;
    }

    public void setSelectionProbability(double selectionProbability) {
        this.selectionProbability = selectionProbability;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public IAtomContainer getMolecule() {
        return molecule;
    }

    public void setMolecule(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    public ScoreSummary getScoreSummary() {
        return scoreSummary;
    }

    public void setScoreSummary(ScoreSummary scoreSummary) {
        this.scoreSummary = scoreSummary;
    }

    public Long getHashCode() {
        //"0x" + Integer.toHexString(hashCode);
        return hashCode;
    }

    public void setHashCode(Long hashCode) {
        this.hashCode = hashCode;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void resetOldValues() {
        this.rank = 0;
        this.selectionProbability = 0d;
        this.numberOfWins = 0;
    }

    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Individual individual = (Individual) o;
        return individual.getHashCode().equals(this.hashCode);

    }

    
    public int hashCode() {
        return this.hashCode != null ? this.hashCode.hashCode() : 0;
    }
}
