/* HMBCJudgeConfigurator.java
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

package seneca.gui.configurators;

import seneca.core.SenecaDataset;
import seneca.core.assigners.HMBCAssigner;
import seneca.judges.HMBCJudge;

/**
 * This class provides a gui for configuring a 2D-spectrum Judges
 */
public class HMBCJudgeConfigurator extends TwoDSpectrumJudgeConfigurator {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HMBCJudgeConfigurator(SenecaDataset sd) {
        super(sd, (HMBCJudge) sd.getJudge("HMBCJudge"), new HMBCAssigner(sd),
                "HMBC");
    }

}
