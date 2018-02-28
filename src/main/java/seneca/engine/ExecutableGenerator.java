package seneca.engine;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.core.*;
import seneca.core.assigners.CarbonShiftAssigner;
import seneca.core.assigners.HHCOSYAssigner;
import seneca.core.assigners.HMBCAssigner;
import seneca.core.assigners.SpectrumAssigner;
import seneca.judges.*;
import seneca.structgen.StructureGenerator;
import seneca.structgen.StructureGeneratorResult;
import seneca.structgen.ea.EAStochasticGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGeneratorMaker;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 06/03/2013
 * Time: 15:22
 * Class to start structure generation through commandline input
 */
public class ExecutableGenerator {

    private int serverCount = 1;
    private int structuresToStore = 30;
    private boolean useLogger = false;
    private boolean combineResults = false;
    private transient SpectrumAssigner spectrumAssigner;

    private SpecMLReader smlReader = null;
    private SenecaDataset senecaDataset = null;
    private ChiefJustice boss = null;
    private StructureGenerator structureGenerator = null;
    private List<Judge> judges = null;
    private List<Future<StructureGeneratorResult>> results = null;
    private ExecutorService executor = null;
    private IAtomContainer expectedMolecule = null;
    private TanimotoCalculator tanimotoCalculator = null;
    private Map<String, List<String>> results_map = null;
    private HashGenerator<Long> hashGenerator = null;
    private float tanimotoSum = 0f;
    private List<Integer> ranks = null;
    private int exactMatch = 0;
    private int totalStructuresSize = 0;

    private boolean doTanimotoCalc = false;
    private static final Logger logger = Logger.getLogger(ExecutableGenerator.class);

    public ExecutableGenerator() {
        this.structureGenerator = new EAStochasticGenerator();
        this.judges = new ArrayList<Judge>();
        this.results_map = new HashMap<String, List<String>>();
        this.ranks = new ArrayList<Integer>();
        this.hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
    }

    public void initiateStructureGeneration() {
        try {
            assignHydrogensAndCheck();
            initiateJudges();
            assignBoss();
            execute();
            extractAndWriteResults();
            logFinalResults();
            logger.info("Completed tasks");
            System.out.println("Completed tasks");
            executor.shutdownNow();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void readSpecSML(String file) {
        smlReader = new SpecMLReader(new File(file), true);
        senecaDataset = smlReader.getSenecaDataset();
    }

    private void assignHydrogensAndCheck() {
        if (!senecaDataset.getIsAtomPropertiesAssigned()) {
            new CarbonShiftAssigner(senecaDataset).assign();
        } else {
            logger.info("Atom properties already assigned in the dataset is read");
        }
        // senecaDataset.checkForAtomProperties();
    }

    private void initiateJudges() {
        if (senecaDataset.getAtomContainer() == null) {
            logger.info("Atomcontainer not found !");
            return;
        }
        for (Judge j : judges) {
            Judge judge = (Judge) senecaDataset.getJudge(j.getName());
            if (judge instanceof HOSECodeJudge) {
                AtomCenteredFragmentJudge judge1 = (AtomCenteredFragmentJudge) judge;
                configureHOSEbased(judge1);
                judge.setEnabled(true);
            }
            if (judge instanceof NMRShiftDbJudge) {
                AtomCenteredFragmentJudge judge1 = (AtomCenteredFragmentJudge) judge;
                configureHOSEbased(judge1);
                judge.setEnabled(true);
            }
            if (judge instanceof NPLikenessJudge) {
                if (!senecaDataset.getAtomContainer().contains(new Atom("H"))) {
                    judge.setAtomCount(senecaDataset.getAtomContainer().getAtomCount());
                } else {
                    IAtomContainer molWithoutHydrogens = AtomContainerManipulator.removeHydrogens(senecaDataset.getAtomContainer());
                    judge.setAtomCount(molWithoutHydrogens.getAtomCount());
                }
                judge.setEnabled(true);
            }

            if (judge instanceof AntiBredtJudge) {
                judge.setAtomCount(senecaDataset.getAtomContainer().getAtomCount());
                judge.setEnabled(true);
            }
            if ((judge instanceof HMBCJudge) && (senecaDataset.ch_hetcorlr.size() != 0)) {
                TwoDSpectrumJudge twoDJudge = (TwoDSpectrumJudge) judge;
                if (!senecaDataset.getIsAtomPropertiesAssigned()) {
                    System.out.println("hmbc true");
                    System.out.println("Atom properties not configured fully. Please use Seneca GUI to make sure the hydrogens " +
                            "are properly assigned to the hetero atoms.");
                    logger.info("For HMBC Atom properties not configured fully. Please use Seneca GUI to make sure the hydrogens " +
                            "are properly assigned to the hetero atoms.");
                    System.exit(0);
                }
                spectrumAssigner = new HMBCAssigner(senecaDataset);
                configureAssignerBased(twoDJudge);
                logger.info("hmbc handled");
            }
            if ((judge instanceof HHCOSYJudge) && (senecaDataset.hhcosy.size() != 0)) {
                TwoDSpectrumJudge twoDJudge = (TwoDSpectrumJudge) judge;
                if (!senecaDataset.getIsAtomPropertiesAssigned()) {
                    System.out.println("hhcosy judge");
                    System.out.println("Atom properties not configured fully. Please use Seneca GUI to make sure the hydrogens " +
                            "are properly assigned to the hetero atoms.");
                    logger.info("For HHCOSY Atom properties not configured fully. Please use Seneca GUI to make sure the hydrogens " +
                            "are properly assigned to the hetero atoms.");
                    System.exit(0);
                }
                spectrumAssigner = new HHCOSYAssigner(senecaDataset);
                configureAssignerBased(twoDJudge);
                logger.info("hhcosy handled");
            }
        }
    }

    private void configureHOSEbased(AtomCenteredFragmentJudge judge) {
        int atomCount = senecaDataset.getAtomContainer().getAtomCount();
        int ccount = 0;
        double[] tempshifts = new double[atomCount];

        for (int f = 0; f < atomCount; f++) {
            if (senecaDataset.getAtomContainer().getAtom(f).getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                tempshifts[ccount] = ((Float) senecaDataset.getAtomContainer().getAtom(f).getProperty(
                        CDKConstants.NMRSHIFT_CARBON)).floatValue();
                ccount++;
            }
        }
        double[] shifts = new double[ccount];
        System.arraycopy(tempshifts, 0, shifts, 0, ccount);
        judge.setCarbonShifts(shifts);
    }

    private void configureAssignerBased(TwoDSpectrumJudge judge) {
        spectrumAssigner.setSenecaDataset(senecaDataset);
        if (!spectrumAssigner.assign() || judge.assignment == null) {
            judge.setEnabled(false);
            return;
        }
        judge.setEnabled(true);
    }

    private void assignBoss() {
        List judges = new ArrayList();
        Judge judge;

        for (int g = 0; g < senecaDataset.judges.size(); g++) {
            judge = ((Judge) senecaDataset.judges.get(g));
            if (judge.getEnabled()) {
                judge.calcMaxScore();
                judges.add(judge.clone());
                System.out.println("Judge added = " + judge.getName());
                logger.info("Judge added = " + judge.getName());
            }
        }
        boss = new ChiefJustice(judges);
    }


    private void execute() throws CloneNotSupportedException {
        executor = Executors.newFixedThreadPool(serverCount);
        List<Callable<StructureGeneratorResult>> tasks = new ArrayList<Callable<StructureGeneratorResult>>();
        for (int i = 0; i < serverCount; i++) {
            tasks.add(getStochasticGenWithParametersAssigned(i));
        }
        try {
            results = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        executor.shutdown();
    }

    private StructureGenerator getStochasticGenWithParametersAssigned(int serverID) throws CloneNotSupportedException {
        StructureGenerator generator = structureGenerator.newInstance();
        logger.info("new instance: " + generator.name);

        if (!useLogger) {
            generator.getStructGenLogger().setLevel(Level.OFF);
            logger.info("Not logging structure generation");
        }
        generator.setUpLogger(serverID);
        ChiefJustice justice = (ChiefJustice) clone(boss);
        justice.initJudges();
        generator.setChiefJustice(justice);
        logger.info("boss set up");

        generator.setAtomContainer((IAtomContainer) senecaDataset.getAtomContainer().clone());

        generator.setDatasetName(senecaDataset.getName());
        logger.info("data name:" + senecaDataset.getName());

        generator.setAnnealingOptions((List) clone(senecaDataset.annealingOptions));
        generator.setNumberOfSteps((Integer) clone(senecaDataset.annealingOptions.get(senecaDataset.NUMBER_OF_STEPS)));

        generator.setMolecularFormula(senecaDataset.molecularFormula);
        logger.info("formula:" + senecaDataset.molecularFormula);

        generator.setStructureGeneratorResult(structuresToStore);
        generator.setCommandline(true);

        return generator;
    }

    private Object clone(Object obj) {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(buf);
            o.writeObject(obj);
            // Now get copies:
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
            return in.readObject();
        } catch (Exception exc) {
            return null;
        }
    }


    private void extractAndWriteResults() {
        logger.info("results size: " + results.size());
        List<IAtomContainer> totalStructures = new ArrayList<IAtomContainer>();

        try {
            for (int i = 0; i < results.size(); i++) {
                Future<StructureGeneratorResult> r = results.get(i);
                FixedSizeStack structures = r.get().structures;
                totalStructuresSize = structures.size();
                File outputSDF = getStructureFileName(i);
                writeStructures(outputSDF, structures);
                if (doTanimotoCalc) {
                    doTanimotoSimilarityCalc(structures, outputSDF);
                }
                if (combineResults) {
                    totalStructures.addAll(structures);
                }
            }
            if (combineResults) {
                writeCombined(getFileName("combined"), totalStructures);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void doTanimotoSimilarityCalc(List<IAtomContainer> structures, File outputSDF) {
        tanimotoCalculator = new TanimotoCalculator(500);
        Map<Boolean, List<String>> booleanListMap = tanimotoCalculator.calculateSimilarity(structures, getExpectedMolecule(), outputSDF.getAbsolutePath());
        for (Boolean b : booleanListMap.keySet()) {
            tanimotoSum += Float.parseFloat(booleanListMap.get(b).get(0));
            if (b) {
                results_map.put(outputSDF.getName(), booleanListMap.get(b));
            }
        }

    }

    private void combine() {
        if (combineResults) {
            logger.info("Combining results");
            List<IAtomContainer> structures = new ArrayList<IAtomContainer>();
            try {
                for (int i = 0; i < results.size(); i++) {
                    Future<StructureGeneratorResult> r = results.get(i);
                    structures.addAll(r.get().structures);
                }
                Utilities.sortByCostValue(structures);
                writeCombined(getFileName("combined"), structures);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private File getStructureFileName(int id) {
        String currentTime = String.valueOf(new Long(new Date().getTime()));
        String logFileName = currentTime + "-" + id + ".sdf";
        String fullname = System.getProperty("userApp.root") + "structures" + File.separator + logFileName;
        logger.info("preparing file to write: " + fullname);
        return createFile(fullname);

    }

    private File getScoreFileName() {
//        String compoundName = this.senecaDataset.getName();
//        String currentTime = String.valueOf(new Long(new Date().getTime()));
//        String scoreFileName = currentTime + "-" + compoundName + ".txt";
        String fullname = System.getProperty("userApp.root") + "scores" + File.separator + System.getProperty("userApp.id") + ".txt";
        logger.info("preparing score file to write: " + fullname);
        return createFile(fullname);
    }

    private File getGlobalStatsFileName() {
//        String compoundName = this.senecaDataset.getName();
//        String currentTime = String.valueOf(new Long(new Date().getTime()));
//        String scoreFileName = currentTime + "-" + compoundName + ".txt";
        String fullname = System.getProperty("userApp.root") + "stats" + File.separator + System.getProperty("userApp.id") + ".txt";
        logger.info("preparing stats file to write: " + fullname);
        return createFile(fullname);
    }

    private File createFile(String fullname) {
        File f = new File(fullname);
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            if (!f.exists())
                f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private void writeStructures(File file, FixedSizeStack structures) {

        try {
            SDFWriter writer = StructureIO.createSDFWriter(file);
            StructureIO.write(writer, structures);

        } catch (FileNotFoundException ex) {
            System.out.println("Oops ! File not found. Please check if the -in file or -out directory is correct");
            logger.info("file not found to write");
            System.exit(0);
        } catch (IOException ex) {
            logger.info("file not found to write");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }

    }

    private File getFileName(String name) {
//        String currentTime = String.valueOf(new Long(new Date().getTime()));
//        String logFileName = currentTime + "-" + name + ".sdf";
        String fullname = System.getProperty("userApp.root") + "structures" + File.separator + System.getProperty("userApp.id") + ".sdf";
        File f = new File(fullname);
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            if (!f.exists())
                f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    private void writeCombined(File file, List<IAtomContainer> structures) {

        try {
            SDFWriter writer = StructureIO.createSDFWriter(file);
            StructureIO.writeSDF(writer, structures);

        } catch (FileNotFoundException ex) {
            System.out.println("Oops ! File not found. Please check if the -in file or -out directory is correct");
            logger.info("file not found to write");
            System.exit(0);
        } catch (IOException ex) {
            logger.info("file not found to write");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }

    }

    private void logFinalResults() {
        File globalResultFile = getGlobalStatsFileName();
        if (doTanimotoCalc) {
            Long exactMatchHash = hashGenerator.generate(getExpectedMolecule());

            logger.info("Final elucidation results");
            System.out.println("Final elucidation results");
            if (results_map.isEmpty()) {
                logger.info("No structure structure found in any runs");
                System.out.println("No structure structure found in any runs");
            } else {
                writeScores(exactMatchHash);
                logger.info("Matching structure is found in " + results_map.size() + "/" + serverCount + " runs");
                System.out.println("Matching structure is found in " + results_map.size() + "/" + serverCount + " runs");
                for (String f : results_map.keySet()) {
                    logger.info(f + results_map.get(f).get(1));
                    System.out.println(f + results_map.get(f).get(1));
                    if (results_map.get(f).get(1).contains(exactMatchHash.toString())) {
                        System.out.println("Exact match found: " + exactMatchHash + " File: " + f);
                        logger.info("Exact match found: " + exactMatchHash + " File: " + f);
                        exactMatch++;
                    }
                }
                logger.info("Exact structure is found in " + exactMatch + "/" + serverCount + " runs");
                System.out.println("Exact structure is found in " + exactMatch + "/" + serverCount + " runs");
            }
            logger.info("Total tanimoto sum = " + tanimotoSum + " Avg : " + tanimotoSum / serverCount);
            System.out.println("Total tanimoto sum = " + tanimotoSum + " Avg : " + tanimotoSum / serverCount);
            writeGlobalStats(globalResultFile);
        }
    }

    private void writeScores(Long exact_match_hash) {
        try {
            File file = getScoreFileName();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // writer.write("Rank;Generation;TanimotoSum" + "\n");
            for (String f : results_map.keySet()) {
                String tanimoto_sum = results_map.get(f).get(0);
                String rank_generation_hashcode = results_map.get(f).get(1);
                String[] results_over_generations = rank_generation_hashcode.split(";");
                for (String s : results_over_generations) {
                    // r is the result from individual generation, sometimes the structures occur many times
                    // but we pick one by matching the exact hash
                    if (!s.isEmpty()) {
                        String[] r = s.split(":");
                        // 1, 3, 5 index value holds Rank generation and hashcode respectively
                        Integer hash = Integer.parseInt(r[5]);
                        if (hash.equals(exact_match_hash)) {
                            ranks.add(Integer.parseInt(r[1]));
                            writer.write(r[1] + ";" + r[3] + ";" + tanimoto_sum + "\n");
                        }
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeGlobalStats(File globalResultFile) {
        // write molecule_name;exact_retrieval_count;matching_retrieval_count;tanimoto_sum;min_rank;max_rank
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(globalResultFile));
            if (results_map.isEmpty()) {
                writer.write(this.senecaDataset.getName() + ";" + totalStructuresSize + ";" + 0 + ";" + 0 + ";" + tanimotoSum / serverCount + ";NA;NA" + "\n");
            } else {
                if (!ranks.isEmpty()) {
                    Collections.sort(ranks);
                    writer.write(this.senecaDataset.getName() + ";" + totalStructuresSize + ";" +
                            exactMatch + ";" +
                            (results_map.size() - exactMatch) + ";" +
                            tanimotoSum / serverCount + ";"
                            + ranks.get(0) + ";" +
                            ranks.get(ranks.size() - 1) + "\n");

                } else {
                    writer.write(this.senecaDataset.getName() + ";" + totalStructuresSize + ";" + exactMatch + ";" + (results_map.size() - exactMatch) + ";" + tanimotoSum / serverCount + ";NA;NA" + "\n");
                }

            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setCombineResults(boolean combineResults) {
        this.combineResults = combineResults;
    }

    public void setStructureGenerator(StructureGenerator generator) {
        this.structureGenerator = generator;
    }

    public void setServerCount(int count) {
        this.serverCount = count;
    }

    public void setStructuresToStore(int count) {
        this.structuresToStore = count;
    }

    public void setJudges(List<Judge> judges) {
        this.judges = judges;
    }

    public void shouldLog(boolean value) {
        this.useLogger = value;
    }

    public IAtomContainer getExpectedMolecule() {
        return expectedMolecule;
    }

    public void setExpectedMolecule(IAtomContainer expectedMolecule) {
        this.expectedMolecule = expectedMolecule;
        this.doTanimotoCalc = true;
    }
}
