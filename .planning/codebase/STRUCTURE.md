# Codebase Structure

**Analysis Date:** 2026-02-05

## Directory Layout

```
seneca/
├── bin/                        # Executable scripts and launchers
├── doc/                        # Documentation
├── src/
│   ├── main/
│   │   ├── java/seneca/
│   │   │   ├── core/           # Data models and core utilities
│   │   │   ├── engine/         # Execution orchestration
│   │   │   ├── gui/            # Swing UI components
│   │   │   ├── judges/         # Scoring/evaluation framework
│   │   │   ├── predictor/      # Chemical analysis tools
│   │   │   ├── structgen/      # Structure generation algorithms
│   │   │   └── compute/        # Compute task abstractions
│   │   └── resources/          # Images, icons, configuration
│   └── test/
│       ├── java/seneca/        # Test classes
│       └── resources/data/     # Test dataset files (*.sml)
├── target/                     # Maven build output
├── pom.xml                     # Maven build configuration
├── README.md                   # Project documentation
└── .planning/                  # GSD planning documents
```

## Directory Purposes

**bin/:**
- Purpose: Contains executable scripts and launcher configuration
- Contains: Platform-specific launchers, batch/shell scripts
- Key files: Various launcher scripts for different environments

**src/main/java/seneca/core/:**
- Purpose: Core data models, utilities, and file I/O
- Contains: Dataset container, spectrum importers/exporters, constants, mathematical tools, exception types
- Key files:
  - `SenecaDataset.java` - Main data container holding spectra and molecular formula
  - `SpecMLReader.java` - XML parsing for SpecML dataset format
  - `SpecMLGenerator.java` - XML generation for dataset serialization
  - `SpectrumImporter1D.java`, `SpectrumImporter2D.java` - Spectral data parsing
  - `Utilities.java`, `MathTools.java` - Helper functions

**src/main/java/seneca/core/assigners/:**
- Purpose: Spectrum signal assignment and prediction
- Contains: Assigners for different NMR spectrum types
- Key files: `HMBCAssigner.java`, `HHCOSYAssigner.java`, `CarbonShiftAssigner.java`

**src/main/java/seneca/core/exception/:**
- Purpose: Custom exception classes
- Contains: Exception hierarchy for application-specific errors
- Key files: `SenecaException.java`, `SenecaIOException.java`, `StructureGeneratorException.java`, `JudgeEvaluationException.java`

**src/main/java/seneca/engine/:**
- Purpose: Orchestration of structure generation workflows
- Contains: Executable generation coordinator, server management
- Key files:
  - `ExecutableGenerator.java` - Command-line/batch generation orchestrator
  - `StructureGeneratorServer.java` - Server infrastructure for distributed generation

**src/main/java/seneca/gui/:**
- Purpose: Swing-based GUI components and controllers
- Contains: Main application window, panels, visualizers, configuration dialogs, action handlers
- Key files:
  - `SenecaMain.java` - Application entry point
  - `Seneca.java` - Main application frame (singleton)
  - `AbstractPanel.java` - Base class for content panels
  - `SenecaComponentFactory.java` - Factory for UI component creation
  - `TreeViewComponentFactory.java` - Factory for tree view components
  - `SenecaDatasetCollection.java` - Multi-dataset management
  - `Parameters.java`, `SenecaParameters.java` - Configuration management
  - `NMRSpectrumTablePanel.java` - Spectral data visualization
  - `MoleculeVisualizer.java` - Structure visualization
  - `PopulationTableFrame.java`, `PopulationVisualizer.java` - Generation result viewing

**src/main/java/seneca/gui/configurators/:**
- Purpose: Configuration UI panels for various components
- Contains: Configurator dialogs for judges, generation engines, spectra settings
- Key files: Various `*Configurator.java` classes for each configurable component

**src/main/java/seneca/gui/actions/:**
- Purpose: Menu and button action handlers
- Contains: Action implementations for file operations, generation control, data manipulation
- Key files: Various `*Action.java` classes implementing UI interactions

**src/main/java/seneca/gui/tables/:**
- Purpose: Custom table models and renderers
- Contains: Table UI components for displaying datasets, results, spectra
- Key files: Various `*Table.java` and `*TableModel.java` classes

**src/main/java/seneca/judges/:**
- Purpose: Structure evaluation and constraint scoring framework
- Contains: Judge base class and concrete implementations
- Key files:
  - `Judge.java` - Abstract base class for all scoring judges
  - `ChiefJustice.java` - Orchestrator for parallel judge evaluation
  - `JudgeResult.java` - Result object containing score and analysis
  - `ScoreSummary.java` - Aggregation of multiple judge scores
  - Concrete judges:
    - `HMBCJudge.java` - HMBC spectrum correlation constraints
    - `HHCOSYJudge.java` - HHCOSY spectrum constraints
    - `NMRShiftDbJudge.java` - NMRShiftDB spectral prediction matching
    - `SymmetryJudge.java` - Molecular symmetry constraints
    - `AntiBredtJudge.java` - Anti-Bredt rule enforcement
    - `RingStrainJudge.java` - Ring strain evaluation
    - `HOSECodeJudge.java` - HOSE code matching
    - `TwoDSpectrumJudge.java` - 2D spectrum correlation

**src/main/java/seneca/predictor/:**
- Purpose: Chemical structure analysis and prediction tools
- Contains: Ring strain and structural rule detectors
- Key files: `RingStrainDetector.java`, `AntiBredtDetector.java`

**src/main/java/seneca/predictor/nmrshiftdb/:**
- Purpose: NMRShiftDB spectral database integration
- Contains: NMRShiftDB client and Lucene search integration
- Key files: Various database client classes, Lucene index interfaces

**src/main/java/seneca/structgen/:**
- Purpose: Stochastic structure generation algorithms
- Contains: Abstract generator framework and algorithm implementations
- Key files:
  - `StructureGenerator.java` - Abstract base for all generation algorithms (implements Callable, Runnable)
  - `StructureGeneratorResult.java` - Result container with generated structures and scores
  - `StructureGeneratorStatus.java` - Status tracking during generation
  - `RandomGenerator.java` - Random structure generation utility
  - Subdirectories for different algorithms:
    - `ea/` - Evolutionary Algorithm implementation
    - `sa/regular/` - Standard Simulated Annealing
    - `sa/adaptive/` - Adaptive Simulated Annealing
    - `sa/annealinglog/` - Annealing process logging
    - `deterministic/` - Deterministic generation approaches

**src/main/java/seneca/structgen/ea/:**
- Purpose: Evolutionary Algorithm for structure generation
- Contains: Population-based stochastic generation
- Key files: `EAStochasticGenerator.java` and supporting mutation/crossover operators

**src/main/java/seneca/structgen/sa/regular/:**
- Purpose: Standard Simulated Annealing implementation
- Contains: Temperature schedule, annealing engine, convergence detection
- Key files:
  - `SAStochasticGenerator.java` - SA algorithm implementation
  - `AnnealingEngine.java` - Core SA temperature/step management
  - `ConvergenceAnnealingEngine.java` - SA with convergence detection
  - `AnnealingSchedule.java` - Temperature cooling schedule

**src/main/java/seneca/structgen/sa/adaptive/:**
- Purpose: Adaptive Simulated Annealing for dynamic temperature control
- Contains: ASA implementation with adaptive parameter adjustment
- Key files:
  - `ASAStochasticGenerator.java` - ASA algorithm
  - `AdaptiveAnnealingEngine.java` - Adaptive temperature management
  - `MoleculeState.java` - State representation for ASA
  - `Step.java`, `State.java` - State transition models

**src/main/java/seneca/compute/:**
- Purpose: Abstract computation task framework
- Contains: Task abstraction for computational work
- Key files: `Task.java`, `Compute.java` - Base task interfaces

**src/main/resources/:**
- Purpose: Non-code resources for GUI and configuration
- Contains: Images, icons, configuration properties
- Key directories:
  - `images/` - UI icons and application graphics
  - `images/tree/` - Tree view node icons
  - `icons/` - Additional icon sets
  - `properties/` - Configuration properties files
  - `policies/` - Security or access policies

**src/test/java/seneca/:**
- Purpose: Unit and integration tests
- Contains: Test classes for core functionality
- Key files: `SENECATests.java`, `CompletnessTest.java`, `gui/SenecaTest.java`

**src/test/resources/data/:**
- Purpose: Test dataset files for validation
- Contains: Sample SpecML files for testing generation algorithms
- Key files: `eurabidiol.sml`, `myrtenol.sml`, `pinene.sml`, `eupatorio.sml` (test molecules)

**target/:**
- Purpose: Maven build output directory
- Contains: Compiled classes, assembled JAR artifacts, generated documentation
- Key files: `Seneca2.1.4-jar-with-dependencies.jar` (executable application)

## Key File Locations

**Entry Points:**
- `src/main/java/seneca/gui/SenecaMain.java`: GUI application launcher
- `src/main/java/seneca/engine/ExecutableGenerator.java`: Command-line orchestrator

**Configuration:**
- `pom.xml`: Maven dependencies and build configuration
- `src/main/resources/properties/`: Application properties
- `src/main/java/seneca/gui/SenecaParameters.java`: Runtime configuration management

**Core Logic:**
- `src/main/java/seneca/core/SenecaDataset.java`: Primary data container
- `src/main/java/seneca/judges/ChiefJustice.java`: Judge orchestration
- `src/main/java/seneca/structgen/StructureGenerator.java`: Generation framework
- `src/main/java/seneca/engine/ExecutableGenerator.java`: Workflow orchestration

**Testing:**
- `src/test/java/seneca/`: Test suite location
- `src/test/resources/data/`: Test datasets

## Naming Conventions

**Files:**
- Java classes: PascalCase (e.g., `SenecaDataset.java`, `HMBCJudge.java`)
- Resource files: lowercase with hyphens or underscores (e.g., `log4j.properties`)
- Test files: Class name + `Test` suffix (e.g., `SenecaTest.java`)
- Data files: lowercase (e.g., `eurabidiol.sml`)

**Directories:**
- Package directories: lowercase with dots as path separators (e.g., `seneca/core/assigners/`)
- Non-code directories: lowercase or specific purpose names (e.g., `resources`, `images`, `data`)

**Java Classes:**
- Algorithm implementations: `*Generator` suffix (e.g., `EAStochasticGenerator`)
- Constraint evaluators: `*Judge` suffix (e.g., `HMBCJudge`)
- Result/data containers: `*Result` suffix (e.g., `StructureGeneratorResult`)
- UI panels: `*Panel` suffix (e.g., `AnnealingViewPanel`)
- Utility/helper: `*Utilities`, `*Tools`, `*Manager` suffixes (e.g., `MathTools`)
- Configurators: `*Configurator` suffix (e.g., `JudgeConfigurator`)
- Actions: `*Action` suffix (e.g., various Action classes in `gui/actions/`)

**Package Organization:**
- Feature packages: Named after domain concept (e.g., `judges`, `structgen`, `core`)
- Subpackages: Organized by algorithm or component type (e.g., `structgen.ea`, `structgen.sa.adaptive`)

## Where to Add New Code

**New Judge/Constraint:**
- Primary code: `src/main/java/seneca/judges/` - extend `Judge.java`
- Configuration UI: `src/main/java/seneca/gui/configurators/` - add corresponding configurator
- Registry: Update `ChiefJustice.initJudges()` and GUI factory to include new judge

**New Generation Algorithm:**
- Implementation: `src/main/java/seneca/structgen/` (or appropriate subdirectory like `sa/` or `ea/`)
- Extend: `StructureGenerator.java`
- Integration: Add to algorithm selection in `ExecutableGenerator` and GUI panels

**New Spectral Analysis Tool:**
- Implementation: `src/main/java/seneca/predictor/`
- Integration: Reference from relevant judges or spectrum assigners

**GUI Enhancement:**
- New panel: `src/main/java/seneca/gui/` extending `AbstractPanel.java`
- Action handler: `src/main/java/seneca/gui/actions/` extending Action pattern
- Table/visualizer: `src/main/java/seneca/gui/tables/` or new visualizer class

**Utilities/Helpers:**
- Shared helpers: `src/main/java/seneca/core/` (for domain logic) or respective package utility class

## Special Directories

**target/:**
- Purpose: Maven build artifacts
- Generated: Yes (Maven builds to this directory)
- Committed: No (gitignored)

**src/main/resources/:**
- Purpose: Non-code runtime resources
- Generated: No (hand-maintained)
- Committed: Yes

**.planning/codebase/:**
- Purpose: GSD codebase analysis documents
- Generated: Yes (via GSD mapping)
- Committed: Yes (for team reference)

**src/test/resources/data/:**
- Purpose: Test case molecular structures in SpecML format
- Generated: No (manually created test data)
- Committed: Yes (required for test reproducibility)

---

*Structure analysis: 2026-02-05*
