/*
 *  TwoDSpectrumJudgeConfigurator.java
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

import com.touchgraph.graphlayout.GLPanel;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.core.assigners.SpectrumAssigner;
import seneca.judges.TwoDSpectrumJudge;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a gui for configuring a 2D-spectrum Judges
 *
 * @author steinbeck
 * @created September 9, 2001
 */
abstract class TwoDSpectrumJudgeConfigurator extends JudgeConfigurator {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Description of the Field
     */
    protected transient SpectrumAssigner sa;
    /**
     * Description of the Field
     */
    protected TwoDSpectrumJudge twoDSpectrumJudge;
    GLPanel glPanel;
    public static final Logger logger = Logger.getLogger(TwoDSpectrumJudgeConfigurator.class);
//    TGPanel tgPanel;

    TwoDSpectrumJudgeConfigurator(SenecaDataset sd, TwoDSpectrumJudge tJ,
                                  SpectrumAssigner sa, String title) {
        super(sd, tJ);
        this.twoDSpectrumJudge = tJ;
        this.sa = sa;
        if (propertiesOkForAssigningJudges) {
            getAutoConfigureButton().doClick();
            clickedAlready = true;
        }
        firstAssignmentMade = true;
    }

    /**
     * Here we produce a visual graph representing the relation between the heavy atoms in the
     * molecule as given by the underlying 2D spectrum.
     */
    void updateGraph() {
        TGPanel tgPanel = glPanel.getTGPanel();
        IAtomContainer ac = sd.getAtomContainer();
        tgPanel.clearAll();
        List nodes = new ArrayList();
        Node node1 = null, node2 = null;
        try {

            for (int f = 0; f < twoDSpectrumJudge.assignment.length; f++) {
                // check the symbol to get ride of the heavy atoms other than carbons
                node1 = new Node(ac.getAtom(f).getSymbol() + "-" + (f + 1));
                nodes.add(node1);
                tgPanel.addNode(node1);
                System.out.println("Added node " + node1);
                logger.info("Added node " + node1);
            }
            for (int i = 0; i < twoDSpectrumJudge.assignment.length; i++) {
                for (int j = 0; j < twoDSpectrumJudge.assignment.length; j++) {
                    if (twoDSpectrumJudge.assignment[i][j][j]) {
                        System.out.println(i + ", " + j);
                        tgPanel.addEdge((Node) nodes.get(i),
                                (Node) nodes.get(j), 10000);
                        if (node2 == null) {
                            node2 = (Node) nodes.get(i);
                        }
                    }
                }
            }


        } catch (Exception exc) {
            exc.printStackTrace();
        }
        // glPanel.setTGPanel(tgPanel);

        // tgPanel.setGraphEltSet(ges);
        // tgPanel.setLocale(node2 ,8);
        // tgPanel.setSelect(node2);
        glPanel.setPreferredSize(new Dimension(300, 300));
        glPanel.revalidate();
    }

    /**
     * Automatically configures the twoDSpectrumJudges using the SpectrumAssigner in charge.
     */
    
    public void autoconfigure() {
        sa.setSenecaDataset(sd);
        if (!sa.assign() || twoDSpectrumJudge.assignment == null) {
            twoDSpectrumJudge.setEnabled(false);
            return;
        }
        twoDSpectrumJudge.setEnabled(true);
        updateGraph();
    }

    
    protected Box constructCenterBox() {
        Box centerBox = new Box(BoxLayout.X_AXIS);
        glPanel = new GLPanel();
        glPanel.getTGPanel().setGraphEltSet(new GraphEltSet());
//        new TGPanel().getG;
        centerBox.add(glPanel);
        return centerBox;
    }

    
    protected void report() {
        JFrame frame = new JFrame();
        frame.setTitle("Judge Configuration Report");
        JTextPane text = new JTextPane();
        text.setPreferredSize(new Dimension(400, 400));
        text.setText(judge.toString());
        frame.getContentPane().add(text);
        frame.pack();
        frame.setVisible(true);

    }
}
