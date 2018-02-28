/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.AtomContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalai
 */
public class DadSplitter {

    IAtomContainer leftOver = null;
    IAtomContainer dad = null;

    public DadSplitter(IAtomContainer dad) {
        this.dad = new AtomContainer(dad);
    }

    public IAtomContainer extract(int max) throws Exception {
        System.out.println("Molecule ---- Atom Count  ---- Bond count");
        System.out.println("Daddy - " + dad.getAtomCount() + "  " + dad.getBondCount());
        System.out.println("Max - " + max);

        // IAtom start = getAnyTerminalAtomFrom(dad);
        IAtom start = dad.getAtom(0);
        List<IAtom> sphere = new ArrayList<IAtom>();
        sphere.add(start);

        IAtomContainer retained = dad.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer removeAllVisitedFlags = removeAllVisitedFlagsOf(dad);
        dad = removeAllVisitedFlags;
        breadthFirstSearch(sphere, retained, max);

        System.out.println("Result - " + retained.getAtomCount() + "  " + retained.getBondCount());
        System.out.println("leftover - " + leftOver.getAtomCount() + "  " + leftOver.getBondCount());
        return removeAllVisitedFlagsOf(retained);
    }

    private IAtom getAnyTerminalAtomFrom(IAtomContainer moleculeToSplit) {
        IAtom selectedAtom = null;
        for (IAtom atom : moleculeToSplit.atoms()) {
            if (moleculeToSplit.getConnectedAtomsList(atom).size() == 1) {
                selectedAtom = atom;
                break;
            }
        }
        return selectedAtom;
    }

    private IAtomContainer removeAllVisitedFlagsOf(IAtomContainer result) {
        for (IBond bond : result.bonds()) {
            bond.setFlag(CDKConstants.VISITED, false);
        }
        for (IAtom atom : result.atoms()) {
            atom.setFlag(CDKConstants.VISITED, false);
        }
        return result;
    }

    private void breadthFirstSearch(List<IAtom> sphere, IAtomContainer tracedMolecule, int max) {
        IAtom nextAtom;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        for (IAtom atom : sphere) {

            tracedMolecule.addAtom(atom);
            // first copy LonePair's and SingleElectron's of this Atom as they need
            // to be copied too
            List<ILonePair> lonePairs = dad.getConnectedLonePairsList(atom);
            for (ILonePair lonePair : lonePairs) {
                tracedMolecule.addLonePair(lonePair);
            }

            List<ISingleElectron> singleElectrons = dad.getConnectedSingleElectronsList(atom);
            for (ISingleElectron singleElectron : singleElectrons) {
                tracedMolecule.addSingleElectron(singleElectron);
            }

            // now look at bonds
            List<IBond> bonds = dad.getConnectedBondsList(atom);
            for (IBond bond : bonds) {
                if (!bond.getFlag(CDKConstants.VISITED)) {
                    //    System.out.println("Traced bond: " + bond.getID());
                    tracedMolecule.addBond(bond);
                    bond.setFlag(CDKConstants.VISITED, true);
                }
                nextAtom = bond.getConnectedAtom(atom);
                if (!nextAtom.getFlag(CDKConstants.VISITED)) {
                    newSphere.add(nextAtom);
                    nextAtom.setFlag(CDKConstants.VISITED, true);
                }
            }
            if (max > -1 && tracedMolecule.getAtomCount() == max) {
                System.out.println("BFS: " + max + " returning.");
                this.leftOver = removeAllVisitedFlagsOf(partitionDadFrom(tracedMolecule));
                return;
            }
        }
        if (newSphere.size() > 0) {
            breadthFirstSearch(newSphere, tracedMolecule, max);
        }
    }

    private IAtomContainer partitionDadFrom(IAtomContainer tracedMolecule) {
        removeFromDadBondsConnectingTo(tracedMolecule);
        IAtomContainer temporarySet = splitAtomsLonePairsAndElectronsFrom(tracedMolecule);
        IAtomContainer splitBonds = splitBondsFrom(tracedMolecule);
        temporarySet.add(splitBonds);
        return temporarySet;
    }

    private IAtomContainer splitAtomsLonePairsAndElectronsFrom(IAtomContainer tracedMolecule) {
        IAtomContainer temporaryContainer = tracedMolecule.getBuilder().newInstance(IAtomContainer.class);
        for (IAtom atom : dad.atoms()) {
            if (!tracedMolecule.contains(atom)) {
                temporaryContainer.addAtom(atom);
            }
            List<ILonePair> lonePairs = dad.getConnectedLonePairsList(atom);
            for (ILonePair lonePair : lonePairs) {
                if (!tracedMolecule.contains(lonePair)) {
                    temporaryContainer.addLonePair(lonePair);
                }
            }
            List<ISingleElectron> singleElectrons = dad.getConnectedSingleElectronsList(atom);
            for (ISingleElectron singleElectron : singleElectrons) {
                if (!tracedMolecule.contains(singleElectron)) {
                    temporaryContainer.addSingleElectron(singleElectron);
                }
            }
        }
        return temporaryContainer;
    }

    private void removeFromDadBondsConnectingTo(IAtomContainer tracedMolecule) {
        for (int i = 0; i < tracedMolecule.getAtomCount(); i++) {
            IAtom atom = tracedMolecule.getAtom(i);
            List<IAtom> connectedAtomsList = dad.getConnectedAtomsList(atom);
            for (int j = 0; j < connectedAtomsList.size(); j++) {
                if (!tracedMolecule.contains(connectedAtomsList.get(j))) {
                    IBond bond = dad.getBond(atom, connectedAtomsList.get(j));
                    dad.removeBond(atom, connectedAtomsList.get(j));
                    tracedMolecule.removeBond(atom, connectedAtomsList.get(j));
                    // System.out.println("Removed bonds: " + bond.getID());
                }
            }
        }
    }

    private IAtomContainer splitBondsFrom(IAtomContainer tracedMolecule) {
        IAtomContainer temporaryContainer = tracedMolecule.getBuilder().newInstance(IAtomContainer.class);
        for (IBond bond : dad.bonds()) {
            if (!tracedMolecule.contains(bond)) {
                //System.out.println("Temporary bonds: " + bond.getID());
                if (bond.getAtomCount() == 2) {
                    temporaryContainer.addBond(bond);
                }
            }
        }
        return temporaryContainer;
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

    public IAtomContainer getLeftOver() {
        return this.leftOver;
    }
}
