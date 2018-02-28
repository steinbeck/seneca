/*
 *  $RCSfile: ConvergenceAnnealingEngineConfigurator.java,v $
 *  $Author: steinbeck $
 *  $Date: 2001/09/14 14:43:56 $
 *  $Revision: 1.2 $
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
package seneca.gui.configurators;

import seneca.core.SenecaDataset;
import seneca.structgen.sa.regular.ConvergenceAnnealingEngine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

/**
 * Description of the Class
 *
 * @author steinbeck @created September 10, 2001
 */
public class ConvergenceAnnealingEngineConfigurator extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ConvergenceAnnealingEngine annealingEngine;
    JTable table;
    SenecaDataset sd = null;
    List annealingOptions = null;
    int numberOfSteps = 4000;

    /**
     * Constructor for the AnnealingScheduleConfigurator object
     *
     * @param sd Description of Parameter
     */
    public ConvergenceAnnealingEngineConfigurator(SenecaDataset sd) {
        super();
        this.sd = sd;
        annealingOptions = sd.getAnnealingOptions();
        Object obj = null;
        for (int f = 0; f < annealingOptions.size(); f++) {
            obj = annealingOptions.get(f);
            if (obj instanceof ConvergenceAnnealingEngine) {
                annealingEngine = (ConvergenceAnnealingEngine) obj;
                break;
            }
        }
        if (annealingEngine == null) {
            annealingEngine = new ConvergenceAnnealingEngine();
            annealingOptions.add(sd.ANNEALING_ENGINE, annealingEngine);
            // annealingOptions.addElement(annealingEngine);
        }
        annealingOptions.add(sd.NUMBER_OF_STEPS, numberOfSteps);
        setLayout(new BorderLayout());
        table = new JTable(new CAECTableModel());
        add("Center", table);
    }

    class CAECTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        final String[] columnNames = {"Parameter", "Value"};
        final String[] rowNames = {"Initial Acceptance Probablitiy",
                "Plateau Steps", "Cooling Factor", "max. Uphill Steps", "Number of steps (only for adaptive SA)"};

        
        public int getColumnCount() {
            return columnNames.length;
        }

        
        public int getRowCount() {
            return rowNames.length;
        }

        
        public String getColumnName(int col) {
            return columnNames[col];
        }

        
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return rowNames[row];
            }
            switch (row) {
                case 0:
                    return new Double(
                            annealingEngine.getInitialAcceptanceProbability());
                case 1:
                    return new Long(annealingEngine.getMaxPlateauSteps());
                case 2:
                    return new Double(annealingEngine.getCoolingRate());
                case 3:
                    return new Long(annealingEngine.getMaxUphillSteps());
                case 4:
                    return numberOfSteps;
            }
            return null;
        }

        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's editable.
         */
        
        public boolean isCellEditable(int row, int col) {
            // Note that the data/cell address is constant,
            // no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's data can change.
         */
        
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) {
                return;
            }
            switch (row) {
                case 0:
                    annealingEngine.setInitialAcceptanceProbability(((Double) value).doubleValue());
                    break;
                case 1:
                    annealingEngine.setMaxPlateauSteps(((Double) value).longValue());
                    break;
                case 2:
                    annealingEngine.setCoolingRate(((Double) value).doubleValue());
                    break;
                case 3:
                    annealingEngine.setMaxUphillSteps(((Double) value).longValue());
                    break;
                case 4:
                    numberOfSteps = ((Double) value).intValue();
                    updateSD();
                    break;
            }
            fireTableCellUpdated(row, col);
        }

        private void updateSD() {

            Integer exisitingValue = (Integer) annealingOptions.get(sd.NUMBER_OF_STEPS);
            if (exisitingValue != numberOfSteps) {
                annealingOptions.remove(sd.NUMBER_OF_STEPS);
                annealingOptions.add(sd.NUMBER_OF_STEPS, numberOfSteps);
            }
        }
    }
}
