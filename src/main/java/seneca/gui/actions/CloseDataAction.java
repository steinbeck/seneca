package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.core.SenecaDataset;
import seneca.gui.Seneca;
import seneca.gui.SenecaParameters;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 30/01/2013
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */

public class CloseDataAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CloseDataAction.class);

    public CloseDataAction() {
        super(Seneca.closeAction);
    }

    
    public void actionPerformed(ActionEvent actionEvent) {
        SenecaDataset currentDataset = Seneca.getInstance().getSenecaDatasets().getCurrentDataset();
        if (currentDataset != null) {
            String currentFile = new SenecaParameters().getSenecaCurrentNewDataFileLocation();
            if (!currentFile.isEmpty()) {
                closeDataSet();
            } else {
                saveDataBeforeClosing();
            }
        }
    }

    private void saveDataBeforeClosing() {
        String msg = "Do you want to save data before closing ?";
        int answer = JOptionPane.showConfirmDialog(Seneca.getInstance(), msg, "Save", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            new SaveAllAction().askUserToSave();
        } else if (answer == JOptionPane.NO_OPTION) {
            closeDataSet();
        }
    }

    private void closeDataSet() {
        SenecaDataset currentDataset = Seneca.getInstance().getSenecaDatasets().getCurrentDataset();
        Seneca.getInstance().getSenecaDatasets().remove(currentDataset);
        logger.info("Removing dataset: " + currentDataset.getName());
        JSplitPane datasetPane = Seneca.getInstance().getDatasetPane();
        new SenecaParameters().setSenecaCurrentNewDataFileLocation("");
        Seneca.getInstance().getContentPane().remove(datasetPane);
        Seneca.getInstance().getContentPane().repaint();
    }
}
