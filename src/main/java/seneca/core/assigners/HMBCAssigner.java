/*
 *  HMBCAssigner.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.core.assigners;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import casekit.model.NMRSignal;
import casekit.model.NMRSpectrum;

import seneca.core.SenecaDataset;
import seneca.judges.HMBCJudge;

import java.util.ArrayList;
import java.util.List;

/**
 * Reduces an HMBC spectrum to a list of heavy atom pairs.
 *
 * @author steinbeck
 * @created 27. Juni 2001
 */
public class HMBCAssigner extends SpectrumAssigner {

    private static final Logger logger = Logger.getLogger(HMBCAssigner.class);
    HMBCJudge hmbcJudge = null;
    private SenecaDataset sd;

    /**
     * Constructor for the HMBCAssigner object
     *
     * @param sd The seneca dataset for which the operation is to be performed
     */
    public HMBCAssigner(SenecaDataset sd) {
        this.sd = sd;
        hmbcJudge = (HMBCJudge) sd.getJudge("HMBCJudge");

    }

    /**
     * Reduces an HMBC spectrum to a list of heavy atom pairs
     *
     * @return true if the operation succeeded
     */
    
    public boolean assign() {
        NMRSignal hetcorlrSignal = null;
        NMRSignal hetcorSignal = null;
        float carbonshift;
        float protonshift;
        List fromCarbonAtoms = null;
        List toCarbonAtoms = null;
        int from = 0;
        int to = 0;
        int toAlt = 0;
        if (sd.ch_hetcorlr == null)
            return false;
        if (sd.ch_hetcorlr.size() == 0)
            return false;

        if (hmbcJudge == null) {
            hmbcJudge = new HMBCJudge();
        }
        IAtomContainer ac = sd.getAtomContainer();
        /*
           * Check, if there is a complete carbon shift assignment. If not, refuse
           * to do the job
           */
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtom(i).getSymbol().equals("C")
                    && ac.getAtom(i).getProperty(CDKConstants.NMRSHIFT_CARBON) == null) {
                return false;
            }
        }
        if (hmbcJudge.assignment == null
                || hmbcJudge.assignment.length == 0) {
            hmbcJudge.assignment = new boolean[ac.getAtomCount()][ac
                    .getAtomCount()][ac.getAtomCount()];
        }
        /*
           * Now we know that each node has a carbon chemical shift assignment. We
           * will now crawl the hetcor-lr and hetcor spectrum
           */
        for (int i = 0; i < sd.ch_hetcorlr.size(); i++) {
            // get signal i and corresponding shifts from x and y
            hetcorlrSignal = (NMRSignal) sd.ch_hetcorlr.getSignal(i);
            carbonshift = hetcorlrSignal.getShift(NMRSpectrum.NUC_CARBON);
            protonshift = hetcorlrSignal.getShift(NMRSpectrum.NUC_PROTON);
            System.out.println("c shift: " + carbonshift + " H shift: " + protonshift);
            logger.info(carbonshift + " - " + protonshift);
            // get a list of closest carbons to the carbon shift with deviation 0.05
            fromCarbonAtoms = pickClosestCarbons(carbonshift, (float) 0.05);
            // get a new hetcor signal that is close to the proton signal with deviation 0.01
            hetcorSignal = (NMRSignal) sd.hetcor.pickClosestSignal(protonshift,
                    NMRSpectrum.NUC_PROTON, (float) 0.03);
//					NMRSpectrum.NUC_PROTON, (float) 0.01);
            // get the carbon shift

                try {

                    if(hetcorSignal != null){
                    carbonshift = hetcorSignal.getShift(NMRSpectrum.NUC_CARBON);
                    // get the atomcontainer(s) assocaited with that carbon shift with a deviation of 0.05
                    toCarbonAtoms = pickClosestCarbons(carbonshift, (float) 0.05);
                    //iterate through all the origin carbons and assign the closest atomcontainers based on the proton shift
                    for (int j = 0; j < fromCarbonAtoms.size(); j++) {
                        from = ac.getAtomNumber(((IAtom) fromCarbonAtoms
                                .get(j)));
                        if (toCarbonAtoms.size() < 1)
                            throw new Exception("HMBCAssigner: no carbon atoms close by");
                        to = ac.getAtomNumber(((IAtom) toCarbonAtoms.get(0)));
//                        if (hmbcJudge.assignment == null
//                                || hmbcJudge.assignment.length == 0) {
//                            hmbcJudge.assignment = new boolean[ac.getAtomCount()][ac
//                                    .getAtomCount()][ac.getAtomCount()];
//                        }
                        hmbcJudge.assignment[from][to][to] = true;
                        logger.info("Adding assignment: " + (from + 1) + " - " + (toAlt + 1));
                        for (int k = 1; k < toCarbonAtoms.size(); k++) {
                            toAlt = ac.getAtomNumber(((IAtom) toCarbonAtoms
                                    .get(k)));

                            hmbcJudge.assignment[from][to][toAlt] = true;
                            logger.info("Adding alternative assignment: " + (from + 1) + " - " + (toAlt + 1));
                        }
                    }
                    }
                } catch (Exception exc) {
                    System.out.println("returning false for hmbc assigner");
                    return false;
                }

        }
        return true;
    }

    /**
     * This method returns a List with nodes of elementtype 'C' that have all
     * the same shift and whose shift value does not deviate more than
     * 'pickprecision' from the one given by 'shift'. The purpose of this method
     * is to find all ambiguous assignments of hetcorlr signals with the 1D
     * carbon shifts assigned to each node. In the case of symmetry, one may
     * have the same carbon shift for more than one carbon atom. Due to the
     * assignment method, the values with the 1D carbon assignment are
     * consistent. Only the carbon axis shift values in the hetcor and hetcorlr
     * might be different due to peak picking imperfections. This we search the
     * closest shift value between 1D carbon spectrum and hetcorlr and onces we
     * found it, we make an exact search for that value within the carbon atoms
     * in order to find all ambiguous assignments.
     *
     * @param shift         A carbon chemical shift value that is looked for in the carbon
     *                      atom array
     * @param pickprecision A maximum deviation that 'shift' may have from the carbon
     *                      shifts that are searched
     * @return A List with carbon atoms that fullfill the criteria defined
     *         above
     */
    public List pickClosestCarbons(float shift, float pickprecision) {
        int thisPosition = -1;
        IAtom atom = null;
        float diff = Float.MAX_VALUE;
        float compareShift = 0;
        float referenceShift = 0;
        List carbons = new ArrayList();
        IAtomContainer ac = sd.getAtomContainer();
        /*
           * Now we search dimension dim for the chemical shift.
           */
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtom(i).getSymbol().equals("C")) {
                compareShift = ((Float) ac.getAtom(i).getProperty(
                        CDKConstants.NMRSHIFT_CARBON)).floatValue();
                if (diff > Math.abs(compareShift - shift)) {
                    diff = Math.abs(compareShift - shift);
                    referenceShift = compareShift;
                    thisPosition = i;
                }
            }
        }
        if (diff < pickprecision) {
            carbons.add(ac.getAtom(thisPosition));
        }
        /*
           * Here we have found one carbon atom that has the closest shift (from
           * the 1D carbon spectrum assignment) compared to the given shift from
           * the hetcorlr. Since there might be more than one carbon (symmetry or
           * incidental agreement) with one
           */
        for (int i = 0; i < ac.getAtomCount(); i++) {
            atom = (IAtom) ac.getAtom(i);
            if (atom.getSymbol().equals("C")) {
                compareShift = ((Float) atom
                        .getProperty(CDKConstants.NMRSHIFT_CARBON))
                        .floatValue();
                if (compareShift == referenceShift && i != thisPosition) {
                    carbons.add(ac.getAtom(thisPosition));
                }
            }
        }
        return carbons;
    }

}
