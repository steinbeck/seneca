package seneca.structgen.ea;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;
import seneca.core.Utilities;
import uk.ac.ebi.mdk.prototype.hash.HashGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGeneratorMaker;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 30/11/2012
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
/*
 class to explore concentric layers of structural space of a given molecule.
 */
public class ConcentricNicheExpander {

    private HashGenerator<Long> hashGenerator = null;
    private Random random = null;

    public ConcentricNicheExpander() {
        hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
        random = new Random();
    }


    public void expand(List<List<IAtomContainer>> moleculeList, int nSpheres) {
        if (moleculeList.size() == 0) {
            return;
        }
        List<IAtomContainer> lastSphere = moleculeList.get(moleculeList.size() - 1);
        List<IAtomContainer> nextSphereMolecules = new ArrayList<IAtomContainer>();
        System.out.println("last sphere:" + lastSphere.size());
        for (IAtomContainer atomContainer : lastSphere) {
            List<IAtomContainer> mols = InternalVicinitySampler.sample(atomContainer);
            //  System.out.println("mols size:" + mols.size());
            nextSphereMolecules.addAll(mols);
        }
        moleculeList.add(nextSphereMolecules);
        if (moleculeList.size() <= nSpheres) {
            expand(moleculeList, nSpheres);
        }

    }

    public Population<Individual> expand(Individual individual, int toSize) {
        int sphereSize = 0;
        List<List<IAtomContainer>> bigList = new ArrayList<List<IAtomContainer>>();
        List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
        molecules.add(individual.getMolecule());
        bigList.add(molecules);
        List<IAtomContainer> sample = bigList.get(bigList.size() - 1);
        while (sample.size() < toSize) {
            sphereSize++;
            expand(bigList, sphereSize);
            sample = bigList.get(bigList.size() - 1);
            if (sphereSize == 2) {
                System.out.println("SECOND TRIAL");
            }
        }


        Utilities.removeRedundancy(sample);
        System.out.println("Expanding concentric: " + sample.size());

        return selectRandomMoleculesAndMakeNewIndividualsUsing(sample, toSize);
    }


    private Population<Individual> selectRandomMoleculesAndMakeNewIndividualsUsing(List<IAtomContainer> sample, int toSize) {
        int[] randomIndices = getRandomIndices(toSize, sample.size());
        Population<Individual> sampleIndividuals = new Population<Individual>();
        for (int i = 0; i < randomIndices.length; i++) {
            IAtomContainer moleculeInVicinity = sample.get(randomIndices[i]);
            Individual individual = new Individual(moleculeInVicinity);
            individual.setHashCode(hashGenerator.generate(moleculeInVicinity));
            sampleIndividuals.add(individual);
        }
        System.out.println("sampled individuals: " + sampleIndividuals.size());
        return sampleIndividuals;
    }

    private int[] getRandomIndices(int wanted, int totalSize) {
        int[] result = new int[wanted];
        Set<Integer> used = new HashSet<Integer>();

        for (int i = 0; i < wanted; i++) {
            int newRandom;
            do {
                newRandom = random.nextInt(totalSize);
            } while (used.contains(newRandom));
            result[i] = newRandom;
            used.add(newRandom);
        }
        return result;
    }

    public static void main(String[] args) {

        try {
            ConcentricNicheExpander expander = new ConcentricNicheExpander();
            IAtomContainer molecule = StructureIO.readMol("/Users/kalai/Desktop/festuclavine.mol");
            List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
            molecules.add(molecule);
            List<List<IAtomContainer>> bigList = new ArrayList<List<IAtomContainer>>();
            bigList.add(molecules);
            expander.expand(bigList, 2);
            for (int i = 0; i < bigList.size(); i++) {
                System.out.println("sphere size" + bigList.get(i).size());
                System.out.println("after removing redundancy:" + Utilities.removeRedundancy(bigList.get(i)).size());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
