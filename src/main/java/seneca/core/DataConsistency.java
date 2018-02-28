/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.core;

import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import seneca.gui.configurators.JudgeConfiguratorListener;
import seneca.judges.Judge;

/**
 * Class to check seneca dataset satisfies minimum requirements by default to perform structure
 * elucidation
 *
 * @author kalai
 */
public class DataConsistency {

    private SenecaDataset sd = null;
    private boolean propertiesOk = false;
    private boolean isAnnealingParametersSet = false;
    public int is, shouldBe = 0;
    private JudgeConfiguratorListener judgeConfiguratorListener = null;

    public DataConsistency(SenecaDataset sd) {
        this.sd = sd;
    }

    public void setSD(SenecaDataset sd) {
        this.sd = sd;
    }

    public void setJudgeConfiguratorListener(JudgeConfiguratorListener jl) {
        this.judgeConfiguratorListener = jl;
    }

    public boolean isDatasetCompleteForStructureElucidation() {
        if (isPropertiesAssignedWell()
                && isAtleastOneJudgeEnabled()
                && isAnnealingParametersSet()) {
            return true;
        }
        return false;
    }

    public boolean isPropertiesAssignedWell() {
        propertiesOk = isHCountSatisfied(this.sd.getAtomContainer());
        return propertiesOk;
    }

    public boolean isAtleastOneJudgeEnabled() {
        for (int i = 0; i < sd.judges.size(); i++) {
            Judge judge = (Judge) sd.judges.get(i);
            if (judge.getEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnealingParametersSet() {
        /**
         * InitObjects only has 2 parameters in it, namely, convergenceAnnealing and
         * numberOfSteps. So checking for minimum size of 2.
         */
        if (sd.annealingOptions.size() >= 2) {
            isAnnealingParametersSet = true;
        }
        return isAnnealingParametersSet;
    }

    public boolean isHCountSatisfied(IAtomContainer ac) {
        if (ac.getAtomCount() == 0) {
            return false;
        }
        is = getAnyAtomCount(ac, "H");
        IMolecularFormula molecularFormula = sd.getMolecularFormula();
        shouldBe = MolecularFormulaManipulator.getElementCount(
                molecularFormula, new Element("H"));
        if (is == shouldBe) {
            return true;
        }
        return false;
    }

    public int getAnyAtomCount(IAtomContainer mol, String element) {
        if (mol == null) {
            return 0;
        }
        int atomCount = 0;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            if (mol.getAtom(f).getSymbol().equals(element)) {
                atomCount++;
            }
            // add the implicit hydrogens as well
            // Can the number of implicit hydrogens be lower than 0?
            atomCount += mol.getAtom(f).getImplicitHydrogenCount();
        }
        return atomCount;

    }

    public void fireChange() {
        if (judgeConfiguratorListener != null) {
            judgeConfiguratorListener.judgeConfigurationChanged();
        }
    }
}
