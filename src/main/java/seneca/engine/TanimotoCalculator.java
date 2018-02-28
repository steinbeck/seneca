package seneca.engine;

import org.apache.commons.cli.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import seneca.core.SenecaConstants;
import seneca.core.StructureIO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 28/03/2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class TanimotoCalculator {


    float max_min = (1f - 500f);
    float min = 500f;
    float tanimotoCutoff = 0.8f;
    Fingerprinter fingerprinter = null;
    private CommandLineParser parser = null;
    private Options options = null;
    private SDFWriter nearMatchWriter = null;
    private SDFWriter exactMatchWriter = null;
    private boolean shouldWrite = false;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TanimotoCalculator.class);

    public TanimotoCalculator() {
        fingerprinter = new Fingerprinter();
        parser = new BasicParser();
        options = new Options();
        options.addOption(OptionBuilder.withArgName(".mol")
                .hasArg()
                .withDescription("Input .mol file")
                .create("mol"));
        options.addOption(OptionBuilder.withArgName(".sdf")
                .hasArg()
                .withDescription("input sdf")
                .create("sdf"));
        options.addOption("write", true, "Write selected structures to specified file");
        options.addOption(OptionBuilder.withArgName("tanimoto coefficient")
                .hasArg()
                .withDescription("Cut-off to filter and write to SDF")
                .create("cutoff"));

    }

    public TanimotoCalculator(int minRank) {
        this.min = minRank;
        this.max_min = 1f - this.min;
        fingerprinter = new Fingerprinter();
        logger.info("min rank" + this.min + " max_min rank: " + this.max_min);
        System.out.println("min rank" + this.min + " max_min rank: " + this.max_min);
    }

    public void parseUserInput(String[] args) {

        if (args.length < 2) {
            System.out.println("insufficient arguments");
            return;
        }
        CommandLine commandLine = null;
        String mol = "";
        String sdf = "";
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("mol")) {
                mol = (commandLine.getOptionValue("mol"));
            }
            if (commandLine.hasOption("sdf")) {
                sdf = (commandLine.getOptionValue("sdf"));
            }
            if (commandLine.hasOption("cutoff")) {
                tanimotoCutoff = Float.parseFloat(commandLine.getOptionValue("cutoff"));
            }
            if (commandLine.hasOption("write")) {
                nearMatchWriter = StructureIO.createSDFWriter(new File(commandLine.getOptionValue("write")));
                shouldWrite = true;
            }

            IAtomContainer molecule = StructureIO.readMol(mol);
            calculateSimilarity(sdf, molecule);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<Boolean, List<String>> calculateSimilarity(String file, IAtomContainer b) {
        System.out.println("file :" + file);
        IteratingSDFReader sdfReader = null;
        float tanimotoSum = 0f;
        BitSet b_bitset = getFingerPrint(b);
        String nearTanimotoMatchSDFile = getNearTanimotoMatch(file);
        String exactTanimotoMatchSDFile = getExactTanimotoMatch(file);
        try {
            nearMatchWriter = StructureIO.createSDFWriter(new File(nearTanimotoMatchSDFile));
            exactMatchWriter = StructureIO.createSDFWriter(new File(exactTanimotoMatchSDFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Boolean, List<String>> matchMap = new HashMap<Boolean, List<String>>();
        try {
            sdfReader = new IteratingSDFReader(new FileReader(new File(file)), SilentChemObjectBuilder.getInstance());
            sdfReader.setSkip(true);
            int count = 0;
            int writtenCount = 0;
            String rank_generation = "";

            while (sdfReader.hasNext()) {
                IAtomContainer a = null;
                try {
                    a = sdfReader.next();
                    System.out.println("next mol");

                    float coefficient = getTanimotoCoefficient(a, b_bitset);
                    float normalizedRank = getNormalisedRank(a);
                    float tani = normalizedRank * coefficient;
                    if (count < 500) {
                        tanimotoSum += tani;
                    }
                    if (coefficient >= tanimotoCutoff) {
                        nearMatchWriter.write(a);
                        writtenCount++;
                    }
                    if (coefficient == 1f) {
                        exactMatchWriter.write(a);
                        rank_generation += "; R: " + a.getProperty("Rank") + " G: " + a.getProperty(SenecaConstants.GENERATION_NUMBER + " H: " + a.getProperty("HashCode"));
                    }
                } catch (Exception e) {
                }
                count++;

            }
            nearMatchWriter.close();
            exactMatchWriter.close();
            System.out.println("tanimoto sum: " + tanimotoSum);
            System.out.println("Total molecules with coeff >= " + tanimotoCutoff + " is : " + writtenCount);
            if (writtenCount == 0) {
                new File(nearTanimotoMatchSDFile).delete();
            }
            List<String> values = new ArrayList<String>();
            values.add(String.valueOf(tanimotoSum));
            values.add(rank_generation);
            if (!rank_generation.isEmpty()) {
                matchMap.put(true, values);
            } else {
                matchMap.put(false, values);
            }


        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

        return matchMap;
    }

    public Map<Boolean, List<String>> calculateSimilarity(List<IAtomContainer> structures, IAtomContainer b, String file) {
        System.out.println("file :" + file);
        logger.info("Calculating tanomoto similarity for molecules in file :" + file);

        float tanimotoSum = 0f;
        BitSet b_bitset = getFingerPrint(b);
        String nearTanimotoMatchSDFile = getNearTanimotoMatch(file);
        String exactTanimotoMatchSDFile = getExactTanimotoMatch(file);
        try {
            nearMatchWriter = StructureIO.createSDFWriter(new File(nearTanimotoMatchSDFile));
            exactMatchWriter = StructureIO.createSDFWriter(new File(exactTanimotoMatchSDFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Boolean, List<String>> matchMap = new HashMap<Boolean, List<String>>();
        int writtenCount = 0;
        int count = 0;
        String rank_generation = "";

        for (Object obj : structures) {
            IAtomContainer a = (IAtomContainer) obj;
            try {
                float coefficient = getTanimotoCoefficient(a, b_bitset);
                a.setProperty("TanimotoCoefficient", coefficient);
                float normalizedRank = getNormalisedRank(a);
                float tani = normalizedRank * coefficient;
                if (count < 500) {
                    tanimotoSum += tani;
                }
                if (coefficient >= tanimotoCutoff & coefficient < 1f) {
                    nearMatchWriter.write(a);
                    writtenCount++;
                }
                if (coefficient == 1f) {
                    writtenCount++;
                    exactMatchWriter.write(a);
                    rank_generation += ";R:" + a.getProperty("Rank") + ":G:" + a.getProperty(SenecaConstants.GENERATION_NUMBER) + ":H:" + a.getProperty("HashCode");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
        try {
            nearMatchWriter.close();
            exactMatchWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("tanimoto sum: " + tanimotoSum);
        logger.info("tanimoto sum: " + tanimotoSum);
        System.out.println("Total molecules with coeff >= " + tanimotoCutoff + " is : " + writtenCount);
        logger.info("Total molecules with coeff >= " + tanimotoCutoff + " is : " + writtenCount);
        if (writtenCount == 0) {
            logger.info("Deleting : " + nearTanimotoMatchSDFile);
            new File(nearTanimotoMatchSDFile).delete();
            new File(exactTanimotoMatchSDFile).delete();
        }
        List<String> values = new ArrayList<String>();
        values.add(String.valueOf(tanimotoSum));
        values.add(rank_generation);
        if (!rank_generation.isEmpty()) {
            //true for exact results returned.
            matchMap.put(true, values);
        } else {
            matchMap.put(false, values);
        }
        return matchMap;
    }

    private String getNearTanimotoMatch(String name) {
        System.out.println("file name to create tani file: " + new File(name).getName());
        logger.info("Creating tani file using: " + name);
        String fullname = System.getProperty("userApp.root") + "structures" + File.separator + "tanimoto" + File.separator + "nearMatch" + File.separator + new File(name).getName();
        File f = new File(fullname);
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            if (!f.exists())
                f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullname;
    }

    private String getExactTanimotoMatch(String name) {
        System.out.println("file name to create tani file: " + new File(name).getName());
        logger.info("Creating tani file using: " + name);
        String fullname = System.getProperty("userApp.root") + "structures" + File.separator + "tanimoto" + File.separator + "exactMatch" + File.separator + new File(name).getName();
        File f = new File(fullname);
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            if (!f.exists())
                f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullname;
    }

    public float getTanimotoCoefficient(IAtomContainer a, BitSet b) {
        float similarity = 0f;
        try {
            similarity = Tanimoto.calculate(getFingerPrint(a), b);
            // System.out.println("Similarity: " + similarity);
        } catch (CDKException ex) {
            ex.printStackTrace();
            Logger.getLogger(TanimotoCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return similarity;
    }

    public BitSet getFingerPrint(IAtomContainer molecule) {
        IBitFingerprint fingerprint = null;
        try {
            fingerprint = fingerprinter.getBitFingerprint(molecule);
        } catch (CDKException ex) {
            ex.printStackTrace();
            Logger.getLogger(TanimotoCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fingerprint.asBitSet();
    }

    public float getNormalisedRank(IAtomContainer molecule) {
        float normalisedRank = 0f;
        Integer rank = molecule.getProperty("Rank");

        if (rank != null) {
            int r = rank;
            normalisedRank = Math.abs((r - min) / max_min);
            // System.out.println("rank: " + r + " normalized: " + normalisedRank);
        } else {
            System.out.println("no rank");
            logger.info("No rank found");
        }

        return normalisedRank;
    }

//    public static void main(String[] args) {
//        long start = System.currentTimeMillis();
//
//
//        String originalFile = "/Users/kalai/commandlineseneca/Originals/newSet/didymellamideB.mol";
//        String resultSet = "/Users/kalai/commandlineseneca/GeneratedData/didymellamideB/onlyNMRshiftdb/structures/1364362807806-combined.sdf";
//        IAtomContainer mol = null;
//        try {
//            mol = StructureIO.readMol(originalFile);
//            new TanimotoCalculator().calculateSimilarity(resultSet, mol);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("finished in: " + (end - start) / 1000 + " s");
//
//    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        TanimotoCalculator calc = new TanimotoCalculator();
        calc.parseUserInput(args);
        long end = System.currentTimeMillis();
        System.out.println("finished in: " + (end - start) / 1000 + " s");
    }
}
