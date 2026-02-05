# Testing Patterns

**Analysis Date:** 2026-02-05

## Test Framework

**Runner:**
- JUnit 4.10
- Configured in `pom.xml` with test scope dependency
- Maven integration - run via `mvn test`

**Assertion Library:**
- JUnit 4 assertions (org.junit.Assert)
- Methods: `assertEquals()`, `assertNotNull()`, `assertTrue()`, `assertFalse()`

**Run Commands:**
```bash
mvn test                 # Run all tests
mvn clean test           # Clean and run tests
mvn test -Dtest=TestName # Run specific test class
```

## Test File Organization

**Location:**
- Tests are separated from source code
- Test root: `src/test/java/`
- Parallel structure to main: `seneca.gui.SenecaTest` matches `seneca.gui` package

**Naming:**
- Test classes: PascalCase with "Test" suffix
  - `SENECATests.java`
  - `SenecaTest.java`
  - `CompletnessTest.java`

**Structure:**
```
src/test/java/
├── seneca/
│   ├── SENECATests.java
│   ├── CompletnessTest.java
│   └── gui/
│       └── SenecaTest.java
```

## Test Structure

**Suite Organization:**

From `src/test/java/seneca/SENECATests.java`:
```java
public class SENECATests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        return suite;
    }

    public void testCoverageC10H16() {

    }
}
```

**Patterns:**
- Static `suite()` method for test aggregation (JUnit 3 style, legacy approach)
- Main method for CLI test running
- Individual test methods use `test*()` naming convention

**Setup Pattern:**

From `src/test/java/seneca/gui/SenecaTest.java`:
```java
public class SenecaTest {

    Seneca seneca = null;

    @Before
    public void setup() {
        seneca = Seneca.getInstance();
    }

    @Test
    public void testSenecaConstruction() {
        assertNotNull(seneca);
    }
}
```

**Key observations:**
- Uses `@Before` annotation for setup (JUnit 4)
- Uses `@Test` annotation for test methods
- Instance variable initialized to null, then populated in setup
- Singleton pattern assumption (`getInstance()`)

## Mocking

**Framework:** Not detected

**What to Mock:**
- No explicit mocking framework dependency in pom.xml (no Mockito, EasyMock, etc.)
- Tests appear to use real objects or rely on singleton patterns

**What NOT to Mock:**
- Domain objects (IAtomContainer, NMRSpectrum) are used directly
- File I/O operations may use real files (see CompletnessTest reading from files)

## Fixtures and Factories

**Test Data:**

From `src/test/java/seneca/CompletnessTest.java`:
```java
public void testCompletness() {
    IAtomContainer ac = null;
    HashMap hashmap = new HashMap();
    MoleculeHashGenerator generator = new HashGeneratorMaker()
        .depth(10).elemental().perturbed().molecular();
    SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Unique);
    String hashCode;
    long counter = 0;
    RandomGenerator rg = null;
    String smiles = null;
    int[] hcount = {3,3,3,2,2,1,1,1,0,0};
    long startingtime = 0, endtime = 0;
    long maxStrucCount = 1000000;

    // Test initialization using CDK APIs
    SingleStructureRandomGenerator srg = new SingleStructureRandomGenerator();
    SDFWriter sw = new SDFWriter(new FileWriter("a-pinene-isomers-with-given-h-count.sdf"));
    SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
    ac = sp.parseSmiles("CC1=CCC2CC1C2(C)C");
}
```

**Location:**
- Fixtures are embedded directly in test methods
- No separate fixture factory classes detected
- Test data uses string representations (SMILES) to create domain objects

## Coverage

**Requirements:** Not enforced

**Tools:**
- No code coverage configuration in pom.xml (no JaCoCo, Cobertura, etc.)
- Coverage not tracked or reported

**View Coverage:**
- Not configured - would need to add maven-jacoco-plugin or similar

## Test Types

**Unit Tests:**
- `src/test/java/seneca/gui/SenecaTest.java` - Tests basic object construction and getter methods
- Scope: Single class functionality
- Approach: Direct instantiation and assertion

Example:
```java
@Test
public void testSenecaConstruction() {
    assertNotNull(seneca);
}

@Test
public void testSenecaLogger() {
    assertNotNull(seneca.getLogger());
}

@Test
public void testSenecaTitle() {
    assertEquals("Seneca", seneca.getTitle());
}
```

**Integration Tests:**
- `src/test/java/seneca/CompletnessTest.java` - Integrates multiple systems:
  - CDK library (chemical structure generation)
  - SMILES parsing
  - Structure generation and validation
  - File I/O operations
- Tests molecular structure generation completeness
- Uses real file system and chemical structure objects

**E2E Tests:**
- Not detected
- No UI automation or end-to-end workflow testing found

## Common Patterns

**Async Testing:**
- Not applicable - codebase is synchronous (no async/await patterns)

**Error Testing:**
- Limited error testing found
- No explicit exception assertion tests detected
- See `CompletnessTest.java` lines 107-111: Error handling via e.printStackTrace()

```java
catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

**Timeout Testing:**
- Not detected
- No @Test(timeout=...) annotations used

**Parameterized Testing:**
- Not detected
- No parameterized test runner usage

## Test Gaps and Issues

**Empty/Stub Tests:**
- `src/test/java/seneca/SENECATests.java` line 40-42: Contains empty test method
```java
public void testCoverageC10H16() {

}
```

**Limited Coverage:**
- Only 3 test classes for entire project
- Main source tree has ~30+ Java files
- Estimated coverage: <10%

**No Test Infrastructure:**
- No test utilities or base classes
- No test data builders or factories
- No test configuration management

**Anti-patterns:**
- `CompletnessTest.java` is structured as both test and utility class
  - Contains main() method for standalone execution
  - Mixes test logic with implementation logic
  - Uses System.out.println() for output instead of logging
  - Writes files to hardcoded absolute paths (e.g., `/Users/steinbeck/Downloads/...`)

## Test Execution

**Running Tests:**
```bash
# Run all tests via Maven
mvn test

# Run specific test
mvn test -Dtest=SenecaTest

# Run with output
mvn -X test
```

**Test Report Location:**
- Maven: `target/surefire-reports/` (after running tests)
- Console output: Direct stdout/stderr

---

*Testing analysis: 2026-02-05*
