/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.similarity.Tanimoto;
import seneca.core.StructureIO;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class SimilarityCalculator {

    Fingerprinter fingerprinter = null;

    public SimilarityCalculator() {
        fingerprinter = new Fingerprinter();
    }

    public float calculateSimilarity(Individual a, Individual b) {
        return calculateSimilarity(a.getMolecule(), b.getMolecule());
    }

    public float calculateSimilarity(IAtomContainer a, IAtomContainer b) {
        float similarity = 0f;
        try {
            similarity = Tanimoto.calculate(getFingerPrint(a), getFingerPrint(b));
            System.out.println("Similarity: " + similarity);
        } catch (CDKException ex) {
            Logger.getLogger(SimilarityCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return similarity;
    }

    public BitSet getFingerPrint(IAtomContainer molecule) {
        BitSet fingerprint = new BitSet();
//            try {
//               fingerprint = fingerprinter.getFingerprint(molecule);
//            } catch (CDKException ex) {
//                  Logger.getLogger(SimilarityCalculator.class.getName()).log(Level.SEVERE, null, ex);
//            }
        return fingerprint;
    }

    public void removeRendundancy(List<IAtomContainer> molecules) {
        List<IAtomContainer> leftOver = new ArrayList<IAtomContainer>();
        List list = new ArrayList();
        for (int i = 0; i < molecules.size(); i++) {
            for (int j = 0; j < molecules.size(); j++) {
                if (i != j) {
                    if (!isNotUnique(list, i)) {
                        if (calculateSimilarity(molecules.get(i), molecules.get(j)) == 1f) {
                            list.add(j);
                        }
                    }
                }
            }
        }
    }

    public boolean isNotUnique(List list, int i) {
        for (int j = 0; j < list.size(); j++) {
            if (list.contains(i)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            IAtomContainer a = StructureIO.readMol("/Users/kalai/Desktop/alphaPinene.mol");
            IAtomContainer b = StructureIO.readMol("/Users/kalai/Desktop/myrtenol.mol");
            new SimilarityCalculator().calculateSimilarity(a, b);
        } catch (Exception ex) {
            Logger.getLogger(SimilarityCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
