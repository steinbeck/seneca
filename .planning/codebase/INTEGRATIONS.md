# External Integrations

**Analysis Date:** 2026-02-05

## APIs & External Services

**NMR Shift Database Prediction:**
- NMRShiftDB - HOSE code spectroscopy shift predictions
  - SDK/Client: Apache Lucene 4.0.0-BETA for local indexing
  - Data format: SDF (Structure Data Format) files
  - Implementation: `seneca.predictor.nmrshiftdb.lucene.LuceneHoseIndexer`
  - Integration: Reads NMRShiftDB SDF exports, indexes with HOSE codes for chemical shift lookup

**Spectrum Analysis APIs:**
- No direct external spectrum APIs detected
- Data imported from local files (1D NMR, 2D COSY, HMBC, NOESY)

## Data Storage

**Databases:**
- Not applicable - No JDBC, SQL, or database connections detected
- All data stored as local files (file-based storage)

**File Storage:**
- Local filesystem only
  - SDF files (Structure Data Format) for molecular structures
  - SpecML XML format for spectrum data
  - Lucene index files for HOSE code search indexes
  - Location: OS-specific application data directories via `seneca.core.FilePreference`
    - macOS: `/Library/Application Support/Seneca/lucene-4`
    - Windows: `%APPDATA%\Seneca\lucene-4`
    - Linux: `~/.appdata/Seneca/lucene-4`
  - Configuration: `src/main/resources/properties/`

**File I/O Implementation:**
- SDF Reader/Writer: `seneca.core.StructureIO`
  - `readSDF()` - Loads molecular structures from SDF files using CDK's `IteratingSDFReader`
  - `writeSDF()` - Writes structures to SDF format with metadata
  - `readMol()` - Reads single MDL MOL files
- SpecML Reader/Writer: `seneca.core.SpecMLReader`, `seneca.core.SpecMLGenerator`
  - Handles spectrum metadata and peak assignments in XML format

**Caching:**
- In-memory caching via `FixedSizeStack` for structure results
- Lucene indexes provide search-speed caching for HOSE code lookups
- No external caching service

## Authentication & Identity

**Auth Provider:**
- Not applicable - No authentication system
- Standalone desktop application with no user accounts
- Optional server mode uses thread executors in `seneca.core.ServerConstants` (local-only threading)

## Monitoring & Observability

**Error Tracking:**
- Not detected - No external error tracking service

**Logs:**
- Log4j 1.2.16 logging framework
  - Configuration: `src/main/resources/properties/logging.properties`
  - Server logging: `src/main/resources/properties/logging.server.properties`
  - Categories: File-based logging to application data directory
  - Log output: Application and server process logs

**Application Monitoring:**
- Status monitoring via `seneca.gui.tables.StructureGenerationServerTableModel` for server threads
- Execution status tracked in `seneca.structgen.StructureGeneratorStatus`

## CI/CD & Deployment

**Hosting:**
- Standalone desktop application (no cloud hosting)
- Self-contained JAR distribution for Windows, macOS, Linux
- GitHub repository: https://github.com/steinbeck/seneca (per commit history)

**CI Pipeline:**
- Not configured - Manual builds via Maven
- Maven build command: `mvn clean package` (creates `Seneca2.1.4-jar-with-dependencies.jar`)
- Artifact assembly: Maven Assembly Plugin creates fat JAR with all dependencies

**Distribution:**
- Standalone executable JAR: `Seneca2.1.4.jar`
- Single file deployment on any Java 6+ system
- macOS: DMG installer format supported (Dock icon handling via `apple.laf.useScreenMenuBar`)

## Environment Configuration

**Required env vars:**
- None - Application uses system properties
- OS detection: `System.getProperty("os.name")` for platform-specific behavior
- User home: `System.getProperty("user.home")` for data directory location
- Java version: Compiled for Java 6, tested with Java 22+

**System Properties (Optional):**
- `apple.laf.useScreenMenuBar=true` - macOS Dock menu bar integration
- `com.apple.mrj.application.apple.menu.about.name=SENECA` - macOS app name

**Secrets location:**
- Not applicable - No secrets, API keys, or credentials managed
- File-based data storage with OS-level file permissions

## Webhooks & Callbacks

**Incoming:**
- Not applicable - No webhook endpoints
- Server mode (`seneca.engine.ExecutableGenerator`) supports command-line execution but no HTTP webhooks

**Outgoing:**
- Not applicable - No external callbacks or webhooks

**Calculation Callbacks:**
- Internal event system: `seneca.structgen.StructureGeneratorStatus` for progress reporting to GUI
- Server execution: `seneca.gui.tables.StructureGenerationServerTableModel` for monitoring generator threads
- Thread-based concurrency: `java.util.concurrent.ExecutorService` for parallel structure generation

## Data Import/Export Formats

**Import:**
- SDF (Structure Data Format) - Molecular structures and spectroscopy data
- MOL (MDL MOL format) - Single structure files
- SpecML (XML) - NMR spectrum metadata and assignments
- CSV/Text - Command-line input parsing via `seneca.core.CommandInputParser`

**Export:**
- SDF - Output structures with ranking and scoring metadata
- SpecML - Spectrum assignments and results
- SMILES - Canonical structure representation via CDK
- InChI - Structure canonicalization for deduplication

**Lucene Index Export/Import:**
- HOSE code indexes stored in Lucene directory format
- Serialized statistics data in custom format

---

*Integration audit: 2026-02-05*
