package dtd;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StandardFieldsTest {

    private StandardFields standardFields;

    @Before
    public void setUp() throws Exception {
        standardFields = new StandardFields();
    }

    @After
    public void tearDown() throws Exception {
        standardFields = null;
    }

    @Test
    public void testSetVersion() {
        standardFields.setVersion(5.0);
        assertEquals(5.0, standardFields.getVersion(), 0.0001);
    }
}
