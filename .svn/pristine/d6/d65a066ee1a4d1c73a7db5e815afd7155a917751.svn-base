package seneca.gui.tables;

import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import seneca.core.SenecaDataset;

import javax.swing.table.AbstractTableModel;

public class GeneralTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    SenecaDataset sd;

    String[] names = {"Parameter", "Setting"};

    public GeneralTableModel(SenecaDataset sd) {
        this.sd = sd;
    }

    
    public void setValueAt(Object aValue, int row, int column) {
        switch (row) {
            case 0:
                if (column == 1) {
                    sd.setName((String) aValue);
                }
                break;
            case 1:
                if (column == 1) {
                    sd.setDescription((String) aValue);
                }
                break;
            case 2:
                if (column == 1) {
                    sd.setMolecularFormula((String) aValue);
                    fireTableCellUpdated(row, column);
                }
                break;
            case 3:
                if (column == 1) {
                    sd.setMolecularMass(new Float((String) aValue)
                            .floatValue());
                    break;
                }
        }
    }

    
    public int getColumnCount() {
        return names.length;
    }

    
    public int getRowCount() {
        return 4;
    }

    
    public Object getValueAt(int row, int column) {
        switch (row) {
            case 0:
                if (column == 0) {
                    return "Filename";
                } else {
                    return sd.getName();
                }
            case 1:
                if (column == 0) {
                    return "Description";
                } else {
                    return sd.getDescription();
                }
            case 2:
                if (column == 0) {
                    return "Molecular Formula";
                } else {
                    return MolecularFormulaManipulator.getString(sd
                            .getMolecularFormula());
                }
            case 3:
                if (column == 0) {
                    return "Molecular Mass";
                } else {
                    if (sd.getMolecularFormula() != null
                            && sd.getMolecularMass() == 0) {
                        // return new Float(new
                        // MFAnalyser(sd.getMolecularFormula()).getMass());
                        return new Float(
                                MolecularFormulaManipulator
                                        .getMajorIsotopeMass(sd
                                                .getMolecularFormula()));
                    }
                    return new Double(sd.getMolecularMass());
                }
        }
        return "-";
    }

    
    public String getColumnName(int column) {
        return names[column];
    }

    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        } else {
            return true;
        }
    }
}