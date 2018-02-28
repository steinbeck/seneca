/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.ea;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGeneratorMaker;

import java.util.Map;

/**
 * @author kalai
 */
public class OffSpringProducer {

    //  private RandomGenerator randomGenerator = null;
    private HashGenerator<Long> hashGenerator = null;
    private static final Logger logger = Logger.getLogger(OffSpringProducer.class);
    private RandomGenerator randomGenerator = new RandomGenerator(null);

    public OffSpringProducer() {
        //   randomGenerator = new RandomGenerator(null);
        hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
    }

    public Population produceExactOffspringsFrom(Population<Individual> selectedParents) {
        Population<Individual> offSpringsWithParents = new Population<Individual>();
        for (Individual parent : selectedParents) {
            offSpringsWithParents.add(parent);
            offSpringsWithParents.add(mutateOneMoreOffspringFrom(parent));
        }
        return offSpringsWithParents;
    }

    public Individual mutateOneMoreOffspringFrom(Individual individual) {
        Individual newIndividual = null; //(IAtomContainer) individual.getMolecule().clone();
        try {
            IAtomContainer toMutate = (IAtomContainer) individual.getMolecule().clone();
            randomGenerator.setMolecule(toMutate);
            randomGenerator.mutate(toMutate);
            IAtomContainer mutatedMolecule = randomGenerator.getMolecule();
            //IAtomContainer mutatedMolecule = Utilities.mutate(toMutate);
            newIndividual = new Individual(mutatedMolecule);
            newIndividual.setHashCode(hashGenerator.generate(mutatedMolecule));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (NullPointerException e) {
            logger.error(e);
            newIndividual.setHashCode(new Long(0));
        } catch (Exception e) {
            logger.error(e);
            return individual;
        }
        return newIndividual;
    }

    private void printOutOffsprings(Population<Individual> addedOffSprings) {
        System.out.println("-------off springs-------");
        for (Individual ind : addedOffSprings) {
            IAtomContainer mole = ind.getMolecule();
            //  System.out.println(gen.createSMILES(mole) + " - " + ind.getFitness());
            System.out.println("fitness - " + ind.getFitness());
        }

    }

    private void printMoleculeProperties(IAtomContainer molecule) {
        Map<Object, Object> properties = molecule.getProperties();
        System.out.println("Properties size : " + properties.size());
        for (Map.Entry e : properties.entrySet()) {
            System.out.println("Key:" + e.getKey());
            System.out.println("Value:" + e.getValue());
        }
    }
}
