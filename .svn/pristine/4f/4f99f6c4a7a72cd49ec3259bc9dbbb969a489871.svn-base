/* 
 * $RCSfile: AnnealingScheduleInterface.java,v $
 * $Author: steinbeck $
 * $Date: 2004/02/16 09:50:54 $
 * $Revision: 1.3 $
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

/**
 * AnnealingSchedules need to implement this interface
 */

public interface AnnealingScheduleInterface {

    /**
     * Cool the system down by one step. How much that is or if the system is
     * cooled at all is left to the implementation.
     *
     * @return True, if the system did the cooling step, false if it has reached
     *         the end.
     */
    public boolean cool();

    /**
     * Call this to initialize the annealing schedule
     */
    public void init();

    /**
     * Method that decides if a particular move is accepted
     *
     * @return True if the move is accepted.
     */
    public boolean isAccepted(double deltaE);

    /**
     * A verbose representation of this schedule
     *
     * @return A verbose representatin of this schedule
     */
    
    public String toString();
}
