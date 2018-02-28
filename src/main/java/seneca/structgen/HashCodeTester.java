/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;
import uk.ac.ebi.mdk.prototype.hash.HashGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGeneratorMaker;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class HashCodeTester {

    HashGenerator<Long> hashGenerator = null;

    public HashCodeTester() {
        hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
    }

    public void createHashCode(IAtomContainer molecule) {
        Long generate = hashGenerator.generate(molecule);
        System.out.println("generated: " + generate);
    }

    public static void main(String[] args) {
        try {
            IAtomContainer readMol = StructureIO.readMol("/Users/kalai/Develop/projects/NP-inCASE/secondWave/originals/spone-unknown.mol");
            HashCodeTester tester = new HashCodeTester();
            tester.createHashCode(readMol);

        } catch (Exception ex) {
            Logger.getLogger(HashCodeTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
