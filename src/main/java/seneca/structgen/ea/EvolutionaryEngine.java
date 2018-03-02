/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.FixedSizeStack;
import seneca.core.SenecaConstants;
import seneca.core.Utilities;
import seneca.judges.ChiefJustice;
import seneca.judges.ScoreSummary;
import seneca.structgen.StructureGeneratorResult;
import seneca.structgen.annealinglog.CommonAnnealingLog;
import seneca.structgen.ea.crossover.CrossOver;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class will take the population and the fitness function as input. Evaluate the population of
 * individuals based on fitness. Do parent(eachIndividual) selection based on fitness, to generate
 * offsprings. Mutate function to generate offsprings. (Stochastic)
 * <p/>
 * Survivor selector (deterministic) will make the population size remain constant by eliminating
 * old and unfit ones
 *
 * @author kalai
 */
public class EvolutionaryEngine implements EvolvingEngine {

    private long timeWhenImprovementMade, runningTime = 0L;
    private double maxScoreFromJustice = 0d;
    private double lastBestFitness = 0d;
    private int generations = 1;
    private int changingLastBestGeneration, noImprovementCounter = 0;
    private int initialPopulationSize = 0;
    private int resultCount = 0;
    private boolean stopEvolving = false;
    private boolean terminationConditionsMet = false;
    private boolean maxScoreSet = false;
    private boolean runningWithExpandedPopulation = false;
    private Population<Individual> population = null;
    private ParentSelector parentSelector = null;
    private OffSpringProducer offSpringProducer = null;
    private PopulationSorter populationSorter = null;
    private NicheSearcher nicheSearcher = null;
    private ChiefJustice justice = null;
    private CommonAnnealingLog annealingLog = null;
    private IAtomContainer bestStructure = null;
    private ScoreSummary bestScoreSummary = null;
    private LifeTimeEvaluator lifeTimeEvaluator = null;
    private StructureGeneratorResult structureGeneratorResult = null;
    private FixedSizeStack previousBestIndividuals = null;
    private Population<Individual> aggregate = null;
    //private PopulationTableFrame tableFrame = null;
    private ConcentricNicheExpander concentricNicheExpander = null;
    private Logger evolutionLogger = null;
    private Logger logger = Logger.getLogger(EvolutionaryEngine.class);
    private DecimalFormat formatter = new DecimalFormat("#.####");
    private MoleculeHashGenerator hashGenerator = null;
    private CrossOver crossOver = null;
    //  FileWriter redundancyWriter = null;


    private boolean commandline = false;

    public EvolutionaryEngine() {

        parentSelector = new ParentSelector();
        lifeTimeEvaluator = new LifeTimeEvaluator();
        offSpringProducer = new OffSpringProducer();
        this.annealingLog = new CommonAnnealingLog();
        populationSorter = new PopulationSorter();
        formatter = new DecimalFormat("##.####");
        nicheSearcher = new NicheSearcher();
        concentricNicheExpander = new ConcentricNicheExpander();
        //tableFrame = new PopulationTableFrame();
        hashGenerator = new HashGeneratorMaker().depth(16).elemental().molecular();
        crossOver = new CrossOver(stopEvolving);
        aggregate = new Population<Individual>();
    }

    
    public void initAnnealing(Population population, ChiefJustice justice) {
        this.population = population;
        this.justice = justice;
        this.initialPopulationSize = population.size();
        this.parentSelector.getTournaments().setInitialPopulationSize(this.population.size());
    }

    public void setProgressLogger(Logger logger) {
        this.evolutionLogger = logger;
    }

    
    public void run() {
//        try {
        System.out.println("Running evolutionary engine..");

        // redundancyWriter = new FileWriter(new File("/Users/kalai/Develop/projects/NP-inCASE/r-" + String.valueOf(new Long(new Date().getTime())) + ".txt"));
        setGenerationNumber(population);
        Population evaluatedPopulation = evaluate(population);
        // redundancyWriter.write("total;redundant\n");
        // redundancyWriter.write(evaluatedPopulation.size() + ";" + evaluatedPopulation.countRedundancy() + "\n");
        if (terminationConditionsMetBy(evaluatedPopulation)) {
            return;
        }
        evaluatedPopulation.setGeneration(generations);
        // tableFrame.add(evaluatedPopulation);
        do {
            //System.out.println("/////////////////////////////////");
            generations++;
            //System.out.println(generations + "---------------");
            Population parentsAndOffSprings = offSpringProducer.produceExactOffspringsFrom(evaluatedPopulation);
            setGenerationNumber(parentsAndOffSprings);
            evaluatedPopulation = evaluate(parentsAndOffSprings);

            updateStructureGenerator(populationSorter.sortByFitness(evaluatedPopulation));
            updateResultsHolderWith(evaluatedPopulation);

            Population<Individual> ranksReset = resetOldValues(evaluatedPopulation);
            Population survived = doSurvivorSelection(ranksReset);
            evaluatedPopulation = survived;
            //  redundancyWriter.write(evaluatedPopulation.size() + ";" + evaluatedPopulation.countRedundancy() + "\n");

            // doDiversityCheck(evaluatedPopulation);
            //    updateTable(evaluatedPopulation);
        } while (!terminationConditionsMetBy(evaluatedPopulation));
        updateResultsHolderWith(evaluatedPopulation);
        if (commandline) {
            convertAggregateToResult();
        }
        System.out.println("Finished : Now ready for display");
        //  redundancyWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void setGenerationNumber(Population<Individual> population) {
        for (Individual individual : population) {
            if (individual.getGeneration() == 0) {
                individual.setGeneration(generations);
            }
//            IAtomContainer mol = individual.getMolecule();
//            if (mol.getProperty(SenecaConstants.GENERATION_NUMBER) == null) {
//                mol.setProperty((SenecaConstants.GENERATION_NUMBER), generations);
//            }
        }
    }

    private Population<Individual> doDiversityCheck(Population<Individual> evaluatedPopulation) {
        //     if (generations % 50 == 0)
        if (noImprovementCounter >= 25) {
            //     Individual best = evaluatedPopulation.get(0);
            noImprovementCounter = 0;
            System.out.println("stagnant for 50 gens so crossing over");
            int originalSize = evaluatedPopulation.size();
            evaluatedPopulation.removeRedundancy();
            int currentSize = evaluatedPopulation.size();
            int toAdd = originalSize - currentSize;
            System.out.println("original: " + originalSize + " , after removeAll: " + currentSize + " , to add: " + toAdd);
            if (toAdd == 0) {
                evaluatedPopulation = parentSelector.deleteWorstIndividuals(evaluatedPopulation);
                System.out.println("After deleting: " + evaluatedPopulation.size());
            }
            //    evaluatedPopulation.addAll(concentricNicheExpander.expand(best, toAdd));
            evaluatedPopulation.addAll(crossOver.crossAndExpand(evaluatedPopulation, toAdd));
            System.out.println("After crossing and adding: " + evaluatedPopulation.size());
            return evaluatedPopulation;
        }
        return evaluatedPopulation;
    }

    private Population<Individual> evaluate(Population<Individual> population) {
        Population<Individual> withFitness = new Population<Individual>();
        for (Individual individual : population) {
            if (individual.getFitness() > 0d) {
                withFitness.add(individual);
            } else {
                withFitness.add(fitnessOf(individual));
            }
        }
        return withFitness;
    }

    private Population<Individual> evaluateByThreading(Population<Individual> population) {
        Population<Individual> withFitness = new Population<Individual>();
        List<Future<Individual>> individuals = new ArrayList<Future<Individual>>();
        ExecutorService executor = Executors.newFixedThreadPool(population.size());
        for (Individual individual : population) {
            if (individual.getFitness() > 0d) {
                withFitness.add(individual);
            } else {
                Future<Individual> evaluated = executor.submit(new FitnessCalculator(
                        (ChiefJustice) Utilities.cloneObject(justice),
                        individual));
                individuals.add(evaluated);
            }
        }
        executor.shutdown();

        for (Future<Individual> ind : individuals) {
            try {
                withFitness.add(ind.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return withFitness;
    }

    private Individual fitnessOf(Individual individual) {
        ScoreSummary summary = this.justice.getScoreByThreading(individual.getMolecule());
        assignMaxScoreOnlyOnceToCalculateCost(summary.maxScore);
        if (maxScoreSet) {
            // summary.costValue = summary.score / this.maxScoreFromJustice;
            individual.setScoreSummary(summary);
            individual.setFitness(summary.costValue);
        }
        return individual;
    }

    private void assignMaxScoreOnlyOnceToCalculateCost(Double summary_maxScore) {
        if (!maxScoreSet) {
            if (summary_maxScore != -1d) {
                this.maxScoreFromJustice = summary_maxScore;
                maxScoreSet = true;
            }
        }
    }

    private Population<Individual> resetOldValues(Population<Individual> evaluated) {
        for (Individual individual : evaluated) {
            individual.resetOldValues();
        }
        return evaluated;
    }

    private Population<Individual> doSurvivorSelection(Population<Individual> population) {
        Population<Individual> parents = getHalfAsParents(population);
        return (noImprovementMadeRecently() ? expandOrReduce(parents) : parents);
    }

    private Population<Individual> getHalfAsParents(Population<Individual> population) {
        Population withOffsprings = parentSelector.useTournamentsToSelect(population);
        return withOffsprings;
    }

    private boolean noImprovementMadeRecently() {
        //System.out.println("last one: " + (generations - changingLastBestGeneration));
        return ((generations - changingLastBestGeneration) > 5);
    }

    private Population<Individual> expandOrReduce(Population<Individual> population) {
        return (!isExpanded() ? expand(population) : reduce(population));
    }

    private boolean isExpanded() {
        return runningWithExpandedPopulation;
    }

    private Population<Individual> expand(Population<Individual> population) {
        Population withoutWorst = parentSelector.deleteWorstIndividuals(population);
        Population expanded = nicheSearcher.expand(withoutWorst);
        //System.out.println("EXPANDED size: " + expanded.size());
        changingLastBestGeneration = generations;
        runningWithExpandedPopulation = true;
        return expanded;
    }

    private Population<Individual> reduce(Population<Individual> population) {
        Population reduced = parentSelector.useTournamentsToReduce(population);
        //System.out.println("REDUCED size: " + reduced.size());
        runningWithExpandedPopulation = false;
        changingLastBestGeneration = generations;
        return reduced;
    }

    private void printOutGenerationResult(Population<Individual> population) {
        System.out.println("---------size: " + population.size() + "--------------");
        for (Individual individual : population) {
            System.out.println("Fitness = " + individual.getFitness()
                    + " rank: " + individual.getRank() + " rankBasedFitness: "
                    + individual.getSelectionProbability());
        }
    }

//    private void updateTable(final Population<Individual> evaluated) {
//        int reminder = generations % 150;
//        if (reminder <= 3) {
//            System.out.println(generations);
//            printOutGenerationResult(evaluated);
//            evaluated.setGeneration(generations);
//            SwingUtilities.invokeLater(new Runnable() {
//                
//                public void run() {
//                    {
//                        System.out.println("running..");
//                        //tableFrame.add(evaluated);
//                    }
//                }
//            });
//        }
//    }

    private void printAfterExpansion(Population<Individual> evaluted) {
        for (Individual individual : evaluted) {
            System.out.println("Fitness = " + individual.getFitness()
                    + " win rank: " + individual.getNumberOfWins()
                    + " Fitness: " + individual.getFitness());
        }
    }

    private void updateStructureGenerator(Population<Individual> evaluated) {
        if (!commandline) {
            int bestStructureIndex = 0;
            Individual best = evaluated.get(bestStructureIndex);
            Double fitness = best.getScoreSummary().costValue;
            updateProgressLog(evaluated);
            //    updateProgressLog(best.getScoreSummary());
            checkImproved(fitness);
            if (ConnectivityChecker.isConnected(best.getMolecule())) {
                this.bestStructure = best.getMolecule();
                this.annealingLog.addEntry(generations, 0d, fitness);
                this.bestScoreSummary = best.getScoreSummary();
            }
        } else {
            addToAggregate(evaluated);
            updateProgressLog(evaluated);
        }
    }

    private void updateProgressLog(ScoreSummary scoreSummary) {
        evolutionLogger.info(generations + ";" + formatter.format(scoreSummary.costValue) + scoreSummary.allJudgeScores);
    }

    private void updateProgressLog(Population<Individual> population) {
        for (Individual i : population) {
            updateProgressLog(i.getScoreSummary());
        }
    }

    private void checkImproved(Double fitness) {
        if (fitness > this.lastBestFitness) {
            this.lastBestFitness = fitness;
            this.changingLastBestGeneration = this.generations;
            this.noImprovementCounter = 0;
            this.timeWhenImprovementMade = System.currentTimeMillis();
            //System.out.println("FITNESS UPDATE: Last best generation : " + changingLastBestGeneration);
            return;
        } else {
            runningTime = System.currentTimeMillis();
            noImprovementCounter++;
        }
    }

    private void updateResultsHolderWith(Population evaluated) {
        if (!commandline) {
            if (structureGeneratorResult.structures.isEmpty()) {
                previousBestIndividuals = new FixedSizeStack(resultCount);
                addAllStructures(evaluated);
            } else {
                add(evaluated);
            }
        }
    }

    private void addAllStructures(Population<Individual> evaluated) {
        for (Individual individual : evaluated) {
            structureGeneratorResult.structures.push(copyPropertiesOf(individual));
            previousBestIndividuals.push(individual);
        }
    }

    private IAtomContainer copyPropertiesOf(Individual individual) {
        IAtomContainer molecule = individual.getMolecule();
        molecule.setProperty("Score", formatter.format(individual.getFitness()));
        molecule.setProperty("HashCode", individual.getHashCode());
        molecule.setProperty(SenecaConstants.GENERATION_NUMBER, individual.getGeneration());
        return molecule;
    }

    private void add(Population<Individual> evaluated) {
        Population<Individual> sorted = mergeSort(evaluated);
        structureGeneratorResult.structures = new FixedSizeStack(resultCount);
        for (int i = sorted.size() - 1; i >= 0; i--) {
            Individual individual = sorted.get(i);
            IAtomContainer moleculeWithScore = copyPropertiesOf(individual);
            structureGeneratorResult.structures.push(moleculeWithScore);
            previousBestIndividuals.push(individual);
        }
    }

    private Population mergeSort(Population<Individual> evaluated) {
        Population<Individual> merged = new Population<Individual>();
        merged.addAll(previousBestIndividuals);
        merged.addAll(evaluated);
        merged.removeRedundancy();
        Population<Individual> sorted = populationSorter.sortByFitness(merged);
        // sorted.removeRedundancy();
        return sorted;
    }

    private void addToAggregate(Population<Individual> evaluated) {
        aggregate.addAll(evaluated);
        //  System.out.println(aggregate.size());
    }

    private boolean terminationConditionsMetBy(Population<Individual> evaluated) {
        if (stopEvolving) {
            return true;
        }
//        for (Individual individual : evaluated) {
//            if (individual.getFitness() == TerminationConditions.maxmimumFitness) {
//                updateStructureGenerator(individual);
//                terminationConditionsMet = true;
//                logger.info("Maximum fitness reached");
//                return true;
//            }
//        }
        if (generations == TerminationConditions.maximumGenerations) {
            terminationConditionsMet = true;
            logger.info("Terminated as maximum allowed generations(" + TerminationConditions.maximumGenerations + ") reached");
            return true;
        }
//            if (noImprovementCounter == TerminationConditions.maximumAllowedGenerationsWithNoImprovement) {
//                  System.out.println("NO IMPROVEMENT IN: " + noImprovementCounter);
//                  terminationConditionsMet = true;
//                  return true;
//            }
//        if ((runningTime - timeWhenImprovementMade) >= TerminationConditions.maximumTimeAllowedIn_ms_WithoutNoImprovement) {
//            System.out.println("NO IMPROVEMENT IN: " + (runningTime - timeWhenImprovementMade) / 60000 + " - mins");
//            logger.info("NO IMPROVEMENT IN: " + (runningTime - timeWhenImprovementMade) / 60000 + " - mins");
//            terminationConditionsMet = true;
//            return true;
//        }
        return false;
    }

    private void updateStructureGenerator(Individual individual) {
        Double fitness = individual.getScoreSummary().costValue;
        if (ConnectivityChecker.isConnected(individual.getMolecule())) {
            this.bestStructure = individual.getMolecule();
            this.annealingLog.addEntry(generations, 0d, fitness);
            this.bestScoreSummary = individual.getScoreSummary();
            System.out.println("Fitness : " + fitness);
        }
    }


    private void convertAggregateToResult() {
        logger.info("Sorting the aggregate: " + aggregate.size());
        System.out.println("Sorting the aggregate: " + aggregate.size());
        aggregate.removeRedundancy();
        logger.info("After redundancy removal: " + aggregate.size());
        System.out.println("After redundancy removal: " + aggregate.size());

        Population<Individual> sorted = populationSorter.sortByFitness(aggregate);
        structureGeneratorResult.structures = new FixedSizeStack(sorted.size());
        for (int i = sorted.size() - 1; i >= 0; i--) {
            Individual individual = sorted.get(i);
            IAtomContainer moleculeWithScore = copyPropertiesOf(individual);
            structureGeneratorResult.structures.push(moleculeWithScore);
        }
    }


    public CommonAnnealingLog getUpdatedAnnealingLog() {
        return this.annealingLog;
    }

    public IAtomContainer getBestStructure() {
        return this.bestStructure;
    }

    public ScoreSummary getScoreSummary() {
        return this.bestScoreSummary;
    }


    
    public boolean isFinished() {
        return terminationConditionsMet;
    }

    
    public int getGenerations() {
        return generations;
    }

    
    public void stopEvolving() {
        this.stopEvolving = true;
        System.out.println("Okay, stopping evolution...");
    }

    public void setStructureGeneratorResult(StructureGeneratorResult structureGeneratorResult) {
        this.structureGeneratorResult = structureGeneratorResult;
        this.resultCount = structureGeneratorResult.size();
    }

    public boolean isCommandline() {
        return commandline;
    }

    public void setCommandline(boolean commandline) {
        this.commandline = commandline;
    }

}
