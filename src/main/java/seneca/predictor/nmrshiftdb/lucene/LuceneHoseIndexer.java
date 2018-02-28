/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb.lucene;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import seneca.predictor.nmrshiftdb.Statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to read SDFile from NMRShiftDB and for every "C-atom" make atom Centered HOSE codes and map
 * with its corresponding chemicalSHifts
 *
 * @author kalai
 */
public class LuceneHoseIndexer implements Serializable {

    private static final String HOSE = "hose";
    private static final String SPHERE = "sphere";
    private static final String MEAN_SHIFT = "meanshift";
    private static final String CONFIDENCE_LIMIT = "confidencelimit";
    private static final String SPECTRA_TYPE = "Spectrum 13C";
    private static final String FIRST_SPECTRA = "Spectrum 13C 0";
    private static final String NMR_ID = "nmrshiftdb2 ID";
    private List<Multimap<String, Double>> multiMapList;
    private List<HashMap<String, Statistics>> hoseStatMapList;
    private IteratingSDFReader reader;
    private HOSECodeGenerator hoseGenerator;
    private DecimalFormat formatter = null;
    StringTokenizer stringTokenizer;

    public LuceneHoseIndexer() {
        hoseGenerator = new HOSECodeGenerator();
        formatter = new DecimalFormat("###.##");
    }

    public static void main(String[] args) throws Exception {

        LuceneHoseIndexer shiftDataExtractor = new LuceneHoseIndexer();
        // String inFile = "/Users/kalai/Develop/projects/SpectraPrediction/RecentData/nmrshiftdb2withsignals.sdf";
        String inFile = "/Users/kalai/Develop/projects/SpectraPrediction/RecentData/nmrshiftdb2withsignals.sdf";
        //"/Users/kalai/Develop/projects/SpectraPrediction/nmrshiftdbSmall.sdf";
        String outFile = "/Users/kalai/Develop/projects/SpectraPrediction/RecentData/lucene-4/";
        shiftDataExtractor.extractDataInAndSerializeOut(inFile, outFile);
    }

    public void extractDataInAndSerializeOut(String inFile, String outFile) throws Exception {
        long start = System.currentTimeMillis();
        read(inFile);
        initializeMaps();
        iterateOverMoleculesAndExtractData();
        printMap(multiMapList);
        doStats();
        print(hoseStatMapList);
        indexTheMap(outFile);
        long end = System.currentTimeMillis();
        System.out.println("Finished task in : " + (end - start) + " ms");
    }

    private void read(String sdfFile) {
        try {
            reader = new IteratingSDFReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());
            reader.setSkip(true);
        } catch (IOException ex) {
            Logger.getLogger(LuceneHoseIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeMaps() {
        multiMapList = new ArrayList<Multimap<String, Double>>();
        hoseStatMapList = new ArrayList<HashMap<String, Statistics>>();
        for (int i = 0; i < 4; i++) {
            Multimap<String, Double> multimap = ArrayListMultimap.create();
            HashMap<String, Statistics> hoseStats = Maps.newHashMap();
            multiMapList.add(multimap);
            hoseStatMapList.add(hoseStats);
        }
    }

    private void iterateOverMoleculesAndExtractData() throws CDKException {
        int count = 0;
        int skipped = 0;
        int moleculeWithMoreSpectrum = 0;
        while (reader.hasNext()) {
            try {
                IAtomContainer molecule = (IAtomContainer) reader.next();
                System.out.println(count++);//+ " ID: " + (String) molecule.getProperty(NMR_ID));
//                if (propertiesCalculatedFor(molecule)) { //Commented out since the method was not found. Probably an artifact
                if (true)
                {
                    Map<Object, Object> allSpectraProperties = getAllAvailableSpectralPropertiesIn(molecule);

                    for (Object spectrum : allSpectraProperties.keySet()) {
                        HashMap<Integer, Double> atomIDAndShiftMap = mapAtomIDAndShiftFrom(molecule, (String) spectrum);
                        if (!atomIDAndShiftMap.isEmpty()) {
                            generateHosesForCarbonAtomsAndMap(molecule, atomIDAndShiftMap);
                        } else {
                            skipped++;
                        }
                    }
                    if (allSpectraProperties.size() > 1) {
                        moleculeWithMoreSpectrum++;
                    }
                }
            } catch (NumberFormatException nfe) {
            }
        }
        System.out.println("skipped size - " + skipped);
        System.out.println("Molecule with more spectrum: " + moleculeWithMoreSpectrum);
    }
    

    public static void addImplicitHydrogensToSatisfyValency(IAtomContainer mol) {

        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        for (IAtom atom : mol.atoms()) {
            try {
                IAtomType type = matcher.findMatchingAtomType(mol, atom);
                if (type != null) {
                    AtomTypeManipulator.configure(atom, type);
                    hAdder.addImplicitHydrogens(mol, atom);
                }
            } catch (CDKException ex) {
                continue;
                //Logger.getLogger(ShiftDataExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Map<Object, Object> getAllAvailableSpectralPropertiesIn(IAtomContainer molecule) {
        Map<Object, Object> properties = molecule.getProperties();
        Map<Object, Object> onlySpectrum = new HashMap<Object, Object>();
        for (Entry e : properties.entrySet()) {
            String key = (String) e.getKey();
            if (key.contains(SPECTRA_TYPE)) {
                onlySpectrum.put(key, e.getValue());
            }
        }
        return onlySpectrum;
    }

    private HashMap<Integer, Double> mapAtomIDAndShiftFrom(IAtomContainer molecule, String spectra) {

        HashMap<Integer, Double> atomIDToShiftMap = new HashMap<Integer, Double>();
        String shiftList = (String) molecule.getProperty(spectra);
        if (!shiftList.isEmpty()) {
            String[] trimList = shiftList.split("\\|");
            for (String trim : trimList) {
                String[] shift_AtomID = trim.split(";");
                if (shift_AtomID.length == 3) {
                    String shiftValue = shift_AtomID[0];
                    String atomID = shift_AtomID[2];
                    //System.out.println("atom id: " + atomID + " - shift: " + shiftValue);
                    atomIDToShiftMap.put(Integer.parseInt(atomID), Double.parseDouble(shiftValue));
                }
            }
            return atomIDToShiftMap;
        }
        return atomIDToShiftMap;
    }

    private void generateHosesForCarbonAtomsAndMap(IAtomContainer molecule, HashMap<Integer, Double> atomIDAndShiftMap) {
        int atomCount = 0;
        //  System.out.println("start----");
        for (IAtom atom : molecule.atoms()) {
            for (Integer atomID : atomIDAndShiftMap.keySet()) {
                if (atomID.equals(atomCount)) {
                    Double shiftValue = atomIDAndShiftMap.get(atomID);
                    String hoseCode = getHoseCode(atom, molecule);
                    //System.out.println(atomID + "---" + hoseCode + ":" + shiftValue);
                    addToSuitableMap(shiftValue, hoseCode);
                }
            }
            atomCount++;
        }
        //    System.out.println("END----");

    }

    private String getHoseCode(IAtom atom, IAtomContainer molecule) {
        String[] splittedHose = null;
        try {
            String hose = hoseGenerator.getHOSECode(molecule, atom, 6, true);
            // The ; removes carbon atom Numbering
            splittedHose = hose.split(";");
        } catch (CDKException ex) {
            Logger.getLogger(LuceneHoseIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return splittedHose[1];
    }

    private void addToSuitableMap(Double shiftValue, String hoseCode) {
        for (int i = 3; i >= 0; i--) {
            int height = i + 1;
            String reducedCode = getReduced(hoseCode, height);
            multiMapList.get(i).put(reducedCode, Double.parseDouble(formatter.format(shiftValue)));
        }
    }

    private String getReduced(String hoseCode, int specifiedHeight) {
        StringBuffer hoseCodeBuffer = new StringBuffer();
        stringTokenizer = new StringTokenizer(hoseCode, "()/");
        for (int k = 0; k < specifiedHeight; k++) {
            if (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                hoseCodeBuffer.append(token);
            }
            if (k == 0) {
                hoseCodeBuffer.append("(");
            } else if (k == 3) {
                hoseCodeBuffer.append(")");
            } else {
                hoseCodeBuffer.append("/");
            }
        }
        return hoseCodeBuffer.toString();
    }

    private void doStats() throws Exception {
        for (int i = 0; i < multiMapList.size(); i++) {
            Multimap<String, Double> multimap = multiMapList.get(i);
            for (String hose : multimap.keySet()) {
                Collection<Double> shifts = multimap.get(hose);
                Double mean = getMeanValue(shifts);
                Double standardDeviation = getStandardDeviation(shifts, mean);
                Double confidenceLimit = getConfidenceLimit(standardDeviation, shifts.size());
                // System.out.println("STAT: mean=" + mean + ", std=" + standardDeviation + ",CL=" + confidenceLimit + ", sampleSize=" + shifts.size());
                hoseStatMapList.get(i).put(hose, new Statistics(mean, standardDeviation, Double.parseDouble(formatter.format(confidenceLimit))));
            }
        }
    }

    private Double getMeanValue(Collection<Double> shiftValues) {
        Double sum = 0d;
        for (Double shift : shiftValues) {
            sum += shift;
        }
        return sum / shiftValues.size();
    }

    private Double getVarianceOf(Collection<Double> shiftValues, Double mean) {
        Double variance = 0.0;
        for (Double shift : shiftValues) {
            Double differenceSqaured = (shift - mean) * (shift - mean);
            variance += differenceSqaured;
        }
        return variance / shiftValues.size();
    }

    private Double getStandardDeviation(Collection<Double> shiftValues, Double mean) {
        Double variance = getVarianceOf(shiftValues, mean);
        return Math.sqrt(variance);
    }

    private Double getConfidenceLimit(Double standardDeviation, int sampleSize) {
        /**
         * Student t-distribution for two-sided 95% confidence limit is 12.71
         */
        Double t_distribution = 12.71;
        Double standardError = standardDeviation / Math.sqrt(sampleSize);
        Double confidenceLimit = t_distribution * standardError;
        return confidenceLimit < 5d ? 5d : confidenceLimit;
    }

    private void print(List<HashMap<String, Statistics>> hoseStatMapList) {
        System.out.println("MAP WITH STATS");
        for (HashMap<String, Statistics> map : hoseStatMapList) {
            System.out.println("Map size: " + map.keySet().size());
//                  for (Entry<String, Statistics> entry : map.entrySet()) {
//                        System.out.println(entry.getKey() + " : " + entry.getValue().averageShift + "," + entry.getValue().standardDeviation);
//                  }
        }
    }

    private void printMap(List<Multimap<String, Double>> multiMapList) {
        System.out.println("INITIAL MAP");
        for (Multimap<String, Double> map : multiMapList) {
            System.out.println("Map size: " + map.keySet().size());
//                  for (String code : map.keySet()) {
//                        Collection<Double> shifts = map.get(code);
//                        System.out.println(code + " ; " + shifts.toString());
//                  }

        }

    }

    private void indexTheMap(String indexPath) throws IOException {

        Analyzer analyzer = new KeywordAnalyzer();
        IndexWriterConfig indexWriterConfiguration = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        Directory directory = FSDirectory.open(new File(indexPath));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfiguration);
        int hoseSphere = 1;
        int totalDocsAdded = 0;

        for (HashMap<String, Statistics> map : hoseStatMapList) {
            for (Entry<String, Statistics> entry : map.entrySet()) {
                index(entry, indexWriter, hoseSphere);
                totalDocsAdded++;
            }
            hoseSphere++;
        }
        System.out.println("total added: " + totalDocsAdded);
        indexWriter.close();

    }

    private void index(Entry<String, Statistics> entry, IndexWriter indexWriter, int hoseSphere) throws IOException {
        String hose = entry.getKey();
        Statistics stats = entry.getValue();
        Document doc = new Document();
        doc.add(new StringField(HOSE + "-" + hoseSphere, hose, Field.Store.NO));
        doc.add(new DoubleField(MEAN_SHIFT, stats.averageShift, Field.Store.YES));
        doc.add(new DoubleField(CONFIDENCE_LIMIT, stats.confidenceLimit, Field.Store.YES));
        System.out.println("indexing doc : " + doc.toString());
        indexWriter.addDocument(doc);
    }
}
