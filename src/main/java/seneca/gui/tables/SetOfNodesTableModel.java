package seneca.gui.tables;

import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;

import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;

public class SetOfNodesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SetOfNodesTableModel.class);
    String[] names = {"No.", "Symbol", "Hydrogens", "Assigned 13C Shift"};
    SenecaDataset sd;

    public SetOfNodesTableModel(SenecaDataset sd) {
        this.sd = sd;
    }

    
    public void setValueAt(Object aValue, int row, int column) {

        if (column == 2) {
            try {
                int i = Integer.parseInt((String) aValue);
                if (i >= 0 && i <= 4) {
                    sd.getAtomContainer().getAtom(row)
                            .setImplicitHydrogenCount(i); // modified
                }
            } catch (Exception exc) {
                logger.info(exc.getMessage());
            }

        }
        if (column == 3) {
            sd.getAtomContainer()
                    .getAtom(row)
                    .setProperty(CDKConstants.NMRSHIFT_CARBON,
                            new Float((String) aValue));
            System.out.println("setting shift value count  = " + aValue);
            logger.info("setting shift value count  = " + aValue);
        }
        sd.stateChanged(new ChangeEvent(this));
    }

    
    public int getColumnCount() {
        return names.length;
    }

    
    public int getRowCount() {
        if (sd.getAtomContainer() == null) {
            return 0;
        }
        return sd.getAtomContainer().getAtomCount();
    }

    
    public Object getValueAt(int row, int column) {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return "";
        }
        switch (column) {
            case 0:
                return String.valueOf(row + 1);
            case 1:
                return String.valueOf(ac.getAtom(row).getSymbol()); // mod
            case 2:
                if (ac.getAtom(row).getImplicitHydrogenCount() < 0) {
                    return "undefined";
                } else {
                    return String.valueOf(ac.getAtom(row)
                            .getImplicitHydrogenCount());
                }
            case 3:
                if (ac.getAtom(row).getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                    return String.valueOf(ac.getAtom(row).getProperty(
                            CDKConstants.NMRSHIFT_CARBON));
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
        if (col == 2) {
            return true;
        }
        return false;
    }
}