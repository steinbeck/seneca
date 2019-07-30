package seneca.core;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import seneca.engine.ExecutableGenerator;
import seneca.judges.*;
import seneca.structgen.ea.TerminationConditions;
import seneca.structgen.sa.adaptive.ASAStochasticGenerator;
import seneca.structgen.sa.regular.SAStochasticGenerator;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 06/03/2013
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 * <p/>
 * Class to parse commandline user input for structure elucidation
 */
public class CommandInputParser {

    private boolean userDirectorySet = false;

    private CommandLineParser parser = null;
    private Options options = null;
    private HelpFormatter formatter;
    private ExecutableGenerator structureElucidator = null;
    private List<Judge> judges = null;
    private static final String VERSION = "Seneca 2.0";
    private static final String DESCRIPTION = " SENECA is a java program package for\n " +
            "Computer Assisted Structure Elucidation (CASE) of organic molecules\n " +
            "While it currently uses mainly 1D and 2D NMR spectroscopy for the CASE process\n " +
            "it is open to all types of spectroscopic data.";
    private static final String HELP_DESCRIPTION = "\t Input [Options] [Targets] for the application are specified below. " +
            "The structure elucidation by default uses, Evolutionary Algorithm (EA) and Hose-code judge for doing CASE.";
    private static final Logger logger = Logger.getLogger(CommandInputParser.class);


    public CommandInputParser() {
        parser = new BasicParser();
        options = new Options();
        formatter = new HelpFormatter();
        judges = new ArrayList<Judge>();
        judges.add(new HOSECodeJudge());
        structureElucidator = new ExecutableGenerator();
        options.addOption("h", "help", false, "Usage information");
        options.addOption(OptionBuilder.withArgName(".SML")
                .hasArg()
                .withDescription("Input .SML file for CASE")
                .create("in"));
        options.addOption(OptionBuilder.withArgName("DIRECTORY")
                .hasArg()
                .withDescription("Output directory for structures and logs")
                .create("out"));
//        options.addOption(OptionBuilder.withArgName("DIRECTORY")
//                .hasArg()
//                .withDescription("Output directory for final stats file")
//                .create("stat"));
//        options.addOption(OptionBuilder.withArgName("DIRECTORY")
//                .hasArg()
//                .withDescription("Output directory for final scores file")
//                .create("score"));
        options.addOption(OptionBuilder.withArgName("MOLfile")
                .hasArg()
                .withDescription("Expected molecule")
                .create("mol"));
        options.addOption(OptionBuilder.withArgName("NUMBER")
                .hasArg()
                .withDescription("Number of top structures to store; Default: 30")
                .create("structures"));
        options.addOption(OptionBuilder.withArgName("NUMBER")
                .hasArg()
                .withDescription("Number of servers for parallel evolution; Default: 1")
                .create("servers"));
        options.addOption(OptionBuilder.withArgName("DIRECTORY")
                .hasArg()
                .withDescription("Location of NMRShiftDB lucene index")
                .create("index"));
        options.addOption(OptionBuilder.withArgName("NUMBER")
                .hasArg()
                .withDescription("Maximum number of generations for EA")
                .create("generations"));
        options.addOption("sa", false, "Use Simulated annealing algorithm (Default: EA)");
        options.addOption("asa", false, "Use Adaptive Simulated annealing algorithm (Default: EA)");
        options.addOption("nplikeness", false, "Use NP-Likeness judge");
        options.addOption("antibredt", false, "Use anti-bredt judge");
        options.addOption("ringstrain", false, "Use strained ring detector judge");
        options.addOption("nohosecode", false, "Dont use hose-code judge");
        options.addOption("nmrshiftdb", false, "Use NMRShiftDB judge");
        options.addOption("hmbc", false, "Use HMBC judge");
        options.addOption("hhcosy", false, "Use HH-COSY judge");
        options.addOption("log", false, "Log evolution");
        options.addOption("v", "version", false, "Version & product description");
        options.addOption("combine", false, "Use to combine structures from all servers into one file");

    }

    public void parseUserInput(String[] args) {
        logger.info("Arguments taken: " + args);
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("help")) {
                formatter.printHelp(HELP_DESCRIPTION + "\n", options);
                System.out.println("If -structures or -logs specified and no -out specified, the files will be written to the " +
                        "directory of the input .sml");
                System.exit(0);
            }
            if (commandLine.hasOption('v')) {
                System.out.println(VERSION);
                System.out.println(DESCRIPTION);
                System.exit(0);
            }
            if (commandLine.hasOption("in")) {
                structureElucidator.readSpecSML(commandLine.getOptionValue("in"));
            }
            if (commandLine.hasOption("out")) {
                setUserDirectory(commandLine.getOptionValue("out"));
            }
//            if (commandLine.hasOption("stat")) {
//                setStatDirectory(commandLine.getOptionValue("stat"));
//            }
//            if (commandLine.hasOption("score")) {
//                setScoreDirectory(commandLine.getOptionValue("score"));
//            }
            if (commandLine.hasOption("mol")) {
                try {
                    structureElucidator.setExpectedMolecule(StructureIO.readMol(commandLine.getOptionValue("mol")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (commandLine.hasOption("sa")) {
                structureElucidator.setStructureGenerator(new SAStochasticGenerator());
            }
            if (commandLine.hasOption("asa")) {
                structureElucidator.setStructureGenerator(new ASAStochasticGenerator());
            }
            if (commandLine.hasOption("structures")) {
                int value = Integer.parseInt(commandLine.getOptionValue("structures"));
                structureElucidator.setStructuresToStore(value);
                logger.info("structure to write: " + value);
                System.out.println("value " + value);
                if (!commandLine.hasOption("out") && !commandLine.hasOption("in")) {
                    System.out.println("Nothing to write");
                }
            }
            if (commandLine.hasOption("servers")) {
                structureElucidator.setServerCount(Integer.parseInt(commandLine.getOptionValue("servers")));
            }
            if (commandLine.hasOption("combine")) {
                structureElucidator.setCombineResults(true);
            }
//            if (commandLine.hasOption("nplikeness")) {
//                judges.add(new NPLikenessJudge());
//            }
            if (commandLine.hasOption("antibredt")) {
                judges.add(new AntiBredtJudge());
            }
            if (commandLine.hasOption("ringstrain")) {
                judges.add(new RingStrainJudge());
            }
            if (commandLine.hasOption("nmrshiftdb")) {
                judges.add(new NMRShiftDbJudge());
                if (!commandLine.hasOption("index")) {
                    System.out.println("Please specify NMRShiftDB lucene directory using -index to use nmrshiftdb judge");
                    System.exit(0);
                }
            }
            if (commandLine.hasOption("hhcosy")) {
                judges.add(new HHCOSYJudge());
            }
            if (commandLine.hasOption("hmbc")) {
                judges.add(new HMBCJudge());
            }
            if (commandLine.hasOption("nohosecode")) {
                judges.remove(0);
            }
            if (commandLine.hasOption("index")) {
                setLuceneDirectory(commandLine.getOptionValue("index"));
            }
            if (commandLine.hasOption("generations")) {
                TerminationConditions.setMaximumGenerations(Integer.parseInt(commandLine.getOptionValue("generations")));
            }
            if (commandLine.hasOption("log")) {
                structureElucidator.shouldLog(true);
                if (commandLine.hasOption("out")) {
                    setUserDirectory(commandLine.getOptionValue("out"));
                    setupLogging();
                } else if (commandLine.hasOption("in")) {
                    String name = new File(commandLine.getOptionValue("in")).getParent();
                    System.out.println(name);
                    setUserDirectory(name);
                    setupLogging();
                } else {
                    System.out.println("Nothing to log");
                }
            }
            if (commandLine.getArgList().size() > 0) {
                System.out.println("Unrecognised input option. Please Check the input options using -help");
            }
            if (commandLine.hasOption("in") && !userDirectorySet) {
                String name = new File(commandLine.getOptionValue("in")).getParent();
                System.out.println(name);
                setUserDirectory(name);
            }

            if (commandLine.hasOption("asa") && commandLine.hasOption("sa")) {
                System.out.println("Specify any one algorithm. sa/asa; If none specified EA is used.");
                System.exit(0);
            }

        } catch (UnrecognizedOptionException ure) {
            System.out.println("Unrecognised input option. Please Check the input options using -help");
            System.exit(0);
        } catch (ParseException ex) {
            System.out.println("Unrecognised input option. Please Check the input options using -help");
            System.exit(0);
        }
    }

    private void setUserDirectory(String name) {
        System.setProperty("userApp.id", String.valueOf(new Long(new Date().getTime())));
        if (!userDirectorySet) {
            File file = new File(name);
            if (file.isDirectory()) {
                System.setProperty("userApp.root", name + File.separator + "seneca" + File.separator);
                logger.info("User app directory set: " + name + File.separator + "seneca" + File.separator);
                userDirectorySet = true;
            } else {
                System.out.println("Creating directory..");
                file.mkdirs();
                System.setProperty("userApp.root", file.getAbsolutePath() + File.separator);
                logger.info("User app directory set: " + file.getAbsolutePath() + File.separator);
                userDirectorySet = true;
            }
        }
    }

    private void setStatDirectory(String name) {
        File file = new File(name);
        if (file.isDirectory()) {
            System.setProperty("userApp.stat.root", name + File.separator);
            logger.info("User app stat directory set: " + name + File.separator);
        } else {
            System.out.println("Creating directory..");
            file.mkdirs();
            System.setProperty("userApp.stat.root", file.getAbsolutePath() + File.separator);
            logger.info("User app directory set: " + file.getAbsolutePath() + File.separator);
        }
    }

    private void setScoreDirectory(String name) {
        File file = new File(name);
        if (file.isDirectory()) {
            System.setProperty("userApp.score.root", name + File.separator);
            logger.info("User app score directory set: " + name + File.separator);
        } else {
            System.out.println("Creating directory..");
            file.mkdirs();
            System.setProperty("userApp.score.root", file.getAbsolutePath() + File.separator);
            logger.info("User app directory set: " + file.getAbsolutePath() + File.separator);
        }
    }


    private void setupLogging() {
        try {
            Properties loggingProps = new Properties();
            loggingProps.load(this.getClass().getClassLoader().getResourceAsStream("properties/logging.properties"));
            PropertyConfigurator.configure(loggingProps);
        } catch (Exception exc) {
            System.err.println("Could not setup file logging");
            logger.error("Could not setup logging");
        }
    }

    private void setLuceneDirectory(String value) {
        if (new File(value).isDirectory()) {
            System.setProperty("lucene.root", value);
        } else {
            System.out.println("Cant find lucene index !");
            System.exit(0);
        }
    }


    public void startStructureElucidation() {
        structureElucidator.setJudges(judges);
        structureElucidator.initiateStructureGeneration();
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Run with -help for usage information");
                System.exit(0);
            }
            logger.info("Arguments taken: " + args);
            long start = System.currentTimeMillis();
            System.out.println(Arrays.asList(args));
            CommandInputParser parser = new CommandInputParser();
            parser.parseUserInput(args);
            parser.startStructureElucidation();
            long end = System.currentTimeMillis();
            System.out.println("Finished in " + (end - start) / 1000 + " s");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
