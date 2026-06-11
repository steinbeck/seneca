# SENECA 2.1.4 — Build Reproducibility

## Requirements

- **JDK 21+** required. The project uses `maven.compiler.source=21` and `maven.compiler.target=21`.
  Tested on JDK 25 (target 21). Earlier JDKs will fail to compile.
- **Maven 3.x** (tested with 3.9.x). Install via `brew install maven` or download from https://maven.apache.org/

## casekit Local Maven Install (one-time setup per machine)

The `casekit` library is not available on Maven Central and must be installed into the local Maven
repository before building. This step is a prerequisite for a fresh clone.

```bash
mvn install:install-file \
    -Dfile=/path/to/casekit.jar \
    -DgroupId=casekit \
    -DartifactId=casekit \
    -Dversion=1.0 \
    -Dpackaging=jar
```

Replace `/path/to/casekit.jar` with the actual path to the casekit jar on your machine.
On the primary development machine this has already been done; the jar lives in `~/.m2/repository/casekit/casekit/1.0/`.

## Build Command

```bash
cd /path/to/seneca
mvn clean package -DskipTests
```

This produces:

```
target/Seneca2.1.4-jar-with-dependencies.jar
```

A fat jar containing all dependencies (including CDK, casekit, log4j).
Build time: approximately 30–60 seconds on a modern laptop.

## Headless CLI Invocation

```bash
java -cp target/Seneca2.1.4-jar-with-dependencies.jar seneca.core.CommandInputParser \
    -in <compound>.sml \
    -sa -hmbc -hhcosy \
    -structures 30 \
    -out <outdir>
```

Output SDF files land at `<outdir>/seneca/structures/<timestamp>-0.sdf` when `<outdir>` is a
pre-existing directory.

## ScoreReporter CLI (post-anneal per-judge breakdown)

```bash
java -cp target/Seneca2.1.4-jar-with-dependencies.jar seneca.core.ScoreReporter \
    -in <compound>.sml \
    -mol <structure.sdf>
```

Prints a `SENECA_JUDGE_BREAKDOWN:` tagged JSON line to stdout with per-judge name, raw score, and max score.
Used by the faulon-ng oracle harness (`scripts/seneca_oracle/run_oracle.py`).

## Reference Compounds

Test SML files for eurabidiol, monochaetin, and preconen are at:
```
src/test/resources/data/eurabidiol.sml
src/test/resources/data/monochaetin.sml
src/test/resources/data/preconen.sml
```

## Notes

- The `--add-exports` JVM flag is NOT required. The `com.apple.eawt.Application` usage in
  `SenecaMain.java` has been replaced with `java.awt.Taskbar` (available since JDK 9).
- Do not run `SanitizeMol` inside the SA step loop — this is a known performance pitfall.
