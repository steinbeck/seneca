/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

/**
 * Sorts population in descending order based on fitness
 *
 * @author kalai
 */
public class PopulationSorter {

    private double averageFitnessOfThePopulation = 0d;

    public PopulationSorter() {
    }

    public double getAverageFitness(Population<Individual> population) {
        double totalFitnessOfPopulation = 0d;
        for (Individual individual : population) {
            double fitness = individual.getScoreSummary().costValue;
            totalFitnessOfPopulation += fitness;
        }
        averageFitnessOfThePopulation = totalFitnessOfPopulation / population.size();
        return averageFitnessOfThePopulation;
    }

    public Population<Individual> sortByFitness(Population<Individual> population) {
        boolean somethingsChanged = false;
        Object o1, o2;
        do {
            somethingsChanged = false;
            for (int f = 0; f < population.size() - 1; f++) {
                if (population.get(f).getFitness() < population.get(f + 1).getFitness()) {
                    o1 = population.get(f + 1);
                    population.remove(f + 1);
                    population.add(f, (Individual) o1);
                    somethingsChanged = true;
                }
            }
        } while (somethingsChanged);
        return population;
    }
}
