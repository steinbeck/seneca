/* AnnealingLog.java
 *
 * Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This software is published and distributed under artistic license.
 * The intent of this license is to state the conditions under which this Package 
 * may be copied, such that the Copyright Holder maintains some semblance
 * of artistic control over the development of the package, while giving the 
 * users of the package the right to use and distribute the Package in a
 * more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * The complete text of the license can be found in a file called LICENSE 
 * accompanying this package.
 */

package seneca.structgen.annealinglog;

/**
 * AnnealingLog records the development of the overall score
 * and the temperature over time (iteration steps, i.e.)
 */

public class CommonAnnealingLog implements java.io.Serializable, Cloneable {
    int startsize = 100;
    int growsize = 100;
    int currentsize = startsize;
    int seriesCount = 1;
    int typeCount = 2;

    Double[][][] values = new Double[seriesCount][currentsize][typeCount + 1];

    static int SCORES = 0;
    static int TEMPERATURES = 1;
    static int ITERATION = 0;
    static int Y = 1;

    int[] itemCount;

    int iterationAxisStepsize = 500;

    public CommonAnnealingLog() {
        this(1, 2);
    }


    /**
     * Initializes a new Annealing Log.
     * This class can store different types of information (temperature, score, etc.)
     * for a number of different series (different StochasticGenerators).
     * The regular log of one Generator will obviously have only one series
     * but if used in the seneca client it can store the data from many servers.
     *
     * @param seriesCount The number of series (number of generators)
     * @param typeCount   The number of types of information (excluding x axis type)
     */
    public CommonAnnealingLog(int seriesCount, int typeCount) {
        this.seriesCount = seriesCount;
        this.typeCount = typeCount;
        values = new Double[seriesCount][currentsize][typeCount + 1];
        itemCount = new int[seriesCount];
    }


    public void addEntry(double temperature, double score) {
        addEntry((double) (itemCount[0] * iterationAxisStepsize), temperature, score);
    }

    public void addEntry(double iteration, double temperature, double score) {
        addEntry(new Double(iteration), new Double(temperature), new Double(score));
    }

    public void addEntry(Double iteration, Double temperature, Double score) {
        Double[] entries = {iteration, score, temperature};
        addEntry(0, itemCount[0], entries);
    }

    public void addEntry(int series, int item, Double[] entries) {
        if (item >= currentsize) {
            growArrays();
        }
        for (int f = 0; f < entries.length; f++) {
            values[series][item][f] = entries[f];
        }
        itemCount[series]++;
    }

    public Double[] getEntry(int series, int thisEntry) {
        Double[] entry = new Double[typeCount + 1];
        for (int f = 0; f < typeCount + 1; f++) {
            entry[f] = values[series][thisEntry][f];
        }
        return entry;
    }


    void growArrays() {
        int oldsize = currentsize;
        currentsize += growsize;
        Double[][][] newValues = new Double[seriesCount][currentsize][typeCount + 1];
        for (int x = 0; x < seriesCount; x++) {
            for (int y = 0; y < oldsize; y++) {
                for (int z = 0; z < typeCount + 1; z++) {
                    newValues[x][y][z] = values[x][y][z];
                }
            }
        }
        values = newValues;
    }

    public Double[][][] getValues() {
        Double[][][] retvalues = new Double[seriesCount][maxItemCount()][typeCount + 1];
        for (int x = 0; x < seriesCount; x++) {
            for (int y = 0; y < itemCount[x]; y++) {
                for (int z = 0; z < typeCount + 1; z++) {
                    retvalues[x][y][z] = values[x][y][z];
                }
            }
        }

        return retvalues;
    }

    public int getGrowsize() {
        return this.growsize;
    }

    public void setGrowsize(int growsize) {
        this.growsize = growsize;
    }


    public int getStartsize() {
        return this.startsize;
    }

    public void setStartsize(int startsize) {
        this.startsize = startsize;
    }


    public int getIterationAxisStepsize() {
        return this.iterationAxisStepsize;
    }

    public void setIterationAxisStepsize(int iterationAxisStepsize) {
        this.iterationAxisStepsize = iterationAxisStepsize;
    }

    public int getTotalEntriesCountIn(int series) {
        return itemCount[series];
    }

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("MyObject can't clone");
        }
        return o;
    }


    public int getSeriesCount() {
        return this.seriesCount;
    }

    public void setSeriesCount(int seriesCount) {
        this.seriesCount = seriesCount;
    }


    public int getTypeCount() {
        return this.typeCount;
    }

    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }

    public int maxItemCount() {
        int max = 0;
        for (int f = 0; f < itemCount.length; f++) {
            if (itemCount[f] > max) {
                max = itemCount[f];
            }
        }
        return max;
    }

    public String toString() {
        String s = "Annealing Log -> ";
        for (int x = 0; x < seriesCount; x++) {
            s += "Series " + x + ": ";
            for (int y = 0; y < itemCount[x]; y++) {
                s += y + "[ ";
                for (int z = 0; z < typeCount + 1; z++) {
                    s += values[x][y][z] + " ";

                }
                s += "] ";
            }
            s += "\n";
        }
        return s;
    }
}
