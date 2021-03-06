package seneca.gui.tables;

import casekit.model.NMRSignal;
import casekit.model.NMRSpectrum;
import seneca.core.SenecaDataset;

import javax.swing.table.AbstractTableModel;

/**
 * This is the table summarizing the 1D carbon spectra, based on which the
 * hydrogen count assignments are done.
 *
 * @author steinbeck
 * @created September 9, 2001
 */
public class Carbon1DTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    SenecaDataset sd = null;
    String[] names = {"Assign X times", "[ppm]", "Int.", "DEPT-90",
            "Int.", "DEPT-135", "Int."};

    public Carbon1DTableModel(SenecaDataset sd) {
        this.sd = sd;
    }

    
    public void setValueAt(Object aValue, int row, int column) {

    }

    
    public int getColumnCount() {
        return names.length;
    }

    
    public int getRowCount() {
        if (sd.carbon1D == null) {
            return 0;
        }
        return sd.carbon1D.size();
    }

    
    public Object getValueAt(int row, int column) {
        NMRSignal signal = null, deptSignal = null, bbSignal = null;
        boolean found = false;
        switch (column) {
            case 0:
                return "-";

            case 1:
                signal = (NMRSignal) sd.carbon1D.getSignal(row);
                if (signal == null) {
                    return "-";
                } else {
                    return String.valueOf(signal.shift[NMRSignal.DIM_ONE - 1]);
                }
            case 2:
                signal = (NMRSignal) sd.carbon1D.getSignal(row);
                if (signal == null) {
                    return "";
                } else {
                    return String.valueOf(signal.intensity);
                }
            case 3:
                if (row < sd.carbon1D.size()) {
                    signal = (NMRSignal) sd.carbon1D.getSignal(row);
                    found = false;
                    for (int f = 0; f < sd.dept90.size(); f++) {
                        deptSignal = (NMRSignal) sd.dept90.getSignal(f);
                        bbSignal = (NMRSignal) sd.carbon1D.pickClosestSignal(
                                deptSignal.getShift(0), NMRSpectrum.NUC_CARBON,
                                (float) 1.0);
                        if (bbSignal == signal) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return "-";
                    } else {
                        return String
                                .valueOf(deptSignal.shift[NMRSignal.DIM_ONE - 1]);
                    }
                } else {
                    return "-";
                }
            case 4:
                if (row < sd.carbon1D.size()) {
                    signal = (NMRSignal) sd.carbon1D.getSignal(row);
                    found = false;
                    for (int f = 0; f < sd.dept90.size(); f++) {
                        deptSignal = (NMRSignal) sd.dept90.getSignal(f);
                        bbSignal = (NMRSignal) sd.carbon1D.pickClosestSignal(
                                deptSignal.getShift(0), NMRSpectrum.NUC_CARBON,
                                (float) 1.0);
                        if (bbSignal == signal) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return "-";
                    } else {
                        return String.valueOf(deptSignal.intensity);
                    }
                } else {
                    return "-";
                }
            case 5:
                if (row < sd.carbon1D.size()) {
                    signal = (NMRSignal) sd.carbon1D.getSignal(row);
                    found = false;
                    for (int f = 0; f < sd.dept135.size(); f++) {
                        deptSignal = (NMRSignal) sd.dept135.getSignal(f);
                        bbSignal = (NMRSignal) sd.carbon1D.pickClosestSignal(
                                deptSignal.getShift(0), NMRSpectrum.NUC_CARBON,
                                (float) 1.0);
                        if (bbSignal == signal) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return "-";
                    } else {
                        return String
                                .valueOf(deptSignal.shift[NMRSignal.DIM_ONE - 1]);
                    }
                } else {
                    return "-";
                }
            case 6:
                if (row < sd.carbon1D.size()) {
                    signal = (NMRSignal) sd.carbon1D.getSignal(row);
                    found = false;
                    for (int f = 0; f < sd.dept135.size(); f++) {
                        deptSignal = (NMRSignal) sd.dept135.getSignal(f);
                        bbSignal = (NMRSignal) sd.carbon1D.pickClosestSignal(
                                deptSignal.getShift(0), NMRSpectrum.NUC_CARBON,
                                (float) 1.0);
                        if (bbSignal == signal) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return "-";
                    } else {
                        return String.valueOf(deptSignal.intensity);
                    }
                } else {
                    return "-";
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
        return true;
    }
}