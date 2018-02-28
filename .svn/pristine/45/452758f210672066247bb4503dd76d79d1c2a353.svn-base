/*
 *  HOSECodeJudgeConfigurator.java
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
package seneca.gui.configurators;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.judges.NMRShiftDbJudge;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * This class provides a gui for configuring a HOSECodeJudge
 *
 * @author steinbeck @created September 9, 2001
 */
public class NMRShiftDBJudgeConfigurator extends JudgeConfigurator {

    private static final long serialVersionUID = 1L;
    NMRShiftDbJudge nmrShiftDbJudge = null;
    private boolean isAutoConfigured = false;

    public NMRShiftDBJudgeConfigurator(SenecaDataset sd) {
        super(sd, (NMRShiftDbJudge) sd.getJudge("NMRShiftDbJudge"));
        this.nmrShiftDbJudge = (NMRShiftDbJudge) sd.getJudge("NMRShiftDbJudge");
        autoconfigure();
        firstAssignmentMade = true;
    }

    
    public void autoconfigure() {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return;
        }

        int atomCount = ac.getAtomCount();
        int ccount = 0;
        double[] tempshifts = new double[atomCount];

        for (int f = 0; f < atomCount; f++) {
            if (ac.getAtom(f).getProperty(CDKConstants.NMRSHIFT_CARBON) != null) {
                tempshifts[ccount] = ((Float) ac.getAtom(f).getProperty(
                        CDKConstants.NMRSHIFT_CARBON)).floatValue();
                ccount++;
            }
        }
        double[] shifts = new double[ccount];
        System.arraycopy(tempshifts, 0, shifts, 0, ccount);
        nmrShiftDbJudge.setCarbonShifts(shifts);
        isAutoConfigured = true;
    }

    protected String getMessage() {
        StringBuffer m = new StringBuffer();
        m.append("<html>");
        m.append("<b>NMRShiftDbJudge</b> uses about 600 1-sphere HOSE codes ");
        m.append("to validate the carbon environment of a structure ");
        m.append("during the stochastic search process in terms of ");
        m.append("hybridization state and hetero atom attachments. ");
        m.append("There is no need for any configurations here. ");
        m.append("Press the 'autoconfigure' button or check the 'activate' box. ");
        m.append("<b>Important Note: </b><i>You should not use this Judge in conjunction ");
        m.append("with HybridizationJudge and HeteroBondsJudge. Use either HOSECodeJudge ");
        m.append("or the combination of HybridizationJudge or HeteroBondsJudge</i>.");
        m.append("</html>");
        return m.toString();
    }

    
    protected Box constructCenterBox() {
        Box centerBox = new Box(BoxLayout.X_AXIS);
        JEditorPane reportPane = new JEditorPane();
        reportPane.setEditable(false);
        reportPane.setEditorKit(new HTMLEditorKit());
        reportPane.setText(getMessage());
        JScrollPane scrollpane = new JScrollPane(reportPane);
        scrollpane.setPreferredSize(new Dimension(200, 400));
        centerBox.add(scrollpane);
        return centerBox;
    }

    public boolean isAutoConfigured() {
        return isAutoConfigured;
    }
}
