/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

/**
 * Class to assign life-time to individuals based on fitness. Assigned life-time is reduced in
 * consecutive generations and when reaches 0, removed from the population.
 *
 * @author kalai
 */
public class LifeTimeEvaluator {

    public Population<Individual> assignLifeTimeForIndividualsIn(Population<Individual> population) {
        for (Individual individual : population) {
            if (individual.getLifeTime() > 0) {
                long lifetime = individual.getLifeTime();
                individual.setLifeTime(lifetime - 1);
            } else if (individual.getLifeTime() == 0) {
                if (individual.hasSeenGenerations() == false) {
                    long lifetime = getLifeTimeFor(individual);
                    individual.setLifeTime(lifetime);
                    individual.setHasSeenGenerations(true);
                } else {
                    if (!individual.isIsBest()) {
                        individual.setShouldRemoved(true);
                    }
                }
            }
        }
        return population;
    }

    public long assignLifeTimeFor(Individual individual) {

        if (individual.getLifeTime() == 0) {
            long lifetime = getLifeTimeFor(individual);
            return lifetime;
        } else {
            return individual.getLifeTime() - 1;
        }
    }

    private long getLifeTimeFor(Individual individual) {
        double fitness = individual.getFitness();
        double lifetime = fitness * 5;
        return Math.round(lifetime);
    }
}
