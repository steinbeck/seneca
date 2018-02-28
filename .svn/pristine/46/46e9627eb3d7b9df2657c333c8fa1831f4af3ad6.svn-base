/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class to select specified number of individuals as survivors from the total pool of parents and
 * offsprings. Selection of individual is based on the number of wins from round-robin tournaments.
 * The ones with higher wins will make it into the next round.
 *
 * @author kalai
 */
public class TournamentSelection {

    private SelectionProbabilityAssigner selectionProbabilityAssigner = null;
    private Random random = null;
    private int sizeOfCurrentPopulationToEvaluate = 0;
    private int fixedPopulationSize = 0;
    private int indexOfCurrentlyCompetingIndividual = 0;
    private int numberOfOpponents = 10;
    private Population<Individual> populationForThisTournament = null;
    private Population<Individual> survivors = null;

    public TournamentSelection() {
        selectionProbabilityAssigner = new SelectionProbabilityAssigner();
        random = new Random();
        populationForThisTournament = new Population<Individual>();
    }

    public void setInitialPopulationSize(int initialPopulationSize) {
        this.fixedPopulationSize = initialPopulationSize;
    }

    public Population<Individual> selectTheWinnersFrom(Population<Individual> population) {
        survivors = new Population<Individual>();
        Population<Individual> potentialParents = selectionProbabilityAssigner.assignSelectionProbabilitiesFor(population);
        populationForThisTournament = potentialParents;
        autoSelectTopTwo();
        sizeOfCurrentPopulationToEvaluate = populationForThisTournament.size();
        conductCompetition();
        return chooseTopHalfWinners();
    }

    public Population<Individual> selectTheWinnersReducing(Population<Individual> population) {
        survivors = new Population<Individual>();
        Population<Individual> potentialParents = selectionProbabilityAssigner.assignSelectionProbabilitiesFor(population);
        populationForThisTournament = potentialParents;
        autoSelectTopTwo();
        sizeOfCurrentPopulationToEvaluate = populationForThisTournament.size();
        conductCompetition();
        return chooseWinnersAsSurvivorsOfSpecified();
    }

    private void autoSelectTopTwo() {
        for (int i = 0; i <= 1; i++) {
            Individual bestOne = populationForThisTournament.get(i);
            bestOne.setIsBest(true);
            survivors.add(bestOne);
            populationForThisTournament.remove(i);
        }
    }

    private void conductCompetition() {
        for (Individual currentIndividual : populationForThisTournament) {
            indexOfCurrentlyCompetingIndividual = populationForThisTournament.indexOf(currentIndividual);
            determineTotalWinsFor(currentIndividual);
        }
    }

    private void determineTotalWinsFor(Individual competor) {
        /*
        * choose ten other individuals from the population to act as opponents.
        */
        int winCount = 0;
        Population<Individual> opponents = getRandomOpponents();
        for (Individual opponent : opponents) {
            if (competor.getSelectionProbability() > opponent.getSelectionProbability()) {
                winCount++;
            }
        }
        competor.setNumberOfWins(winCount);
    }

    private Population<Individual> getRandomOpponents() {
        int opponentsSoFar = 0;
        Population<Individual> opponents = new Population<Individual>();
        int[] randomIndices = getRandomOpponentIndices();
        for (int i = 0; i < randomIndices.length; i++) {
            opponents.add(populationForThisTournament.get(randomIndices[i]));
        }
        return opponents;
    }

    private int[] getRandomOpponentIndices() {
        int[] result = new int[numberOfOpponents];
        Set<Integer> used = new HashSet<Integer>();

        for (int i = 0; i < numberOfOpponents; i++) {
            int newRandom;
            do {
                newRandom = random.nextInt(sizeOfCurrentPopulationToEvaluate);
            } while (used.contains(newRandom) || newRandom == indexOfCurrentlyCompetingIndividual); //
            result[i] = newRandom;
            used.add(newRandom);
        }
        return result;
    }

    private Population<Individual> chooseTopHalfWinners() {
        Population<Individual> sortedBasedOnWinCounts = sortByWinCount(populationForThisTournament);
        /**
         * The top two individuals are auto-selected so we reduce the index size by 1.
         */
        int numberOfIndividualsToBeChosen = (populationForThisTournament.size() / 2) - 1;
        for (int i = 0; i < numberOfIndividualsToBeChosen; i++) {
            survivors.add(sortedBasedOnWinCounts.get(i));
        }
        return survivors;
    }

    private Population<Individual> sortByWinCount(Population<Individual> populationWithWinCounts) {
        boolean somethingsChanged = false;
        Object o1;
        do {
            somethingsChanged = false;
            for (int f = 0; f < populationWithWinCounts.size() - 1; f++) {
                if (populationWithWinCounts.get(f).getNumberOfWins() < populationWithWinCounts.get(f + 1).getNumberOfWins()) {
                    o1 = populationWithWinCounts.get(f + 1);
                    populationWithWinCounts.remove(f + 1);
                    populationWithWinCounts.add(f, (Individual) o1);
                    somethingsChanged = true;
                }
            }
        } while (somethingsChanged);
        return populationWithWinCounts;
    }


    private Population<Individual> chooseWinnersAsSurvivorsOfSpecified() {
        Population<Individual> sortedBasedOnWinCounts = sortByWinCount(populationForThisTournament);
        int numberOfIndividualsToBeChosen = fixedPopulationSize - 2;
        for (int i = 0; i < numberOfIndividualsToBeChosen; i++) {
            survivors.add(sortedBasedOnWinCounts.get(i));
        }
        return survivors;
    }


    public void setNumberOfOpponents(int numberOfOpponents) {
        this.numberOfOpponents = numberOfOpponents;
    }

    private void printOut(Population<Individual> evalutedPopulation) {
        for (Individual individual : evalutedPopulation) {
            System.out.println("Fitness = " + individual.getFitness() + "SelProb: " + individual.getSelectionProbability() + "win based rank: " + individual.getNumberOfWins());
        }
    }
}
