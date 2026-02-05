# Architecture

**Analysis Date:** 2026-02-05

## Pattern Overview

**Overall:** MVC (Model-View-Controller) with specialized algorithm engines. Seneca is a computer-assisted structure elucidation (CASE) system that uses stochastic structure generation combined with chemical constraint judges to iteratively refine molecular structure predictions.

**Key Characteristics:**
- Event-driven GUI layer (Swing-based)
- Separation between data models (datasets, spectra) and computational engines
- Pluggable judge system for constraint evaluation (scoring)
- Multiple stochastic generation algorithms (Evolutionary Algorithm, Simulated Annealing)
- Concurrent execution via ExecutorService for parallel structure generation
- XML-based serialization for dataset persistence (SpecML format)

## Layers

**GUI/Presentation Layer:**
- Purpose: Provides Swing-based interface for user interaction with datasets, spectra visualization, and structure generation control
- Location: `src/main/java/seneca/gui/`
- Contains: Main frame (`Seneca.java`), panels (`AbstractPanel.java`, `AnnealingViewPanel.java`, `NMRSpectrumTablePanel.java`), visualizers (`MoleculeVisualizer.java`, `PopulationVisualizer.java`), configuration UI (`configurators/`), action handlers (`actions/`)
- Depends on: Core data models, configuration management, UI factories
- Used by: User interaction, triggering structure generation workflows

**Data Model Layer:**
- Purpose: Represents the problem domain - chemical datasets with spectra, molecular formulas, and metadata
- Location: `src/main/java/seneca/core/`
- Contains: `SenecaDataset.java` (container for spectrum data), spectrum importers, constants, utilities
- Depends on: CDK library for molecular structures, CASEKIT library for NMR spectra models
- Used by: GUI layer, generation engines, judges

**Spectrum Processing Layer:**
- Purpose: Handles import and parsing of NMR spectral data from external formats
- Location: `src/main/java/seneca/core/` (importers) and `src/main/java/seneca/core/assigners/`
- Contains: `SpecMLReader.java`, `SpecMLGenerator.java`, `SpectrumImporter1D.java`, `SpectrumImporter2D.java`, spectrum assigners
- Depends on: XML parsing, CDK, CASEKIT
- Used by: Data model initialization, preprocessing before generation

**Evaluation/Scoring Layer:**
- Purpose: Evaluates candidate structures against chemical constraints and spectral data
- Location: `src/main/java/seneca/judges/`
- Contains: Judge base class and implementations (`HMBCJudge.java`, `HHCOSYJudge.java`, `NMRShiftDbJudge.java`, `SymmetryJudge.java`, `AntiBredtJudge.java`, etc.), `ChiefJustice.java` (orchestrator)
- Depends on: CDK for molecular analysis, NMRShiftDB for spectral prediction
- Used by: Structure generation engines to score generated candidates

**Structure Generation Engine Layer:**
- Purpose: Implements stochastic algorithms for exploring chemical structure space
- Location: `src/main/java/seneca/structgen/`
- Contains: Abstract `StructureGenerator.java`, Evolutionary Algorithm implementation (`ea/EAStochasticGenerator.java`), Simulated Annealing (`sa/SAStochasticGenerator.java`, `sa/ASAStochasticGenerator.java`), random structure generator (`RandomGenerator.java`)
- Depends on: CDK for molecular manipulation, judges for scoring, callable/concurrent interfaces
- Used by: Execution engine and command-line interface

**Execution/Orchestration Layer:**
- Purpose: Coordinates structure generation workflows - manages dataset loading, judge initialization, parallel execution, and result aggregation
- Location: `src/main/java/seneca/engine/`
- Contains: `ExecutableGenerator.java` (command-line orchestrator), `StructureGeneratorServer.java` (server infrastructure)
- Depends on: All layers below (data, judges, generators)
- Used by: Entry points (command-line and GUI)

**Prediction/Analysis Layer:**
- Purpose: Auxiliary chemical analysis tools for constraint detection and NMR prediction
- Location: `src/main/java/seneca/predictor/` and `src/main/java/seneca/predictor/nmrshiftdb/`
- Contains: Ring strain and anti-Bredt rule detectors, NMRShiftDB integration with Lucene full-text search
- Depends on: CDK, Lucene search library
- Used by: Judges for constraint evaluation

## Data Flow

**Structure Elucidation Workflow:**

1. **Data Loading**: User loads a SpecML dataset file via GUI (`Seneca.openFile()`)
2. **Parsing**: `SpecMLReader.parse()` deserializes the dataset and initializes `SenecaDataset` with spectra and molecular formula
3. **Preprocessing**: Spectrum assigners process spectral peaks; molecular formula is converted to initial atom container
4. **Judge Initialization**: `ChiefJustice.initJudges()` creates and configures enabled judges based on available spectral data
5. **Generation Initiation**: User selects algorithm (EA or SA) and number of parallel servers in GUI
6. **Parallel Generation**: `ExecutableGenerator.execute()` submits multiple `StructureGenerator` tasks to `ExecutorService` thread pool
7. **Iteration Loop** (per generator):
   - Generate random/mutated molecular structure
   - `ChiefJustice.scoreStructure()` evaluates structure against all judges in parallel
   - `StructureGenerator` accepts/rejects based on algorithm logic (EA: fitness; SA: temperature probability)
   - Stores top-scoring structures in population/results list
8. **Convergence**: Generation terminates when max steps reached or user stops process
9. **Result Extraction**: Top structures written to SDF file; `TanimotoCalculator` compares with known structure if provided
10. **GUI Update**: Results displayed in tables and molecule visualizers

**State Management:**
- `SenecaDataset` maintains mutable state: list of judges, spectra, atom container, generation sessions
- Generation results stored in `StructureGeneratorResult` objects
- GUI observes dataset changes via `ChangeListener` pattern
- ChiefJustice maintains judge list and parallel evaluation state

## Key Abstractions

**Judge Framework:**
- Purpose: Provides pluggable constraint scoring system
- Examples: `src/main/java/seneca/judges/Judge.java` (base), `src/main/java/seneca/judges/HMBCJudge.java`, `src/main/java/seneca/judges/SymmetryJudge.java`
- Pattern: Template method + Callable interface; each judge implements `call()` returning `JudgeResult` with score and analysis

**StructureGenerator Framework:**
- Purpose: Abstraction for stochastic generation algorithms
- Examples: `src/main/java/seneca/structgen/StructureGenerator.java` (abstract), `src/main/java/seneca/structgen/ea/EAStochasticGenerator.java`, `src/main/java/seneca/structgen/sa/regular/SAStochasticGenerator.java`
- Pattern: Extends `Callable<StructureGeneratorResult>` and `Runnable`; algorithms override scoring and iteration logic

**Spectrum Model:**
- Purpose: Unified representation of 1D/2D NMR spectral data
- Examples: `casekit.model.NMRSpectrum`, `casekit.model.NMRSignal` (external from CASEKIT library)
- Pattern: Data holder; Seneca layers use via specialized importers and assigners

**ChiefJustice Pattern:**
- Purpose: Orchestrates parallel judge evaluation
- Location: `src/main/java/seneca/judges/ChiefJustice.java`
- Pattern: Coordinator pattern using `ExecutorService` to parallelize judge calls; aggregates scores via `ScoreSummary`

## Entry Points

**GUI Entry Point:**
- Location: `src/main/java/seneca/gui/SenecaMain.java`
- Triggers: Direct application launch
- Responsibilities: OS-specific setup (macOS menu bar), Swing initialization, singleton instantiation of main `Seneca` frame

**Seneca GUI Application:**
- Location: `src/main/java/seneca/gui/Seneca.java`
- Triggers: GUI main frame initialization
- Responsibilities: Manages window layout, menu bar, file dialogs, dataset collection; observes dataset change events; coordinates data/generation panels; logs application lifecycle

**Command-Line Entry Point:**
- Location: `src/main/java/seneca/engine/ExecutableGenerator.java` (primary) called from main/custom entry
- Triggers: Batch/non-interactive invocation
- Responsibilities: Parses command-line arguments, loads dataset, initializes judges, executes generation, writes results

## Error Handling

**Strategy:** Custom exception hierarchy with logging fallback

**Patterns:**
- `seneca.core.exception.SenecaException` - Base checked exception
- `seneca.core.exception.SenecaIOException` - File/parsing errors
- `seneca.core.exception.StructureGeneratorException` - Generation failures
- `seneca.core.exception.JudgeEvaluationException` - Scoring errors
- Uses Log4j for error/warning logging throughout
- GUI errors shown via `ErrorMessages` dialog utility

## Cross-Cutting Concerns

**Logging:** Log4j configured via property file; separate loggers for main app, structure generation, annealing evolution. `Logger.getLogger()` pattern used throughout.

**Validation:** `DataConsistency.java` performs pre-generation validation; `CorrectGeometricConfiguration.java` sanitizes generated structures.

**Authentication:** Not applicable (single-user desktop application).

**Concurrency:** `ExecutorService` for parallel judge evaluation and multiple structure generator execution; `Callable` interface used for async judge scoring; `Future` pattern for result collection.

---

*Architecture analysis: 2026-02-05*
