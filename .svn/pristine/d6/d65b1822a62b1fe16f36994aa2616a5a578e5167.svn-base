package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.core.SenecaDataset;
import seneca.gui.Seneca;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class NewAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final Logger logger = Logger.getLogger(NewAction.class);

    public NewAction() {
        super(Seneca.newAction);

    }

    
    public void actionPerformed(ActionEvent e) {
        Seneca.senecaDatasets.add(new SenecaDataset("untitled-"
                + (new Date().hashCode())));
        SenecaDataset sd = Seneca.senecaDatasets.getCurrentDataset();

//        SenecaDatasetFrame sdf = new SenecaDatasetFrame(sd);
//        Seneca.getInstance().getContentPane().add(sdf);
//        sdf.toFront();
//        try {
//            sdf.setVisible(true);
//            sdf.setSelected(true);
//        } catch (java.beans.PropertyVetoException pvexc) {
//            System.err.println("Problem activating new JInternalFrame");
//            logger.error("Problem activating new JInternalFrame\n" + pvexc.getMessage() );
//        }
//        SenecaDataset sd = new SenecaDataset("untitled-" + (new Date().hashCode())) ;
//        Seneca.getInstance().setCurrentDataset(sd);
        JSplitPane currentPane = Seneca.getTreeFactory().loadTreeViewSettings(sd, true);
        Seneca.getInstance().setDatasetPane(currentPane);
        Seneca.getInstance().getContentPane().add(currentPane);

    }
}