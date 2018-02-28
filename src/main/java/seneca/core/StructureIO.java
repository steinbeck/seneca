/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.core;

import net.sf.jniinchi.INCHI_OPTION;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author kalai
 */
public class StructureIO {

    static final SmilesGenerator smilesGenerator = new SmilesGenerator();

    public static SDFWriter createSDFWriter(String fileToWrite) throws IOException {
        return createSDFWriter(new File(fileToWrite));
    }

    public static SDFWriter createSDFWriter(File file) throws IOException {
        SDFWriter writer = new SDFWriter(new FileWriter(file));
        return writer;
    }

    public static void writeSDF(SDFWriter writer, List<IAtomContainer> molecules) throws Exception {
        for (IAtomContainer mol : molecules) {
            writer.write(mol);
        }
        writer.close();
    }

    public static void writeMol(SDFWriter writer, IAtomContainer mol) throws Exception {
        writer.write(mol);
        writer.close();
    }

    public static void write(SDFWriter writer, FixedSizeStack molecules) throws Exception {
        int rank = 1;
        for (int i = 0; i < molecules.size(); i++) {
            IAtomContainer molecule = (IAtomContainer) molecules.elementAt(i);
            molecule.setProperty("Rank", rank);
            writer.write(molecule);
            rank++;
        }
        writer.close();
    }

    public static List<IAtomContainer> readSDF(String inputSDFile) throws Exception {
        List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
        IteratingSDFReader sdfReader = new IteratingSDFReader(new FileReader(new File(inputSDFile)), SilentChemObjectBuilder.getInstance());
        while (sdfReader.hasNext()) {
            IAtomContainer mol =  sdfReader.next();
           // mol.setProperty("UUID", UUID.randomUUID());
            molecules.add(mol);
        }
        return molecules;
    }

    public static IteratingSDFReader createSDFReader(String inputSDFile) throws Exception {
        IteratingSDFReader sdfReader = new IteratingSDFReader(new FileReader(new File(inputSDFile)), SilentChemObjectBuilder.getInstance());
        return sdfReader;
    }

    public static IAtomContainer readMol(String inputSDFile) throws Exception {
        MDLReader mdlReader = new MDLReader(new FileReader(new File(inputSDFile)));
        return mdlReader.read(new AtomContainer());
    }

    public static FixedSizeStack removeRedundantStructures(FixedSizeStack molecules) throws Exception {
        System.out.println("Removing redunddant stsr : " + molecules.size());
        Map<String, String> inchiScoreMap = new HashMap<String, String>();
        FixedSizeStack nonRedundantStructures = new FixedSizeStack(molecules.size());
        List<net.sf.jniinchi.INCHI_OPTION> list = new ArrayList<net.sf.jniinchi.INCHI_OPTION>();
        DecimalFormat formatter = new DecimalFormat("##.####");
        list.add(INCHI_OPTION.SNon);
        list.add(INCHI_OPTION.FixedH);
        InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
        for (int i = 0; i < molecules.size(); i++) {
            IAtomContainer molecule = (IAtomContainer) molecules.get(i);
            if (molecule != null) {
                InChIGenerator gen = factory.getInChIGenerator(molecule, list);
                String inchi = gen.getInchi();
                inchiScoreMap.put(inchi, (String) molecule.getProperty("Score"));
            }
        }

        for (Map.Entry inchi_score : inchiScoreMap.entrySet()) {
            InChIToStructure intostruct = factory.getInChIToStructure(
                    (String) inchi_score.getKey(), SilentChemObjectBuilder.getInstance());
            AtomContainer container = (AtomContainer) intostruct.getAtomContainer();
            container.setProperty("Score", (String) inchi_score.getValue());
            nonRedundantStructures.add(container);
        }
        System.out.println("AFter removing redunndat : " + nonRedundantStructures.size());
        return nonRedundantStructures;
    }

    public static String createSmiles(IAtomContainer molecule) {
        return smilesGenerator.createSMILES(molecule);
    }

    public static void main(String[] args) {
        try {
            List<IAtomContainer> mols = StructureIO.readSDF("/Users/kalai/euro.sdf");
            System.out.println("mols size: " + mols.size());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
