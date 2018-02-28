/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui.actions;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.engine.StructureGeneratorServer;
import seneca.gui.AbstractPanel;
import seneca.judges.ChiefJustice;
import seneca.judges.Judge;
import seneca.structgen.StructureGenerator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author kalai
 */
public class StartStructureGenerationAction extends AbstractAction {

    public static final Logger logger = Logger.getLogger(StartStructureGenerationAction.class);
    Integer numberOfSteps = 0;
    ChiefJustice boss = null;
    SenecaDataset senecaDataset = null;
    List sgServerProcesses = null;
    AbstractPanel panel = null;

    public StartStructureGenerationAction(AbstractPanel panel) {
        this.panel = panel;
        this.senecaDataset = panel.sd;
        this.sgServerProcesses = panel.sgServerProcesses;
    }

    
    public void actionPerformed(ActionEvent e) {
        disableOtherAlgorithms();
        getNumberOfStepsFromUser();
        initiateJudgesAndAssignBoss();
        getServerAndSetStructureGenerator();
        disableStartButton();
    }

    private void disableOtherAlgorithms() {
        ButtonGroup buttonGroup = this.panel.buttonGroup;
        for (Enumeration radioButtons = buttonGroup.getElements(); radioButtons.hasMoreElements(); ) {
            JRadioButton radioButton = (JRadioButton) radioButtons.nextElement();
            if (radioButton.getModel() != buttonGroup.getSelection()) {
                radioButton.setEnabled(false);
            }
        }
    }

    private void getNumberOfStepsFromUser() {
        for (int f = 0; f < senecaDataset.annealingOptions.size(); f++) {
            Object obj = senecaDataset.annealingOptions.get(f);
            if (obj instanceof Integer) {
                numberOfSteps = (Integer) obj;
                break;
            }
        }
    }

    private void initiateJudgesAndAssignBoss() {
        List judges = new ArrayList();
        for (int g = 0; g < senecaDataset.judges.size(); g++) {
            Judge judge = ((Judge) senecaDataset.judges.get(g));
            if (judge.getEnabled()) {
                judge.calcMaxScore();
                judges.add(judge.clone());
                System.out.println("Judge added = " + judge.getName());
                logger.info("Judge added = " + judge.getName());
            }
        }
        boss = new ChiefJustice(judges);
    }

    private void getServerAndSetStructureGenerator() {
        StructureGeneratorServer structureGeneratorServer;
        int rows[] = panel.table.getSelectedRows();
        int[] selectedRows = new int[sgServerProcesses.size()];
        for (int f = 0; f < rows.length; f++) {
            selectedRows[rows[f]] = 1;
        }
        for (int f = 0; f < sgServerProcesses.size(); f++) {
            if (selectedRows[f] == 1) {
                structureGeneratorServer = (StructureGeneratorServer) sgServerProcesses.get(f);
                StructureGenerator generator;
                try {
                    generator = getStochasticGenWithParametersAssigned(f);
                    structureGeneratorServer.setStructureGenerator(generator);
                    structureGeneratorServer.executeTask();
                } catch (CloneNotSupportedException ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
        panel.tableUpdateTimer.start();
        panel.tableUpdateTimer.setDelay(1000 * sgServerProcesses.size());
    }

    private StructureGenerator getStochasticGenWithParametersAssigned(int serverID) throws CloneNotSupportedException {
        StructureGenerator generator = setWhichStructureGenerator();
        generator.setUpLogger(serverID);
        ChiefJustice justice = (ChiefJustice) clone(boss);
        justice.initJudges();
        generator.setChiefJustice(justice);
        generator.setAtomContainer((IAtomContainer) senecaDataset.getAtomContainer().clone());
        generator.setDatasetName(senecaDataset.getName());
        generator.setAnnealingOptions((List) clone(senecaDataset.annealingOptions));
        generator.setNumberOfSteps((Integer) clone(senecaDataset.annealingOptions.get(senecaDataset.NUMBER_OF_STEPS)));
        generator.setMolecularFormula(senecaDataset.molecularFormula);
        return generator;
    }

    private Object clone(Object obj) {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(buf);
            o.writeObject(obj);
            // Now get copies:
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
            return in.readObject();
        } catch (Exception exc) {
            exc.getMessage();
            return null;
        }
    }

    private StructureGenerator setWhichStructureGenerator() {
        StructureGenerator generator = panel.structureGenerator;
        logger.info("Structure generator used: " + panel.structureGenerator.getName());
        return generator.newInstance();
    }

    private void disableStartButton() {
        this.panel.startLocalServerButton.setEnabled(false);
        this.panel.startStructureGeneratorButton.setEnabled(false);
        this.panel.stopSelectedButton.setEnabled(true);
    }
}
