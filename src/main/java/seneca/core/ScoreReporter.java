package seneca.core;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import seneca.engine.ExecutableGenerator;
import seneca.judges.ChiefJustice;
import seneca.judges.HHCOSYJudge;
import seneca.judges.HMBCJudge;
import seneca.judges.HOSECodeJudge;
import seneca.judges.Judge;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ScoreReporter — thin CLI class for post-anneal per-judge scoring.
 *
 * Usage:
 *   java -cp Seneca2.1.4-jar-with-dependencies.jar seneca.core.ScoreReporter \
 *       -in <compound.sml> -mol <structure.sdf>
 *
 * Prints a SENECA_JUDGE_BREAKDOWN: line to stdout with per-judge JSON breakdown
 * (HOSECodeJudge, HMBCJudge, HHCOSYJudge). Does NOT run any SA loop.
 *
 * D-01/D-02: Reports all three active judges that are active under the standard
 * -sa -hmbc -hhcosy CLI flags.
 */
public class ScoreReporter {

    public static void main(String[] args) {
        try {
            // Parse -in and -mol arguments
            String smlFile = null;
            String molFile = null;
            for (int i = 0; i < args.length - 1; i++) {
                if ("-in".equals(args[i])) {
                    smlFile = args[i + 1];
                } else if ("-mol".equals(args[i])) {
                    molFile = args[i + 1];
                }
            }
            if (smlFile == null || molFile == null) {
                System.err.println("Usage: ScoreReporter -in <compound.sml> -mol <structure.sdf>");
                System.exit(1);
            }

            // Load the SML and configure judges via ExecutableGenerator
            ExecutableGenerator generator = new ExecutableGenerator();
            generator.readSpecSML(smlFile);

            // Register all three standard judges (HOSECodeJudge always, HMBCJudge + HHCOSYJudge
            // for the standard -sa -hmbc -hhcosy invocation context)
            List<Judge> judges = new ArrayList<Judge>();
            judges.add(new HOSECodeJudge());
            judges.add(new HMBCJudge());
            judges.add(new HHCOSYJudge());

            // Build and configure ChiefJustice (wires judges, runs initiateJudges + assignBoss)
            ChiefJustice chiefJustice = generator.buildChiefJustice(judges);

            // Read the first molecule from the SDF file using IteratingSDFReader (CDK pattern
            // from StructureIO.readSDF)
            IAtomContainer molecule = null;
            IteratingSDFReader sdfReader = new IteratingSDFReader(
                    new FileReader(new File(molFile)),
                    SilentChemObjectBuilder.getInstance());
            if (sdfReader.hasNext()) {
                molecule = sdfReader.next();
            }
            sdfReader.close();

            if (molecule == null) {
                System.err.println("ScoreReporter: no molecule found in SDF file: " + molFile);
                System.exit(1);
            }

            // Score molecule — prints SENECA_JUDGE_BREAKDOWN: line to stdout
            chiefJustice.getScoreWithBreakdown(molecule);

            System.exit(0);
        } catch (Exception e) {
            System.err.println("ScoreReporter error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
