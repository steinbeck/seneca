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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import seneca.core.SenecaDataset;
import seneca.core.Utilities;
import seneca.judges.NPLikenessJudge;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * This class provides a gui for configuring a NPLikenessJudge
 *
 * @author Kalai
 */
public class NPLikenessJudgeConfigurator extends JudgeConfigurator {

    private static final long serialVersionUID = 1L;
    NPLikenessJudge npLikenessJudge = null;
    SenecaDataset sd = null;

    public NPLikenessJudgeConfigurator(SenecaDataset sd) {
        super(sd, (NPLikenessJudge) sd.getJudge("NPLikenessJudge"));
        this.sd = sd;
        this.npLikenessJudge = (NPLikenessJudge) sd.getJudge("NPLikenessJudge");
        autoconfigure();
        firstAssignmentMade = true;
    }

    
    public void autoconfigure() {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return;
        }
        if (!ac.contains(new Atom("H"))) {
            //    npLikenessJudge.setAtomCount(ac.getAtomCount());
            npLikenessJudge.setAtomCount(Utilities.getAnyAtomCount(ac, "C"));
        } else {
            IAtomContainer molWithoutHydrogens = AtomContainerManipulator.removeHydrogens(ac);
            //     npLikenessJudge.setAtomCount(molWithoutHydrogens.getAtomCount());
            npLikenessJudge.setAtomCount(Utilities.getAnyAtomCount(molWithoutHydrogens, "C"));
        }
    }

    protected String getMessage() {
        StringBuffer m = new StringBuffer();
        m.append("<html>");
        m.append("<b>NPLikenessJudge</b> creates atom centered fragments ");
        m.append("of the molecule and calculates natural product likeness ");
        m.append("of each of the fragment by looking up known knowledge-base of natural ");
        m.append("product like fragments. ");
        m.append("There is no need for any configurations here. ");
        m.append("Press the 'autoconfigure' button or check the 'activate' box. ");
//            m.append("<b>Important Note: </b><i>Use this judge only if the NMR data corresponds to ");
//            m.append("a natural product or a metabolite</i>.");
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

}
