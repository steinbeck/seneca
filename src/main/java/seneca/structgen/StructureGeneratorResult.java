/* StructureGeneratorResult.java
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
import org.openscience.cdk.silent.AtomContainer;
import seneca.core.FixedSizeStack;
import seneca.judges.ScoreSummary;
import uk.ac.ebi.mdk.prototype.hash.HashGenerator;
import uk.ac.ebi.mdk.prototype.hash.HashGeneratorMaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store the result of a Structure Generation *
 */
public class StructureGeneratorResult implements java.io.Serializable,
        Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int size = 30;
    public FixedSizeStack structures = new FixedSizeStack(30);
    public FixedSizeStack scoreSummaries = new FixedSizeStack(30);
    private HashGenerator<Long> hashGenerator = null;

    public StructureGeneratorResult() {
        hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
    }

    public StructureGeneratorResult(int size) {
        if (size > 0) {
            this.size = size;
            structures = new FixedSizeStack(this.size);
            scoreSummaries = new FixedSizeStack(this.size);
            hashGenerator = new HashGeneratorMaker().withDepth(8).withBondOrderSum().nullable().build();
        }
    }

    /**
     * Sorts the structures in descending order with respect to the score *
     */
    public void sort() {
        boolean somethingsChanged;
        Object o1, o2;
        do {
            somethingsChanged = false;
            for (int f = 0; f < structures.size() - 1; f++) {
                if (((ScoreSummary) scoreSummaries.elementAt(f)).costValue < ((ScoreSummary) scoreSummaries.elementAt(f + 1)).costValue) {
                    o1 = structures.elementAt(f + 1);
                    o2 = scoreSummaries.elementAt(f + 1);
                    structures.removeElementAt(f + 1);
                    scoreSummaries.removeElementAt(f + 1);
                    structures.insertElementAt(o1, f);
                    scoreSummaries.insertElementAt(o2, f);
                    somethingsChanged = true;
                }
            }
        } while (somethingsChanged);
    }

    public void sortByCostValue() {
        boolean somethingsChanged;
        Object o1;
        do {
            somethingsChanged = false;
            for (int f = 0; f < structures.size() - 1; f++) {
                AtomContainer molecule_f = (AtomContainer) structures.elementAt(f);
                AtomContainer molecule_f_1 = (AtomContainer) structures.elementAt(f + 1);
                if (Double.parseDouble((String) molecule_f.getProperty("Score")) < Double.parseDouble((String) molecule_f_1.getProperty("Score"))) {
                    o1 = structures.elementAt(f + 1);
                    structures.removeElementAt(f + 1);
                    structures.insertElementAt(o1, f);
                    somethingsChanged = true;
                }
            }
        } while (somethingsChanged);
    }

    public int size() {
        return this.size;
    }

    public void removeIsomorphism() {
        List<Long> hashCodes = new ArrayList<Long>();
        FixedSizeStack tempStack = new FixedSizeStack(this.structures.size());
        for (int i = 0; i < this.structures.size(); i++) {
            Long hashCode = hashGenerator.generate((IAtomContainer) this.structures.get(i));
            if (!hashCodes.contains(hashCode)) {
                hashCodes.add(hashCode);
                tempStack.add((IAtomContainer) this.structures.get(i));
            }
        }
        this.structures = tempStack;
    }

    public void mergeByStructures(StructureGeneratorResult sgr) {
        // this.size += sgr.size();
        this.structures.setSize(this.structures.size() + sgr.size());
        // this.scoreSummaries.setSize(this.structures.size() + sgr.size());
        for (int f = 0; f < sgr.size(); f++) {
            this.structures.push(sgr.structures.elementAt(f));
            //this.scoreSummaries.push(sgr.scoreSummaries.elementAt(f));
        }
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
