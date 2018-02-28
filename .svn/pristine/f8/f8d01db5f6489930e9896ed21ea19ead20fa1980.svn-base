package seneca.gui.actions;

import seneca.gui.ExtensionFileFilter;
import seneca.gui.Seneca;
import seneca.gui.SenecaParameters;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class OpenAllAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OpenAllAction() {
        super(Seneca.openAllAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        askUserToChoose();
    }

    private void askUserToChoose() {

        SenecaParameters sp = new SenecaParameters();
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
}