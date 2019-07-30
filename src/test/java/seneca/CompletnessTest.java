package seneca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.hash.AtomHashGenerator;
import org.openscience.cdk.hash.HashGeneratorMaker;
import org.openscience.cdk.hash.MoleculeHashGenerator;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.structgen.SingleStructureRandomGenerator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import net.sf.jniinchi.INCHI_RET;

public class CompletnessTest {

	public static void main(String[] args)
	{
		new CompletnessTest().testCompletness();
		/*try {
			new CompletnessTest().generateUniqueSmiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public CompletnessTest() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void testCompletness()
	{	IAtomContainer ac = null;
		HashMap hashmap = new HashMap();
		MoleculeHashGenerator generator = new HashGeneratorMaker().depth(10).elemental().perturbed().molecular();
		SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Unique);
		String hashCode;
		long counter = 0;
		RandomGenerator rg = null;
		String smiles = null;
		int[] hcount = {3,3,3,2,2,1,1,1,0,0}; 
		long startingtime = 0, endtime = 0;
		long maxStrucCount = 1000000;
		try {
			SingleStructureRandomGenerator srg = new SingleStructureRandomGenerator();
			SDFWriter sw = new SDFWriter(new FileWriter("a-pinene-isomers-with-given-h-count.sdf"));
			//SingleStructureRandomGenerator srg = new SingleStructureRandomGenerator();
			SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
			ac = sp.parseSmiles("CC1=CCC2CC1C2(C)C");
			//ac = sp.parseSmiles("CC(CCC=C(C)C)C1CC(C2(C1(CC=C3C2=CCC4C3(CCC(C4(C)C)O)C)C)C)O");
			
			//ac.removeAllBonds();
			//ac = MolecularFormulaManipulator.getAtomContainer("C10", SilentChemObjectBuilder.getInstance());
			
			//for (int f = 0; f < ac.getAtomCount(); f++) ac.getAtom(f).setImplicitHydrogenCount(hcount[f]);
			//srg.setAtomContainer(ac);
			//ac = srg.generate();
			System.out.println("Generating " + maxStrucCount + " isomers of ");
			System.out.println(sg.create(ac));
			rg = new RandomGenerator(ac);
			startingtime = System.currentTimeMillis();
			do {
				rg.proposeStructure();
				//rg.mutate(ac);
				rg.acceptStructure();
				if (ConnectivityChecker.isConnected(ac)) 
				{
					hashCode = sg.create(ac);
					
					//hashCode = generator.generate(ac);
				//hashCode = getInChI(ac);
				//System.out.println(hashCode);
				//sw.write(ac);
					counter++;
					if (!hashmap.containsKey(hashCode))
					{
						hashmap.put(hashCode, hashCode);
						System.out.println(hashmap.size() + " in a total of " + counter);
						
						//sw.write(ac);
					}	
				}	
			}while(hashmap.size() < 4308);
			System.out.println(hashmap.size() + " in a total of " + counter);
			//}while(counter < maxStrucCount);
			//sw.close();
			
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
		endtime = System.currentTimeMillis();
		System.out.println("Duration: " + (endtime - startingtime) + " ms");

		
	}
	
	public void generateUniqueSmiles() throws Exception
	{
		String smiles = null;
		int counter = 0; 
		//File infile = new File("/Users/steinbeck/Downloads/a-pinene-isomers-with-given-h-count.sdf");
		File infile = new File("/Users/steinbeck/Downloads/molgen-pinene-3-2-3-2.sdf");
		FileOutputStream fos = new FileOutputStream("/Users/steinbeck/Downloads/C10H16-isomers-with-degenerate-smiles.sdf");
		SDFWriter writer = new SDFWriter(fos);
		//BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Unique);
		HashMap map = new HashMap();
		IAtomContainer ac = null;
		IAtomContainer duplicate = null;
		
		
		IteratingSDFReader iterator = new IteratingSDFReader(
				new FileReader(infile),
				SilentChemObjectBuilder.getInstance()
				);
		
		while(iterator.hasNext())
		{
			System.out.println(counter++);
			ac = iterator.next();
			smiles = sg.create(ac);
			if(!map.containsKey(smiles)) map.put(smiles, ac);
			else
			{
				duplicate = (IAtomContainer)map.get(smiles);
				ac.setTitle(counter + ": " + smiles);
				duplicate.setTitle(counter + ": " + smiles);
				writer.write(ac);
				writer.write(duplicate);
			}
			
			//bw.write(smiles + "\n");
		}
		//bw.close();
		
		writer.close();
		iterator.close();
		fos.close();
	}
	

}
