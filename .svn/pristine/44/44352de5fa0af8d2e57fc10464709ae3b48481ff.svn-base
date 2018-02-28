/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import java.util.Random;

/**
 * Class to randomly sample individuals from the population based on their selection probability
 * distribution.
 *
 * @author kalai
 */
public class StochasticUniversalSampler {

    SelectionProbabilityAssigner selectionProbabilityAssigner = null;
    Random random = null;

    public StochasticUniversalSampler() {
        selectionProbabilityAssigner = new SelectionProbabilityAssigner();
        random = new Random();
    }

    public Population<Individual> sample(Population<Individual> population) {

        int current_member = 0;
        int i = 0;
        double minRange = 0d;
        //System.out.println("population size -" + population.size());
        double populationSize = population.size();
        double maxRange = 1d / populationSize;
        double selectionProbability = 0d;
        // System.out.println("Max range - " + maxRange);
        /**
         * pick up a randomValue (r) uniformly from [0,1/population.size]
         */
        double randomValue = minRange + (random.nextDouble() * (maxRange));
        double sumOfRandomValues = randomValue;

        //System.out.println("initial r value = " + randomValue);

        Population<Individual> potentialParents = selectionProbabilityAssigner.assignSelectionProbabilitiesFor(population);

        Population<Individual> sampledParents = new Population<Individual>();

        do {
            // System.out.println("current member - " + current_member);
            do {
                sampledParents.add(current_member, potentialParents.get(i));
                randomValue = randomValue + maxRange;
                sumOfRandomValues = sumOfRandomValues + randomValue;
                current_member++;
                // System.out.println("Random value - " + randomValue + "Current member - " + current_member);
                selectionProbability = potentialParents.get(i).getSelectionProbability();
                //   System.out.println("sel prob : " + selectionProbability);
            } while (randomValue <= selectionProbability);
            i++;
            // System.out.println("i = " + i);
        } while (current_member < population.size() / 2);
//            
        return sampledParents;
    }
}
