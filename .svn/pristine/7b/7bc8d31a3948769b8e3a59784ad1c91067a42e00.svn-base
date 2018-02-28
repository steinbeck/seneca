package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.core.SpecMLGenerator;
import seneca.core.exception.SenecaIOException;
import seneca.gui.ExtensionFileFilter;
import seneca.gui.Seneca;
import seneca.gui.SenecaParameters;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveAllAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final Logger logger = Logger.getLogger(SaveAllAction.class);

    public SaveAllAction() {
        super(Seneca.saveAllAction);
    }

    
    public void actionPerformed(ActionEvent e) {

        if (Seneca.getInstance().getSenecaDatasets().size() == 0) {
            return;
        }
        String currentFile = new SenecaParameters().getSenecaCurrentNewDataFileLocation();
        if (currentFile.isEmpty()) {
            askUserToSave();
        } else {
            String msg = "Do you want to overwrite ?";
            int answer = JOptionPane.showConfirmDialog(Seneca.getInstance(), msg, "Save", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                saveData(new File(currentFile));
            } else if (answer == JOptionPane.NO_OPTION) {
                askUserToSave();
            }
        }
    }

    public void askUserToSave() {
        SenecaParameters sp = new SenecaParameters();
        JFileChooser chooser = new JFileChooser(sp.getSenecaFileLocation());
        ExtensionFileFilter filter = new ExtensionFileFilter();
        filter.addExtension("sml");
        filter.setDescription("SENECA Dataset");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(Seneca.getInstance());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        sp.setSenecaFileLocation(file.getParent());
        sp.setSenecaLastLoadedFileLocation(file.toString());
        sp.setSenecaCurrentNewDataFileLocation(file.toString());
        saveData(file);
    }

    private void saveData(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(new SpecMLGenerator().convert(Seneca.senecaDatasets
                    .getCurrentDataset()));
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            String s = "An IO error ocurred in the java subsystem.";
            JOptionPane.showMessageDialog(null, s, "IO error",
                    JOptionPane.ERROR_MESSAGE);
            logger.error(s);
        } catch (SenecaIOException sioe) {
            String s = "An IO error ocurred due to an inconsistency in the SENECA datastructure.";
            s += "This should never have happened. Please contact the SENECA team.";
            JOptionPane.showMessageDialog(null, s, "IO error",
                    JOptionPane.ERROR_MESSAGE);
            logger.error(s);
            logger.error(sioe.getMessage());
        }
    }

}