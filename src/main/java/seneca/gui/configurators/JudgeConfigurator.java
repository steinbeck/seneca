/*
 *  JudgeConfigurator.java
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
package seneca.gui.configurators;

import org.apache.log4j.Logger;
import seneca.core.SenecaDataset;
import seneca.core.SpecMLGenerator;
import seneca.judges.Judge;
import seneca.judges.JudgeListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class provides a gui for configuring a Judges
 *
 * @author steinbeck @created September 9, 2001
 */
public abstract class JudgeConfigurator extends JPanel implements JudgeListener, ChangeListener {

    private static final long serialVersionUID = 1L;
    protected SenecaDataset sd;
    protected Judge judge;
    protected JCheckBox checkBoxToEnable;
    protected JTable table;
    protected JScrollPane scrollpane;
    protected JButton generateButton;
    protected boolean propertiesOkForAssigningJudges = false;
    protected boolean clickedAlready = false;
    protected boolean firstAssignmentMade = false;
    private static final Logger logger = Logger.getLogger(JudgeConfigurator.class);

    JudgeConfigurator() {
    }

    JudgeConfigurator(SenecaDataset sd, Judge judge) {
        super();
        this.sd = sd;
        this.judge = judge;

        generateButton = new JButton("Autoconfigure Judge");
        generateButton.setToolTipText("Autoconfigures " + judge.getName());
        generateButton.addActionListener(new AutoAssignAction());
        sd.addChangeListener(this);
        if (this.sd.getIsAtomPropertiesAssigned()) {
            propertiesOkForAssigningJudges = true;
            logger.info("properties are okay to configure " + judge.getName());
            System.out.println("properties are okay to configure " + judge.getName());

        }
        judge.setJudgeListener(this);
        setLayout(new BorderLayout());
        add("Center", constructCenterBox());
        add("South", constructSouthBox());
    }

    
    public void judgeDataChanged() {
        checkBoxToEnable.setSelected(judge.getEnabled());
        reactOnJudgeDataChange();
        System.out.println("changing in jug cong");
    }

    protected void reactOnJudgeDataChange() {
        sd.dataConsistency.fireChange();
    }

    protected Border getTitledBorder() {
        Border etchedBorder = BorderFactory.createEtchedBorder();
        return BorderFactory.createTitledBorder(etchedBorder, judge.name);
    }

    protected abstract Box constructCenterBox();

    protected Box constructSouthBox() {

        Box southBox = new Box(BoxLayout.X_AXIS);
        JButton reportButton = new JButton("Report Judgeconfig");
        reportButton.setToolTipText("Opens a window with judge configuration in ASCII output");
        reportButton.addActionListener(new ReportAction());
        southBox.add(generateButton);
        southBox.add(reportButton);
        southBox.add(Box.createHorizontalGlue());
        checkBoxToEnable = new JCheckBox("Activate this Judge?", judge.getEnabled());
        checkBoxToEnable.addActionListener(new EnableAction());
        southBox.add(checkBoxToEnable);
        return southBox;
    }

    class AutoAssignAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        AutoAssignAction() {
            super("Autoconfigure");
        }

        
        public void actionPerformed(ActionEvent e) {
            judge.setEnabled(true);
            autoconfigure();
        }
    }

    public void autoconfigure() {
    }

    class ReportAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        ReportAction() {
            super("report");
        }

        
        public void actionPerformed(ActionEvent e) {
            report();
        }
    }

    protected void report() {
    }

    public static String getXML(Judge judge, Integer indentLevel,
                                String indentString) {
        int iL = indentLevel.intValue();
        String iS = indentString;
        StringBuffer xml = new StringBuffer();
        xml.append(SpecMLGenerator.getIndent(iL, iS) + "<judge title=\""
                + judge.name + "\">\n");
        xml.append(SpecMLGenerator.getIndent(iL, iS) + "</judge>\n");
        return xml.toString();
    }

    public static Judge getJudgeFromXML() {
        return null;
    }

    class EnableAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        EnableAction() {
            super("Enable");
        }

        
        public void actionPerformed(ActionEvent e) {
            JCheckBox jcb = (JCheckBox) e.getSource();
            judge.setEnabled(jcb.isSelected());
            autoConfigureJudge();
        }
    }

    public JButton getAutoConfigureButton() {
        return this.generateButton;
    }

    protected void autoConfigureJudge() {
        if (!clickedAlready) {
            getAutoConfigureButton().doClick();
            clickedAlready = true;
            this.sd.fireChange();
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        if (judge != null && firstAssignmentMade) {
            SenecaDataset senecaDataset = (SenecaDataset) e.getSource();
            if (senecaDataset.getIsAtomPropertiesAssigned()) {
                autoConfigureJudge();
            } else {
                judge.setEnabled(false);
                clickedAlready = false;
            }
        }

    }
}
