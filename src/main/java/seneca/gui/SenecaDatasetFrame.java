package seneca.gui;

/*
 * SenecaDatasetFrame.java
 *
 * Copyright (C) 1997, 1998, 1999 Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license. The intent of this license is
 * to state the conditions under which this Package may be copied, such that the Copyright Holder
 * maintains some semblance of artistic control over the development of the package, while giving
 * the users of the package the right to use and distribute the Package in a more-or-less customary
 * fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * The complete text of the license can be found in a file called LICENSE accompanying this package.
 */

import seneca.core.SenecaDataset;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * @author steinbeck @created September 9, 2001
 */
public class SenecaDatasetFrame extends JInternalFrame {

    /**
     * @since
     */
      /*
       * The idea is that this SenecaDatasetFrame should be open as long as a this dataset handled.
       * That means, any form manipulating the data in this dataset must register with this
       * SenecaDatasetFrame as an EventListener
       */
    public transient EventListenerList changeListeners = new EventListenerList();
    /**
     * @since
     */
    boolean DEBUG = true;
    boolean isClosed = false;
    /**
     * @since
     */
    SenecaDataset sd;
    JDesktopPane jdp;
    private String MF = "";
    static TreeViewComponentFactory treeFactory;

    /**
     * Constructs a dataset properties sheet from SenecaDataset number 'thisone' of the vector
     * 'dsvect'
     *
     * @param sd Description of Parameter
     */
    public SenecaDatasetFrame(SenecaDataset sd) {
        super(sd.getName(), true, true, true, true);
        this.sd = sd;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        treeFactory = TreeViewComponentFactory.getInstance();
        loadTree();

        addInternalFrameListener(new ExitHandler());
        setVisible(true);

    }

    private void loadTree() {
        JSplitPane splitPane = treeFactory.loadTreeViewSettings(sd, true);
        getContentPane().add(splitPane);
    }

    /**
     * Adds a feature to the ChangeListener attribute of the SenecaDatasetFrame object
     *
     * @param x The feature to be added to the ChangeListener attribute
     */
    public void addChangeListener(ChangeListener x) {
        changeListeners.add(ChangeListener.class, x);
        // bring it up to date with current state
        x.stateChanged(new ChangeEvent(this));
    }

    /**
     * @param x
     */
    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

    /**
     * @since
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

    public class ExitHandler extends InternalFrameAdapter {

        
        public void internalFrameClosing(InternalFrameEvent e) {
            new ExitDialog().show();
        }
    }


}
