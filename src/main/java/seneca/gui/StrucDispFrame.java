/* StrucDispFrame.java
 *
 * Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license.
 * The intent of this license is to state the conditions under which this Package 
 * may be copied, such that the Copyright Holder maintains some semblance
 * of artistic control over the development of the package, while giving the 
 * users of the package the right to use and distribute the Package in a
 * more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * The complete text of the license can be found in a file called LICENSE 
 * accompanying this package.
 */
package seneca.gui;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import seneca.core.StructureIO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/*
 *
 * @ kalai
 */
public class StrucDispFrame extends JFrame {

    protected StrucContainer panel;
    protected JScrollPane scrollPane;
    protected int noOfStructures = 0;
    protected Dimension jmdDim = new Dimension(320, 220);
    protected JPanel southPanel;
    protected JButton closeButton;
    protected JButton saveSDFButton;
    protected List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
    public static boolean standAlone = false;
    private static final Logger logger = Logger.getLogger(StrucDispFrame.class);

    public StrucDispFrame() {
        super();
        getContentPane().setLayout(new BorderLayout());
        setTitle("Structure generation summary");

        panel = new StrucContainer(this);
        panel.setVisible(true);
        scrollPane = new JScrollPane(panel);
        southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 2));
        closeButton = new JButton("Close");
        closeButton.addActionListener(new closeAction(this));
        saveSDFButton = new JButton("Save SDF");
        saveSDFButton.addActionListener(new SaveMDLAction(this));

        southPanel.add(saveSDFButton);
        southPanel.add(closeButton);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        getContentPane().add("Center", scrollPane);
        getContentPane().add("South", southPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void addStructure(JPanel jmd, String title, IAtomContainer molecule) {
        noOfStructures++;
        jmd.setPreferredSize(jmdDim);
        jmd.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 13), Color.BLUE));

        molecules.add(molecule);
        panel.add(jmd);
        panel.revalidate();
        scrollPane.revalidate();
    }

    public void addStructure(JPanel jmd, String title) {
        noOfStructures++;
        jmd.setPreferredSize(jmdDim);
        jmd.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 13), Color.BLUE));

        panel.add(jmd);
        panel.revalidate();
        scrollPane.revalidate();
    }

    class StrucContainer extends JPanel {

        JFrame frame;

        public StrucContainer(JFrame f) {
            super();
            this.frame = f;
            frame.setBackground(Color.lightGray);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        }

        public Dimension getPreferredSize() {
            int width, height;
            width = frame.getContentPane().getSize().width;
            if (width < jmdDim.width) {
                width = jmdDim.width;
            }
            height = ((noOfStructures / ((int) width / jmdDim.width)) + 1) * jmdDim.height;
            height = (int) (height * 1.2); //1.2
            return new Dimension(width, height);
        }

        public Rectangle getBounds() {
            return new Rectangle(new java.awt.Point(0, 0), getPreferredSize());
        }
    }

    /**
     * Action class for the close button of ConnTableFrame
     */
    class closeAction extends AbstractAction {

        JFrame frame;

        closeAction(JFrame f) {
            super("CloseTableFrame");
            this.frame = f;
        }

        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    class SaveMDLAction extends AbstractAction {

        Frame frame;
        String fileToWrite = "";

        SaveMDLAction(Frame frame) {
            super("saveMDL");
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent e) {
            fileToWrite = getFileNameAndVerify();
            SDFWriter writer = null;
            if (!fileToWrite.isEmpty()) {
                try {
                    writer = StructureIO.createSDFWriter(fileToWrite);
                    StructureIO.writeSDF(writer, molecules);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Could not create SDF file");
                }
            }

        }

        private String getFileNameAndVerify() {
            String local = getFileName();
            if (!local.equalsIgnoreCase("nullnull")) {
                return local;
            } else {
                return "";
            }
        }

        private String getFileName() {
            String fileName;
            String dirName;
            FileDialog f = new FileDialog(frame, "Save SDF Files", FileDialog.SAVE);
            f.show();
            fileName = f.getFile();
            dirName = f.getDirectory();
            return dirName + fileName;
        }
    }
}
