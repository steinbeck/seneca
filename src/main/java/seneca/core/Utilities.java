/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.openscience.cdk.Bond;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.gui.StructureImageGenerator;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class contains method to clone atomContainer container with same atoms but different
 * bonds.
 *
 * @author kalai
 */
public final class Utilities {

    static final MoleculeHashGenerator hashGenerator = new HashGeneratorMaker().depth(16).elemental().molecular();
    private static final RandomGenerator randomGenerator = new RandomGenerator(null);
    static final ElectronDonation model = ElectronDonation.cdk();
    static final CycleFinder cycles = Cycles.cdkAromaticSet();
    static final Aromaticity aromaticity = new Aromaticity(model, cycles);

    public static Object clone(IAtomContainer molecule) throws CloneNotSupportedException {
        IAtomContainer clone = molecule.getBuilder().newInstance(IAtomContainer.class);
        // clone all atoms
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            clone.addAtom(molecule.getAtom(f));
        }
        // clone bonds
        IBond bond;
        IBond newBond;
        IAtom[] newAtoms;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            bond = molecule.getBond(i);
            newBond = new Bond();
            newBond.setOrder(bond.getOrder());
            newBond.setStereo(bond.getStereo());
            newAtoms = new IAtom[bond.getAtomCount()];
            for (int j = 0; j < bond.getAtomCount(); ++j) {
                // newAtoms[j] = clone.getAtom(molecule.getAtomNumber((bond.getAtom(j))));
                newAtoms[j] = bond.getAtom(j);
            }
            newBond.setAtoms(newAtoms);
            clone.addBond(newBond);
        }
        ILonePair lp;
        for (int i = 0; i < molecule.getLonePairCount(); ++i) {
            lp = molecule.getLonePair(i);
            clone.addLonePair(lp);
        }
        ISingleElectron se;
        for (int i = 0; i < molecule.getSingleElectronCount(); ++i) {
            se = molecule.getSingleElectron(i);
            clone.addSingleElectron(se);
        }
        return clone;
    }

    public static List<IAtomContainer> removeRedundancy(List<IAtomContainer> molecule) {
        List<IAtomContainer> temp = new ArrayList<IAtomContainer>();
        Multimap<Number, IAtomContainer> map = ArrayListMultimap.create();
        for (int i = 0; i < molecule.size(); i++) {
            Number hash = hashGenerator.generate(molecule.get(i));
            map.put(hash, molecule.get(i));
        }
        System.out.println("map size: " + map.size());
        for (int i = 0; i < map.size(); i++) {
            for (IAtomContainer atomContainer : map.get(i)) {
                temp.add(atomContainer);
                break;
            }
        }
        System.out.println("temp size: " + temp.size());
        return temp;
    }


    public static Object cloneObject(Object obj) {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(buf);
            o.writeObject(obj);
            // Now get copies:
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
            return in.readObject();
        } catch (Exception exc) {
            exc.getMessage();
            return null;
        }
    }


    public static void calculateProperties(IAtomContainer molecule) {
        Object isPerceived = molecule.getProperty(SenecaConstants.AROMATICITY_PERCEIVED);
        if (isPerceived == null) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
                aromaticity.apply(molecule);
                molecule.setProperty(SenecaConstants.AROMATICITY_PERCEIVED, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sortByCostValue(List<IAtomContainer> structures) {
        boolean somethingsChanged;
        Object o1;
        do {
            somethingsChanged = false;
            for (int f = 0; f < structures.size() - 1; f++) {
                AtomContainer molecule_f = (AtomContainer) structures.get(f);
                AtomContainer molecule_f_1 = (AtomContainer) structures.get(f + 1);
                if (Double.parseDouble((String) molecule_f.getProperty("Score")) < Double.parseDouble((String) molecule_f_1.getProperty("Score"))) {
                    o1 = structures.get(f + 1);
                    structures.remove(f + 1);
                    structures.add(f, (IAtomContainer) o1);
                    somethingsChanged = true;
                }
            }
        } while (somethingsChanged);
    }

    public static int getAnyAtomCount(IAtomContainer ac, String atom) {
        int count = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtom(i).getSymbol().equals(atom)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isBondOrderSumPreserved(IAtomContainer molecule) {
        if (alreadyChecked(molecule)) {
            return true;
        }
        for (IAtom atom : molecule.atoms()) {
            Double bondordersum = atom.getProperty(SenecaConstants.ATOM_BOND_ORDER_SUM);
            if (bondordersum != AtomContainerManipulator.getBondOrderSum(molecule, atom)) {
                molecule.setProperty(SenecaConstants.BOND_ORDER_VERIFIED, false);
                return false;
            }
        }
        molecule.setProperty(SenecaConstants.BOND_ORDER_VERIFIED, true);
        return true;
    }

    private static boolean alreadyChecked(IAtomContainer molecule) {
        Boolean checked = molecule.getProperty(SenecaConstants.BOND_ORDER_VERIFIED);
        return (checked != null) ? checked.booleanValue() : false;
    }

    public static IAtomContainer mutate(IAtomContainer toMutate) {
        randomGenerator.setMolecule(toMutate);
        randomGenerator.mutate(toMutate);
        IAtomContainer mutatedMolecule = randomGenerator.getMolecule();
//        if (isBondOrderSumPreserved(mutatedMolecule)) {
//            return mutatedMolecule;
//        } else {
//            return mutate(toMutate);
//        }
        return mutatedMolecule;
    }

    public synchronized static void setBondOrderSumForAtoms(IAtomContainer molecule) {
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        } catch (CDKException e) {
            e.printStackTrace();
        }
        for (IAtom atom : molecule.atoms()) {
            //System.out.println(molecule.getAtomNumber(atom) + ":" + atom.getSymbol() + " - " + AtomContainerManipulator.getBondOrderSum(molecule, atom) + " - Valency: " + atom.getValency() + " - " + AtomContainerManipulator.countHydrogens(molecule, atom));
            atom.setProperty(SenecaConstants.ATOM_BOND_ORDER_SUM, AtomContainerManipulator.getBondOrderSum(molecule, atom));
        }
    }

    public static void writeImage(IAtomContainer atomContainer, String filename) {
        try {
            if (ConnectivityChecker.isConnected(atomContainer)) {
                BufferedImage image = new StructureImageGenerator().generateStructureImageWithAtomNumber(atomContainer, new Dimension(186, 186));
                ImageIO.write((RenderedImage) image, "PNG", new File(filename));

            } else {
                IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
                System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
                int count = 1;
                for (IAtomContainer container : partitionIntoMolecules.atomContainers()) {
                    writeImage(container, filename + " - " + count);
                    count++;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
