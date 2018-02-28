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
 * Date: 21/03/2013
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public class EditDataAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EditDataAction.class);

    public EditDataAction() {
        super(Seneca.editAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        SenecaDataset currentDataset = Seneca.getInstance().getSenecaDatasets().getCurrentDataset();
        if (currentDataset == null) {
            return;
        }
        Seneca.getInstance().getSenecaDatasets().remove(currentDataset);
        JSplitPane datasetPane = Seneca.getInstance().getDatasetPane();
        if (datasetPane == null) {
            return;
        }
        logger.info("Reloading for editing: " + currentDataset.getName());
        Seneca.getInstance().getContentPane().remove(datasetPane);
        Seneca.getInstance().getSenecaDatasets().add(currentDataset);
        JSplitPane currentPane = Seneca.getTreeFactory().loadTreeViewSettings(currentDataset, true);
        SenecaParameters sp = new SenecaParameters();
        sp.setSenecaCurrentNewDataFileLocation(sp.getSenecaLastLoadedFileLocation());
        Seneca.getInstance().setDatasetPane(currentPane);
        Seneca.getInstance().getContentPane().add(currentPane);
        Seneca.getInstance().getContentPane().repaint();

    }
}
