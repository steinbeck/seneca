package seneca.gui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Test
    public void testSenecaLogger() {
        assertNotNull(seneca.getLogger());
    }

    @Test
    public void testSenecaTitle() {
        assertEquals("Seneca", seneca.getTitle());
    }

}
