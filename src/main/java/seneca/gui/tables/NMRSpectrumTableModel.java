package seneca.gui.tables;

import casekit.model.NMRSignal;
import casekit.model.NMRSpectrum;

import javax.swing.table.AbstractTableModel;

public class NMRSpectrumTableModel extends AbstractTableModel {

    /**
     *
     */
    private final NMRSpectrum nmrSpectrum;
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public NMRSpectrumTableModel(NMRSpectrum nmrSpectrum) {
        this.nmrSpectrum = nmrSpectrum;
    }

    /**
     * @param aValue The new ValueAt value
     * @param row    The new ValueAt value
     * @param column The new ValueAt value
     */
    
    public void setValueAt(Object aValue, int row, int column) {
        NMRSignal signal;
        signal = (NMRSignal) nmrSpectrum.getSignal(row);
        if (column >= 1 && column <= nmrSpectrum.dim) {
            signal.shift[column - 1] = ((Float) aValue).floatValue();
        }
        if (column == nmrSpectrum.dim + 1) {
            signal.intensity = ((Float) aValue).floatValue();
        }
        if (column == nmrSpectrum.dim + 2) {
            if (((String) aValue).equals("NONE")) {
                signal.phase = NMRSignal.PHASE_NONE;
                signal.intensity = 0f;
            }
            if (((String) aValue).equals("POSITIVE")) {
                signal.phase = NMRSignal.PHASE_POSITIVE;
                signal.intensity = 1f;
            }
            if (((String) aValue).equals("NEGATIVE")) {
                signal.phase = NMRSignal.PHASE_NEGATIVE;
                signal.intensity = -1f;
            }
        }
    }

    /**
     * @return The ColumnCount value
     */
    
    public int getColumnCount() {
        if (nmrSpectrum == null) {
            return 0;
        }
        return nmrSpectrum.dim + 3;
    }

    /**
     * @return The RowCount value
     */
    
    public int getRowCount() {
        if (nmrSpectrum == null) {
            return 0;
        }
        return nmrSpectrum.size();
    }

    /**
     * @param row    Description of Parameter
     * @param column Description of Parameter
     * @return The ValueAt value
     */
    
    public Object getValueAt(int row, int column) {
        NMRSignal signal;
        signal = (NMRSignal) nmrSpectrum.getSignal(row);
        if (column == 0) {
            return new Integer(row + 1);
        }
        if (column >= 1 && column <= nmrSpectrum.dim) {
            return new Float(signal.shift[column - 1]);
        }
        if (column == nmrSpectrum.dim + 1) {
            return new Float(signal.intensity);
        }
        if (column == nmrSpectrum.dim + 2) {
            return NMRSignal.PHASENAMES[signal.phase];
        }
        return "-";
    }

    /**
     * @param column Description of Parameter
     * @return The ColumnName value
     */
    
    public String getColumnName(int column) {
        if (column == 0) {
            return "No.";
        }
        if (column >= 1 && column <= nmrSpectrum.dim) {
            return nmrSpectrum.nucleus[column - 1];
        }
        if (column == nmrSpectrum.dim + 1) {
            return "Intensity";
        }
        if (column == nmrSpectrum.dim + 2) {
            return "Phase";
        }
        return "-";
    }

    /**
     * @param c Description of Parameter
     * @return The ColumnClass value
     */
    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * @param row Description of Parameter
     * @param col Description of Parameter
     * @return The CellEditable value
     */
    
    public boolean isCellEditable(int row, int col) {
        return true;
    }

}