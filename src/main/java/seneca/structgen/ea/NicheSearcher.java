/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.Utilities;
import seneca.judges.ChiefJustice;
import seneca.judges.ScoreSummary;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class to increase the population size to a specified percentage, by doing niche search around the
 * best individuals.
 *
 * @author kalai
 */
public class NicheSearcher {

    private int newIndividualsToBeProduced = 0;
    private int inputPopulationSize = 0;
    private int currentSampleSize = 0;
    private int topCandidatesToBeSampled = 4;
    private int moleculesToBeChosenFromEachSample = 0;
    private Random random = null;
    private Population<Individual> newPopulationNiche = null;
    private Population<Individual> current = null;
    private PopulationSorter populationSorter = null;
    private ChiefJustice chiefjustice = null;
    private OffSpringProducer offspringProducer = null;
    private MoleculeHashGenerator hashGenerator = null;

    public NicheSearcher() {

        random = new Random();
        populationSorter = new PopulationSorter();
        offspringProducer = new OffSpringProducer();
        hashGenerator = new HashGeneratorMaker().depth(16).elemental().molecular();

    }

    public Population<Individual> expand(Population<Individual> parentPopulation) {
        newPopulationNiche = new Population<Individual>();
        inputPopulationSize = parentPopulation.size();
        determineHowManyMoleculesToSelectFromEachSample();
        Population<Individual> sortedPopulation = populationSorter.sortByFitness(parentPopulation);
        current = new Population<Individual>();
        current.addAll(sortedPopulation);
        Population<Individual> nicheSearched = nicheSearchForThe(sortedPopulation);
        nicheSearched.addAll(sortedPopulation);
        return nicheSearched;
    }

    private void determineHowManyMoleculesToSelectFromEachSample() {
        this.newIndividualsToBeProduced = inputPopulationSize;
        this.moleculesToBeChosenFromEachSample = this.newIndividualsToBeProduced / this.topCandidatesToBeSampled;
    }

    private Population<Individual> nicheSearchForThe(Population<Individual> sorted) {
        if (requiredIndividualsSampledSuccessfullyFrom(sorted)) {
            return newPopulationNiche;
        } else {
            return sampleTopIndividualsUntilRequirementIsMet(sorted);
        }
    }

    private boolean requiredIndividualsSampledSuccessfullyFrom(Population<Individual> sortedPopulation) {
        int individualsSampledSuccessfully = 0;
        int unSuccessfulAttempts = 0;
        boolean success = false;
        do {
            for (int i = 0; i < sortedPopulation.size(); i++) {
                Individual individual = sortedPopulation.get(i);
                List<IAtomContainer> nicheSearchedMolecules = doNicheSearchForThis(individual);

                if (nicheSearchedMolecules.size() >= moleculesToBeChosenFromEachSample) {
                    Population<Individual> nicheIndividuals = selectRandomMoleculesAndMakeNewIndividualsUsing(nicheSearchedMolecules);
                    newPopulationNiche.addAll(nicheIndividuals);
                    individualsSampledSuccessfully++;
                } else {
                    unSuccessfulAttempts++;
                }
                if (individualsSampledSuccessfully == topCandidatesToBeSampled) {
                    success = true;
                    break;
                }
            }
            if (unSuccessfulAttempts == sortedPopulation.size()) {
                success = false;
                break;
            }
        } while (individualsSampledSuccessfully != topCandidatesToBeSampled);
        return success;
    }

    private List<IAtomContainer> doNicheSearchForThis(Individual individual) {

        List<IAtomContainer> sample = InternalVicinitySampler.sample(individual.getMolecule());
        return sample.isEmpty() ? new ArrayList<IAtomContainer>() : sample;
        //sample = VicinitySampler.sample(individual.getMolecule());

//        if (sample.isEmpty()) {
//            if (!ConnectivityChecker.isConnected(individual.getMolecule())) {
//                System.out.println("DISCONNECTED TO NICHE SEARCH");
//            }
//            return new ArrayList<IAtomContainer>();
//        }
//        return sample;

    }

    private Population<Individual> selectRandomMoleculesAndMakeNewIndividualsUsing(List<IAtomContainer> sample) {
        currentSampleSize = sample.size();
        int[] randomIndices = getRandomIndices();
        Population<Individual> sampleIndividuals = new Population<Individual>();
        for (int i = 0; i < randomIndices.length; i++) {
            IAtomContainer moleculeInVicinity = sample.get(randomIndices[i]);
            Individual individual = new Individual(moleculeInVicinity);
            individual.setHashCode(hashGenerator.generate(moleculeInVicinity));
            sampleIndividuals.add(individual);
        }
        return sampleIndividuals;
    }

    private int[] getRandomIndices() {
        int[] result = new int[moleculesToBeChosenFromEachSample];
        Set<Integer> used = new HashSet<Integer>();

        for (int i = 0; i < moleculesToBeChosenFromEachSample; i++) {
            int newRandom;
            do {
                newRandom = random.nextInt(currentSampleSize);
            } while (used.contains(newRandom));
            result[i] = newRandom;
            used.add(newRandom);
        }
        return result;
    }

    private Population<Individual> sampleTopIndividualsUntilRequirementIsMet(Population<Individual> sorted) {
        System.out.println("UNTIL requirement met");
        int unSuccessfulAttempts = 0;
        do {
            for (int i = 0; i < sorted.size(); i++) {
                Individual individual = sorted.get(i);
                List<IAtomContainer> nicheSearchedMolecules = doNicheSearchForThis(individual);
                if (nicheSearchedMolecules.size() > 0) {
                    Population<Individual> nicheIndividuals = makeIndividualsFrom(nicheSearchedMolecules);
                    if (succesfullyAdded(nicheIndividuals)) {
                        break;
                    }
                } else {
                    unSuccessfulAttempts++;
                }
            }
            if (unSuccessfulAttempts == sorted.size()) {
                mutateAndFillUp();
                System.out.println("mutated in niche search: " + newPopulationNiche.size());
                break;
            }
        } while (newPopulationNiche.size() == newIndividualsToBeProduced);
        return newPopulationNiche;
    }

    private Population<Individual> makeIndividualsFrom(List<IAtomContainer> sample) {
        Population<Individual> sampleIndividuals = new Population<Individual>();
        for (int i = 0; i < sample.size(); i++) {
            IAtomContainer moleculeInVicinity = sample.get(i);
            Individual individual = new Individual(moleculeInVicinity);
            individual.setHashCode(hashGenerator.generate(moleculeInVicinity));
            sampleIndividuals.add(individual);
        }
        return sampleIndividuals;
    }

    private boolean succesfullyAdded(Population<Individual> currentNiche) {
        int individualsSoFar = newPopulationNiche.size();
        int currentIndividualsSize = currentNiche.size();
        int required = newIndividualsToBeProduced - individualsSoFar;

        boolean maxReached = false;
        if (currentIndividualsSize <= required) {
            newPopulationNiche.addAll(currentNiche);
        } else {
            for (int i = 0; i < required; i++) {
                newPopulationNiche.add(currentNiche.get(i));
            }
        }
        if (newPopulationNiche.size() == newIndividualsToBeProduced) {
            maxReached = true;
            return maxReached;
        }
        return maxReached;
    }


    private void mutateAndFillUp() {

        int missing = newIndividualsToBeProduced - newPopulationNiche.size();
        if (missing > 0) {
            System.out.println("missing: - " + missing);
            for (int i = 0; i < missing; i++) {
                newPopulationNiche.add(offspringProducer.mutateOneMoreOffspringFrom(current.get(i)));
            }
        }
    }

    /**
     * below methods are to select niche individuals based on fitness instead of random shot
     */
    private Population<Individual> selectTopMoleculesAndMakeNewIndividualsUsing(List<IAtomContainer> sample) throws Exception {
        Population<Individual> evaluated = calculateFitness(sample);
        Population<Individual> sortedPopulation = populationSorter.sortByFitness(evaluated);
        Population<Individual> individualsToReport = new Population<Individual>();
        for (int i = 0; i < moleculesToBeChosenFromEachSample; i++) {
            individualsToReport.add(sortedPopulation.get(i));
        }
        return individualsToReport;
    }

    private Population<Individual> calculateFitness(List<IAtomContainer> sample) throws Exception {
        currentSampleSize = sample.size();
        Population<Individual> sampleIndividuals = new Population<Individual>();
        for (int i = 0; i < sample.size(); i++) {
            IAtomContainer molecule = sample.get(i);
            Individual newIndividual = new Individual(molecule);
            newIndividual.setFitness(fitnessOf(molecule));
            newIndividual.setHashCode(hashGenerator.generate(molecule));
            sampleIndividuals.add(newIndividual);
        }
        return sampleIndividuals;
    }

    private Population<Individual> calculateFitnessByThreading(List<IAtomContainer> sample) throws Exception {
        currentSampleSize = sample.size();
        Population<Individual> sampleIndividuals = new Population<Individual>();
        List<Future<Individual>> individuals = new ArrayList<Future<Individual>>();
        ExecutorService executor = Executors.newFixedThreadPool(sample.size());
        for (int i = 0; i < sample.size(); i++) {
            IAtomContainer molecule = sample.get(i);
            Individual newIndividual = new Individual(molecule);
            newIndividual.setHashCode(hashGenerator.generate(molecule));
            individuals.add(executor.submit(new FitnessCalculator(
                    (ChiefJustice) Utilities.cloneObject(chiefjustice),
                    newIndividual)));

        }
        executor.shutdown();

        for (Future<Individual> ind : individuals) {
            try {
                sampleIndividuals.add(ind.get());
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return sampleIndividuals;
    }


    private double fitnessOf(IAtomContainer moleculeInVicinity) throws Exception {
        ScoreSummary summary = this.chiefjustice.getScore(moleculeInVicinity);
        return summary.costValue;
    }

    public void setInputPopulationSize(int inputPopulationSize) {
        this.inputPopulationSize = inputPopulationSize;
    }

    public void setNumberOfTopCandidatesToBeSampled(int numberOfTopCandidatesToBeSampled) {
        this.topCandidatesToBeSampled = numberOfTopCandidatesToBeSampled;
    }

    public void setChiefjustice(ChiefJustice chiefjustice) {
        this.chiefjustice = chiefjustice;
    }
}
