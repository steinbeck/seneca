package seneca.structgen.sa.adaptive;

import org.openscience.cdk.interfaces.IAtomContainer;

public class MoleculeState implements State {

    public enum Acceptance {ACCEPT, REJECT, UNKNOWN}

    ;

    public final IAtomContainer molecule;

    public final Acceptance acceptance;

    public final int stepIndex;

    public MoleculeState(IAtomContainer molecule, Acceptance acceptance, int stepIndex) {
        this.molecule = molecule;
        this.acceptance = acceptance;
        this.stepIndex = stepIndex;
    }

    public int getStep() {
        return this.stepIndex;
    }

}
