/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to table look up of stored serialized signatures and retrieve appropriate shifts for the
 * given molecule.
 *
 * @author kalai
 */
public class C13ShiftPredictor {

    HOSETable shiftTable;
    HOSECodeGenerator hoseGenerator = null;
    ElectronDonation model;
    CycleFinder cycles;
    Aromaticity aromaticity;

    public C13ShiftPredictor() {
        shiftTable = HOSETable.getInstance();
        hoseGenerator = new HOSECodeGenerator();
        model       = ElectronDonation.cdk();
        cycles      = Cycles.cdkAromaticSet();
        aromaticity = new Aromaticity(model, cycles);

    }

    public Double[] predictShiftsFor(IAtomContainer molecule) throws CDKException {
        List<Double> shifts = new ArrayList<Double>();
        calculatePropertiesFor(molecule);
        for (IAtom atom : molecule.atoms()) {
            if (atom.getSymbol().equals("C")) {
                String hose = getHose(atom, molecule);
                Double averageShiftValue = shiftTable.getShift(hose);
                shifts.add(averageShiftValue);
            }
        }
        Double[] shiftsArray = shifts.toArray(new Double[shifts.size()]);
        return shiftsArray;
    }

    public double predictShiftFor(String hose) throws CDKException {
        return shiftTable.getShift(hose);
    }

    private void calculatePropertiesFor(IAtomContainer molecule) throws CDKException {
//            AtomContainerManipulator.removeHydrogens(molecule);
//            addImplicitHydrogensToSatisfyValency(molecule);
        AtomContainerManipulator.removeHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        aromaticity.apply(molecule);
    }

    public static void addImplicitHydrogensToSatisfyValency(IAtomContainer mol) throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        for (IAtom atom : mol.atoms()) {
            IAtomType type = matcher.findMatchingAtomType(mol, atom);
            if (type != null) {
                AtomTypeManipulator.configure(atom, type);
                hAdder.addImplicitHydrogens(mol, atom);
            }
        }
    }

    private String getHose(IAtom atom, IAtomContainer molecule) throws CDKException {
        String hoseCode = hoseGenerator.getHOSECode(molecule, atom, 6, true);
        String[] split = hoseCode.split(";");
        //System.out.println("input: " + split[1]);

        return split[1];
    }
}
