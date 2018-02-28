/* StructureGeneratorStatus.java
 *
 * Copyright (C) 1997, 1998, 1999, 2000  Dr. Christoph Steinbeck
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
package seneca.structgen;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.judges.ScoreSummary;
import seneca.structgen.annealinglog.CommonAnnealingLog;

import java.awt.image.BufferedImage;

/**
 * Class to transmit the current status of a Structure Generation Process
 */
public class StructureGeneratorStatus implements java.io.Serializable,
        Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static String[] statusStrings = {"Idle", "Running",
            "Holding Results", "Stopped", "Lost thread", "Finished"
    };
    public static int IDLE = 0;
    public static int RUNNING = 1;
    public static int HOLDING = 2;
    public static int STOPPED = 3;
    public static int LOST = 4;
    public static int FINISHED = 5;
    public String datasetName = null;
    public String molecularFormula = null;
    public long iteration = 0;
    public IAtomContainer bestStructure = null;
    public String status = null;
    public ScoreSummary bestEvaluation = null;
    public BufferedImage bestStructureImage = null;
    public CommonAnnealingLog annealingLog = null;
    public long timeTaken = 0l;

    public StructureGeneratorStatus() {

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
}
