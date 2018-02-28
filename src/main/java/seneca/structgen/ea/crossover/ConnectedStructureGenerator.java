/*  $Revision$ $Author$ $Date$    
 *
 *  Copyright (C) 1997-2009  Christoph Steinbeck, Stefan Kuhn <shk3@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Randomly generates a single, connected, correctly bonded structure from a number of fragments.
 * <p>Assign hydrogen counts to each heavy atom. The hydrogens should not be in the atom pool but
 * should be assigned implicitly to the heavy atoms in order to reduce computational cost.
 *
 * @author steinbeck @cdk.created 2001-09-04 @cdk.module structgen @cdk.githash
 */
public class ConnectedStructureGenerator {

    private ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(ConnectedStructureGenerator.class);
    SaturationChecker satCheck;

    /**
     * Constructor for the PartialFilledStructureMerger object.
     */
    public ConnectedStructureGenerator() {
        satCheck = new SaturationChecker();
    }

    /**
     * Randomly generates a single, connected, correctly bonded structure from a number of
     * fragments. IMPORTANT: The AtomContainers in the set must be connected. If an AtomContainer
     * is disconnected, no valid result will be formed
     *
     * @param ac The fragments to generate for.
     * @return The newly formed structure.
     * @throws CDKException No valid result could be formed.
     */
    public IAtomContainer generate(IAtomContainer ac) throws CDKException {
        int iteration = 0;
        boolean structureFound = false;
        System.out.println("Trying to Generate offsprings...");
        do {
            iteration++;
            boolean bondFormed;
            do {
                bondFormed = false;

                for (IAtom atom : ac.atoms()) {
                    if (!satCheck.isSaturated(atom, ac)) {
                        IAtom partner = getAnotherUnsaturatedNode(atom, ac);
                        if (partner != null) {
                            double cmax1 = satCheck.getCurrentMaxBondOrder(atom, ac);
                            double cmax2 = satCheck.getCurrentMaxBondOrder(partner, ac);
                            double max = Math.min(cmax1, cmax2);
                            double order = Math.min(Math.max(1.0, max), 3.0);//(double)Math.round(Math.random() * max)
                            logger.debug("cmax1, cmax2, max, order: " + cmax1 + ", " + cmax2 + ", " + max + ", " + order);
                            ac.addBond(
                                    ac.getBuilder().newInstance(IBond.class,
                                            atom, partner, BondManipulator.createBondOrder(order)));
                            System.out.println("One bond formed !");
                            bondFormed = true;
                        }
                    }
                }

            } while (bondFormed);
            if (satCheck.allSaturated(ac)) {
                structureFound = true;
            }
            System.out.println("Iteration : " + iteration);
        } while (!structureFound && iteration < 30);

        if (!structureFound) {
            throw new CDKException("Could not combine the fragments!!");
        }
        return ac;
    }

    /**
     * Gets a randomly selected unsaturated atom from the set. If there are any, it will be from
     * another container than exclusionAtom.
     *
     * @return The unsaturated atom.
     */

    private IAtom getAnotherUnsaturatedNode(IAtom exclusionAtom, IAtomContainer atomContainer) throws CDKException {

        for (IAtom atom : atomContainer.atoms()) {
            if (!atom.equals(exclusionAtom)) {
                if (!satCheck.isSaturated(atom, atomContainer)) {
                    return atom;
                }
            }
        }
        return null;
    }
}
