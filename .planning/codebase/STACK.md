# Technology Stack

**Analysis Date:** 2026-02-05

## Languages

**Primary:**
- Java 6+ - Desktop GUI application (compiled with Java 6, runs on Java 22+)

**Secondary:**
- XML - Configuration and data serialization (SpecML format)

## Runtime

**Environment:**
- Java Runtime Environment (JRE) 6.0 or higher
- Tested with Java 22 and 23

**Build Tool:**
- Apache Maven 3.9.9

**Package Manager:**
- Maven - Dependency management via `pom.xml`
- Lockfile: present (`pom.xml` in `/Users/steinbeck/Dropbox/develop/seneca/pom.xml`)

## Frameworks

**Core:**
- Swing - Version unspecified - Desktop GUI framework, primary UI library
- JGoodies Forms 1.2.1 - Layout management for forms

**Chemistry/Science:**
- CDK (Chemistry Development Kit) Bundle 2.2 - Molecular structure representation and manipulation
- Xalan 2.7.0 - XSLT processing for XML transformations

**Data Processing:**
- Apache Lucene 4.0.0-BETA - Full-text search and indexing for HOSE code lookups
- JFreeChart 1.0.13 - Charting and visualization library
- GraphLayout 1.2.1 - Graph visualization

**System Integration:**
- Apple JavaExtensions 1.4 - macOS-specific integration (Dock icons, menu bar)
- Mac Widgets 0.9.5 - macOS native UI widget styling

**Testing:**
- JUnit 4.10 - Unit testing framework

**Utilities:**
- Apache Commons CLI 1.2 - Command-line argument parsing
- Google Collections 1.0 - Collection utilities (lists, maps, multimaps)

## Key Dependencies

**Critical:**
- org.openscience.cdk:cdk-bundle 2.2 - Chemical structure processing, the foundation for molecular analysis
- org.apache.lucene:lucene-core 4.0.0-BETA - Full-text indexing for NMR shift database lookups in `seneca.predictor.nmrshiftdb.lucene`
- jfree:jfreechart 1.0.13 - Charting for spectrum visualization and annealing logs
- net.sf.jniinchi - InChI (IUPAC International Chemical Identifier) generation for structure canonicalization

**Infrastructure:**
- org.apache.log4j:log4j 1.2.16 - Logging framework for application and server logging
- com.google.collections:google-collections 1.0 - Multimap support for HOSE code indexing
- xalan:xalan 2.7.0 - XML transformation for SpecML data parsing

## Configuration

**Environment:**
- OS-specific file preference system handled in `seneca.core.FilePreference`
  - macOS: `/Library/Application Support/Seneca/lucene-4`
  - Windows: `%APPDATA%\Seneca\lucene-4`
  - Linux: `~/.appdata/Seneca/lucene-4`
- Log4j logging configuration: `src/main/resources/properties/logging.properties` and `logging.server.properties`
- Application properties: `src/main/resources/properties/Seneca.properties` (menu definitions, UI strings)

**Build:**
- Maven Assembly Plugin 2.3 - Creates fat JAR with all dependencies
- Compiler target: Java 6 source/target level (`maven.compiler.source=1.6`, `maven.compiler.target=1.6`)
- Final artifact: `Seneca2.1.4.jar` (standalone executable JAR)
- Main class: `seneca.gui.SenecaMain`

## Platform Requirements

**Development:**
- Java Development Kit (JDK) 6 or higher (compiled against 1.6)
- Maven 3.0+
- macOS, Windows, or Linux operating system

**Production:**
- Java Runtime Environment (JRE) 6 or higher
- 300+ MB disk space for Lucene indexes (HOSE database)
- OS-specific application data directory support

**GUI Requirements:**
- Swing-compatible display (X11 on Linux, native on Windows/macOS)
- macOS-specific features (Dock integration) handled via `apple.*` system properties

---

*Stack analysis: 2026-02-05*
