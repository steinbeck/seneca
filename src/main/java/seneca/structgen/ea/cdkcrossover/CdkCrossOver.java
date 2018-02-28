/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.cdkcrossover;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import seneca.core.StructureIO;
import seneca.gui.MoleculeVisualizer;
import seneca.gui.StructureImageGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class CdkCrossOver {

    private MoleculeVisualizer visualizer = null;
    private List<IAtomContainer> moleculesToDisplay = null;
    private LocalCrossoverMachine crossOverMachine = null;
    private static final String ASSIGN_COLOR = "Bond_color";

    public CdkCrossOver() {
        visualizer = new MoleculeVisualizer();
        moleculesToDisplay = new ArrayList<IAtomContainer>();
        crossOverMachine = new LocalCrossoverMachine();
    }

    public void cross(IAtomContainer dad, IAtomContainer mom) throws CDKException, Exception {
        addParentsToDisplayList(dad, mom);
        printOut(dad);
        List<IAtomContainer> crossed = crossOverMachine.doCrossover(dad, mom);
        for (int i = 0; i < crossed.size(); i++) {
            IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(crossed.get(i));
            addThisToDisplayList(crossed.get(i), "Child - " + i + " - " + MolecularFormulaManipulator.getString(formula));
            writeImage(crossed.get(i), "Child - " + i + " - " + MolecularFormulaManipulator.getString(formula));
        }
    }

    private void printOut(IAtomContainer dad) {

        System.out.println("AToms: ");
        for (IAtom atom : dad.atoms()) {
            System.out.println(atom);
        }
        System.out.println("Bonds");
        for (IBond bond : dad.bonds()) {
            System.out.println(bond);
        }
        System.out.println("Single electrons");
        for (ISingleElectron se : dad.singleElectrons()) {
            System.out.println(se);
        }
        System.out.println("Lone pairs");
        for (ILonePair lp : dad.lonePairs()) {
            System.out.println(lp);
        }

    }

    private void addParentsToDisplayList(IAtomContainer dad, IAtomContainer mom) throws CDKException, Exception {
        dad.setProperty(ASSIGN_COLOR, Color.RED);
        mom.setProperty(ASSIGN_COLOR, Color.BLUE);
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(dad);
        //addThisToDisplayList(dad, "dad - " + MolecularFormulaManipulator.getString(formula));
        writeImage(dad, "dad - " + MolecularFormulaManipulator.getString(formula));
        formula = MolecularFormulaManipulator.getMolecularFormula(mom);
        //addThisToDisplayList(mom, "mom - " + MolecularFormulaManipulator.getString(formula));
        writeImage(mom, "mom - " + MolecularFormulaManipulator.getString(formula));
    }

    private void addThisToDisplayList(IAtomContainer molecule, String title) throws CDKException {

        if (ConnectivityChecker.isConnected(molecule)) {
            System.out.println(title + " is connected");
            molecule.setProperty(CDKConstants.TITLE, title);
            moleculesToDisplay.add(molecule);
        } else {
            System.out.println(title + " is disconnected");
            IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(molecule);
            System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
            int count = 1;
            for (IAtomContainer atomContainer : partitionIntoMolecules.atomContainers()) {
                atomContainer.setProperty(CDKConstants.TITLE, title);
                moleculesToDisplay.add(atomContainer);
                count++;
            }
        }
    }

    private void writeImage(IAtomContainer atomContainer, String title) throws Exception {
        if (ConnectivityChecker.isConnected(atomContainer)) {

            System.out.println(title + " is connected");
            BufferedImage image = new StructureImageGenerator().generateStructureImage(atomContainer, new Dimension(186, 186));
            String filename = "/Users/kalai/cdkCrossOver/" + title + ".png";
            ImageIO.write((RenderedImage) image, "PNG", new File(filename));

        } else {
            System.out.println(title + " is disconnected");
            IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
            System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
            int count = 1;
            for (IAtomContainer container : partitionIntoMolecules.atomContainers()) {
                writeImage(container, title + " - " + count);
                count++;
            }
        }
    }

    public static void main(String[] args) {
        try {
            CdkCrossOver cdkCrossOver = new CdkCrossOver();
            IAtomContainer dad = StructureIO.readMol("/Users/kalai/Downloads/C15H20O3/absicin.mol");
            IAtomContainer mom = StructureIO.readMol("/Users/kalai/Downloads/C15H20O3/santamarin.mol");
//                  IAtomContainer dad = StructureIO.readMol("/Users/kalai/Downloads/quino/indoleAcit.mol");
//                  IAtomContainer mom = StructureIO.readMol("/Users/kalai/Downloads/quino/quinoline.mol");
            cdkCrossOver.cross(dad, mom);

            //cdkCrossOver.cross(MoleculeFactory.makeAzulene(), MoleculeFactory.makeAzulene());
//                  List<IAtomContainer> parents = StructureIO.readSDF("/Users/kalai/Downloads/crossed.sdf");
//                   cdkCrossOver.cross(parents.get(0), parents.get(1));
        } catch (Exception ex) {
            Logger.getLogger(CdkCrossOver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
