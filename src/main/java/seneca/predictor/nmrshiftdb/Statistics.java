/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb;

/**
 * Utility class to store statistics of a given HOSE-code
 *
 * @author kalai
 */
public class Statistics {

    public double averageShift = 0d;
    public double standardDeviation = 0d;
    public double confidenceLimit = 0d;

    public Statistics(double mean, double stdDeviation) {
        this.averageShift = mean;
        this.standardDeviation = stdDeviation;
    }

    public Statistics(double mean, double stdDeviation, double confidenceLimit) {
        this.averageShift = mean;
        this.standardDeviation = stdDeviation;
        this.confidenceLimit = confidenceLimit;
    }
}
