/*
 *  StructureGenerator.java
 *
 *  Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.structgen;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.SingleStructureRandomGenerator;
import seneca.judges.ChiefJustice;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @author steinbeck @created September 3, 2001
 */
public abstract class StructureGenerator implements Callable<StructureGeneratorResult>, Cloneable, Runnable {

    /**
     * Is this generator generating?
     */
    public boolean running = false;
    /**
     * Are we in debug mode?
     */
    public boolean debug = false;
    /**
     * The version of this StructureGenerator
     */
    public String version = null;
    /**
     * The name of this StructureGenerator
     */
    public String name = null;
    /**
     * The name of the dataset currently worked upon
     */
    public String datasetName = null;
    /**
     * A datastructure summarizing the full result of this run
     */
    public StructureGeneratorResult structureGeneratorResult;
    /**
     * An AtomContainer to start with by filling it with bonds.
     */
    public IAtomContainer atomContainer = null;

    protected long startTime = System.currentTimeMillis();

    protected long timeTaken = 0l;

    protected boolean stopRunning = false;

    private boolean commandline = false;


    /**
     * A List with objects for initializing the structure generator which is responsible for
     * identifying the right one.
     */
    public List annealingOptions;
    public String molecularFormula = "";
    public int numberOfSteps = 0;
    public ChiefJustice chiefJustice = null;
    public DecimalFormat decimalFormat = new DecimalFormat("##.###");
    public Logger structGenLogger = Logger.getLogger(StructureGenerator.class.getPackage().getName());
    public Logger annealingEvolutionLogger = null;
    public Random random = new Random();

    public StructureGenerator() {
        this("StructGen");
    }

    public StructureGenerator(String name) {
        structureGeneratorResult = new StructureGeneratorResult();
        this.name = name;
    }

    public StructureGenerator newInstance() {
        StructureGenerator newGenerator = null;
        try {
            newGenerator = this.getClass().newInstance();
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {

        }
        return newGenerator;
    }

    public void setUpLogger(int serverID) {
        if (LogManager.getLogger(structGenLogger.getName()).getLevel().equals(org.apache.log4j.Level.OFF)) {
            annealingEvolutionLogger = structGenLogger;
        } else {
//            String currentTime = String.valueOf(new Long(new Date().getTime()));
//            String logFileName = currentTime + "-" + serverID + ".log";
            String fullFileName = System.getProperty("userApp.root") + "serverLogs" + File.separator + this.name + File.separator + System.getProperty("userApp.id") + ".log";
            String loggerName = this.name + "-" + serverID;
            annealingEvolutionLogger = Logger.getLogger(loggerName);
            structGenLogger.info("Log file for server ID " + serverID + " is: " + fullFileName);

            Properties prop = new Properties();
            prop.setProperty("log4j.logger." + loggerName, "DEBUG, file");
            prop.setProperty("log4j.appender.file", "org.apache.log4j.FileAppender");
            prop.setProperty("log4j.appender.file.File", fullFileName);
            prop.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
            prop.setProperty("log4j.appender.file.layout.ConversionPattern", "%m%n");
            PropertyConfigurator.configure(prop);
        }
    }

    protected IAtomContainer generateSingleRandomStructure() {

        IAtomContainer startMolecule = getAtomContainer();

        SingleStructureRandomGenerator ssrg = null;
        try {
            ssrg = new SingleStructureRandomGenerator(random.nextLong());
            ssrg.setAtomContainer(startMolecule);
            startMolecule = ssrg.generate();
            //  StructureIO.writeMol(new SDFWriter(new FileWriter(new File("/Users/kalai/Develop/projects/NP-inCASE/"+String.valueOf(new Long(new Date().getTime()))+".mol"))),startMolecule);
            //    Utilities.writeImage(startMolecule,"/Users/kalai/Develop/projects/NP-inCASE/"+ System.currentTimeMillis() +".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("returning first molecule..");
        structGenLogger.info("Initial seed molecule generated");
        return startMolecule;
    }

    protected long timeTakenSoFar() {
        if (running) {
            timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            return timeTaken;
        } else {
            return timeTaken;
        }
    }


    public abstract void execute() throws Exception;

    public abstract Object getStatus() throws java.lang.Exception;
    
    public void start() {
    }

    public void stop() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatasetName(String s) {
        this.datasetName = s;
    }

    public void setAnnealingOptions(List initObjects) {
        this.annealingOptions = initObjects;
    }

    public void setVerbose(boolean v) {
        if (running) {
            return;
        }
        debug = v;
    }

    public void setAtomContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
    }

    public void setNumberOfSteps(int steps) {
        this.numberOfSteps = steps;
    }

    public void setChiefJustice(ChiefJustice chiefJustice) {
        this.chiefJustice = chiefJustice;
    }

    public void setStructureGeneratorResult(int size) {
        this.structureGeneratorResult = new StructureGeneratorResult(size);
    }


    public void setMolecularFormula(String molFormula) {
        this.molecularFormula = molFormula;
    }

    public String getName() {
        return name;
    }

    public List getAnnealingOptions() {
        return annealingOptions;
    }

    public Object getResult() {
        return structureGeneratorResult.clone();
    }

    public String getVersion() {
        return version;
    }

    public IAtomContainer getAtomContainer() {
        return this.atomContainer;
    }

    public String getDatasetName(String s) {
        return this.datasetName;
    }

    public StructureGeneratorResult getStructureGeneratorResult() {
        return this.structureGeneratorResult;
    }


    public Logger getStructGenLogger() {
        return this.structGenLogger;
    }

    public boolean isCommandline() {
        return commandline;
    }

    public void setCommandline(boolean commandline) {
        this.commandline = commandline;
    }

}
