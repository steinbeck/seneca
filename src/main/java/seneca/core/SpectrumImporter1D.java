/*
 *  SpectrumImporter1D2D.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.core;

import java.util.StringTokenizer;
import org.openscience.spectra.model.NMRSpectrum;
import org.openscience.spectra.model.NMRSignal;

/**
 * Importer tool class for clipboard and file-based peak data
 *
 * @author steinbeck
 * @created 10. September 2004
 */
public class SpectrumImporter1D {
    /**
     * Constructor for the SpectrumImporter1D object
     */
    public SpectrumImporter1D() {
    }

    /**
     * Imports a 1D peak list from what we call a simple list. The list has the
     * format shift;intensity, one pair per line There should be no invalid
     * lines before and after the shift values.
     *
     * @param s           A string with the data to be parsed into an NMRSpectrum
     * @param nmrSpectrum A prepare NMRSpectrum, initialized with the correct nucleus,
     *                    and dimensionality
     * @return Description of the Return Value
     */
    public static NMRSpectrum importSimpleList1D(String s,
                                                 NMRSpectrum nmrSpectrum) {
        float ppm;
        float procInt;
        float[] shifts;
        NMRSignal cnmrsig;
        String tokenString = null;
        int phase = 0;
        StringTokenizer strTok = new StringTokenizer(s, "; \n");
        // StringTokenizer strTok = new StringTokenizer(s);
        do {
            // chemical shift;
            tokenString = strTok.nextToken().trim();
            System.out.println("Next Token: " + tokenString);
            ppm = Float.parseFloat(tokenString);
            // intensity;
            tokenString = strTok.nextToken().trim();
            System.out.println("Next Token: " + tokenString);
            procInt = new Float(tokenString).floatValue();
            shifts = new float[1];
            shifts[0] = ppm;
            if (procInt > 0) {
                phase = NMRSignal.PHASE_POSITIVE;
            }
            if (procInt < 0) {
                phase = NMRSignal.PHASE_NEGATIVE;
            }

            cnmrsig = new NMRSignal(nmrSpectrum.nucleus, shifts, procInt, phase);
            nmrSpectrum.addSignal(cnmrsig);
        } while (strTok.hasMoreTokens());
        return nmrSpectrum;
    }

    public static NMRSpectrum importShifts1D(String s,
                                             NMRSpectrum nmrSpectrum) {
        float ppm;
        float procInt;
        float[] shifts;
        NMRSignal cnmrsig;
        String tokenString = null;
        int phase = 0;
        StringTokenizer strTok = new StringTokenizer(s, "\n");
        // StringTokenizer strTok = new StringTokenizer(s);
        do {
            // chemical shift;
            tokenString = strTok.nextToken().trim();
            System.out.println("Next Token: " + tokenString);
            ppm = Float.parseFloat(tokenString);
            shifts = new float[1];
            shifts[0] = ppm;
            // intensity;

            procInt = new Float(1.0);
            phase = NMRSignal.PHASE_POSITIVE;
            cnmrsig = new NMRSignal(nmrSpectrum.nucleus, shifts, procInt, phase);
            nmrSpectrum.addSignal(cnmrsig);
        } while (strTok.hasMoreTokens());
        return nmrSpectrum;
    }

    /**
     * Imports a 1D peak list produced by WinNMR from the Clipboard
     *
     * @param s           A string with the data to be parsed into an NMRSpectrum
     * @param nmrSpectrum A prepare NMRSpectrum, initialized with the correct nucleus,
     *                    and dimensionality
     * @return Description of the Return Value
     */
    public static NMRSpectrum importWinNMR1D(String s, NMRSpectrum nmrSpectrum) {
        float ppm;
        float procInt;
        float[] shifts;
        NMRSignal cnmrsig;
        int phase = 0;
        s = s.substring(s.indexOf("Peak Picking results:"));
        s = s.substring(s.indexOf("1"));
        StringTokenizer strTok = new StringTokenizer(s);
        do {
            // number of peak
            strTok.nextToken();
            // data point;
            strTok.nextToken();
            // frequency;
            strTok.nextToken();
            // chemical shift;
            ppm = Float.parseFloat(strTok.nextToken().trim());
            // intensity;
            strTok.nextToken();
            // percent intensity;

            procInt = new Float(strTok.nextToken().trim()).floatValue();
            shifts = new float[1];
            shifts[0] = ppm;
            if (procInt > 0) {
                phase = NMRSignal.PHASE_POSITIVE;
            }
            if (procInt < 0) {
                phase = NMRSignal.PHASE_NEGATIVE;
            }

            cnmrsig = new NMRSignal(nmrSpectrum.nucleus, shifts, procInt, phase);
            nmrSpectrum.addSignal(cnmrsig);
        } while (strTok.hasMoreTokens());
        return nmrSpectrum;
    }

    /**
     * Imports a 1D peak list produced by WinNMR from the Clipboard
     *
     * @param s           A sing with
     * @param nmrSpectrum Description of the Parameter
     * @return Description of the Return Value
     */
    public static NMRSpectrum importMestrec1D(String s, NMRSpectrum nmrSpectrum) {
        float ppm;
        float procInt;
        float[] shifts;
        String temp = null;
        NMRSignal cnmrsig;
        int phase = 0;
        s = s.substring(s.indexOf("*TO*"));
        s = s.substring(s.indexOf("------"));
        s = s.substring(s.indexOf("1"));
        StringTokenizer strTok = new StringTokenizer(s);
        do {
            // number of peak
            temp = strTok.nextToken();
            System.out.println("peak number: " + temp);
            // data point;
            temp = strTok.nextToken();
            System.out.println("data point: " + temp);
            // chemical shift;
            temp = strTok.nextToken();
            System.out.println("ppm: " + temp);
            ppm = Float.parseFloat(temp.trim());
            // Frequenzy in Hz;
            temp = strTok.nextToken();
            System.out.println("peak number: " + temp);
            // intensity;
            temp = strTok.nextToken();
            System.out.println("intensity: " + temp);
            procInt = new Float(temp.trim()).floatValue();
            shifts = new float[1];
            shifts[0] = ppm;
            if (procInt > 0) {
                phase = NMRSignal.PHASE_POSITIVE;
            }
            if (procInt < 0) {
                phase = NMRSignal.PHASE_NEGATIVE;
            }

            cnmrsig = new NMRSignal(nmrSpectrum.nucleus, shifts, procInt, phase);
            nmrSpectrum.addSignal(cnmrsig);
        } while (strTok.hasMoreTokens());
        return nmrSpectrum;
    }

}
