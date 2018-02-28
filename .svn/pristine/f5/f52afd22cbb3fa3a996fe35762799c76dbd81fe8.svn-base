/*
 *  SymmetryJudgeConfigurator.java
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.judges.SymmetryJudge;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * This class provides a gui for configuring a SymmetryJudgeConfigurator
 *
 * @author steinbeck
 * @created September 9, 2001
 */
public class SymmetryJudgeConfigurator extends JudgeConfigurator {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    SymmetryJudge symJudge = null;

    /**
     * Constructor for the SymmetryJudgeConfigurator object
     *
     * @param sd Description of Parameter
     */
    public SymmetryJudgeConfigurator(SenecaDataset sd) {
        super(sd, (SymmetryJudge) sd.getJudge("SymmetryJudge"));
        this.symJudge = (SymmetryJudge) sd.getJudge("SymmetryJudge");
    }

    /**
     * Description of the Method
     */
    
    public void autoconfigure() {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return;
        }
        int atomCount = ac.getAtomCount();
        symJudge.clearSymmetryClasses();
        float shift1 = 0;
        float shift2 = 0;
        boolean symClassExists = false;
        boolean[] symClass = new boolean[atomCount];
        for (int f = 0; f < atomCount; f++) {
            if (ac.getAtom(f).getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                shift1 = ((Float) ac.getAtom(f).getProperty(
                        CDKConstants.NMRSHIFT_CARBON)).floatValue();
            }
            for (int g = f + 1; g < atomCount; g++) {
                if (ac.getAtom(g).getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                    shift2 = ((Float) ac.getAtom(g).getProperty(
                            CDKConstants.NMRSHIFT_CARBON)).floatValue();

                    if (shift1 == shift2) {
                        if (!symClassExists) {
                            symClassExists = true;
                            symClass = new boolean[atomCount];
                            for (int i = 0; i < symClass.length; i++) {
                                symClass[i] = false;
                            }
                            symClass[f] = true;
                        }
                        symClass[g] = true;
                    }
                }
            }
            if (symClassExists) {
                symJudge.addSymmetryClass(symClass);
            }
            symClassExists = false;
        }
        table.setModel(new SymmetryTableModel());
        scrollpane.revalidate();
    }

    /**
     * Description of the Method
     *
     * @return Description of the Returned Value
     */
    
    protected Box constructCenterBox() {
        Box centerBox = new Box(BoxLayout.X_AXIS);
        table = new JTable(new SymmetryTableModel());
        scrollpane = new JScrollPane(table);
        scrollpane.setBorder(getTitledBorder());
        scrollpane.setPreferredSize(new Dimension(200, 400));
        centerBox.add(scrollpane);
        return centerBox;
    }

    /**
     * Description of the Class
     *
     * @author steinbeck
     * @created September 9, 2001
     */
    class SymmetryTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor for the SymmetryTableModel object
         */
        SymmetryTableModel() {
        }

        /**
         * Sets the ValueAt attribute of the SymmetryTableModel object
         *
         * @param aValue The new ValueAt value
         * @param row    The new ValueAt value
         * @param column The new ValueAt value
         */
        
        public void setValueAt(Object aValue, int row, int column) {
            switch (column) {
                case 0:
                    // You can't change the numbering
                    break;
                case 1:
                    // You can't change the symbols.
                    break;
                case 2:
                    // You can't change the shift values
                    break;
                default:
                    break;
            }
            fireTableDataChanged();
        }

        /**
         * Gets the ColumnCount attribute of the SymmetryTableModel object
         *
         * @return The ColumnCount value
         */
        
        public int getColumnCount() {
            // String[] names = {"No.", "Symb.", "Shift", "sp3", "sp2", "sp"};
            if (sd.getAtomContainer() == null || symJudge == null) {
                return 0;
            }
            return sd.getAtomContainer().getAtomCount() + 3;
        }

        /**
         * Gets the RowCount attribute of the SymmetryTableModel object
         *
         * @return The RowCount value
         */
        
        public int getRowCount() {
            if (sd.getAtomContainer() == null || symJudge == null) {
                return 0;
            }
            return symJudge.getSymmetryClassCount();
        }

        /**
         * Gets the ValueAt attribute of the SymmetryTableModel object
         *
         * @param row    Description of Parameter
         * @param column Description of Parameter
         * @return The ValueAt value
         */
        
        public Object getValueAt(int row, int column) {
            IAtomContainer ac = sd.getAtomContainer();
            switch (column) {
                case 0:
                    return String.valueOf(row + 1);
                case 1:
                    return ac.getAtom(row).getSymbol();
                case 2:
                    return String.valueOf(ac.getAtom(
                            symJudge.getClassMember(row, 1)).getProperty(
                            CDKConstants.NMRSHIFT_CARBON));
                default:
                    // System.out.println(row + " - " + column);
                    boolean[] symmetryClass = symJudge.getSymmetryClass(row);
                    // System.out.println("Symmetryclass size: " +
                    // symmetryClass.length);
                    return new Boolean(symmetryClass[column - 3]);
            }
        }

        /**
         * Gets the ColumnName attribute of the SymmetryTableModel object
         *
         * @param column Description of Parameter
         * @return The ColumnName value
         */
        
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "No.";
                case 1:
                    return "Symb.";
                case 2:
                    return "Shift";
                default:
                    return new String(sd.getAtomContainer().getAtom(column - 3)
                            .getSymbol()
                            + (column - 2));
            }
        }

        /**
         * Gets the ColumnClass attribute of the SymmetryTableModel object
         *
         * @param c Description of Parameter
         * @return The ColumnClass value
         */
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /**
         * Gets the CellEditable attribute of the SymmetryTableModel object
         *
         * @param row Description of Parameter
         * @param col Description of Parameter
         * @return The CellEditable value
         */
        
        public boolean isCellEditable(int row, int col) {
            if (col < 4) {
                return false;
            } else {
                return true;
            }
        }

    }
}
