/* HHCOSYJudgeConfigurator.java
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
import seneca.core.assigners.HHCOSYAssigner;
import seneca.judges.HHCOSYJudge;

/**
 * This class provides a gui for configuring a 2D-spectrum Judges
 */
public class HHCOSYJudgeConfigurator extends TwoDSpectrumJudgeConfigurator {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HHCOSYJudgeConfigurator(SenecaDataset sd) {

        super(sd, (HHCOSYJudge) sd.getJudge("HHCOSYJudge"), new HHCOSYAssigner(
                sd), "HHCOSY");
    }

}
