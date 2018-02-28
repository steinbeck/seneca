package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.gui.ExtensionFileFilter;
import seneca.gui.Seneca;
import seneca.gui.SenecaParameters;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author kalai
 */

public class OpenPreviousAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(OpenPreviousAction.class);
    private SenecaParameters sp = null;

    public OpenPreviousAction() {
        super(Seneca.openPreviousFileAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        sp = new SenecaParameters();
        if (sp.getSenecaLastLoadedFileLocation().equals("/")) {
            askUserToChoose();
        } else {
            loadPreviousFile();
        }
    }

    private void askUserToChoose() {

        sp = new SenecaParameters();
        JFileChooser chooser = new JFileChooser(sp.getSenecaFileLocation());
        ExtensionFileFilter filter = new ExtensionFileFilter();
        filter.addExtension("sml");
        filter.setDescription("SENECA Dataset");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(Seneca.getInstance());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        sp.setSenecaFileLocation(file.getParent());
        sp.setSenecaLastLoadedFileLocation(file.toString());
        Seneca.getInstance().openFile(file);
    }

    private void loadPreviousFile() {
        String previousFileName = sp.getSenecaLastLoadedFileLocation();
        logger.info("Opening previous file: " + previousFileName);
        Seneca.getInstance().openFile(new File(previousFileName));
        sp.setSenecaCurrentNewDataFileLocation(previousFileName);
    }
}