# Coding Conventions

**Analysis Date:** 2026-02-05

## Naming Patterns

**Files:**
- Class names use PascalCase: `Utilities.java`, `SenecaDataset.java`, `SpecMLGenerator.java`
- Exception classes suffix with "Exception": `SenecaException.java`, `SenecaIOException.java`, `JudgeEvaluationException.java`
- Test files use PascalCase with "Test" suffix: `SENECATests.java`, `SenecaTest.java`, `CompletnessTest.java`

**Functions/Methods:**
- Use camelCase: `importSimpleList1D()`, `removeRedundancy()`, `setIndentString()`, `getFormulaDescription()`
- Boolean getters use "is" prefix: `isDatasetCompleteForStructureElucidation()`, `isPropertiesAssignedWell()`, `isAtleastOneJudgeEnabled()`
- Setter methods use "set" prefix: `setName()`, `setMolecularFormula()`, `setAtomContainer()`
- Static factory or utility methods clearly named: `importSimpleList1D()`, `max()`, `min()`

**Variables:**
- Use camelCase for instance and local variables: `counter`, `smiles`, `hashCode`, `acceptanceProbability`, `coolingRate`
- Single letter loop variables for common loops: `i`, `f`, `j`
- Descriptive names for parameters: `nmrSpectrum`, `nmrSignal`, `atomContainer`, `senecaDataset`
- Static final constants use UPPER_SNAKE_CASE: `serialVersionUID`, `ANNEALING_ENGINE`, `NUMBER_OF_STEPS`

**Types/Interfaces:**
- Interface implementations use the interface name as prefix or suffix
- Use CDK interfaces extensively: `IAtomContainer`, `IAtom`, `IBond`, `IMolecularFormula`, `NMRSpectrum`

## Code Style

**Formatting:**
- No explicit formatting configuration (eslint, prettier) detected
- Indentation appears inconsistent - some files use 4 spaces, some use tabs
- Brace style: mixed (some Allman, some K&R)
- See `src/main/java/seneca/core/MathTools.java` - single space in for loop conditions
- See `src/main/java/seneca/core/Utilities.java` - multiple styles in same class

**Linting:**
- Not detected - no .eslintrc or similar configuration

## Import Organization

**Order:**
1. Standard Java imports (java.util.*, java.io.*, etc.)
2. Third-party library imports (org.*, com.*, net.sf.*, casekit.*)
3. CDK imports (org.openscience.cdk.*)
4. Project internal imports (seneca.*)

**Examples from `src/main/java/seneca/core/SpecMLReader.java`:**
```java
import org.apache.log4j.Logger;                    // Log4j
import org.openscience.cdk.interfaces.IAtom;       // CDK
import org.openscience.cdk.silent.Atom;            // CDK
import casekit.model.NMRSignal;                    // Third-party
import seneca.core.exception.SenecaIOException;    // Project internal
```

**Path Aliases:**
- Not used - fully qualified names throughout

## Error Handling

**Exception Hierarchy:**
- Custom exceptions extend `Exception` base class: `SenecaException extends Exception`
- Four custom exception types in `src/main/java/seneca/core/exception/`:
  - `SenecaException.java` - Generic application exception
  - `SenecaIOException.java` - I/O operations
  - `JudgeEvaluationException.java` - Judge evaluation failures
  - `StructureGeneratorException.java` - Structure generation failures

**Pattern:**
```java
public SenecaException(String message) {
    super(message);
}
```

**Usage Pattern:**
- Checked exceptions are typically caught and logged
- See `src/test/java/seneca/CompletnessTest.java`: mixed try-catch blocks with `e.printStackTrace()` and commented-out TODO comments
- See `src/main/java/seneca/core/SpecMLReader.java`: exceptions caught and logged via logger.debug()

**Anti-patterns:**
- Some files use `e.printStackTrace()` for error handling (lines 110, 109-110 in CompletnessTest.java)
- Some files have bare catches with empty blocks or stub implementations

## Logging

**Framework:** Apache Log4j 1.2.16 (legacy version)

**Initialization Pattern:**
```java
private static final Logger logger = Logger.getLogger(ClassName.class);
```

**Examples:**
- `src/main/java/seneca/core/SpecMLGenerator.java`: `private static final Logger logger = Logger.getLogger(SpecMLGenerator.class);`
- `src/main/java/seneca/core/SpecMLReader.java`: `private static final Logger logger = Logger.getLogger(SpecMLReader.class);`

**Logging Methods Used:**
- `logger.info()` - General information messages
- `logger.debug()` - Debug-level messages for detailed parsing information

**Examples from `src/main/java/seneca/core/SpecMLReader.java`:**
```java
logger.info("We found an nmr spectrum of type " + specType);
logger.debug("\n** Parsing error" + ", line " + lineNumber);
logger.info("Setting up parameters for simulated annealing");
```

**Anti-pattern:** Mixed use of `System.out.println()` and `System.err.println()` alongside logger calls
- See `src/main/java/seneca/core/SpecMLReader.java` line 69: `System.out.println("using SpecML reader");`
- See `src/main/java/seneca/core/SpecMLReader.java` line 203: `System.out.println("creating molecule: " + ...);`
- See `src/test/java/seneca/CompletnessTest.java` line 77-78: Uses System.out for test output

## Comments

**When to Comment:**
- File headers use extensive RCS comment blocks with author, date, revision (legacy pattern)
- Class-level JavaDoc comments present in some files
- Inline comments rare but appear for algorithm explanations

**JavaDoc/TSDoc:**
- Some usage but inconsistent
- Example from `src/main/java/seneca/core/Utilities.java`:
```java
/**
 * Utility class contains method to clone atomContainer container with same atoms but different
 * bonds.
 *
 * @author kalai
 */
```

- Example from `src/main/java/seneca/core/MathTools.java`:
```java
/**
 * Analog of Math.max that returns the largest double value in an array of dpubles.
 *
 * @param   values  the values to be searched for the largest value among them
 * @return   the largest value among a set of given values
 */
```

**TODO Comments:**
- Scattered throughout codebase (7 instances found)
- Most are auto-generated IDE stubs from old development
- See `src/main/java/seneca/structgen/deterministic/LucyDeterministicGenerator.java` line 37: `//TODO Must implement this method`
- See `src/main/java/seneca/gui/Seneca.java` line 364: `/* TODO create a method to initialize all the tabs to insert the spectra`

## Function Design

**Size:** Mixed - ranges from 2-line utility methods to 50+ line parsing/processing methods

**Parameters:**
- Typically use domain-specific types (IAtomContainer, NMRSpectrum) rather than primitives
- Java conventions followed: pass objects that implement interfaces

**Return Values:**
- Use strongly-typed returns (IAtomContainer, List, String, etc.)
- Boolean returns for validation methods
- Null returns not explicitly prevented but not common

**Examples:**
- `src/main/java/seneca/core/Utilities.java` line 48-82: `clone()` method returns Object, performs deep copy with proper type casting
- `src/main/java/seneca/core/DataConsistency.java` line 40-48: Boolean validation method with early returns
- `src/main/java/seneca/core/MathTools.java` line 65-74: Stateless utility methods with primitive returns

## Module Design

**Exports:**
- Public classes expose functionality through public methods
- Heavy use of domain objects (IAtomContainer from CDK)
- Static utility classes use "public static" patterns extensively

**Examples:**
- `src/main/java/seneca/core/Utilities.java`: Static methods for molecular operations
- `src/main/java/seneca/core/MathTools.java`: Static mathematical utility methods
- `src/main/java/seneca/core/StructureIO.java`: Static methods for reading/writing structures

**Barrel Files:**
- Not used - no index/barrel export files detected
- Imports use fully qualified class names

---

*Convention analysis: 2026-02-05*
