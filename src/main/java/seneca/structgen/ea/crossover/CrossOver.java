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
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.structgen.ea.Individual;
import seneca.structgen.ea.Population;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class CrossOver {

    private int numberOfAtomsToBeModified = 0;
    private int numberOfAtomsToBeRetained = 0;
    private IAtomContainer leftOverFragment, childA, childB = null;
    private List<IAtomContainer> retainedFragments = null;
    private List<IAtomContainer> toBeModifiedFragments = null;
    private DadSplitter dadSplitter = null;
    private StructureMerger structureMerger = null;
    private double cutRatio = 0.5;
    //private CrossOverColorer colorer = null;
    private RandomGenerator randomGenerator = null;
    private MomSplitter momSplitter = null;
    private IAtomContainer dad, mom = null;
    private static final String ATOM_INDEX = "AtomIndex";
    private static final String BOND_ORDER_SUM = "BondOrderSum";
    private int generationNumber = 0;
    private int iterationCOunt = 0;
    private MoleculeHashGenerator hashGenerator = null;
    private boolean stopCrossOver = false;

    public CrossOver() {
        structureMerger = new StructureMerger();
        //    colorer = new CrossOverColorer();
        hashGenerator = new HashGeneratorMaker().depth(16).elemental().molecular();
    }

    public CrossOver(boolean stopEvolving) {
    		this();
        this.stopCrossOver = stopEvolving;
    }

    public double getCutRatio() {
        return cutRatio;
    }

    public void setCutRatio(double cutRatio) {
        this.cutRatio = cutRatio;
    }

    private List<IAtomContainer> getRetainedFragments() {
        return retainedFragments;
    }

    private List<IAtomContainer> getToBeModifiedFragments() {
        return toBeModifiedFragments;
    }

    public Population<Individual> crossAndExpand(Population<Individual> population, int required) {
        generationNumber++;
        Population<Individual> crossed = new Population<Individual>();

        for (int i = 0; i < population.size(); i++) {
            for (int j = 0; j < population.size(); j++) {
                if (stopCrossOver) {
                    return crossed;
                }
                if (i != j) {
                    try {
                        if (crossed.size() < required) {
                            iterationCOunt++;
                            crossed.addAll(cross(population.get(i), population.get(j)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        while (crossed.size() != required) {
            crossed.remove(crossed.size() - 1);
        }
        return crossed;
    }

    public Population<Individual> cross(Individual a, Individual b) throws Exception {
        Population<Individual> twoBestIndividuals = new Population<Individual>();
        twoBestIndividuals.add(a);
        twoBestIndividuals.add(b);
        return cross(twoBestIndividuals);
    }

    public Population<Individual> cross(Population<Individual> twoBestIndividuals) throws Exception {
        initiateParents(twoBestIndividuals);
        initializeContainers();
        setBondIdentifiersFor(twoBestIndividuals);
        // setAtomIdentifiersFor(twoBestIndividuals);
        decideNumberOfBondsToBeChanged();
        fragmentParentsBasedOnTheDecision();
        return produceOffSprings();
        //crossFragmentsOfParents();

    }

    private void initiateParents(Population<Individual> twoBestIndividuals) {
        if (twoBestIndividuals.size() == 2) {
            this.dad = twoBestIndividuals.get(0).getMolecule();
            this.mom = twoBestIndividuals.get(1).getMolecule();
            //     colorer.colorFull(dad, Color.blue);
            //     colorer.colorFull(mom, Color.red);
//            writeImage(dad, "dad");
//            writeImage(mom, "mom");
            setupBondOrdersAndAtomIndices();
        }
    }

    private void setupBondOrdersAndAtomIndices() {
        setUpAtomIndices(mom);
        setUpAtomIndices(dad);
        setUpBondOrderSUm(mom);
        setUpBondOrderSUm(dad);
    }

    private void setUpAtomIndices(IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms()) {
            atom.setProperty(ATOM_INDEX, molecule.getAtomNumber(atom));
        }
    }

    private void setUpBondOrderSUm(IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms()) {
            atom.setProperty(BOND_ORDER_SUM, AtomContainerManipulator.getBondOrderSum(molecule, atom));
        }
    }

    private void initializeContainers() {
        retainedFragments = new ArrayList<IAtomContainer>();
        toBeModifiedFragments = new ArrayList<IAtomContainer>();
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

    private void setAtomIdentifiersFor(Population<Individual> twoBestIndividuals) {
        String identifier = "C";
        for (int i = 0; i <= 1; i++) {
            Individual individual = twoBestIndividuals.get(i);
            IAtomContainer molecule = individual.getMolecule();
            getAtomIDsFor(molecule, identifier);
            int charValue = identifier.charAt(0);
            identifier = String.valueOf((char) (charValue + 1));
        }
    }

    private void getAtomIDsFor(IAtomContainer atomContainer, String id) {
        int atomCount = 1;
        for (IAtom atom : atomContainer.atoms()) {
            atom.setID(id + "-" + atomCount);
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
        System.out.println("DAD bonordersum - " + getTotalBondOrderSumFor(this.dad));
        IAtomContainer toModify = dadSplitter.extract(numberOfAtomsToBeModified);
        IAtomContainer retained = dadSplitter.getLeftOver();
        getToBeModifiedFragments().add(toModify);
        getRetainedFragments().add(retained);

        //   colorer.colorFull(toModify, Color.blue);
        //    colorer.colorFull(retained, Color.blue);
        //   colorer.colorPartial(toModify, Color.blue);
        //   colorer.colorPartial(retained, Color.blue);
//        writeImage(toModify, getRetainedFragments().size() + "dadToModify");
//        writeImage(retained, getToBeModifiedFragments().size() + "dadToRetain");

    }

    private void seperateMom() throws Exception {
        System.out.println("MOM bonordersum - " + getTotalBondOrderSumFor(this.mom));
        IAtomContainer toBeModifiedDad = getToBeModifiedFragments().get(0);
        IAtomContainer toBeModifiedMom = momSplitter.selectFragmentToModify(toBeModifiedDad);


        if (toBeModifiedMom.getAtomCount() == toBeModifiedDad.getAtomCount()) {
            System.out.println("to modify dad:mum MATCH..");
        }
        IAtomContainer retainedMomMolecule = momSplitter.getRetainedMomMolecule(toBeModifiedMom);

        System.out.println("Result - " + toBeModifiedMom.getAtomCount() + "  " + toBeModifiedMom.getBondCount());
        System.out.println("leftover - " + retainedMomMolecule.getAtomCount() + "  " + retainedMomMolecule.getBondCount());

        getToBeModifiedFragments().add(toBeModifiedMom);
        getRetainedFragments().add(retainedMomMolecule);

        //    colorer.colorFull(toBeModifiedMom, Color.red);
        //     colorer.colorFull(retainedMomMolecule, Color.red);


        //   colorer.colorPartial(toBeModifiedMom, Color.red);
        //    colorer.colorPartial(retainedMomMolecule, Color.red);
//        writeImage(toBeModifiedMom, getToBeModifiedFragments().size() + "momToModify");
//        writeImage(retainedMomMolecule, getRetainedFragments().size() + "momToRetain");

    }

    private void crossFragmentsOfParents() {
        childA = new AtomContainer(mom.getAtomCount(), mom.getBondCount(), mom.getLonePairCount(), mom.getSingleElectronCount());
        childB = new AtomContainer(mom.getAtomCount(), mom.getBondCount(), mom.getLonePairCount(), mom.getSingleElectronCount());
        // add modified fragment and bonds
        System.out.println("Merging to modify frags............");
        createChildA();
        createChildB();
        System.out.println("creATED CHILD A AND B");
//            for (IAtom atom : getToBeModifiedFragments().get(0).atoms()) {
//                  childB.addAtom(atom);
//            }
//            for (IBond bond : getToBeModifiedFragments().get(0).bonds()) {
//                  childB.addBond(bond);
//            }
//            for (IAtom atom : getToBeModifiedFragments().get(1).atoms()) {
//                  childA.addAtom(atom);
//                  // childA.setAtom((Integer) atom.getProperty(ATOM_INDEX), atom);
//            }
//            for (IBond bond : getToBeModifiedFragments().get(1).bonds()) {
//                  childA.addBond(bond);
//            }
//            System.out.println("Merging to retain frags............");
//            for (IAtom atom : getRetainedFragments().get(0).atoms()) {
//                  childA.addAtom(atom);
//                  //childA.setAtom((Integer) atom.getProperty(ATOM_INDEX), atom);
//
//            }
//            for (IBond bond : getRetainedFragments().get(0).bonds()) {
//                  childA.addBond(bond);
//            }
//            //System.out.println("-----------");
//            for (IAtom atom : getRetainedFragments().get(1).atoms()) {
//                  // System.out.println("index : " + (Integer) atom.getProperty(ATOM_INDEX));
//                  childB.addAtom(atom);
//                  // childB.setAtom((Integer) atom.getProperty(ATOM_INDEX), atom);
//
//            }
//            for (IBond bond : getRetainedFragments().get(1).bonds()) {
//                  childB.addBond(bond);
//            }
        System.out.println("child b : " + childB.getAtomCount());
        setAtomsAccordingToRecordedIndices(childA);
        setAtomsAccordingToRecordedIndices(childB);
        System.out.println("child - A : " + childA.getAtomCount() + " - " + childA.getBondCount());
        System.out.println("child - B : " + childB.getAtomCount() + " - " + childB.getBondCount());
        System.out.println("MOM: " + mom.getAtomCount() + " - " + mom.getBondCount());
        System.out.println("DAD : " + dad.getAtomCount() + " - " + dad.getBondCount());
        System.out.println("CHILD A");
        for (IAtom atomm : childA.atoms()) {
            System.out.println("Index: " + childA.getAtomNumber(atomm) + " Symbol : " + atomm.getSymbol() + " BondorderSum: " + atomm.getProperty(BOND_ORDER_SUM));
        }
        System.out.println("CHILD B");
        for (IAtom atomm : childB.atoms()) {
            System.out.println("Index: " + childB.getAtomNumber(atomm) + " Symbol : " + atomm.getSymbol() + " BondorderSum: " + atomm.getProperty(BOND_ORDER_SUM));
        }
        System.out.println("FINISHED writing tentative parents");

    }

    private void createChildA() {
        childA.setAtoms(getAtomArray(getRetainedFragments().get(0)));
        childA.setBonds(getBondArray(getRetainedFragments().get(0)));
        childA.setAtoms(getAtomArray(getToBeModifiedFragments().get(1)));
        childA.setBonds(getBondArray(getToBeModifiedFragments().get(1)));
    }

    private void createChildB() {
        childB.setAtoms(getAtomArray(getRetainedFragments().get(1)));
        childB.setBonds(getBondArray(getRetainedFragments().get(1)));
        childB.setAtoms(getAtomArray(getToBeModifiedFragments().get(0)));
        childB.setBonds(getBondArray(getToBeModifiedFragments().get(0)));
    }

    private IAtom[] getAtomArray(IAtomContainer molecule) {
        return AtomContainerManipulator.getAtomArray(molecule);
    }

    private IBond[] getBondArray(IAtomContainer molecule) {
        return AtomContainerManipulator.getBondArray(molecule);
    }

    private void setAtomsAccordingToRecordedIndices(IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms()) {
            molecule.setAtom((Integer) atom.getProperty(ATOM_INDEX), atom);
        }
    }

    private Population<Individual> produceOffSprings() throws Exception {
        Population<Individual> offSpringsFromCrossOver = new Population<Individual>();
        for (int i = 0; i <= 1; i++) {
            int indexToBeUsed = 1;
            if (i == 1) {
                indexToBeUsed--;
            }
            IAtomContainerSet fragmentsToCombine = new AtomContainerSet();
            IAtomContainer retainedFragment = getRetainedFragments().get(i);
            IAtomContainer toBeModified = getToBeModifiedFragments().get(indexToBeUsed);
            addToBeModifiedFragmentsTo(fragmentsToCombine, retainedFragment);
            addToBeModifiedFragmentsTo(fragmentsToCombine, toBeModified);
            System.out.println("FRAGMENTS TO COMBINE SIZE : " + fragmentsToCombine.getAtomContainerCount());
            Individual child = makeChildFrom(i, fragmentsToCombine);
            if (child != null) {
                offSpringsFromCrossOver.add(child);
            }

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

    private Individual makeChildFrom(int i, IAtomContainerSet fragmentsToCombine) {
        Individual offSpring = null;
        try {

            IAtomContainer merged = structureMerger.generate(fragmentsToCombine);
            if (ConnectivityChecker.isConnected(merged)) {
                System.out.println("CONNECTED CHILD: " + merged.getAtomCount() + " ; " + merged.getBondCount());
                //        colorer.determineBondsToColor(merged);
                //        writeImage(merged, i + "recombinedChild");
                for (IAtom momAtom : merged.atoms()) {
                    System.out.println("Index: " + merged.getAtomNumber(momAtom) + " Symbol : " + momAtom.getSymbol() + " BondorderSum: " + merged.getBondOrderSum(momAtom));
                }
                System.out.println("BONDORDERSUM : " + getTotalBondOrderSumFor(merged));
//                randomGenerator = new RandomGenerator(merged);
//                randomGenerator.mutate(merged);
//                IAtomContainer mutatedChild = randomGenerator.getMolecule();
                //       colorer.determineBondsToColor(mutatedChild);
                //        writeImage(mutatedChild, i + "mutatedChild");

                //offSpring.setMolecule(mutatedChild);
                offSpring = new Individual(merged);
                offSpring.setHashCode(hashGenerator.generate(merged));
            }
            System.out.println("---------------------------------------------");
        } catch (CDKException ex) {
            System.out.println("Structure formation failed..");
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

    private int getTotalImplicitHCount(IAtomContainer molecule) {
        int hcount = 0;
        for (IAtom atom : molecule.atoms()) {
            hcount += AtomContainerManipulator.countHydrogens(molecule, atom);
        }
        return hcount;
    }

    private void printBondIdentifiers(IAtomContainer atomContainer) {
        for (IBond bond : atomContainer.bonds()) {
            System.out.println("Bond: " + bond.getID());
        }
    }

    private void printAtomIdentifiers(IAtomContainer atomContainer) {
        for (IAtom atom : atomContainer.atoms()) {
            System.out.println("Atom: " + atom.getID());
        }
    }

    public void quickCheckSaturation(IAtomContainer molecule) throws Exception {

        SaturationChecker satCheck = new SaturationChecker();
        //        System.out.println("//////////////////////////////////////////////////////////");
        System.out.println("Saturation : " + satCheck.allSaturated(molecule) + " H count " + getTotalImplicitHCount(molecule));

        //System.out.println("Total atom count - " + molecule.getAtomCount() + "Hetero : " + AtomContainerManipulator.getHeavyAtoms(molecule).size());
//            for (IAtom atom : molecule.atoms()) {
//                  System.out.println(atom.getSymbol().toString() + " - " + satCheck.isSaturated(atom, molecule) + " - max order: "
//                          + molecule.getMaximumBondOrder(atom) + " - bond order sum : " + molecule.getBondOrderSum(atom));
//                  System.out.println("ConnectedAtoms : " + molecule.getConnectedAtomsCount(atom) + " Implicit-H : " + atom.getImplicitHydrogenCount());
//            }
//            System.out.println("SMILES: " + new SmilesGenerator().createSMILES(molecule));
        //   System.out.println("//////////////////////////////////////////////////////////");

    }

//    public void writeImage(IAtomContainer atomContainer, String title) {
//        try {
//            if (ConnectivityChecker.isConnected(atomContainer)) {
//                BufferedImage image = new StructureImageGenerator().generateStructureImageWithAtomNumber(atomContainer, new Dimension(186, 186));
//                String filename = "/Users/kalai/images/" + generationNumber + "-" + iterationCOunt + "-" + title + ".png";
//                ImageIO.write((RenderedImage) image, "PNG", new File(filename));
//
//            } else {
//                IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
//                System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
//                int count = 1;
//                for (IAtomContainer container : partitionIntoMolecules.atomContainers()) {
//                    writeImage(container, title + " - " + count);
//                    count++;
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(CrossOver.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
