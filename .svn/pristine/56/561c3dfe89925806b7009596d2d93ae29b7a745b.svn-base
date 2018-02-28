/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to visualizeAllMoleculesIn molecules
 *
 * @author kalai
 */
public class MoleculeVisualizer {

    StructureImageGenerator imageGenerator = null;
    StrucDispFrame displayFrame = null;
    List<IAtomContainer> molecules = null;
    private static final String ImageIconProperty = "ImageIcon";

    public MoleculeVisualizer() {
        this.imageGenerator = new StructureImageGenerator();

    }

    public void visualizeAllMoleculesIn(String sdfile) throws Exception {
        molecules = StructureIO.readSDF(sdfile);
        displayFrame = new StrucDispFrame();
        Thread queryThread = new Thread() {

            
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    displayStructures();
                    long finishTime = System.currentTimeMillis();
                    System.out.println("Completed display in " + (finishTime - startTime) / 1000 + " secs");
                } catch (Exception ex) {
                }
            }
        };
        queryThread.start();
        displayFrame.pack();
    }

    public void visualize(List<IAtomContainer> molecules) throws Exception {
        this.molecules = molecules;
        displayFrame = new StrucDispFrame();
        Thread queryThread = new Thread() {

            
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    displayStructures();
                    long finishTime = System.currentTimeMillis();
                    System.out.println("Completed display in " + (finishTime - startTime) / 1000 + " secs");
                } catch (Exception ex) {
                }
            }
        };
        queryThread.start();
        displayFrame.pack();
    }

    private void displayStructures() throws Exception {
        for (IAtomContainer molecule : molecules) {
            ImageIcon icon = generateImageIconOf(molecule);
            molecule.setProperty(ImageIconProperty, icon);
        }
    }

    private ImageIcon generateImageIconOf(IAtomContainer molecule) {
        BufferedImage image = null;
        try {
            image = imageGenerator.generateStructureImageWithAtomNumber(molecule, new Dimension(186, 186));
            //System.out.println("generated image..");

        } catch (Exception ex) {
            Logger.getLogger(MoleculeVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ImageIcon(image);
    }

    private void displayStructureOf(final IAtomContainer molecule) {
        SwingUtilities.invokeLater(new Runnable() {

            
            public void run() {
                {
                    JPanel strPanel = new JPanel();
                    strPanel.setBackground(Color.WHITE);
                    ImageIcon icon = (ImageIcon) molecule.getProperty(ImageIconProperty);
                    JLabel strLabel = new JLabel(icon);
                    strPanel.add(strLabel, BorderLayout.CENTER);
                    molecule.removeProperty(ImageIconProperty);
                    displayFrame.addStructure(strPanel, (String) molecule.getProperty(CDKConstants.TITLE), molecule);
                    displayFrame.repaint();
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            new MoleculeVisualizer().visualizeAllMoleculesIn("/Users/kalai/Downloads/C15H20O3/artabsin.sdf");
        } catch (Exception ex) {
            Logger.getLogger(MoleculeVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
