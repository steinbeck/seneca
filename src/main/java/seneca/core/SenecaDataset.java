/*
 *  $RCSfile: SenecaDataset.java,v $
 *  $Author: steinbeck $
 *  $Date: 2004/02/16 09:50:53 $
 *  $Revision: 1.6 $
 *
 *  Copyright (C) 1997 - 2001  Dr. Christoph Steinbeck
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
package seneca.core;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.MolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.spectra.model.NMRSpectrum;
import seneca.judges.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * SenecaDataset.java Collection of datastructures (like those representing spectra) that make up a
 * dataset (belonging to one particular compound or structure elucidation project) in Seneca.
 *
 * @author steinbeck @created 30. Mai 2001
 */
public class SenecaDataset implements java.io.Serializable, ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SenecaDataset.class);
      /*
       * Some general properties like a name for this dataset, the molecular formula and the
       * associated mass. Nothing but a unique name is essential, though.
       */

    /**
     * Is this dataset being unloaded?
     */
    public boolean destroy = false;

      /*
       * A set of spectra. Since one can actually very well have two spectra of the same kind in a
       * dataset (e.g. NOESY's with different mixing times), I'm thinking of makeing this a little
       * more flexible.
       */
    /**
     * The 1D broadband decoupled carbon spectrum
     */
    public NMRSpectrum carbon1D = null;
    public NMRSpectrum dept90 = null;
    public NMRSpectrum dept135 = null;
    public NMRSpectrum hetcor = null;
    public NMRSpectrum ch_hetcorlr = null;
    public NMRSpectrum nh_hetcorlr = null;
    public NMRSpectrum hhcosy = null;
    public NMRSpectrum noesy = null;

      /*
       * And finally the set of nodes that is generated from whatever information is suitable. So
       * far, still lacking a good idea for what to do in cases where I do not have a molecular
       * formula, it's generated from this single, straightforward piece of information.
       */
    /**
     * A List with objects for initializing the StructureGenerator
     */
    public List annealingOptions = null;
    public int ANNEALING_ENGINE = 0;
    public int NUMBER_OF_STEPS = 1;
    /**
     * An AtomContainer holding the initial information on the atoms available in the molecular
     * formula
     */
    public IAtomContainer atomContainer;
    public List judges = null;
    /**
     * A List to store all sessions (calculations) that have been done on this dataset
     */
    public List sessions = null;
    public List serverList = null;
    protected transient EventListenerList changeListeners = null;
    private String name = null;
    private String description = "none";
    private float molecularMass = 0;
    public static String carbon1d_name = "Carbon1D";
    public static String dept90_name = "DEPT-90";
    public static String dept135_name = "DEPT-135";
    public static String hetcor_name = "1JCH correlation";
    public static String ch_hetcorlr_name = "CH long-range correlation";
    public static String nh_hetcorlr_name = "NH long-range correlation";
    public static String hhcosy_name = "H,H-COSY";
    public static String noesy_name = "NOESY";
    public IMolecularFormula newmolecularFormula = null;
    public String molecularFormula = "";
    private boolean atomPropertiesAssignedWell = false;
    public DataConsistency dataConsistency = null;

    public SenecaDataset() {
        carbon1D = new NMRSpectrum(NMRSpectrum.SPECTYPE_BB, "carbon1d");
        dept90 = new NMRSpectrum(NMRSpectrum.SPECTYPE_DEPT, "dept90");
        dept135 = new NMRSpectrum(NMRSpectrum.SPECTYPE_DEPT, "dept135");
        hetcor = new NMRSpectrum(NMRSpectrum.SPECTYPE_HSQC, "hetcor");
        ch_hetcorlr = new NMRSpectrum(NMRSpectrum.SPECTYPE_HMBC, "ch_hetcorlr");
        nh_hetcorlr = new NMRSpectrum(NMRSpectrum.SPECTYPE_NHCORR,
                "nh_hetcorlr");
        hhcosy = new NMRSpectrum(NMRSpectrum.SPECTYPE_HHCOSY, "hhcosy");
        noesy = new NMRSpectrum(NMRSpectrum.SPECTYPE_NOESY, "noesy");
        annealingOptions = new ArrayList();
        judges = new ArrayList();
        sessions = new ArrayList();
        serverList = new ArrayList();
        changeListeners = new EventListenerList();
        newmolecularFormula = new MolecularFormula();
            /*
             * Carbon1D dataset sets spectype in its contructor
             */
        carbon1D.specType = carbon1d_name;
        dept90.specType = dept90_name;
        dept135.specType = dept135_name;
        hetcor.specType = hetcor_name;
        ch_hetcorlr.specType = ch_hetcorlr_name;
        nh_hetcorlr.specType = nh_hetcorlr_name;
        hhcosy.specType = hhcosy_name;
        noesy.specType = noesy_name;
        dataConsistency = new DataConsistency(this);

    }

    public SenecaDataset(String name) {
        this();
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
        fireChange();
    }

    /**
     * Set the molecular formula for this dataset. Also seems to be a good place to initialize the
     * set of rings.
     *
     * @param MF The new MolecularFormula value
     */
    public void setMolecularFormula(String MF) {
        this.molecularFormula = MF;
        newmolecularFormula = MolecularFormulaManipulator.getMolecularFormula(MF,
                SilentChemObjectBuilder.getInstance());

        System.out.println("Molecular formula set in SD: " + MF);
        logger.info("Molecular formula set in SD: " + MF);
        fireChange();
    }


    public void setMolecularMass(float MS) {
        this.molecularMass = MS;
        fireChange();
    }

    public void setDescription(String d) {
        description = d;
        fireChange();
    }

    public void setCurrent() {
        fireChange();
    }

    public void setAnnealingOptions(List initObjects) {
        this.annealingOptions = initObjects;
    }

    public void setAtomContainer(IAtomContainer ac) {
        this.atomContainer = ac;
        checkForAtomProperties();
        fireChange();
    }

    public void setIsAtomPropertiesAssigned(boolean assignment) {
        this.atomPropertiesAssignedWell = assignment;
    }

    public boolean getIsAtomPropertiesAssigned() {
        checkForAtomProperties();
        return this.atomPropertiesAssignedWell;
    }

    public List getAnnealingOptions() {
        return annealingOptions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public IMolecularFormula getMolecularFormula() {
        return newmolecularFormula;
    }

    public float getMolecularMass() {
        return molecularMass;
    }

    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    public List getJudges() {
        return judges;
    }

    public Object getJudge(String name) {
        if (!name.startsWith("seneca.judges")) {
            name = "seneca.judges." + name;
        }
        for (int f = 0; f < judges.size(); f++) {
            if (name.equals(judges.get(f).getClass().getName())) {
                return judges.get(f);
            }
        }
        try {
            return Class.forName(name).newInstance();
        } catch (Exception exc) {
            System.out.println("Could not instantiate class " + name);
            logger.error("Could not instantiate class " + name);
        }
        return null;
    }

    public void destroy() {
        destroy = true;
        System.out.println("SenecaDataset " + getName()
                + " says: Arrgh - I�m being destroyed...");
        logger.info("SenecaDataset " + getName() + " says: Arrgh - I�m being destroyed...");
        fireChange();
    }

    public boolean init() {

        logger.info("Initiating senecadataset");
        judges.add(new HHCOSYJudge());
        judges.add(new HMBCJudge());
        judges.add(new SymmetryJudge());
        judges.add(new NMRShiftDbJudge());
        judges.add(new HOSECodeJudge());
        judges.add(new AntiBredtJudge());
        judges.add(new RingStrainJudge());
        //judges.add(new NPLikenessJudge());
        for (int f = 0; f < judges.size(); f++) {
            logger.info(judges.get(f));
        }
        return true;
    }

    public void addChangeListener(ChangeListener x) {
        if (changeListeners == null) {
            changeListeners = new EventListenerList();
        }
        changeListeners.add(ChangeListener.class, x);
        // bring it up to date with current state
        x.stateChanged(new ChangeEvent(this));
    }

    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

    
    public void stateChanged(ChangeEvent e) {
        checkForAtomProperties();
        fireChange();
    }

    public void checkForAtomProperties() {
        if (getAtomContainer() != null) {
            this.setIsAtomPropertiesAssigned(dataConsistency.isPropertiesAssignedWell());
        }
    }

    public void fireChange() {
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
