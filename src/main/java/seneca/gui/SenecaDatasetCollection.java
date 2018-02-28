/*
 *  SenecaDatasetCollection.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
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

import seneca.core.SenecaDataset;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;

/**
 * SenecaDatasetCollection.java An extension of a simple List to store
 * SenecaDatasets.
 *
 * @author steinbeck
 * @created September 9, 2001
 */

public class SenecaDatasetCollection extends ArrayList implements ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Description of the Field
     */
    protected transient EventListenerList changeListeners = new EventListenerList();
    /**
     * Description of the Field
     */
    protected int currentDatasetNumber;

    /**
     * Constructor for the SenecaDatasetCollection object
     */
    public SenecaDatasetCollection() {
    }

    /**
     * Sets the CurrentDatasetNumber attribute of the SenecaDatasetCollection
     * object
     *
     * @param thisOne The new CurrentDatasetNumber value
     */
    public void setCurrentDatasetNumber(int thisOne) {
        if (thisOne != currentDatasetNumber) {
            currentDatasetNumber = thisOne;
            fireChange();
        }
    }

    /**
     * Sets the CurrentDatasetNumber attribute of the SenecaDatasetCollection
     * object
     *
     * @param thisOne The new CurrentDatasetNumber value
     */
    public void setCurrentDatasetNumber(String thisOne) {
        for (int f = 0; f < size(); f++) {
            if (((SenecaDataset) get(f)).getName().equals(thisOne)
                    && f != currentDatasetNumber) {
                currentDatasetNumber = f;
                fireChange();
                return;
            } else if (((SenecaDataset) get(f)).getName().equals(thisOne)
                    && f == currentDatasetNumber) {
                return;
            }
        }
        System.err.println("Error: No such dataset!");
    }

    /**
     * Get an String array with the of all
     *
     * @return The Names value
     */
    public String[] getNames() {
        String[] names = new String[size()];
        for (int f = 0; f < names.length; f++) {
            names[f] = ((SenecaDataset) get(f)).getName();
        }
        return names;
    }

    /**
     * Gets the CurrentDatasetNumber attribute of the SenecaDatasetCollection
     * object
     *
     * @return The CurrentDatasetNumber value
     */
    public int getCurrentDatasetNumber() {
        if (size() == 0) {
            return -1;
        }
        return currentDatasetNumber;
    }

    /**
     * Gets the CurrentDataset attribute of the SenecaDatasetCollection object
     *
     * @return The CurrentDataset value
     */
    public SenecaDataset getCurrentDataset() {
        if (size() == 0) {
            return null;
        }
        return (SenecaDataset) get(currentDatasetNumber);
    }

    /**
     * Use this method instead of 'addElement' to make sure that a ChangeEvent
     * is fired.
     *
     * @param obj Description of Parameter
     * @return Description of the Returned Value
     */
    
    public boolean add(Object obj) {

        ((SenecaDataset) obj).addChangeListener(this);
        super.add(obj);
        setCurrentDatasetNumber(size() - 1);
        fireChange();
        return true;
    }

    /**
     * Use this method instead of 'removeElementAt' to make sure that a
     * ChangeEvent is fired.
     *
     * @param f Description of Parameter
     * @return Description of the Returned Value
     */
    
    public Object remove(int f) {
        Object obj = super.remove(f);
        fireChange();
        return obj;
    }

    /**
     * Use this method instead of 'removeElementAt' to make sure that a
     * ChangeEvent is fired.
     *
     * @param o Description of Parameter
     * @return Description of the Returned Value
     */
    
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        fireChange();
        return removed;
    }

    // Listener notification support

    /**
     * Adds a feature to the ChangeListener attribute of the
     * SenecaDatasetCollection object
     *
     * @param x The feature to be added to the ChangeListener attribute
     */
    public void addChangeListener(ChangeListener x) {
        changeListeners.add(ChangeListener.class, x);
        // bring it up to date with current state
        x.stateChanged(new ChangeEvent(this));
    }

    /**
     * Description of the Method
     *
     * @param x Description of Parameter
     */
    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

    /**
     * This collection of SenecaDatasets registers itself with all it members to
     * be informed if on of them changes
     *
     * @param e Description of Parameter
     */

    
    public void stateChanged(ChangeEvent e) {
        SenecaDataset sd = (SenecaDataset) e.getSource();
        if (sd.destroy) {
            System.out.println("SenecaDatasetCollection says: SenecaDataset "
                    + sd.getName() + " is removed now...");
            System.out.println(remove(sd));
            sd = null;
        }
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
