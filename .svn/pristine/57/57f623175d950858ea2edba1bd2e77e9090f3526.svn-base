/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.mutation;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import seneca.core.StructureIO;
import seneca.gui.StructureImageGenerator;
import seneca.structgen.RandomGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class MutationSphereSelector {

    RandomGenerator randomGenerator = null;

    public IAtomContainer mutate(IAtomContainer molecule) {
        writeImage(molecule, "initial");
        randomGenerator = new RandomGenerator(molecule);
        randomGenerator.mutate(molecule);
        IAtomContainer molecule1 = randomGenerator.getMolecule();
        writeImage(molecule1, "mutated");
        return molecule1;
    }

    public void writeImage(IAtomContainer atomContainer, String title) {
        try {
            if (ConnectivityChecker.isConnected(atomContainer)) {
                BufferedImage image = new StructureImageGenerator().generateStructureImageWithAtomNumber(atomContainer, new Dimension(186, 186));
                String filename = "/Users/kalai/images/mutation/" + title + ".png";
                ImageIO.write((RenderedImage) image, "PNG", new File(filename));

            } else {
                IAtomContainerSet partitionIntoMolecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
                System.out.println("partitioned size - " + partitionIntoMolecules.getAtomContainerCount());
                int count = 1;
                for (IAtomContainer container : partitionIntoMolecules.atomContainers()) {
                    writeImage(container, title + " - " + count);
                    count++;
                }
            }
        } catch (Exception ex) {

        }
    }

    public static void main(String[] args) {
        try {
            IAtomContainer molecule = StructureIO.readMol("/Users/kalai/Desktop/alphaPinene.mol");
            IAtomContainer mutated = new MutationSphereSelector().mutate(molecule);
            List<IAtomContainer> toSee = new ArrayList<IAtomContainer>();
            toSee.add(molecule);
            toSee.add(mutated);
        } catch (Exception ex) {
            Logger.getLogger(MutationSphereSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
