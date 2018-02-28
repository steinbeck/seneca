package seneca.gui.actions;

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.compute.Compute;
import seneca.gui.AbstractPanel;
import seneca.gui.StrucDispFrame;
import seneca.gui.StructureImageGenerator;
import seneca.structgen.StructureGeneratorResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * @author kalai
 */
public class SummariseElucidatedResultsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final AbstractPanel structureGenerationServerPanel;
    private StructureImageGenerator imageGenerator;
    StructureGeneratorResult structureGeneratorResult = null;
    StrucDispFrame displayFrame = null;
    int count = 1;
    private static final String IMAGE_ICON = "ImageIcon";
    public static final Logger logger = Logger.getLogger(SummariseElucidatedResultsAction.class);

    public SummariseElucidatedResultsAction(AbstractPanel structureGenerationServerPanel) {
        super("Show Current");
        this.structureGenerationServerPanel = structureGenerationServerPanel;
        this.imageGenerator = new StructureImageGenerator();
    }

    
    public void actionPerformed(ActionEvent e) {
        count = 1;
        displayFrame = new StrucDispFrame();
        displayFrame.setPreferredSize(new Dimension(1024, 768));
        Thread queryThread = new Thread() {
            
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    extractResultandDisplay();
                    long finishTime = System.currentTimeMillis();
                    System.out.println("Completed display in " + (finishTime - startTime) / 1000 + " secs");
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        };
        queryThread.start();
        displayFrame.pack();
    }

    private void extractResultandDisplay() throws Exception {

        extractStructureGenerationResult();
        createImageIcons();
    }

    private void extractStructureGenerationResult() throws Exception {
        this.structureGeneratorResult = new StructureGeneratorResult(this.structureGenerationServerPanel.sgServerProcesses.size() * 30);
        for (int i = 0; i < this.structureGenerationServerPanel.sgServerProcesses.size(); i++) {
            Compute sgi = (Compute) this.structureGenerationServerPanel.sgServerProcesses.get(i);
            StructureGeneratorResult result = (StructureGeneratorResult) sgi.getTaskResult();
            if (result != null) {
                int members = result.structures.size();
                System.out.println("members size in extractStrREsult = " + members);
                logger.info("Total entries in summary = " + members);
                for (int f = 0; f < members; f++) {
                    structureGeneratorResult.structures.push(result.structures.get(f));
                }
            }
        }
        structureGeneratorResult.removeIsomorphism();
        structureGeneratorResult.sortByCostValue();
    }

    private void createImageIcons() throws Exception {

        int totalStructures = structureGeneratorResult.size();
        System.out.println("Structures to display = " + totalStructures);
        logger.info("Structures to display = " + totalStructures);
        int soFarDisplayed = 0;
        for (int j = 0; j < totalStructures; j++) {
            if (soFarDisplayed >= 30) {
                return;
            }
            System.out.println(j);
            IAtomContainer molecule = (IAtomContainer) structureGeneratorResult.structures.elementAt(j);
            if (ConnectivityChecker.isConnected(molecule)) {
                ImageIcon icon = generateImageIconOf(molecule);
                molecule.setProperty(IMAGE_ICON, icon);
                display(molecule);
                soFarDisplayed++;
            }
        }
    }

    private ImageIcon generateImageIconOf(IAtomContainer molecule) {
        BufferedImage image = null;
        try {
            image = imageGenerator.generateStructureImage(molecule, new Dimension(186, 186));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return new ImageIcon(image);
    }

    private void display(final IAtomContainer molecule) {
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                {
                    JPanel strPanel = new JPanel();
                    strPanel.setBackground(Color.WHITE);
                    ImageIcon icon = (ImageIcon) molecule.getProperty(IMAGE_ICON);
                    if (icon != null) {
                        JLabel strLabel = new JLabel(icon);
                        strPanel.add(strLabel, BorderLayout.CENTER);
                        molecule.removeProperty(IMAGE_ICON);
                        displayFrame.addStructure(strPanel, count + "- Score:" + molecule.getProperty("Score").toString(), molecule);
                        displayFrame.repaint();
                        count++;
                    }
                }
            }
        });
    }

}
