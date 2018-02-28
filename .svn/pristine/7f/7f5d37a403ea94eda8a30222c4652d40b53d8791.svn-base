/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.HOSECodeGenerator;
import seneca.core.StructureIO;
import seneca.gui.Seneca;
import seneca.gui.SenecaParameters;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author kalai
 */
public class HOSEIndex {

    private static final String HOSE = "hose-";
    private static final String SPHERE = "sphere";
    private static final String MEAN_SHIFT = "meanshift";
    private static final String CONFIDENCE_LIMIT = "confidencelimit";
    private static final Logger logger = Logger.getLogger(HOSEIndex.class);
    private int hitsSize = 1;
    private IndexSearcher searcher;
    private Directory directory;
    private IndexReader reader;
    private Analyzer analyzer;
    private int sphereHeight = 4;
    private HOSECodeGenerator hoseGenerator;
    private StringTokenizer stringTokenizer;
    File selectedDir = null;
    File luceneDir = null;

    public HOSEIndex() {
        long start = System.currentTimeMillis();
        try {
            luceneDir = new File(getLuceneDirectory());
            if (!luceneDir.exists()) {
                getLuceneInputFromUser();
            }
            directory = FSDirectory.open(luceneDir);
            reader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(reader);
            analyzer = new KeywordAnalyzer();
            hoseGenerator = new HOSECodeGenerator();
        } catch (IOException ex) {
            System.out.println("exception in lucene index reading");
            logger.error(ex.getMessage());
        }
        long end = System.currentTimeMillis();
        System.out.println("Index loaded in :" + (end - start) + " ms");
    }

    private static String getLuceneDirectory() {
//        String luceneDirectory = System.getProperty("userApp.root") + "Data" + File.separator + "lucene-4";
        String luceneDirectory = System.getProperty("lucene.root");
        logger.info("Lucene directory: " + luceneDirectory);
        return luceneDirectory;
    }

    private void getLuceneInputFromUser() {

        String text = "NMRShiftDB Lucene index for this judge is missing. Please point to lucene dirrectory that is distributed with this package to use this judge";
        JTextArea textArea = new JTextArea(text);
        textArea.setColumns(30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(textArea.getPreferredSize().width, 1);
        textArea.setEditable(false);
        textArea.setBackground((Color) UIManager.get("OptionPane.background"));
        JOptionPane.showMessageDialog(
                null, textArea, "Index Missing!", JOptionPane.INFORMATION_MESSAGE);

        JFileChooser chooser = new JFileChooser(new SenecaParameters().getSenecaFileLocation());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(Seneca.getInstance());
        selectedDir = chooser.getSelectedFile();
        if (selectedDir.isDirectory()) {
            JDialog dialog = new JDialog(Seneca.getInstance(), "Setting up index ...");
            dialog.setPreferredSize(new Dimension(250, 20));
            dialog.setLocationRelativeTo(Seneca.getInstance());
            dialog.pack();
            dialog.setVisible(true);
            try {
                FileUtils.copyDirectory(selectedDir, luceneDir);
                dialog.dispose();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

    }

    public static class HOSEIndexHolder {

        private static final HOSEIndex INSTANCE = new HOSEIndex();
    }

    public static HOSEIndex getInstance() {
        return HOSEIndexHolder.INSTANCE;
    }

    public List<Double> getShiftAndConfidenceLimit(String hose) throws IOException {
        List<Double> shiftAndConfidence = new ArrayList<Double>();
        int currentSphere = sphereHeight;
        for (int i = sphereHeight; i > 0; i--) {

            PhraseQuery query = new PhraseQuery();
            query.add(new Term(HOSE + currentSphere, hose));
            BooleanQuery boolQuery = new BooleanQuery();
            boolQuery.add(query, BooleanClause.Occur.MUST);

            TopDocs topDocs = searcher.search(boolQuery, 1);
            ScoreDoc[] hits = topDocs.scoreDocs;
            //          System.out.println("hits size: " + hits.length);
            if (hits.length == 1) {
                return getValues(hits);
            }
            currentSphere--;
            hose = getReduced(hose, currentSphere);
            //         System.out.println("now will look up in : " + currentSphere);
        }

        return shiftAndConfidence;

    }

    private List<Double> getValues(ScoreDoc[] hits) throws IOException {
        List<Double> shiftAndConfidence = new ArrayList<Double>();
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document document = searcher.doc(docId);
            for (IndexableField field : document.getFields()) {
                shiftAndConfidence.add(field.numericValue().doubleValue());
                //  System.out.println(field.numericValue().doubleValue());
            }
        }
        return shiftAndConfidence;
    }

    private String getReduced(String hoseCode, int specifiedHeight) {
        //System.out.println("reducing to: " + specifiedHeight + "," + hoseCode);
        StringBuilder hoseCodeBuffer = new StringBuilder();
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

    public void printValues(IAtomContainer molecule) throws IOException {
        for (IAtom atom : molecule.atoms()) {
            getShiftAndConfidenceLimit(getHoseCode(atom, molecule));
        }

    }

    private String getHoseCode(IAtom atom, IAtomContainer molecule) {
        String[] splittedHose = null;
        try {
            String hose = hoseGenerator.getHOSECode(molecule, atom, 4, true);
            // The ; removes carbon atom Numbering
            splittedHose = hose.split(";");
        } catch (CDKException ex) {
            logger.info(ex.getMessage());
        }
        // System.out.println(splittedHose[1]);
        return splittedHose[1];
    }

    public static void main(String[] args) {
        try {
            IAtomContainer molecule = StructureIO.readMol("/Users/kalai/alpha.sdf");
            new HOSEIndex().printValues(molecule);

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

    }
}
