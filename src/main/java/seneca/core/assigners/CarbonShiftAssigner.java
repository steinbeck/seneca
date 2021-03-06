/*
 *  CarbonShiftAssigner.java
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
import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import casekit.model.NMRSignal;
import casekit.model.NMRSpectrum;
import seneca.core.SenecaDataset;

/**
 * Description of the Class
 *
 * @author steinbeck @created September 9, 2001
 */
public class CarbonShiftAssigner extends SpectrumAssigner {

    SenecaDataset sd;
    double pickPrecision = 0.1;
    private int requiredHeteroAtomAssignment = 0;
    private static Logger logger = Logger.getLogger(CarbonShiftAssigner.class);

    /**
     * Constructor for the CarbonShiftAssigner object
     *
     * @param sd Description of Parameter
     */
    public CarbonShiftAssigner(SenecaDataset sd) {
        this.sd = sd;
    }

    /**
     * Sets the PickPrecision attribute of the CarbonShiftAssigner object
     *
     * @param pickPrecision The new PickPrecision value
     */
    public void setPickPrecision(double pickPrecision) {
        this.pickPrecision = pickPrecision;
    }

    /**
     * Gets the PickPrecision attribute of the CarbonShiftAssigner object
     *
     * @return The PickPrecision value
     */
    public double getPickPrecision() {
        return pickPrecision;
    }

    /**
     * Description of the Method
     *
     * @return Description of the Returned Value
     */
    public int getAnyAtomCount(IAtomContainer ac, String element) {
        int atomCount = 0;

        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (ac.getAtom(f).getSymbol().equals(element)) {
                atomCount++;
            }
        }
        return atomCount;

    }

    
    public boolean assign() {
        StringBuffer warning = new StringBuffer("Warning");
        IAtomContainer ac = null;
        int tempCounter = 0;
        int HCounter = 0;
        float intensitySum = 0;
        NMRSignal cnmrsig = null;
        NMRSignal d90sig = null;
        NMRSignal d135sig = null;
        float cshift = 0;
        // MFAnalyser mfa = new MFAnalyser(sd.getAtomContainer());
        // MolecularFormulaManipulator.

        // normalize(sd.carbon1D);
        intensitySum = sumIntensity(sd.carbon1D);
        int carbonCount = 0;
        if (sd.getAtomContainer() != null && sd.carbon1D != null) {
            ac = sd.getAtomContainer();
            /*
            * This is a stupid system. We need to normalize the intensities such that they
            * simply fit the carbon count. Currently, we rely on the right scale of the
            * intensities. Further, the intensities of the carbon spectrum depend on the
            * H-counts of their respective nuclei. This is also not taken into account. Last
            * not least, we need a manual assignment possibility.
            */
            // if (mfa.getAtomCount("C") == Math.round(intensitySum))
            if (getAnyAtomCount(sd.getAtomContainer(), "C") == Math.round(intensitySum)) {
                /*
                * The arrays are of same length so that we can perform a one-to-one
                * assignment
                */
                tempCounter = 0;
                for (int f = 0; f < ac.getAtomCount(); f++) {
                    if (ac.getAtom(f).getSymbol().equals("C")) {
                        cnmrsig = (NMRSignal) sd.carbon1D.getSignal(tempCounter);
                        logger.debug("Signal with intensity "
                                + cnmrsig.intensity);
                        carbonCount = Math.round(cnmrsig.intensity);
                        logger.debug("Resulting carbon count for this signal: "
                                + carbonCount);
                        for (int g = 0; g < carbonCount; g++) {
                            f += g;
                            cshift = cnmrsig.getShift(NMRSpectrum.NUC_CARBON);
                            d90sig = (NMRSignal) sd.dept90.pickClosestSignal(
                                    cshift, NMRSpectrum.NUC_CARBON,
                                    (float) pickPrecision);
                            d135sig = (NMRSignal) sd.dept135.pickClosestSignal(
                                    cshift, NMRSpectrum.NUC_CARBON,
                                    (float) pickPrecision);
                            ac.getAtom(f).setProperty(
                                    CDKConstants.NMRSHIFT_CARBON,
                                    new Float(cnmrsig.getShift(NMRSpectrum.NUC_CARBON)));
                            if (d135sig != null
                                    && d135sig.phase == NMRSignal.PHASE_POSITIVE
                                    && d90sig == null) {
                                /*
                                * We have a methyl group here
                                */
                                ac.getAtom(f).setImplicitHydrogenCount(3);
                                HCounter += 3;
                            }
                            if (d135sig != null
                                    && d135sig.phase == NMRSignal.PHASE_NEGATIVE) {
                                if (d90sig != null) {
                                    warning.append("Warning: There is a DEPT90 signal within the picking range of Methylene group "
                                            + (f + 1) + "\n");
                                    warning.append("Assignment ambiguous. Please check.\n");
                                }
                                /*
                                * We have a methylene group here
                                */
                                ac.getAtom(f).setImplicitHydrogenCount(2);
                                HCounter += 2;

                            }
                            if (d90sig != null
                                    && d90sig.phase == NMRSignal.PHASE_POSITIVE) {
                                /*
                                * We have a methine group here
                                */
                                ac.getAtom(f).setImplicitHydrogenCount(1);
                                HCounter += 1;
                            }
                            if (d135sig == null && d90sig == null) {
                                /*
                                * We have a quarternary carbon here
                                */
                                ac.getAtom(f).setImplicitHydrogenCount(0);
                            }
                        }
                        tempCounter++;
                    }
                }
            }
            /*
            * Now let's check if all the hydrogens are assigned. If yes, we can set the
            * HCount of all other atoms to zero. Otherwise, the user has the problem :-)
            */
            IMolecularFormula formula = sd.getMolecularFormula();
            int hCountInFormula = MolecularFormulaManipulator.getElementCount(formula, new Element("H"));
            logger.debug("HCount in Formula: "
                    + hCountInFormula
                    + ", HCount in table: " + HCounter);

            if (hCountInFormula == HCounter) {
                for (int f = 0; f < ac.getAtomCount(); f++) {
                    if (!ac.getAtom(f).getSymbol().equals("C")) {
                        ac.getAtom(f).setImplicitHydrogenCount(0);
                    }
                }

            } else {
                assignHydrogenToHeteroAtoms(hCountInFormula - HCounter);
            }

        }
        logger.info("Carbon shifts assignment made");
        System.out.println("Carbon shifts assignment made");
        return true;
    }

    private void assignHydrogenToHeteroAtoms(int required) {
        requiredHeteroAtomAssignment = required;
        System.out.println("required.. " + required);
        int oxygenCount = MolecularFormulaManipulator.getElementCount(
                sd.getMolecularFormula(), new Element("O"));
        int nitrogenCount = MolecularFormulaManipulator.getElementCount(
                sd.getMolecularFormula(), new Element("N"));
        if (nitrogenCount > 0) {
            assignHtoN();
        }
        if (oxygenCount > 0) {
            assignHtoO();
        }
    }

    private void assignHtoN() {
        IAtomContainer ac = sd.getAtomContainer();
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (requiredHeteroAtomAssignment > 0) {
                if (ac.getAtom(f).getSymbol().equals("N")) {
                    ac.getAtom(f).setImplicitHydrogenCount(1);
                    logger.info("Assigned H to N");
                    requiredHeteroAtomAssignment--;
                }
            }
        }
    }

    private void assignHtoO() {
        IAtomContainer ac = sd.getAtomContainer();
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (requiredHeteroAtomAssignment > 0) {
                if (ac.getAtom(f).getSymbol().equals("O")) {
                    ac.getAtom(f).setImplicitHydrogenCount(1);
                    logger.info("Assigned H to O");
                    requiredHeteroAtomAssignment--;
                }
            }
        }
    }

    private NMRSignal getLargestSignal(NMRSpectrum nmr) {
        float maxInt = 0;
        NMRSignal nmrsig = null;
        NMRSignal returnsig = null;
        for (int f = 0; f < nmr.size(); f++) {
            nmrsig = (NMRSignal) nmr.getSignal(f);
            if (nmrsig.intensity > maxInt) {
                returnsig = nmrsig;
                maxInt = nmrsig.intensity;
            }
        }
        return returnsig;
    }

    private float sumIntensity(NMRSpectrum nmr) {
        float intensitySum = 0;
        NMRSignal nmrsig = null;
        for (int f = 0; f < nmr.size(); f++) {
            nmrsig = (NMRSignal) nmr.getSignal(f);
            intensitySum += nmrsig.intensity;
            System.out.println("NMR signal: " + nmrsig.intensity);
        }
        return intensitySum;
    }
}
