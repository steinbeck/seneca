/*
 *  HHCOSYAssigner.java
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
import seneca.judges.HHCOSYJudge;

import java.util.ArrayList;
import java.util.List;

/**
 * Description of the Class
 *
 * @author steinbeck
 * @created September 9, 2001
 */
public class HHCOSYAssigner extends SpectrumAssigner {
    private static final Logger logger = Logger.getLogger(HHCOSYAssigner.class);
    HHCOSYJudge hhcosyJudge = null;
    private SenecaDataset sd;

    /**
     * Constructor for the HHCOSYAssigner object
     *
     * @param sd Description of Parameter
     */
    public HHCOSYAssigner(SenecaDataset sd) {
        this.sd = sd;
        hhcosyJudge = (HHCOSYJudge) sd.getJudge("HHCOSYJudge");
    }

    /**
     * Description of the Method
     *
     * @return Description of the Returned Value
     */
    
    public boolean assign() {
        try {
            NMRSignal hhcosySignal = null;
            NMRSignal hetcorSignal1 = null;
            NMRSignal hetcorSignal2 = null;
            float protonshift1;
            float protonshift2;
            float carbonshift1;
            float carbonshift2;
            List fromCarbonAtoms = null;
            List toCarbonAtoms = null;
            int from;
            int to;
            int toAlt;
            if (sd.hhcosy == null)
                return false;
            if (sd.hhcosy.size() == 0)
                return false;
            if (hhcosyJudge == null) {
                hhcosyJudge = new HHCOSYJudge();
            }
            IAtomContainer ac = sd.getAtomContainer();
            /*
			 * Check, if there is a complete carbon shift assignment. If not,
			 * refuse to do the job
			 */
            for (int i = 0; i < ac.getAtomCount(); i++) {
                if (ac.getAtom(i).getSymbol().equals("C")
                        && ac.getAtom(i).getProperty(
                        CDKConstants.NMRSHIFT_CARBON) == null) {
                    return false;
                }
            }
			/*
			 * Now we know that each node has a carbon chemical shift
			 * assignment. We will now crawl the hetcor-lr and hetcor spectrum
			 */
            for (int i = 0; i < sd.hhcosy.size(); i++) {
                hhcosySignal = (NMRSignal) sd.hhcosy.getSignal(i);
                protonshift1 = hhcosySignal.getShift(0);
                protonshift2 = hhcosySignal.getShift(1);
                logger.info(protonshift1 + " - " + protonshift2);
                hetcorSignal1 = (NMRSignal) sd.hetcor.pickClosestSignal(
                        protonshift1, NMRSpectrum.NUC_PROTON, (float) 0.01);
                carbonshift1 = hetcorSignal1.getShift(NMRSpectrum.NUC_CARBON);
                toCarbonAtoms = pickClosestCarbons(carbonshift1, (float) 0.05);
                hetcorSignal2 = (NMRSignal) sd.hetcor.pickClosestSignal(
                        protonshift2, NMRSpectrum.NUC_PROTON, (float) 0.01);
                carbonshift2 = hetcorSignal2.getShift(NMRSpectrum.NUC_CARBON);
                fromCarbonAtoms = pickClosestCarbons(carbonshift2, (float) 0.05);
                logger.info("List size: " + fromCarbonAtoms.size() + " - " + toCarbonAtoms.size());

                for (int j = 0; j < fromCarbonAtoms.size(); j++) {
                    from = ac.getAtomNumber(((IAtom) fromCarbonAtoms
                            .get(j)));
                    to = ac.getAtomNumber(((IAtom) toCarbonAtoms.get(0)));
                    if (hhcosyJudge.assignment == null
                            || hhcosyJudge.assignment.length == 0) {
                        int atomcount = ac.getAtomCount();
                        hhcosyJudge.assignment = new boolean[atomcount][atomcount][atomcount];
                    }
                    hhcosyJudge.assignment[from][to][to] = true;
                    logger.info("Assigning a 3JHH correlation between Hydrogens at Heavyatom "
                            + from + " and " + to);

                    for (int k = 1; k < toCarbonAtoms.size(); k++) {
                        toAlt = ac.getAtomNumber(((IAtom) toCarbonAtoms
                                .get(j)));

                        hhcosyJudge.assignment[from][to][toAlt] = true;
                    }
                }

            }
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    /**
     * This method returns a List with nodes of elementtype 'C' that have all
     * the same shift and whose shift value does not deviate more than
     * 'pickprecision' from the one given by 'shift'. The purpose of this method
     * is to find all ambiguous assignments of hhcosy signals with the 1D carbon
     * shifts assigned to each node. In the case of symmetry, one may have the
     * same carbon shift for more than one carbon atom. Due to the assignment
     * method, the values with the 1D carbon assignment are consistent. Only the
     * carbon axis shift values in the hetcor and hhcosy might be different due
     * to peak picking imperfections. This we search the closest shift value
     * between 1D carbon spectrum and hhcosy and onces we found it, we make an
     * exact search for that value within the carbon atoms in order to find all
     * ambiguous assignments.
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
		 * the hhcosy. Since there might be more than one carbon (symmetry or
		 * incidental agreement) with one
		 */
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtom(i).getSymbol().equals("C")) {
                compareShift = ((Float) ac.getAtom(i).getProperty(
                        CDKConstants.NMRSHIFT_CARBON)).floatValue();
                if (compareShift == referenceShift && i != thisPosition) {
                    carbons.add(ac.getAtom(thisPosition));
                }
            }
        }
        return carbons;
    }

}
