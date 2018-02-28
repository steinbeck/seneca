package seneca.predictor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import seneca.core.StructureIO;
import seneca.core.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 10/05/2013
 * Time: 15:17
 * <p/>
 * Class to identify molecules with ring strain.
 */
public class RingStrainDetector {

    private static final String QUERY_SMARTS = "[*H0]1-[*H0]23-[*H0]-[*H0]1(-[*H0]2)-[*H0]3;" +
            "[*H0]1-[*H0]-[*H0]23-[*H0]4-[*H0]2-[*H0]134;" +
            "[*H0]1-[*H0]2-[*H0]3-[*H0]-[*H0]123;" +
            "[*H0]12-[*H0]3-[*H0]4-[*H0]1-[*H0]3-[*H0]24";
    private List<String> smarts = null;
    private AllRingsFinder allRingsFinder = null;

    public RingStrainDetector() {
        smarts = new ArrayList<String>();
        String[] smarts_string = QUERY_SMARTS.split(";");
        for (String s : smarts_string) {
            smarts.add(s);
        }
        allRingsFinder = new AllRingsFinder(false);
    }

    public boolean isStrained(IAtomContainer mol) {
        IRingSet ringset = obtainRings(mol);
        return ringset.getAtomContainerCount() == 0 ? false : anaylse(ringset) ?
                true : checkForSomeMorePatterns(mol);
    }

    private boolean checkForSomeMorePatterns(IAtomContainer mol) {
        for (String smartString : smarts) {
            SMARTSQueryTool querytool = new SMARTSQueryTool(smartString, SilentChemObjectBuilder.getInstance());
            try {
                if (querytool.matches(mol)) {
                    return true;
                }
            } catch (CDKException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private IRingSet obtainRings(IAtomContainer mol) {
        IRingSet ringset = null;
        try {
            ringset = allRingsFinder.findAllRings(mol);
        } catch (CDKException e) {
            e.printStackTrace();
        }
        RingSetManipulator.markAromaticRings(ringset);
        return ringset;
    }

    private boolean anaylse(IRingSet ringset) {
        for (IAtomContainer ring : ringset.atomContainers()) {
            if (!ring.getFlag(CDKConstants.ISAROMATIC)) {
                if (nonAromaticAnalysis(ring, ringset)) {
                    return true;
                }

            } else {
                if (aromaticAnalysis(ring, ringset)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean nonAromaticAnalysis(IAtomContainer ring, IRingSet ringSet) {
        if (ring.getAtomCount() < 9) {
            int totalBondOrderSum = AtomContainerManipulator.getSingleBondEquivalentSum(ring);
            if (hasOnlyOneTripleBondIn(ring, totalBondOrderSum)) {
                return true;
            } else {
                int doubleBonds = getDoubleBondCount(ring);
                switch (doubleBonds) {
                    case 1:
                        if (ring.getAtomCount() <= 6 && totalBondOrderSum == ring.getAtomCount() + 1) {
                            if (fusionExists(ring, ringSet)) {
                                return true;
                            }
                        }

                    case 2:
                        if (totalBondOrderSum == ring.getAtomCount() + 2) {
                            return hasAdjacentDoubleBonds(ring);
                        }

                }
            }
        }
        return false;
    }

    private boolean hasOnlyOneTripleBondIn(IAtomContainer ring, int bondSum) {
        if (ring.getAtomCount() <= 8) {
            if (AtomContainerManipulator.getMaximumBondOrder(ring).equals(IBond.Order.TRIPLE)) {
                if (bondSum == ring.getAtomCount() + 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getDoubleBondCount(IAtomContainer ring) {
        int doubleBondCount = 0;
        for (IBond bond : ring.bonds()) {
            if (bond.getOrder() != null) {
                if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
                    doubleBondCount++;
                }
            }

        }
        return doubleBondCount;
    }

    private boolean hasAdjacentDoubleBonds(IAtomContainer ring) {
        for (IAtom atom : ring.atoms()) {
            List<IBond> connectedBonds = ring.getConnectedBondsList(atom);
            int doubleBondsCount = 0;
            for (IBond b : connectedBonds) {
                if (b.getOrder().equals(IBond.Order.DOUBLE)) {
                    doubleBondsCount++;
                }
            }
            if (doubleBondsCount == 2) {
                return true;
            }

        }
        return false;
    }

    private boolean fusionExists(IAtomContainer ring, IRingSet ringSet) {
        IRingSet connectedRings = ringSet.getConnectedRings((IRing) ring);
        if (connectedRings.getAtomContainerCount() == 0) {
            return false;
        }

        for (IAtomContainer ringToCheck : connectedRings.atomContainers()) {
            // Both non-aromatic rings
            if (!ringToCheck.equals(ring) && !ringToCheck.getFlag(CDKConstants.ISAROMATIC)) {
                if (isWeirdlyFused(ring, ringToCheck)) {
                    return true;
                }
            } else if (!ringToCheck.equals(ring) && ringToCheck.getFlag(CDKConstants.ISAROMATIC)) {
                if (weirdAromaticFusionExists(ringToCheck, ring)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWeirdlyFused(IAtomContainer ring, IAtomContainer ringToCheck) {
        int ringSize = ring.getAtomCount();
        int ringtoCheckSize = ringToCheck.getAtomCount();

        switch (ringSize) {
            case 3:
                if (ringtoCheckSize == 6) {
                    return sharesBond(ring, ringToCheck, false);
                } else {
                    return sharesBond(ring, ringToCheck, true);
                }
            case 4:
                if (ringtoCheckSize <= 5) {
                    return sharesBond(ring, ringToCheck, false);
                }
            case 5:
                if (ringtoCheckSize <= 4) {
                    return sharesBond(ring, ringToCheck, false);
                }
            case 6:
                if (ringtoCheckSize == 3) {
                    return sharesBond(ring, ringToCheck, false);
                }
            default:
                return false;
        }
    }

    private boolean sharesBond(IAtomContainer ring, IAtomContainer ringToCheck, boolean canShareDoubleBond) {
        Set<IBond> candidates = findCandidateBondsOfAttachMent(ring);
        for (IBond bond : candidates) {
            if (ringToCheck.contains(bond)) {
                return bond.getOrder().equals(IBond.Order.SINGLE) ? onlySingleBondsIn(ringToCheck) :
                        canShareDoubleBond ? theSharedIsTheOnlyDoubleBondIn(ringToCheck) : false;
            }
        }
        return false;
    }

    private Set<IBond> findCandidateBondsOfAttachMent(IAtomContainer ring) {
               /*
               here we look for double bond in the current ring, and find its adjacent bonds that might be
               candidates of attachment with ringToCheck
                */
        Set<IBond> candidates = new HashSet<IBond>();
        for (IAtom atom : ring.atoms()) {
            List<IBond> connectedBonds = ring.getConnectedBondsList(atom);
            if (BondManipulator.getMaximumBondOrder(connectedBonds).equals(IBond.Order.DOUBLE)) {
                candidates.addAll(connectedBonds);
            }
        }
        return candidates;

    }

    private boolean onlySingleBondsIn(IAtomContainer ringToCheck) {
        return ringToCheck.getAtomCount() == AtomContainerManipulator.getSingleBondEquivalentSum(ringToCheck);
    }

    private boolean theSharedIsTheOnlyDoubleBondIn(IAtomContainer ringToCheck) {
        return AtomContainerManipulator.getSingleBondEquivalentSum(ringToCheck) == ringToCheck.getAtomCount() + 1;
    }

    private boolean weirdAromaticFusionExists(IAtomContainer aromaticRing, IAtomContainer ringToCheck) {

        int connectedRingSize = ringToCheck.getAtomCount();
        if (connectedRingSize > 9) {
            return false;
        }
        int sharedBondsSize = getSharedBonds(aromaticRing, ringToCheck).size();
        switch (sharedBondsSize) {
            case 1:  // ortho connections

                if (connectedRingSize <= 4) {
                    return true;
                }

            case 2:  // meta connections
                if (connectedRingSize <= 7) {
                    return true;
                }


            case 3: // para connections
                if (connectedRingSize <= 9) {
                    return true;
                }
            default:
                return false;
        }
    }

    private boolean aromaticAnalysis(IAtomContainer ring, IRingSet ringSet) {
        IRingSet connectedRings = ringSet.getConnectedRings((IRing) ring);
        if (connectedRings.getAtomContainerCount() == 0) {
            return false;
        }
        for (IAtomContainer ringToCheck : connectedRings.atomContainers()) {
            if (!ringToCheck.equals(ring) && !ringToCheck.getFlag(CDKConstants.ISAROMATIC)) {
                if (weirdAromaticFusionExists(ring, ringToCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<IBond> getSharedBonds(IAtomContainer aromaticRing, IAtomContainer ringToCheck) {
        List<IBond> sharedBonds = new ArrayList<IBond>();
        for (IBond bond : aromaticRing.bonds()) {
            if (ringToCheck.contains(bond)) {
                sharedBonds.add(bond);
            }
        }
        return sharedBonds;
    }

    public static void main(String[] args) {
        RingStrainDetector detector = new RingStrainDetector();
        try {
            IteratingSDFReader reader = StructureIO.createSDFReader("/Volumes/nobackup2/research/steinbeck/kalai/nmr-np-results/RacemosalactoneC/onlyNMRshiftdb/structures/1366931492430-0.sdf");
            //   SDFWriter writer = StructureIO.createSDFWriter("");
            int count = 1;
            int strainedcount = 0;
            AntiBredtDetector anti = new AntiBredtDetector();
            while (reader.hasNext()) {
                IAtomContainer mol = reader.next();
                Utilities.calculateProperties(mol);
                boolean strained = detector.isStrained(mol);
                //        System.out.println("strained: " + strained);
                //        System.out.println(count++ + " - " + strained);
                if (strained) {
                    //    System.out.println("is anti: " + anti.isAntiBredt(mol));
                    //  writer.write(mol);
                    strainedcount++;
                }
                System.out.println(count++);

            }
            // writer.close();
            System.out.println("total strained = " + strainedcount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
