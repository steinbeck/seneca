/*
 *  ExitDialog.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in awr
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.gui;

import org.apache.log4j.Logger;
import seneca.core.SenecaDataset;
import seneca.core.SpecMLGenerator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

/**
 * Displays an exit dialog
 *
 * @author steinbeck
 * @created September 9, 2001
 */
class ExitDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ExitDialog.class);
    JTextArea textArea = null;
    String message = "Your are about to exit to close this frame.\n Do you want to save your data?";
    SenecaDataset sd = null;
    boolean saved = false;
    EventListenerList changeListeners = null;
    boolean debug = false;

    public ExitDialog() {
        super();
        setTitle("Exiting...");
        textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        getContentPane().setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());

        JButton saveButton = new JButton("Save datset");
        JButton exitButton = new JButton("Exit without saving");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(new CancelAction());
        exitButton.addActionListener(new ExitWithoutSavingAction());
        southPanel.add(saveButton);
        southPanel.add(exitButton);
        southPanel.add(cancelButton);
        new EventListenerList();
        getContentPane().add(textArea, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
    }

    public ExitDialog(SenecaDataset sd) {
        this();
        this.sd = sd;
    }

    void setSaved(boolean saved) {
        this.saved = saved;
    }

    boolean getSaved() {
        return saved;
    }

    protected boolean saveDataset() {
        FileWriter fw = null;
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                fw = new FileWriter(file);
                SpecMLGenerator smlGen = new SpecMLGenerator();
                fw.write(smlGen.convert(sd));
                fw.flush();
                fw.close();
                sd.destroy();
                dispose();
            } catch (Exception exc) {
                System.err
                        .println("There's a problem closing this internal frame.\n");
                System.err.println(exc.toString());
                logger.info("There's a problem closing this internal frame");
                logger.error(exc.getMessage());
                return false;
            }
        } else {
            System.out.println("Save command cancelled by user." + "\n");
            logger.info("Save command cancelled by user.");
            return false;
        }
        return false;
    }

    class SaveAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        SaveAction() {
            super("save");
        }

        
        public void actionPerformed(ActionEvent e) {
            saveDataset();
        }
    }

    class CancelAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        CancelAction() {
            super("Cancel");
        }

        
        public void actionPerformed(ActionEvent e) {
            hide();
            dispose();
        }
    }

    class ExitWithoutSavingAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        ExitWithoutSavingAction() {
            super("ExitWithoutSaving");
        }

        
        public void actionPerformed(ActionEvent e) {
            dispose();

        }
    }

	/*
     * Listener notification support methods START here
	 */

    /**
     * Adds a feature to the ChangeListener attribute of the SenecaDataset
     * object
     *
     * @param x The feature to be added to the ChangeListener attribute
     */
    public void addChangeListener(ChangeListener x) {
        if (changeListeners == null)
            changeListeners = new EventListenerList();
        changeListeners.add(ChangeListener.class, x);
    }

    /**
     * Description of the Method
     *
     * @param x Description of Parameter
     */
    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

	/*
	 * Listener notification support methods END here
	 */

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

    /**
     * Description of the Method
     */
    protected void fireChange() {
        // Create the event:
        ChangeEvent c = new ChangeEvent(this);
        // Get the listener list
        Object[] listeners = changeListeners.getListenerList();
        // Process the listeners last to first
        // List is in pairs, Class and instance
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ChangeListener cl = (ChangeListener) listeners[i + 1];
                cl.stateChanged(c);
            }
        }
    }

}
