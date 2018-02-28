/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class to select eligible parents from population.
 *
 * @author kalai
 */
public class ParentSelector {

    private TournamentSelection tournaments = null;
    private PopulationSorter populationSorter = null;
    private Random random = null;

    public ParentSelector() {
        tournaments = new TournamentSelection();
        populationSorter = new PopulationSorter();
        random = new Random();
    }

    public TournamentSelection getTournaments() {
        return tournaments;
    }

    public Population useTournamentsToSelect(Population<Individual> evaluated) {
        return tournaments.selectTheWinnersFrom(evaluated);
    }

    public Population useTournamentsToReduce(Population<Individual> evaluated) {
        // System.out.println("size before sampling - " + fullyEvaluatedPopulation.size());
        return tournaments.selectTheWinnersReducing(evaluated);
    }

    public Population deleteWorstIndividuals(Population<Individual> fullyEvaluatedPopulation) {
        Population<Individual> sortedPopulation = populationSorter.sortByFitness(fullyEvaluatedPopulation);
        int populationSize = sortedPopulation.size();
        int individualsToRemove = (int) (fullyEvaluatedPopulation.size() / 4d);
        //System.out.println("to remove : " + individualsToRemove);
        for (int i = populationSize - 1; i >= (populationSize - individualsToRemove); i--) {
            sortedPopulation.remove(i);
        }
        return sortedPopulation;
    }

    public Population deleteRandomWorstIndividualsFrom(Population<Individual> fullyEvaluatedPopulation) {
            /*
             * remove any 1/4th of the individual randomly from second half of the population.
             */
        Population<Individual> sortedPopulation = populationSorter.sortByFitness(fullyEvaluatedPopulation);
        int populationSize = sortedPopulation.size();
        int[] randomIndices = getRandomIndices(populationSize);
        for (int k = populationSize - 1; k >= 0; k--) {
            for (int j = randomIndices.length - 1; j >= 0; j--) {
                if (k == randomIndices[j]) {
                    sortedPopulation.remove(randomIndices[j]);
                    break;
                }
            }
            if ((populationSize - (int) (populationSize / 4d)) == sortedPopulation.size()) {
                break;
            }
        }
        return sortedPopulation;
    }

    private int[] getRandomIndices(int totalPopulationSize) {

        int halfCount = (int) (totalPopulationSize / 2d);
        int toRemove = (int) (totalPopulationSize / 4d);
        int[] result = new int[toRemove];
        int range = totalPopulationSize - halfCount;
        Set<Integer> used = new HashSet<Integer>();
        for (int i = 0; i < toRemove; i++) {
            int newRandom;
            do {
                newRandom = random.nextInt(range) + halfCount;
            } while (used.contains(newRandom));
            result[i] = newRandom;
            used.add(newRandom);
        }
        return result;
    }
}
