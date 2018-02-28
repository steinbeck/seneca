package seneca.gui.tables;

import seneca.compute.Compute;
import seneca.structgen.StructureGeneratorStatus;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StructureGenerationServerTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    String[] names = {"No.", "ID", "Host", "Dataset", "MF", "Status ",
            "Iter", "Time(s)"};
    List structureGeneratorServerProcesses;

    public StructureGenerationServerTableModel(List structureGeneratorServerProcesses) {
        this.structureGeneratorServerProcesses = structureGeneratorServerProcesses;
    }

    
    public void setValueAt(Object aValue, int row, int column) {
    }

    
    public int getColumnCount() {
        return names.length;
    }

    
    public int getRowCount() {
        return structureGeneratorServerProcesses.size();
    }

    
    public Object getValueAt(int row, int column) {
        Compute server = (Compute) structureGeneratorServerProcesses.get(row);
        switch (column) {
            /*
             * Number of Row
             */
            case 0:
                return String.valueOf(row + 1);
            case 1:
                try {
                    return server.getID();
                } catch (Exception exc) {
                    //System.err.println("getID(): " + exc.toString());
                }

            case 2:
                try {
                    return server.getHostName();
                } catch (Exception exc) {
                    //System.err.println("getHostName(): " + exc.toString());
                }

            case 3:
                try {
                    return ((StructureGeneratorStatus) server.getTaskStatus()).datasetName;
                } catch (Exception exc) {
                    // System.err.println("getDataSet(): " + exc.toString());
                }

            case 4:
                try {
                    return ((StructureGeneratorStatus) server.getTaskStatus()).molecularFormula;
                } catch (Exception exc) {
                    //System.err.println("getMF(): " + exc.toString());
                }

            case 5:
                try {
                    return ((StructureGeneratorStatus) server.getTaskStatus()).status;
                } catch (Exception exc) {
                    //System.err.println("getStatus(): " + exc.toString());
                }
            case 6:
                try {
                    return ((StructureGeneratorStatus) server.getTaskStatus()).iteration;
                } catch (Exception exc) {
                    //System.err.println("getIter(): " + exc.toString());
                }
            case 7:
                try {
                    return ((StructureGeneratorStatus) server.getTaskStatus()).timeTaken;
                } catch (Exception exc) {
                    //System.err.println("getIter(): " + exc.toString());
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

    public void removeRow(int row) {
        structureGeneratorServerProcesses.remove(row);
        fireTableRowsDeleted(row, row);
    }
}