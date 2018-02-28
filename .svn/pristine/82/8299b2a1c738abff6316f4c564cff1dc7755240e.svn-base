/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import java.util.ArrayList;

/**
 * Contains Individuals (atomContainers with Score summary)
 *
 * @author kalai
 */
public class Population<Individual> extends ArrayList<Individual> {

    private double averageSelectionProbability = 0d;
    private double bestFitness = 0d;
    private int bestIndividualIndex = 0;
    private int generation = 0;

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public Population() {
        super();
    }

    public int getBestIndividualIndex() {
        return bestIndividualIndex;
    }

    public void setBestIndividualIndex(int bestIndividualIndex) {
        this.bestIndividualIndex = bestIndividualIndex;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public double getAverageSelectionProbability() {
        return averageSelectionProbability;
    }

    public void setAverageSelectionProbability(double averageSelectionProbability) {
        this.averageSelectionProbability = averageSelectionProbability;
    }

    
    public boolean contains(Object o) {
        for (Object obj : this.toArray()) {
            Individual individual = (Individual) obj;
            if (individual.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public void removeRedundancy() {
        Population<Individual> temp = new Population<Individual>();
        for (Object obj : this.toArray()) {
            if (!temp.contains(obj)) {
                temp.add((Individual) obj);
            }
        }
        this.clear();
        this.addAll(temp);
    }

    public int countRedundancy() {
        Population<Individual> temp = new Population<Individual>();
        for (Object obj : this.toArray()) {
            if (!temp.contains(obj)) {
                temp.add((Individual) obj);
            }
        }
        return (this.size() - temp.size());
    }
}
