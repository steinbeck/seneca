/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import seneca.core.Utilities;

/**
 * @author kalai
 */
public class MomSplitter {

    IAtomContainer mom = null;
    private static final String ATOM_INDEX = "AtomIndex";
    private static final String BOND_ORDER_SUM = "BondOrderSum";

    public MomSplitter(IAtomContainer mom) {
        this.mom = mom;
    }

    public IAtomContainer selectFragmentToModify(IAtomContainer toBeModifiedDad) throws CloneNotSupportedException {
        System.out.println("Selecting in mom splitter......");
        System.out.println("MOM: " + mom.getAtomCount() + " - " + mom.getBondCount());
        IAtomContainer toBeModifiedMom = mom.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer momBackUp = (IAtomContainer) Utilities.clone(mom);
        for (IAtom dadAtom : toBeModifiedDad.atoms()) {
            if (momBackUp.contains(dadAtom)) {
                toBeModifiedMom.addAtom(dadAtom);
            }
        }

        if (toBeModifiedMom.getAtomCount() == toBeModifiedDad.getAtomCount()) {
            System.out.println("Retaining MOM was SUCESS");
        } else {
            System.out.println("Retaining MOM was FAILURE");
        }
        addBondsIfAnyFor(toBeModifiedMom);
        return toBeModifiedMom;
    }

    private void addBondsIfAnyFor(IAtomContainer container) {
        int bondAdded = 0;
        for (int i = 0; i < container.getAtomCount(); i++) {
            for (int j = 0; j < container.getAtomCount(); j++) {
                if (i != j) {
                    IBond bond = mom.getBond(container.getAtom(i), container.getAtom(j));
                    if (bond != null) {
                        if (!container.contains(bond)) {
                            container.addBond(bond);
                            bondAdded++;
                        }
                    }
                }

            }
        }
        System.out.println("Bonds added to modified mom: " + bondAdded);
    }

    public IAtomContainer getRetainedMomMolecule(IAtomContainer toModify) {
        IAtomContainer retainedMom = mom.getBuilder().newInstance(IAtomContainer.class);
        for (IAtom atom : mom.atoms()) {
            if (!toModify.contains(atom)) {
                retainedMom.addAtom(atom);
            }
        }
        addBondsIfAnyFor(retainedMom);
        return retainedMom;
    }
}
