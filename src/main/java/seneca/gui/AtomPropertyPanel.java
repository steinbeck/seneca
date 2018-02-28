/*
 *  $RCSfile: AtomPropertyPanel.java,v $
 *  $Author: steinbeck $
 *  $Date: 2004/02/16 09:50:53 $
 *  $Revision: 1.7 $
 *
 *  Copyright (C) 1997 - 2001  Dr. Christoph Steinbeck
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
package seneca.gui;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.gui.actions.AutoAssignCarbonShiftsAction;
import seneca.gui.tables.Carbon1DTableModel;
import seneca.gui.tables.SetOfNodesTableModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

/**
 * Allow the user to graphically assign carbon shifts to carbon atoms as well as inherent hydrogens
 *
 * @author steinbeck @created September 9, 2001
 */
public class AtomPropertyPanel extends JPanel implements ChangeListener, TableModelListener {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(AtomPropertyPanel.class);
    private Box centerBox;
    Box centerCenterBox;
    Box topCenterCenterBox;
    private Box southBox;
    Border trustSliderBorder;
    private JTable setOfNodesTable;
    private JTable shiftTable;
    private JScrollPane scrollpane;
    private JScrollPane lsp;
    private JButton autoButton;
    private JLabel messagelabel;
    private Border etchedBorder;
    private SenecaDataset sd;
    protected String[] DEPTPhaseDescriptors = {"none", "positive", "negative",};
    private boolean isAssignedWell = false;

    AtomPropertyPanel(SenecaDataset sd) {
        super();
        this.sd = sd;
        sd.addChangeListener(this);

        setLayout(new BorderLayout());
        messagelabel = new JLabel();
        messagelabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        autoButton = new JButton("Assign missing Hydrogens");
        autoButton.addActionListener(new AutoAssignCarbonShiftsAction(sd));
        etchedBorder = BorderFactory.createEtchedBorder();
        centerBox = new Box(BoxLayout.X_AXIS);
        centerCenterBox = new Box(BoxLayout.Y_AXIS);
        topCenterCenterBox = new Box(BoxLayout.X_AXIS);
        southBox = new Box(BoxLayout.X_AXIS);
        setOfNodesTable = new JTable(new SetOfNodesTableModel(sd));
        setOfNodesTable.getModel().addTableModelListener(this);
        shiftTable = new JTable(new Carbon1DTableModel(sd));
        JButton hiddenInternalButtonToAutoAssignCarbonShifts = new JButton("Auto-assign");
        hiddenInternalButtonToAutoAssignCarbonShifts.addActionListener(new AutoAssignCarbonShiftsAction(sd));

        if (!updateMessageLabel()) {
            if (sd.carbon1D.size() != 0) {
                hiddenInternalButtonToAutoAssignCarbonShifts.doClick();
                stateChanged(new ChangeEvent(sd));
            }
        }

        scrollpane = new JScrollPane(setOfNodesTable);
        scrollpane.setBorder(BorderFactory.createTitledBorder(etchedBorder,
                "Atom Properties"));
        scrollpane.setPreferredSize(new Dimension(200, 400));
        lsp = new JScrollPane(shiftTable);
        lsp.setBorder(BorderFactory.createTitledBorder(etchedBorder,
                "Carbon Shifts"));
        lsp.setPreferredSize(new Dimension(200, 400));


        centerBox.add(scrollpane);
        centerBox.add(lsp);

        if (isAssignedWell) {
            System.out.println("assigned well");
            autoButton.setEnabled(false);
        }
        southBox.add(autoButton);
        southBox.add(messagelabel);
        add("Center", centerBox);
        add("South", southBox);

    }

    
    public void stateChanged(ChangeEvent e) {
        if (setOfNodesTable != null) {
            setOfNodesTable.repaint();
            tableChanged(new TableModelEvent(setOfNodesTable.getModel()));
        }
        if (shiftTable != null) {
            shiftTable.repaint();
        }
    }

    
    public void tableChanged(TableModelEvent arg0) {
        //logger.info("TableChange");
        updateMessageLabel();
    }

    public boolean updateMessageLabel() {
        if (sd.getAtomContainer() == null | sd.getMolecularFormula() == null) {
            return false;
        }
        //logger.info("update");
        String msg = "";
        IAtomContainer ac = sd.getAtomContainer();
        boolean success = false;
        if (!sd.getIsAtomPropertiesAssigned()) {
            messagelabel.setForeground(Color.red);
            autoButton.setEnabled(true);
            msg += "HCount in molecular formula: " + sd.dataConsistency.shouldBe;
            msg += " - Sum of HCounts in this table: " + sd.dataConsistency.is;
            msg += "; Please manually assign the missing Hs";

        } else {
            //set implicit hydrogens to zero if negative??
            for (int i = 0; i < ac.getAtomCount(); i++) {
                if (ac.getAtom(i).getImplicitHydrogenCount() < 0) {
                    ac.getAtom(i).setImplicitHydrogenCount(0);
                }
            }
            messagelabel.setForeground(new Color((Color.green).getRGB()).darker());
            isAssignedWell = true;
            autoButton.setEnabled(false);
            msg += "Hurray! : HCount in molecular formula: " + sd.dataConsistency.shouldBe;
            msg += " - Sum of HCounts in this table: " + sd.dataConsistency.is + ". Here no need to self assign";
            success = true;
        }

        messagelabel.setText(msg);
        setOfNodesTable.repaint();
        shiftTable.repaint();
        return success;
    }
}
