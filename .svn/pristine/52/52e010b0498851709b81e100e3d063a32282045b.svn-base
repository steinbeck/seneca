/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.structgen.ea.Individual;
import seneca.structgen.ea.Population;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to visualizeAllMoleculesIn molecules
 *
 * @author kalai
 */
public class PopulationVisualizer {

    StructureImageGenerator imageGenerator = null;
    StrucDispFrame displayFrame = null;
    List<IAtomContainer> molecules = null;
    private static final String ImageIconProperty = "ImageIcon";
    private int generation = 0;

    public PopulationVisualizer() {
        this.imageGenerator = new StructureImageGenerator();
        displayFrame = new StrucDispFrame();
        displayFrame.pack();

    }

    public void visualizeOnTheFly(int generation, Population<Individual> population) throws Exception {
        this.molecules = getMoleculesFrom(population);
        this.generation = generation;
        Thread queryThread = new Thread() {

            
            public void run() {
                try {
                    createImageIcons();
                } catch (Exception ex) {
                }
            }
        };
        queryThread.start();
    }

    private List<IAtomContainer> getMoleculesFrom(Population<Individual> population) {
        List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
        for (Individual individual : population) {
            molecules.add(individual.getMolecule());
        }
        return molecules;
    }

    private void createImageIcons() throws Exception {

        for (int j = 0; j < molecules.size(); j++) {
            System.out.println(j);
            IAtomContainer molecule = (IAtomContainer) molecules.get(j);
            ImageIcon icon = generateImageIconOf(molecule);
            molecule.setProperty(ImageIconProperty, icon);
            display(molecule);
        }
    }

    private ImageIcon generateImageIconOf(IAtomContainer molecule) {
        BufferedImage image = null;
        try {
            if (ConnectivityChecker.isConnected(molecule)) {
                image = imageGenerator.generateStructureImage(molecule, new Dimension(186, 186));
                return new ImageIcon(image);
            }
            //System.out.println("generated image..");

        } catch (Exception ex) {
            Logger.getLogger(PopulationVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void display(final IAtomContainer molecule) {
        SwingUtilities.invokeLater(new Runnable() {

            
            public void run() {
                {
                    JPanel strPanel = new JPanel();
                    strPanel.setBackground(Color.WHITE);
                    ImageIcon icon = (ImageIcon) molecule.getProperty(ImageIconProperty);
                    if (icon != null) {
                        JLabel strLabel = new JLabel(icon);
                        strPanel.add(strLabel, BorderLayout.CENTER);
                        molecule.removeProperty(ImageIconProperty);
                        displayFrame.addStructure(strPanel, generation + "- " + (String) molecule.getProperty("Score"), molecule);
                        displayFrame.repaint();
                    }
                }
            }
        });
    }
}
