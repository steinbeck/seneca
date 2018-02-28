/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea.crossover;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;
import seneca.core.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class VicinitySamplerCheck {

    //RandomGenerator randomGenerator = null;

    public VicinitySamplerCheck() {
    }

    public void checkSampler() throws Exception {
        IAtomContainer molecule = StructureIO.readMol("/Users/kalai/Downloads/daunorobicin.mol");
        System.out.println("Atom count: " + molecule.getAtomCount());
        makeIndividuals(molecule, 1000);
    }

    private void makeIndividuals(IAtomContainer molecule, int populationSize) throws Exception {
        //   randomGenerator = new RandomGenerator(molecule);
        List<IAtomContainer> sample = new ArrayList<IAtomContainer>();
        IAtomContainer possible = molecule;
        System.out.println("Making individuals..");
        for (int i = 0; i < populationSize; i++) {
//                  randomGenerator.mutate(possible);
//                  possible = randomGenerator.getMolecule();
            sample.add(Utilities.mutate(possible));
        }
        checkConnectivities(sample);
    }

    public void checkConnectivities(List<IAtomContainer> sample) {
        int notConnected = 0;
        System.out.println("ToTal size : " + sample.size());
        for (IAtomContainer mol : sample) {
            if (!ConnectivityChecker.isConnected(mol)) {
                notConnected++;
            }
        }
        System.out.println("Not connected: " + notConnected);
    }

    public static void main(String[] args) {
        try {
            new VicinitySamplerCheck().checkSampler();
        } catch (Exception ex) {
            Logger.getLogger(VicinitySamplerCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
