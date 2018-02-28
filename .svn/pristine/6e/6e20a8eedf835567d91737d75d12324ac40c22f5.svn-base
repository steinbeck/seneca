/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

/**
 * Class to assign selection probability to eachIndividual, based on its fitness rank in the
 * population. So, assignment is based on the rank obtained through fitness and not based on fitness
 * itself
 *
 * @author kalai
 */
public class SelectionProbabilityAssigner {

    private PopulationSorter populationSorter = null;
    private int populationSize = 0;
    Population<Individual> populationWithSelectionProbability = null;
    private double selectionPressure = 1.5;

    public SelectionProbabilityAssigner() {
        populationSorter = new PopulationSorter();
    }

    public Population<Individual> assignSelectionProbabilitiesFor(Population<Individual> population) {
        populationSize = population.size();
        populationWithSelectionProbability = new Population<Individual>();
        //   double sumOfProbabilities = 0d;
        Population<Individual> sortedPopulation = populationSorter.sortByFitness(population);
        Population<Individual> individualsWithRank = assignRankToIndividuals(sortedPopulation);
        for (Individual eachIndividual : individualsWithRank) {
            Individual individualWithSelectionProbability = getSelectionProbabilityOf(eachIndividual);
            //    sumOfProbabilities += individualWithSelectionProbability.getSelectionProbability();
            populationWithSelectionProbability.add(individualWithSelectionProbability);
        }
//            populationWithSelectionProbability.setAverageSelectionProbability(sumOfProbabilities / populationSize);
//            double maximumFitness = getMaximumFitnessIn(populationWithSelectionProbability);
//            populationWithSelectionProbability.setBestFitness(maximumFitness);
//            populationWithSelectionProbability.setBestIndividualIndex(0);

        return populationWithSelectionProbability;
    }

    private Population<Individual> assignRankToIndividuals(Population<Individual> sortedPopulation) {
        /**
         * The population is previously sorted in a descending order.So, we get the lowest
         * fitness ones in reverse order and assign lower ranks starting from 0..
         */
        int rank = 0;
        for (int i = sortedPopulation.size() - 1; i >= 0; i--) {
            Individual individual = sortedPopulation.get(i);
            individual.setRank(rank);
            rank++;
        }
        return sortedPopulation;
    }

    private Individual getSelectionProbabilityOf(Individual individual) {
        int rank = individual.getRank();
        double selectionProbability = ((2 - selectionPressure) / populationSize)
                + (((2 * rank) * (selectionPressure - 1)) / (populationSize * (populationSize - 1)));
        individual.setSelectionProbability(selectionProbability);
        return individual;
    }

    private Individual getExponentialSelectionProbabilityOf(Individual individual) {
        int rank = individual.getRank();
        double selectionProbability = (1 - Math.exp(rank)) / populationSize;
        individual.setSelectionProbability(selectionProbability);
        return individual;
    }

    private Individual getFitnessBasedOnRank(Individual individual) {
        int rank = individual.getRank();
        //  double selectionProbability = (1 - Math.exp(rank)) / populationSize;
        double fitnessBasedOnRank = (2 - selectionPressure) + (2 * (selectionPressure - 1) * ((rank - 1) / (populationSize - 1)));
        individual.setSelectionProbability(fitnessBasedOnRank);
        return individual;
    }

    private double getMaximumFitnessIn(Population<Individual> populationWithFitness) {
        int lastIndex = populationWithFitness.size() - 1;
        Individual individual = populationWithFitness.get(lastIndex);
        return individual.getFitness();
    }
}
