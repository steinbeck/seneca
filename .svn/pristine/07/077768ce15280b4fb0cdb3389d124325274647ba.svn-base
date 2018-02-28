/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.predictor.nmrshiftdb;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kalai
 */
public class PredictionTester {

    public String file = "";
    List<IAtomContainer> molecules = null;
    C13ShiftPredictor predictor = null;
    private double[] experimentalShifts = null;
    double[] intensities1 = null;
    double[] intensities2 = null;

    public PredictionTester() {
        predictor = new C13ShiftPredictor();
    }

    private void read(String file) throws Exception {
        molecules = StructureIO.readSDF(file);
        // molecules.add(StructureIO.readMol(file));
    }

    private void goThrough() throws CDKException {
        for (IAtomContainer molecule : molecules) {
            long start = System.currentTimeMillis();
            //                  Double[] predictedShifts = predictor.predictShiftsFor(molecule);
            Double[] retreivedShifts = retreiveShiftsFor(molecule);
            long end = System.currentTimeMillis();
            System.out.println("Time taken: " + (end - start) + "ms");
            setUpExperimentalShiftsAndIntensitiesToCompare(retreivedShifts);
            correlatePredictedAndExperimentalValues(getArrayOf(retreivedShifts));
        }

    }

    public Double[] retreiveShiftsFor(IAtomContainer molecule) throws CDKException {
        predictor = new C13ShiftPredictor();
        return predictor.predictShiftsFor(molecule);
    }

    private void setUpExperimentalShiftsAndIntensitiesToCompare(Double[] shifts) {
        experimentalShifts = new double[shifts.length];
        intensities1 = new double[shifts.length];
        intensities2 = new double[shifts.length];

        experimentalShifts[0] = 116.2;
        experimentalShifts[1] = 144.35;
        experimentalShifts[2] = 47.3;
        experimentalShifts[3] = 38.2;
        experimentalShifts[4] = 41.0;
        experimentalShifts[5] = 31.47;
        experimentalShifts[6] = 31.55;
        experimentalShifts[7] = 22.23;
        experimentalShifts[8] = 26.45;
        experimentalShifts[9] = 21.53;

        for (int k = 0; k < intensities1.length; k++) {
            intensities1[k] = 1.0;

        }
        intensities2 = intensities1;
    }

    private double[] getArrayOf(Double[] shifts) {
        double[] shiftValues = new double[shifts.length];
        for (int i = 0; i < shifts.length; i++) {
            shiftValues[i] = shifts[i];
            System.out.println(i + " - " + shifts[i]);
        }
        print(shiftValues);
        return shiftValues;
    }

    private void correlatePredictedAndExperimentalValues(double[] shifts) {
        double wccValue = WeightedCrossCorrelation.wcc(experimentalShifts, intensities1, shifts, intensities2, 3.0);
        System.out.println("WCC: " + wccValue);
    }

    private void print(double[] predictedShifts) {
        for (int i = 0; i < predictedShifts.length; i++) {
            System.out.println(i + " : " + predictedShifts[i]);
        }
    }

    public static void main(String[] args) {
        try {
            PredictionTester tester = new PredictionTester();
            tester.read("/Users/kalai/alphaPinene.sdf");
            tester.goThrough();
        } catch (Exception ex) {
            Logger.getLogger(PredictionTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
