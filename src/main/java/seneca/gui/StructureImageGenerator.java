/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.RendererModel.ColorHash;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;

import seneca.core.Utilities;
import seneca.core.CorrectGeometricConfiguration;
import seneca.core.NonplanarBonds;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalai
 */
public class StructureImageGenerator {

    private IAtomContainer molecule = null;
    IRenderer chemicalMoleculeRenderer = null;
    StructureDiagramGenerator structureGenerator = null;
    private static final String SPECIFIC_BONDS = "Color_specific_bonds";
    private static final String COLOR_ALL_BONDS = "Color_all_bonds";


    public StructureImageGenerator() {
        this.structureGenerator = new StructureDiagramGenerator();
        this.chemicalMoleculeRenderer = new AtomContainerRenderer(Arrays.asList(new BasicSceneGenerator(),
	                new BasicBondGenerator(), new BasicAtomGenerator()), new AWTFontManager());
    }

    public BufferedImage generateStructureImage(IAtomContainer molecule, Dimension dimension) throws CDKException {

        //Utilities.calculateProperties(molecule);
        this.molecule = molecule;
        IAtomContainer moleculeWithCoordinates = generateCoordinatesForMolecule();
        assignColors(moleculeWithCoordinates);

        BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Rectangle2D bounds = new Rectangle2D.Double(0, 0,
                image.getWidth(),
                image.getHeight());
        Graphics2D g2 = (Graphics2D) image.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, dimension.width, dimension.height);
        AWTDrawVisitor awtDrawVisitor = new AWTDrawVisitor(g2);
        chemicalMoleculeRenderer.paint(moleculeWithCoordinates, awtDrawVisitor, bounds, true);
        //ImageIO.write((RenderedImage) image, "PNG", new File("/Users/kalai/senecaTest.png"));
        g2.dispose();
        return image;
    }

    public BufferedImage generateStructureImageWithAtomNumber(IAtomContainer molecule, Dimension dimension) throws Exception {

        this.chemicalMoleculeRenderer = new AtomContainerRenderer(Arrays.asList(new BasicSceneGenerator(),
                new BasicBondGenerator(), new BasicAtomGenerator(), new AtomNumberGenerator()), new AWTFontManager());
        RendererModel renderer2DModel = chemicalMoleculeRenderer.getRenderer2DModel();
        renderer2DModel.set(
                AtomNumberGenerator.ColorByType.class,
                true);
        return generateStructureImage(molecule, dimension);
    }

    private IAtomContainer generateCoordinatesForMolecule() throws CDKException {
        structureGenerator.setMolecule(molecule);
        structureGenerator.generateCoordinates();
        IAtomContainer layouted = structureGenerator.getMolecule();
        CorrectGeometricConfiguration.correct(layouted);
        NonplanarBonds.assign(layouted);
        return layouted;

    }

    private void assignColors(IAtomContainer moleculeWithCoordinates) {

        if (moleculeWithCoordinates.getProperty(SPECIFIC_BONDS) != null) {
            Map<IChemObject, Color> bondColorMap = new HashMap<IChemObject, Color>();
            Map<Integer, Color> bondIndexColourMap = (Map<Integer, Color>) moleculeWithCoordinates.getProperty(SPECIFIC_BONDS);
            for (Map.Entry entry : bondIndexColourMap.entrySet()) {
                Integer index = (Integer) entry.getKey();
                Color color = (Color) entry.getValue();
                bondColorMap.put(moleculeWithCoordinates.getBond(index.intValue()), color);
            }
            RendererModel renderer2DModel = chemicalMoleculeRenderer.getRenderer2DModel();
            renderer2DModel.set(ColorHash.class, bondColorMap);
            return;
        }
        if (moleculeWithCoordinates.getProperty(COLOR_ALL_BONDS) != null) {
            RendererModel renderer2DModel = chemicalMoleculeRenderer.getRenderer2DModel();
            renderer2DModel.set(BasicBondGenerator.DefaultBondColor.class, (Color) moleculeWithCoordinates.getProperty(COLOR_ALL_BONDS));
        }
    }
}
