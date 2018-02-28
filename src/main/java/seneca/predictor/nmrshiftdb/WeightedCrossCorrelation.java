/* Copyright (C) 2006  Ron Wehrens <r.wehrens@science.ru.nl>
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
package seneca.predictor.nmrshiftdb;

public class WeightedCrossCorrelation {

    public static double wcc(double[] positions1, double[] intensities1,
                             double[] positions2, double[] intensities2,
                             double width) {
//		 WCC is de wcccor op X,Y gedeeld door de wortel uit het
//		 product van de waccor op X en Y...WCC is wcccor X, Y divided by the square root of the
// Product of X and Y. .. waccor
        double finalScore = wcccor(positions1, intensities1, positions2, intensities2, width)
                / Math.sqrt(waccor(positions1, intensities1, width) * waccor(positions2, intensities2, width));
        //* System.out.println("Final score = " +finalScore );
//        return wcccor(positions1, intensities1, positions2, intensities2, width)
//                / Math.sqrt(waccor(positions1, intensities1, width) * waccor(positions2, intensities2, width));

        return finalScore;
    }
//DENOMINATOR

    public static double waccor(double[] positions, double[] intensities, double width) {
        //*  System.out.println("Denominator .......... ");
        int n = positions.length;
        int i, j;
        double sum = 0.0, dif;

        for (i = 0; i < (n - 1); i++) {
            for (j = (i + 1); j < n; j++) {
                //*  System.out.println("Math.abs(positions["+j+"] - positions["+i+"]) - > " + positions[j] + "-" + positions[i] + " -- >" + Math.abs(positions[j] - positions[i]));
                dif = Math.abs(positions[j] - positions[i]); /* check distance */
                if (dif < width) { /* close */
                    //*  System.out.println("Difference is less than width " + width);
                    sum += intensities[i] * intensities[j] * (1.0 - (dif / width));
                    //* System.out.println("sum += intensities["+i+"]" +"* intensities["+j+"] * (1.0 - (dif / width)) = " + intensities[i] * intensities[j] * (1.0 - (dif / width)));
                }
            }
        }

        sum = 2.0 * sum;
        for (i = 0; i < n; i++) {
            sum += intensities[i] * intensities[i];
        }
        //* System.out.println("Final denominator sum = " +  sum);

        return (sum);
    }
// NUMERATOR

    public static double wcccor(double[] positions1, double[] intensities1,
                                double[] positions2, double[] intensities2,
                                double width) {
        //*   System.out.println("Numerator .... ");
        int n1 = positions1.length;
        int n2 = positions2.length;
        int i, j;
        double sum = 0.0, dif;

        for (i = 0; i < n1; i++) {
            for (j = 0; j < n2; j++) {
                //*   System.out.println("Math.abs(positions2["+j+"] - positions1["+i+"]) - > " + positions2[j] + "-" + positions1[i] + " -- >" + Math.abs(positions2[j] - positions1[i]));
                dif = Math.abs(positions2[j] - positions1[i]); /* check distance */
                if (dif < width) /* close */ {
                    //* System.out.println("Difference is less than width " + width);
                    sum += intensities1[i] * intensities2[j] * (1.0 - (dif / width));
                    //*      System.out.println("sum += intensities1["+i+"]" +"* intensities2["+j+"] * (1.0 - (dif / width))" + intensities1[i] * intensities2[j] * (1.0 - (dif / width)));
                }

            }
        }
        //* System.out.println("Final numerator sum = " +  sum);

        return (sum);
    }
}
