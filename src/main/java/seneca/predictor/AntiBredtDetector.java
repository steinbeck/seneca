package seneca.predictor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 13/02/2013
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 * Class to find molecule that violates Bredt's rule. i.e, in rings with atom count 7 or less,
 * bridge atom should not possess a double bond (or) the ring system should not possess a trans double bond
 */
public class AntiBredtDetector {
    private IAtomContainer atomContainer;
    private IAtom a;
    private IAtom b;
    private IAtom c;
    private List<IAtom> d1d2;

    public boolean isAntiBredt(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
        Set<IAtom> sp2HybridAtoms = getAllSP2HybridAtoms();
        return sp2HybridAtoms.isEmpty() ? false :
                !hasRequiredConnectedAtoms(sp2HybridAtoms) ? false :
                        !formsTransDoubleBondInRing(sp2HybridAtoms) ? false : true;
    }


    private Set<IAtom> getAllSP2HybridAtoms() {
        Set<IAtom> sp2HybridAtoms = new HashSet<IAtom>();
        for (IBond bond : atomContainer.bonds()) {
            // System.out.println(bond);
            // System.out.println(bond.getOrder().toString());
            if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
                sp2HybridAtoms.add(bond.getAtom(0));
                sp2HybridAtoms.add(bond.getAtom(1));
            } else {
                if (bond.getAtom(0).getFlag(CDKConstants.ISAROMATIC)) {
                    sp2HybridAtoms.add(bond.getAtom(0));
                }
                if (bond.getAtom(1).getFlag(CDKConstants.ISAROMATIC)) {
                    sp2HybridAtoms.add(bond.getAtom(1));
                }
            }
        }
        return sp2HybridAtoms;
    }


    private boolean hasRequiredConnectedAtoms(Set<IAtom> sp2HybridAtoms) {
        /*
        required is each sp2 atom is connected to at least 2 or 3 atoms
         */
        Set<IAtom> atomsWithRequiredConnectivity = new HashSet<IAtom>(sp2HybridAtoms);
        for (IAtom atom : sp2HybridAtoms) {
            List<IAtom> connectedAtomsList = atomContainer.getConnectedAtomsList(atom);
            if (connectedAtomsList.size() < 2 || connectedAtomsList.size() > 3) {
                atomsWithRequiredConnectivity.remove(atom);
            }
        }
        sp2HybridAtoms = atomsWithRequiredConnectivity;
        return sp2HybridAtoms.isEmpty() ? false : true;
    }

    private boolean formsTransDoubleBondInRing(Set<IAtom> sp2Atoms) {

        for (IAtom b : sp2Atoms) {
            this.b = b;
            List<IAtom> connectedToB = atomContainer.getConnectedAtomsList(this.b);
            if (connectedToB.size() == 2) {
                for (IAtom atom : connectedToB) {
                    IBond.Order order = atomContainer.getBond(atom, this.b).getOrder();
                    if (order.equals(IBond.Order.DOUBLE) || order.equals(IBond.Order.UNSET)) {
                        this.c = atom;
                    } else {
                        this.a = atom;
                    }
                }
                boolean isAntiBredt = isDoubleBondTriSubstitutedAndInRing();
                if (isAntiBredt) {
                    return true;
                }
            } else if (connectedToB.size() == 3) {
                for (IAtom atom : connectedToB) {
                    IBond.Order order = atomContainer.getBond(atom, this.b).getOrder();
                    if (order.equals(IBond.Order.DOUBLE) || order.equals(IBond.Order.UNSET)) {
                        this.c = atom;
                        connectedToB.remove(atom);
                        break;
                    }
                }
                for (IAtom a : connectedToB) {
                    this.a = a;
                    boolean isAntiBredt = isDoubleBondTriSubstitutedAndInRing();
                    if (isAntiBredt) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isDoubleBondTriSubstitutedAndInRing() {
        return isTriSubstitutedDoubleBond() ? isInRing() : false;
    }


    private boolean isTriSubstitutedDoubleBond() {
        List<IAtom> connectedTo_c = atomContainer.getConnectedAtomsList(this.c);
        if (connectedTo_c.size() == 3) {
            assignd1d2(connectedTo_c);
            return true;
        }
        return false;
    }

    private void assignd1d2(List<IAtom> connectedToC) {
        connectedToC.remove(b);
        d1d2 = connectedToC;
    }

    private boolean isInRing() {
        IAtomContainer tracedMolecule = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        List<IAtom> sphere = new ArrayList<IAtom>();
        sphere.add(a);
        breadthFirstSearch(sphere, tracedMolecule, 0);
        return d1_d2_presentIn(tracedMolecule) ? true : false;
    }

    private void breadthFirstSearch(List<IAtom> sphere, IAtomContainer tracedMolecule, int depth) {
        removeAllVisitedFlags();
        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (IAtom atom : sphere) {

            tracedMolecule.addAtom(atom);
            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    tracedMolecule.addBond(bond);
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bond.getConnectedAtom(atom);
                if (nextAtom != b && nextAtom != c) {
                    if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                        newSphere.add(nextAtom);
                        nextAtom.setFlag(CDKConstants.VISITED, true);
                    }
                }
            }
            if (depth == 5) {
                return;
            }
        }
        depth++;
        if (newSphere.size() > 0) {
            breadthFirstSearch(newSphere, tracedMolecule, depth);
        }
        removeAllVisitedFlags();
    }

    private boolean d1_d2_presentIn(IAtomContainer tracedMolecule) {
        return tracedMolecule.contains(d1d2.get(0)) && tracedMolecule.contains(d1d2.get(1)) ? true : false;
    }

    private void removeAllVisitedFlags() {
        for (IBond bond : atomContainer.bonds()) {
            bond.setFlag(CDKConstants.VISITED, false);
        }
        for (IAtom atom : atomContainer.atoms()) {
            atom.setFlag(CDKConstants.VISITED, false);
        }
    }
    

}
