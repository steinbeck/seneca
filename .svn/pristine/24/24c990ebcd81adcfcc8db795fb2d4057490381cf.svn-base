/* 
 * $RCSfile: AnnealingEngine.java,v $
 * $Author: steinbeck $
 * $Date: 2004/02/16 09:50:54 $
 * $Revision: 1.5 $
 *
 * Copyright (C) 1997 - 2001  Dr. Christoph Steinbeck
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

package seneca.structgen.sa.regular;

import org.openscience.cdk.structgen.RandomGenerator;
import seneca.judges.ChiefJustice;

/**
 * The AnnealingEngine controls the course of the tempererature during a
 * Simulated Annealing run according to the data stored in a given
 * AnnealingSchedule configuration object.
 */
public interface AnnealingEngine {

    /**
     * Initializes the annealing procedure each time a new instance of
     * AnnealingSchedule is popped of the schedules List.
     */
    void initAnnealing(RandomGenerator randomGent, ChiefJustice chiefJustice);

    /**
     * Cool down according to the given annealing schedule
     */
    void cool();

    /**
     * Returns the current temperature
     */
    double getTemperature();

    /**
     * True when the annealing run is finished
     */
    boolean isFinished();

    /**
     * The number of Iterations done so far
     */
    long getIterations();

    /**
     * True when the move is accepted upon inspection of the scores
     */
    boolean isAccepted(double recentScore, double lastScore);

    void setConvergenceCounter(long convergenceCounter);
}
