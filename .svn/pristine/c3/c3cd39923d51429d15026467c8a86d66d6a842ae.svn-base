/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The parent molecules bond should be colored different (blue and red), children having mix of blue
 * and red bonds.
 *
 * @author kalai
 */
public class CrossOverColorer {

    private static final String SPECIFIC_BONDS = "Color_specific_bonds";
    private static final String COLOR_ALL_BONDS = "Color_all_bonds";
    private static final String BOND_COLOR = "bond_color";

    public CrossOverColorer() {
    }

    public void colorFull(IAtomContainer molecule, Color color) {
        molecule.setProperty(COLOR_ALL_BONDS, color);
    }

    public void colorPartial(IAtomContainer child, Color color) {
        for (IBond bond : child.bonds()) {
            bond.setProperty(BOND_COLOR, color);
        }
    }

    public void determineBondsToColor(IAtomContainer child) {
        Map<Integer, Color> bondColorMap = new HashMap<Integer, Color>();
        for (IBond bond : child.bonds()) {
            if (bond.getProperty(BOND_COLOR) != null) {
                bondColorMap.put(child.getBondNumber(bond), (Color) bond.getProperty(BOND_COLOR));
            } else {
                bondColorMap.put(child.getBondNumber(bond), Color.black);
            }
        }
        child.removeProperty(COLOR_ALL_BONDS);
        child.setProperty(SPECIFIC_BONDS, bondColorMap);
    }
}
