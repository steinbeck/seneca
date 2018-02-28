/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.gui.StructureImageGenerator;
import seneca.structgen.ea.Individual;
import seneca.structgen.ea.Population;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class BondCrossOver {

    private int numberOfAtomsToBeModified = 0;
    private int numberOfAtomsToBeRetained = 0;
    private IAtomContainer leftOverFragment = null;
    private List<IAtomContainer> retainedFragments = null;
    private List<IAtomContainer> toBeModifiedFragments = null;
    private DadSplitter dadSplitter = null;
    private StructureMerger structureMerger = null;
    private double cutRatio = 0.6;
    private CrossOverColorer colorer = null;
    private RandomGenerator randomGenerator = null;
    private MomSplitter momSplitter = null;
    private IAtomContainer dad, mom = null;
    private static final String ATOM_INDEX = "AtomIndex";
    private static final String BOND_ORDER_SUM = "BondOrderSum";
    private List<List<IBond>> allBondsList = null;
    SaturationChecker satCheck = null;

    public BondCrossOver() throws Exception {
        structureMerger = new StructureMerger();
        colorer = new CrossOverColorer();
        satCheck = new SaturationChecker();
    }

    public double getCutRatio() {
        return cutRatio;
    }

    public void setCutRatio(double cutRatio) {
        this.cutRatio = cutRatio;
    }

    public void cross(Population<Individual> twoBestIndividuals) throws Exception {
        initiateParents(twoBestIndividuals);
        initializeContainers();
        //setBondIdentifiersFor(twoBestIndividuals);
        decideNumberOfBondsToBeChanged();
        fragmentParentsBasedOnTheDecision();
        backUpBondsAndDeleteThem();
        produceOffSpringsByCrossingBonds();
    }

    private void initiateParents(Population<Individual> twoBestIndividuals) {
        if (twoBestIndividuals.size() == 2) {
            this.dad = twoBestIndividuals.get(0).getMolecule();
            this.mom = twoBestIndividuals.get(1).getMolecule();
            colorer.colorFull(dad, Color.blue);
            colorer.colorFull(mom, Color.red);
            writeImage(dad, "dad");
            writeImage(mom, "mom");
            System.out.println("DAD: BondorderSum: " + getTotalBondOrderSumFor(dad));
            System.out.println("MOM: BondorderSum: " + getTotalBondOrderSumFor(mom));
        }
    }

    private void initializeContainers() {
        retainedFragments = new ArrayList<IAtomContainer>();
        toBeModifiedFragments = new ArrayList<IAtomContainer>();
        allBondsList = new ArrayList<List<IBond>>();
        dadSplitter = new DadSplitter(dad);
        momSplitter = new MomSplitter(mom);
    }

    private void setBondIdentifiersFor(Population<Individual> twoBestIndividuals) {
        String identifier = "A";
        for (int i = 0; i <= 1; i++) {
            Individual individual = twoBestIndividuals.get(i);
            IAtomContainer molecule = individual.getMolecule();
            molecule.setID(identifier);
            getBondIDsFor(molecule, identifier);
            int charValue = identifier.charAt(0);
            identifier = String.valueOf((char) (charValue + 1));
        }
    }

    private void getBondIDsFor(IAtomContainer atomContainer, String id) {
        int atomCount = 1;
        for (IBond bond : atomContainer.bonds()) {
            bond.setID(id + "-" + atomCount);
            atomCount++;
        }
    }

    private void decideNumberOfBondsToBeChanged() {
        int atomCount = mom.getAtomCount();
        numberOfAtomsToBeModified = (int) (atomCount * getCutRatio());
        numberOfAtomsToBeRetained = atomCount - numberOfAtomsToBeModified;
        System.out.println("To Modify - " + numberOfAtomsToBeModified + " ;to retain - " + numberOfAtomsToBeRetained);
    }

    private void fragmentParentsBasedOnTheDecision() throws Exception {
        separateDad();
        seperateMom();
    }

    private void separateDad() throws Exception {
        IAtomContainer toModify = dadSplitter.extract(numberOfAtomsToBeModified);
        IAtomContainer retained = dadSplitter.getLeftOver();
        toBeModifiedFragments.add(toModify);
        retainedFragments.add(retained);

        colorer.colorFull(toModify, Color.blue);
        colorer.colorFull(retained, Color.blue);
        colorer.colorPartial(toModify, Color.blue);
        colorer.colorPartial(retained, Color.blue);
        writeImage(toModify, toBeModifiedFragments.size() + "modifiedDad");
        writeImage(retained, retainedFragments.size() + "retainedDad");

    }

    private void seperateMom() throws Exception {
        IAtomContainer toBeModifiedDad = toBeModifiedFragments.get(0);
        IAtomContainer toBeModifiedMom = momSplitter.selectFragmentToModify(toBeModifiedDad);
        IAtomContainer retainedMomMolecule = momSplitter.getRetainedMomMolecule(toBeModifiedMom);

        System.out.println("Result - " + toBeModifiedMom.getAtomCount() + "  " + toBeModifiedMom.getBondCount());
        System.out.println("leftover - " + retainedMomMolecule.getAtomCount() + "  " + retainedMomMolecule.getBondCount());

        toBeModifiedFragments.add(toBeModifiedMom);
        retainedFragments.add(retainedMomMolecule);

        colorer.colorFull(toBeModifiedMom, Color.red);
        colorer.colorFull(retainedMomMolecule, Color.red);
        colorer.colorPartial(toBeModifiedMom, Color.red);
        colorer.colorPartial(retainedMomMolecule, Color.red);
        writeImage(toBeModifiedMom, toBeModifiedFragments.size() + "modifyMom");
        writeImage(retainedMomMolecule, retainedFragments.size() + "retainedMom");

    }

    private void backUpBondsAndDeleteThem() {
        for (int i = 0; i <= 1; i++) {
            IAtomContainer fragmentToBeModified = toBeModifiedFragments.get(i);
            allBondsList.add(Arrays.asList(getBondArray(fragmentToBeModified)));
            fragmentToBeModified.removeAllBonds();
        }
    }

    private IBond[] getBondArray(IAtomContainer molecule) {
        return AtomContainerManipulator.getBondArray(molecule);
    }

    private Population<Individual> produceOffSpringsByCrossingBonds() throws Exception {
        System.out.println("-- MERGING --");
        Population<Individual> offSpringsFromCrossOver = new Population<Individual>();
        for (int i = 0; i <= 1; i++) {
            Color nativeBonds = Color.blue;
            Color foreignBonds = Color.red;
            int indexToBeUsed = 1;
            int count = 1;
            if (i == 1) {
                indexToBeUsed--;
                nativeBonds = Color.red;
                foreignBonds = Color.blue;
                count++;
            }
            IAtomContainerSet fragmentsToCombine = new AtomContainerSet();
            IAtomContainer retainedFragment = retainedFragments.get(i);
            IAtomContainer toBeModified = toBeModifiedFragments.get(i);
            for (IBond bond : allBondsList.get(indexToBeUsed)) {
                toBeModified.addBond(bond);
            }
            colorer.colorPartial(toBeModified, foreignBonds);
            colorer.colorPartial(retainedFragment, nativeBonds);
            writeImage(toBeModified, "afterBondsSwap/" + count + "-modifiedBonds");
            writeImage(retainedFragment, "afterBondsSwap/" + count + "-retained");
            addToBeModifiedFragmentsTo(fragmentsToCombine, retainedFragment);
            addToBeModifiedFragmentsTo(fragmentsToCombine, toBeModified);
            System.out.println("FRAGMENTS TO EXCHANGE SIZE : " + fragmentsToCombine.getAtomContainerCount());
            offSpringsFromCrossOver.add(makeChildFrom(count, fragmentsToCombine, "bonds/"));
            makeOffSpringFrom(i, createDump(i, fragmentsToCombine), "new/");
        }
        return offSpringsFromCrossOver;
    }

    private void addToBeModifiedFragmentsTo(IAtomContainerSet fragmentsToCombine, IAtomContainer moleculeToAdd) {
        if (!ConnectivityChecker.isConnected(moleculeToAdd)) {
            IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(moleculeToAdd);
            for (IAtomContainer molecule : partitionIntoMolecules.atomContainers()) {
                fragmentsToCombine.addAtomContainer(molecule);
            }
        } else {
            fragmentsToCombine.addAtomContainer(moleculeToAdd);
        }
    }

    private Individual makeChildFrom(int i, IAtomContainerSet fragmentsToCombine, String folder) {
        Individual offSpring = null;
        try {

            IAtomContainer possiblyConnectedMolecule = structureMerger.generate(fragmentsToCombine);
            if (ConnectivityChecker.isConnected(possiblyConnectedMolecule)) {
                System.out.println("CONNECTED CHILD: " + possiblyConnectedMolecule.getAtomCount() + " ; " + possiblyConnectedMolecule.getBondCount());
                colorer.determineBondsToColor(possiblyConnectedMolecule);
                writeImage(possiblyConnectedMolecule, folder + i + "recombinedChild");
                for (IAtom momAtom : possiblyConnectedMolecule.atoms()) {
                    System.out.println("Index: " + possiblyConnectedMolecule.getAtomNumber(momAtom) + " Symbol : " + momAtom.getSymbol() + " BondorderSum: " + possiblyConnectedMolecule.getBondOrderSum(momAtom));
                }
                System.out.println("BONDORDERSUM CHILD - " + i + " : " + getTotalBondOrderSumFor(possiblyConnectedMolecule));
                randomGenerator = new RandomGenerator(possiblyConnectedMolecule);
                randomGenerator.mutate(possiblyConnectedMolecule);
                IAtomContainer mutatedChild = randomGenerator.getMolecule();
                colorer.determineBondsToColor(mutatedChild);
                writeImage(mutatedChild, folder + i + "mutatedChild");
                offSpring = new Individual(mutatedChild);
            }
            System.out.println("---------------------------------------------");
        } catch (Exception ex) {
            Logger.getLogger(CrossOver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return offSpring;
    }

    private IAtomContainer createDump(int i, IAtomContainerSet fragmentsToCombine) throws CDKException {
        IAtomContainer combinedMolecule = fragmentsToCombine.getBuilder().newInstance(IAtomContainer.class);
        for (IAtomContainer container : fragmentsToCombine.atomContainers()) {
            combinedMolecule.add(container);
        }
        if (satCheck.allSaturated(combinedMolecule)) {
            System.out.println("DUmp saturated ");
            writeImage(combinedMolecule, "new/" + i + "-combined.png");
        }
        return combinedMolecule;
    }

    private Individual makeOffSpringFrom(int i, IAtomContainer dump, String folder) {
        System.out.println("Using new one...");
        Individual offSpring = null;
        ConnectedStructureGenerator structureGenerator = new ConnectedStructureGenerator();
        try {

            IAtomContainer possiblyConnectedMolecule = structureGenerator.generate(dump);
            if (ConnectivityChecker.isConnected(possiblyConnectedMolecule)) {
                System.out.println("CONNECTED CHILD: " + possiblyConnectedMolecule.getAtomCount() + " ; " + possiblyConnectedMolecule.getBondCount());
                colorer.determineBondsToColor(possiblyConnectedMolecule);
                writeImage(possiblyConnectedMolecule, folder + i + "recombinedChild");
                for (IAtom momAtom : possiblyConnectedMolecule.atoms()) {
                    System.out.println("Index: " + possiblyConnectedMolecule.getAtomNumber(momAtom) + " Symbol : " + momAtom.getSymbol() + " BondorderSum: " + possiblyConnectedMolecule.getBondOrderSum(momAtom));
                }
                System.out.println("BONDORDERSUM CHILD - " + i + " : " + getTotalBondOrderSumFor(possiblyConnectedMolecule));
                randomGenerator = new RandomGenerator(possiblyConnectedMolecule);
                randomGenerator.mutate(possiblyConnectedMolecule);
                IAtomContainer mutatedChild = randomGenerator.getMolecule();
                colorer.determineBondsToColor(mutatedChild);
                writeImage(mutatedChild, folder + i + "mutatedChild");
                offSpring = new Individual(mutatedChild);
            } else {
                System.out.println("NEW: NOT CONNECTED");
            }
            System.out.println("---------------------------------------------");
        } catch (Exception ex) {
            Logger.getLogger(CrossOver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return offSpring;
    }

    private double getTotalBondOrderSumFor(IAtomContainer molecule) {
        double bondOrder = 0;
        for (IAtom atom : molecule.atoms()) {
            bondOrder += AtomContainerManipulator.getBondOrderSum(molecule, atom);
        }
        return bondOrder;
    }

    private void printBondIdentifiers(IAtomContainer atomContainer) {
        for (IBond bond : atomContainer.bonds()) {
            System.out.println("Bond: " + bond.getID());
        }
    }

    public void writeImage(IAtomContainer atomContainer, String title) {
        try {
            if (ConnectivityChecker.isConnected(atomContainer)) {
                BufferedImage image = new StructureImageGenerator().generateStructureImageWithAtomNumber(atomContainer, new Dimension(186, 186));
                String filename = "/Users/kalai/images/bondsCrossover/" + title + ".png";
                ImageIO.write((RenderedImage) image, "PNG", new File(filename));

            } else {
                IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
                System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
                int count = 1;
                for (IAtomContainer container : partitionIntoMolecules.atomContainers()) {
                    writeImage(container, title + " - " + count);
                    count++;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BondCrossOver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
