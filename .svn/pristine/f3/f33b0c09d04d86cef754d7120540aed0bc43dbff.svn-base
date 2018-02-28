/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/**
 * Class to load previously serialized and stored hose:shift map
 *
 * @author kalai
 */
public class HOSETable implements Serializable {

    //private static final String SERIALIZED_DATA_FILE = "data/nmrdb-signals-naive.gz";
    private static final String SERIALIZED_DATA_FILE = "data/nmrdb-signals-CL-12-71.gz";
    private static final Logger logger = Logger.getLogger(HOSETable.class);
    private ImmutableList<ImmutableMap<String, Statistics>> immutableHoseStatMapList;
    private int maxSphereSize = 6;
    StringTokenizer stringTokenizer;

    private HOSETable() {
        try {
            generateHOSEShiftTable();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public static class HOSETableHolder {

        private static final HOSETable INSTANCE = new HOSETable();
    }

    public static HOSETable getInstance() {
        return HOSETableHolder.INSTANCE;
    }

    private void generateHOSEShiftTable() throws IOException, ClassNotFoundException {

        System.out.println("Loading lucene index of training shifts...");
        logger.info("Loading lucene index of training shifts...");
        long start = System.currentTimeMillis();

        InputStream in = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(SERIALIZED_DATA_FILE));
        load(in);
        in.close();

        long end = System.currentTimeMillis();
        System.out.println("training data loaded in " + (end - start) + " ms");
        logger.info("Lucene training data loaded in " + (end - start) + " ms");
    }

    private void load(InputStream in) throws IOException {

        DataInput din = new DataInputStream(in);
        List<ImmutableMap<String, Statistics>> localList = new ArrayList<ImmutableMap<String, Statistics>>();

        for (int i = 0; i < 6; i++) {
            int n = din.readInt();
            HashMap<String, Statistics> hoseAndShifts = Maps.newHashMapWithExpectedSize(n);
            for (int j = 0; j < n; j++) {
                String code = din.readUTF();
                double mean = din.readDouble();
                double stdDev = din.readDouble();
                double cl = din.readDouble();
                hoseAndShifts.put(code, new Statistics(mean, stdDev, cl));
            }
            localList.add(i, new ImmutableMap.Builder<String, Statistics>().putAll(hoseAndShifts).build());
            System.out.println("size: " + hoseAndShifts.size());
            logger.info("Training HOSE fragments size: " + hoseAndShifts.size());
        }
        immutableHoseStatMapList = new ImmutableList.Builder<ImmutableMap<String, Statistics>>().addAll(localList).build();
    }

    public Double getShift(String hoseCode) {
        for (int i = immutableHoseStatMapList.size() - 1; i >= 0; i--) {
            ImmutableMap<String, Statistics> map = immutableHoseStatMapList.get(i);
            if (map.containsKey(hoseCode)) {
                return map.get(hoseCode).averageShift;
            }
            hoseCode = getReduced(hoseCode, i);
        }
        return 0d;
    }

    public Double getConfidenceLimit(String hoseCode) {
        for (int i = immutableHoseStatMapList.size() - 1; i >= 0; i--) {
            ImmutableMap<String, Statistics> map = immutableHoseStatMapList.get(i);
            if (map.containsKey(hoseCode)) {
                return map.get(hoseCode).confidenceLimit;
            }
            hoseCode = getReduced(hoseCode, i);
        }
        return 0d;
    }

    public List<Double> getShiftAndConfidenceLimit(String hoseCode) {
        List<Double> values = new ArrayList<Double>();
        for (int i = immutableHoseStatMapList.size() - 1; i >= 0; i--) {
            // HashMap<String, Statistics> map = hoseStatMapList.get(i);
            ImmutableMap<String, Statistics> map = immutableHoseStatMapList.get(i);
            if (map.containsKey(hoseCode)) {
                values.add(map.get(hoseCode).averageShift);
                values.add(map.get(hoseCode).confidenceLimit);
                return values;
            }
            hoseCode = getReduced(hoseCode, i);
        }
        return values;
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

    public static void main(String[] args) {
        HOSETable instance = HOSETable.getInstance();
    }
}
