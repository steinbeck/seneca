/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.gui.StructureImageGenerator;
import seneca.judges.Judge;
import seneca.structgen.StructureGenerator;
import seneca.structgen.StructureGeneratorResult;
import seneca.structgen.StructureGeneratorStatus;
import seneca.structgen.annealinglog.CommonAnnealingLog;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

/**
 * @author kalai
 */
public class EAStochasticGenerator extends StructureGenerator {

    Population<Individual> population = null;
    //RandomGenerator randomGenerator = null;
    private Thread structGenThread = null;
    EvolutionaryEngine evolEngine = null;
    CommonAnnealingLog annealingLog = null;
    int lastAnnealingCounter = 0;
    StructureImageGenerator imageGenerator = null;
    private IAtomContainer dad, mum = null;
    SaturationChecker satCheck = null;
    private IAtomContainer startMolecule = null;
    private boolean updatedWithZero = false;
    private MoleculeHashGenerator hashGenerator = null;
    private int initialPopulationSize = 16;
    private RandomGenerator randomGenerator = new RandomGenerator(null);

    public EAStochasticGenerator() {
        super();
        name = "EAStochasticGenerator";
        this.annealingLog = new CommonAnnealingLog();
        imageGenerator = new StructureImageGenerator();
        satCheck = new SaturationChecker();
        //  randomGenerator = new RandomGenerator(startMolecule);
        hashGenerator = new HashGeneratorMaker().depth(16).elemental().molecular();
    }

    
    public void start() {

        if (structGenThread == null) {
            structGenThread = new Thread(this, getName());
            structGenThread.start();
            structGenLogger.info("started structure generation");
        }
    }

    
    public void run() {
        Thread.currentThread();
        execute();
        structGenThread = null;
    }

    
    public void execute() {
        long start = System.currentTimeMillis();
        startTime = start;
        running = true;
        evolEngine = new EvolutionaryEngine();
        initializePopulation(initialPopulationSize);
        initiateProgressLogger();
        structGenLogger.info("Initiating evolutionary engine with population: " + initialPopulationSize);
        evolEngine.initAnnealing(population, chiefJustice);
        evolEngine.setProgressLogger(annealingEvolutionLogger);
        evolEngine.setStructureGeneratorResult(structureGeneratorResult);
        evolEngine.run();
        long end = System.currentTimeMillis();
        running = false;
        System.out.println("Finished evolving in : " + (end - start) + " milliSeconds");
        structGenLogger.info("Finished evolving in : " + (end - start) + " milliSeconds");
    }

    public void initializePopulation(int populationSize) {
        population = new Population<Individual>();
        startMolecule = generateSingleRandomStructure();
        //     randomGenerator.setMolecule(startMolecule);
        createInitialPopulation(startMolecule, populationSize);
        // new BondCrossOver().cross(population);
    }


    private void createInitialPopulation(IAtomContainer startMolecule, int populationSize) {
        System.out.println("Making individuals..");
        structGenLogger.info("Making individuals..");
        for (int i = 0; i < populationSize; i++) {
            System.out.println(i);
            IAtomContainer molecule = proposeStructure();
            Individual individual = new Individual(molecule);
            individual.setHashCode(hashGenerator.generate(molecule));
            population.add(individual);
        }
    }

    private IAtomContainer proposeStructure() {
        IAtomContainer anotherStructure = null;
        try {
//            anotherStructure = (IAtomContainer) Utilities.clone(startMolecule);
            anotherStructure = (IAtomContainer) startMolecule.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        randomGenerator.setMolecule(anotherStructure);
        randomGenerator.mutate(anotherStructure);
        IAtomContainer mutatedMolecule = randomGenerator.getMolecule();
        return mutatedMolecule;
    }

    private void initiateProgressLogger() {
        String judges = "";
        System.out.println(chiefJustice);
        for (Object obj : chiefJustice.getJudges()) {
            Judge judge = (Judge) obj;
            judges += ";" + judge.getName();
        }
        System.out.println(judges);
        annealingEvolutionLogger.info("Generations;Fitness" + judges);
    }


    
    public void stop() {
        if (running) {
            this.stopRunning = true;
            evolEngine.stopEvolving();
            structGenLogger.info("stopped structure generation");
        }
    }

    
    public Object getStatus() throws Exception {
        StructureGeneratorStatus sgs = new StructureGeneratorStatus();
        Double[] entry;
        sgs.molecularFormula = molecularFormula;
        sgs.datasetName = this.datasetName;
        sgs.annealingLog = new CommonAnnealingLog();
        this.annealingLog = evolEngine.getUpdatedAnnealingLog();
        int currentCount = this.annealingLog.getTotalEntriesCountIn(0);
        if (!updatedWithZero) {
            sgs.annealingLog.addEntry(0d, 0d, 0d);
            updatedWithZero = true;
        }
        for (int f = lastAnnealingCounter; f < currentCount; f++) {
            entry = this.annealingLog.getEntry(0, f);
            sgs.annealingLog.addEntry(entry[0], entry[1], entry[2]);
        }

        lastAnnealingCounter = currentCount;
        sgs.iteration = evolEngine.getGenerations();
        sgs.bestStructure = evolEngine.getBestStructure();
        sgs.bestEvaluation = evolEngine.getScoreSummary();
        if (getStructGenThread() == null) {
            sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.IDLE];
        } else {
            sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.RUNNING];
        }
        if (stopRunning) {
            sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.STOPPED];
            sgs.timeTaken = timeTakenSoFar();
            return sgs;
        }
        if (evolEngine.isFinished()) {
            sgs.status = StructureGeneratorStatus.statusStrings[StructureGeneratorStatus.FINISHED];
            // if (lastAnnealingCounter != 0 && lastAnnealingCounter == currentCount)
            entry = this.annealingLog.getEntry(0, lastAnnealingCounter - 1);
            sgs.annealingLog.addEntry(entry[0], entry[1], entry[2]);
        }
        sgs.timeTaken = timeTakenSoFar();
        return sgs;
    }

//    
//    public long getSpeed() {
//        long start = System.currentTimeMillis();
//        IAtomContainer testMol = (IAtomContainer) MoleculeFactory.makeAlphaPinene();
//        RandomGenerator rg = new RandomGenerator(testMol);
//        for (int i = 1; i < 10000; i++) {
//            rg.proposeStructure();
//            rg.acceptStructure();
//        }
//        long stop = System.currentTimeMillis();
//        return stop - start;
//    }

    public Thread getStructGenThread() {
        return structGenThread;
    }

    private int getTotalImplicitHCount(IAtomContainer molecule) {
        int hcount = 0;
        for (IAtom atom : molecule.atoms()) {
            hcount += AtomContainerManipulator.countHydrogens(molecule, atom);
        }
        return hcount;
    }

    private void writeImage(IAtomContainer molecule, String name) throws Exception {
        BufferedImage image = imageGenerator.generateStructureImage(molecule, new Dimension(186, 186));
        String filename = "/Users/kalai/Images/" + name;
        ImageIO.write((RenderedImage) image, "PNG", new File(filename));
    }

    private void crossCheckDadAndMum() {
        int match = 0;
        IAtomContainer dad = population.get(0).getMolecule();
        IAtomContainer mom = population.get(1).getMolecule();
        //System.out.println("Daddy");
        for (IAtom dadAtom : dad.atoms()) {
            System.out.println("For dad..");
            for (IAtom momAtom : mom.atoms()) {
                System.out.println("For mom..");
                System.out.println("dad atom : " + dadAtom.getSymbol());
                if (dadAtom.getSymbol().equals(momAtom.getSymbol())) {
                    System.out.println("Symbol equals..");
                    System.out.println("Bond order sum of dad: " + dad.getBondOrderSum(dadAtom));
                    System.out.println("Bond order sum of mom : " + mom.getBondOrderSum(momAtom));
                    if (dad.getBondOrderSum(dadAtom) == mom.getBondOrderSum(momAtom)) {
                        match++;
                        System.out.println("match..");
                        break;
                    }
                }
                System.out.println("no match..");
            }
            // System.out.println(atom.toString());
        }
//            System.out.println("Mommy");
//            for(IAtom atom : mom.atoms()){
//                  System.out.println(atom.toString());
//            }
        System.out.println("Matching atoms : " + match);
    }

    private void checkAtomIndices() {
        IAtomContainer dad = population.get(0).getMolecule();
        IAtomContainer mom = population.get(1).getMolecule();
        System.out.println("Daddy");
        for (IAtom dadAtom : dad.atoms()) {
            System.out.println(dadAtom.toString());
            System.out.println("Index: " + dad.getAtomNumber(dadAtom) + " Symbol : " + dadAtom.getSymbol() + " BondorderSum: " + dad.getBondOrderSum(dadAtom));
        }
        System.out.println("BONDORDERSUM DAD: " + getTotalBondOrderSumFor(dad));
        System.out.println("Mommy");
        for (IAtom momAtom : mom.atoms()) {
            System.out.println(momAtom.toString());
            System.out.println("Index: " + mom.getAtomNumber(momAtom) + " Symbol : " + momAtom.getSymbol() + " BondorderSum: " + mom.getBondOrderSum(momAtom));
        }
        System.out.println("BONDORDERSUM MUM: " + getTotalBondOrderSumFor(mom));

    }

    private double getTotalBondOrderSumFor(IAtomContainer molecule) {
        double bondOrder = 0;
        for (IAtom atom : molecule.atoms()) {
            bondOrder += AtomContainerManipulator.getBondOrderSum(molecule, atom);
        }
        return bondOrder;
    }

    
    public StructureGeneratorResult call() throws Exception {
        long start = System.currentTimeMillis();
        startTime = start;
        running = true;
        evolEngine = new EvolutionaryEngine();
        initializePopulation(initialPopulationSize);
        initiateProgressLogger();
        structGenLogger.info("Initiating evolutionary engine with population: " + initialPopulationSize);
        evolEngine.initAnnealing(population, chiefJustice);
        evolEngine.setProgressLogger(annealingEvolutionLogger);
        evolEngine.setStructureGeneratorResult(structureGeneratorResult);
        evolEngine.setCommandline(true);
        evolEngine.run();
        long end = System.currentTimeMillis();
        running = false;
        System.out.println("Finished evolving in : " + (end - start) + " milliSeconds");
        structGenLogger.info("Finished evolving in : " + (end - start) + " milliSeconds");
        return structureGeneratorResult;
    }
}
