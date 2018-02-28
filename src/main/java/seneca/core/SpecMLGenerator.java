/* SpecMLGenerator.java
 *
 * Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license.
 * The intent of this license is to state the conditions under which this Package
 * may be copied, such that the Copyright Holder maintains some semblance
 * of artistic control over the development of the package, while giving the
 * users of the package the right to use and distribute the Package in a
 * more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * The complete text of the license can be found in a file called LICENSE
 * accompanying this package.
 */

package seneca.core;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import org.openscience.spectra.model.NMRSpectrum;
import org.openscience.spectra.model.NMRSignal;

import seneca.core.exception.SenecaIOException;
import seneca.structgen.sa.regular.ConvergenceAnnealingEngine;

import java.util.ArrayList;
import java.util.List;

public class SpecMLGenerator {
    private static String indent = "  ";
    private static int indentLevel = 0;
    private static final Logger logger = Logger.getLogger(SpecMLGenerator.class);
    private SenecaDataset sd = null;

    public SpecMLGenerator() {
    }

    public void setIndentString(String indent) {
        SpecMLGenerator.indent = indent;
    }

    public static String getIndent() {
        return getIndent(indentLevel, indent);
    }

    public static String getIndent(int indentLevel) {
        return getIndent(indentLevel, indent);
    }

    public static String getIndent(int indentLevel, String indent) {
        StringBuffer result = new StringBuffer();
        for (int i = 1; i <= indentLevel; i++) {
            result.append(indent);
        }
        return result.toString();
    }

    public String convert(SenecaDataset senecaDataset) throws SenecaIOException {
        this.sd = senecaDataset;
        IAtom atom = null;
        StringBuffer cml = new StringBuffer();
        cml.append("<?xml version=\"1.0\" ?>\n");
        cml.append("<senecadataset title=\"" + sd.getName() + "\">\n");
        indentLevel++;

        cml.append(getFormulaDescription());

        // insert connectiontable

        if (sd.getAtomContainer() == null) {
            if (sd.getMolecularFormula() != null) {
                IAtomContainer ac = MolecularFormulaManipulator.getAtomContainer(sd.getMolecularFormula());
                for (int i = ac.getAtomCount() - 1; i >= 0; i--) {
                	atom = ac.getAtom(i);
                    if (!atom.getSymbol().equals("H")) {
                        atom.setImplicitHydrogenCount(0);
                    } else {
                        ac.removeAtom(atom);
                    }
                }
                cml.append(convert(ac));
            }
        } else {
            cml.append(convert(sd.getAtomContainer()));
        }

        // Start streaming the spectra

        cml.append(convert(sd.carbon1D));
        cml.append(convert(sd.dept90));
        cml.append(convert(sd.dept135));
        cml.append(convert(sd.hetcor));
        cml.append(convert(sd.ch_hetcorlr));
        cml.append(convert(sd.hhcosy));
        cml.append(convert(sd.noesy));

        //for (int f = 0; f < sd.judges.size(); f++) {
        // fullJudgeClassName = sd.judges.elementAt(f).getClass().getName();
        // judgeClassName =
        // fullJudgeClassName.substring(fullJudgeClassName.lastIndexOf('.')
        // + 1);
        // className = "seneca.client." + judgeClassName + "Configurator";
        // try
        // {
        // judgeConf = Class.forName(className);
        // parameterTypes[0] = Judge.class;
        // parameterTypes[1] = Integer.class;
        // parameterTypes[2] = String.class;
        // }
        // catch(Exception exc)
        // {
        // System.err.println("No Configurator " + className +
        // " found for judge " + judgeClassName + "\n");
        // exc.printStackTrace();
        // }
        // try
        // {
        // method = judgeConf.getMethod("getXML", parameterTypes);
        // parameters[0] = (Judge)sd.judges.elementAt(f);
        // parameters[1] = new Integer(indentLevel);
        // parameters[2] = indent;
        // cml.append((String)method.invoke(null, parameters));
        // }
        // catch(Exception exc)
        // {
        // System.err.println("No getXML method found for judgeConfigurator "
        // + className + "\n");
        // exc.printStackTrace();
        // }

        //}

//        for (int i = 0; i < sd.annealingSchedules.size(); i++) {
//            cml.append(convert
//                    ((AnnealingSchedule) sd.annealingSchedules.elementAt(i)));
//        }
        if (!sd.getAnnealingOptions().isEmpty()) {
            cml.append(convert(sd.getAnnealingOptions()));
        }


        indentLevel--;
        cml.append("</senecadataset>\n");
        return cml.toString();
    }

    public String escape(String s) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '&') {
                result.append("&amp;");
            } else if (c == '<') {
                result.append("&lt;");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String getFormulaDescription() {
        String formula = "";
        if (sd.getMolecularFormula() != null) {
            formula = getIndent() + "<formula>" + MolecularFormulaManipulator.getString(sd.getMolecularFormula())
                    + "</formula>\n";
        }
        return formula;
    }

    public String convert(IAtomContainer ac) throws SenecaIOException {
        if (ac == null) {
            return "";
        }
        StringBuffer cml = new StringBuffer();
        List printedAtoms = new ArrayList();
        IAtom atom = null, otherAtom = null;
        int atomCount = ac.getAtomCount();
        // Bond[] bonds = null;
        List<IBond> bonds = new ArrayList<IBond>();
        cml.append(getIndent()
                + "<molecule xmlns=\"http://www.xml-cml.org/cml.dtd\" title=\"ConnectionTable\">\n");
        indentLevel++;
        for (int i = 0; i < atomCount; i++) {
            atom = ac.getAtom(i);
            cml.append(getIndent() + "<atom id=\"a" + i + "\">\n");
            indentLevel++;
            cml.append(getIndent() + "<string builtin=\"elementType\">"
                    + atom.getSymbol() + "</string>\n");
            if (atom.getImplicitHydrogenCount() >= 0) {
                cml.append(getIndent() + "<integer builtin=\"hydrogenCount\">"
                        + atom.getImplicitHydrogenCount() + "</integer>\n");
            }
            if (atom.getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                cml.append(getIndent()
                        + "<float title=\"assignedCarbonShift\">"
                        + atom.getProperty(CDKConstants.NMRSHIFT_CARBON)
                        + "</float>\n");
            }
            indentLevel--;
            cml.append(getIndent() + "</atom>\n");
        }
        int bondId = 1;

        for (int i = 0; i < atomCount; i++) {
            atom = ac.getAtom(i);
            printedAtoms.add(atom);
            // bonds = ac.getConnectedBonds(atom);
            bonds = ac.getConnectedBondsList(atom);
            for (int j = 0; j < bonds.size(); j++) {
                // otherAtom = bonds[j].getConnectedAtom(atom);
                otherAtom = bonds.get(j).getConnectedAtom(atom);
                if (!printedAtoms.contains(otherAtom)) {
                    bondId++;
                    try {
                        cml.append(getIndent() + "<bond id=\"b" + bondId
                                + "\" atomRefs=\"a" + i + " a"
                                + ac.getAtomNumber(otherAtom) + "\">\n");
                    } catch (Exception exc) {
                        String s = "An Exception occurred while writing ";
                        s += "a structure to disk. This very exception ";
                        s += "should never happen so please contact ";
                        s += "the SENECA author.";
                        throw new SenecaIOException(s);
                    }
                    indentLevel++;
                    cml.append(getIndent() + "<string builtin=\"order\">"
                            + bonds.get(j).getOrder() + "</string>\n");

                    indentLevel--;
                    cml.append(getIndent() + "</bond>\n");
                }
            }
        }
        indentLevel--;
        cml.append(getIndent() + "</molecule>\n");
        return cml.toString();
    }

    public String convert(NMRSpectrum nmrspect) {

        if (nmrspect == null)
            return "";
        StringBuffer cml = new StringBuffer();
        new StringBuffer();
        new StringBuffer();
        String temp = null;
        cml.append(getIndent()
                + "<spectrum xmlns=\"http://www.nmrshiftdb.org/\" type=\"NMR Experiment\" "
                + "id=\"" + nmrspect.name + "\" convention=\"Seneca\">\n");

        logger.info("Converting " + nmrspect.specType);
        indentLevel++;
        cml.append(getIndent() + "<spectruminfo>\n"); // <spectruminfo>
        indentLevel++;
        cml.append(getIndent() + "<dimension>" + nmrspect.dim
                + "</dimension>\n");
        cml.append(getIndent() + "<type>" + nmrspect.specType + "</type>\n");
        cml.append(getIndent() + "<frequency unit=\"MHz\">"
                + nmrspect.getSpectrometerFrequency() + "</frequency>\n");
        cml.append(getIndent() + "<solvent>" + nmrspect.getSolvent()
                + "</solvent>\n");
        cml.append(getIndent() + "<standard role=\"calibration\">"
                + nmrspect.getStandard() + "</standard>\n");
        System.out.println(nmrspect.nucleus.length);
        for (int i = 0; i < nmrspect.nucleus.length; i++) {
            temp = "f" + (i + 1);
            cml.append(getIndent() + "<axisinfo role=\"" + temp + "\">\n"); // <axisinfo>
            indentLevel++;

            cml.append(getIndent() + "<nucleus>" + nmrspect.nucleus[i]
                    + "</nucleus>\n");
            cml.append(getIndent() + "<property>shift</property>\n");
            cml.append(getIndent() + "<unit>ppm</unit>\n");

            indentLevel--;
            cml.append(getIndent() + "</axisinfo>\n"); // </axisinfo>
        }

        if (nmrspect.size() == 0) {
            indentLevel--;
            cml.append(getIndent() + "</spectruminfo>\n"); // </spectruminfo>
            indentLevel--;
            cml.append(getIndent() + "</spectrum>\n"); // </spectrum>
            return cml.toString();
        }

        indentLevel--;
        cml.append(getIndent() + "</spectruminfo>\n"); // </spectruminfo>

        logger.info(".");

        if (nmrspect.size() > 0) {
            for (int i = 0; i < nmrspect.size(); i++) {
                cml.append(getIndent() + "<signal" + " id=\"" + nmrspect.name
                        + ".p" + i + "\">\n");
                indentLevel++;
                for (int j = 0; j < ((NMRSignal) nmrspect.getSignal(i)).shift.length; j++) {
                    temp = "f" + (j + 1);
                    cml.append(getIndent() + "<location role=\"" + temp + "\">"
                            + ((NMRSignal) nmrspect.getSignal(i)).shift[j]
                            + "</location>\n");
                }
                cml.append(getIndent() + "<intensity type=\"relative\">"
                        + ((NMRSignal) nmrspect.getSignal(i)).intensity
                        + "</intensity>\n");
                cml.append(getIndent() + "<phase>"
                        + ((NMRSignal) nmrspect.getSignal(i)).phase
                        + "</phase>\n");
                indentLevel--;
                cml.append(getIndent() + "</signal>\n");
            }
        }
        indentLevel--;
        cml.append(getIndent() + "</spectrum>\n");
        return cml.toString();

    }

    public String convert(List annealingOptions) {
        if (annealingOptions == null) {
            return "";
        }
        logger.info("Converting annealing options..");
        ConvergenceAnnealingEngine annealingEngine = (ConvergenceAnnealingEngine) annealingOptions.get(0);
        Integer adaptiveSASteps = (Integer) annealingOptions.get(1);
        StringBuffer cml = new StringBuffer();
        cml.append(getIndent() + "<annealingschedules>\n");
        indentLevel++;
        cml.append(getIndent() + "<annealingschedule title=\"SimulatedAnnealing\">\n");

        indentLevel++;
        cml.append(getIndent() + "<initialAcceptanceProbability>"
                + annealingEngine.getInitialAcceptanceProbability()
                + "</initialAcceptanceProbability>\n");
        cml.append(getIndent() + "<noOfSteps>" + annealingEngine.getMaxUphillSteps()
                + "</noOfSteps>\n");
        cml.append(getIndent() + "<plateauSteps>" + annealingEngine.getMaxPlateauSteps()
                + "</plateauSteps>\n");
        cml.append(getIndent() + "<asymptoticCoolingFactor>"
                + annealingEngine.getCoolingRate()
                + "</asymptoticCoolingFactor>\n");
        indentLevel--;
        cml.append(getIndent() + "</annealingschedule>\n");
        cml.append(getIndent() + "<annealingschedule title=\"AdaptiveSimulatedAnnealing\">\n");
        indentLevel++;
        cml.append(getIndent() + "<noOfSteps>" + adaptiveSASteps.intValue()
                + "</noOfSteps>\n");
        indentLevel--;
        cml.append(getIndent() + "</annealingschedule>\n");
        indentLevel--;
        cml.append(getIndent() + "</annealingschedules>\n");
        return cml.toString();
    }

}
