package dtd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageTypeIconTest {

    private PackageTypeIcon packageTypeIcon;

    @Before
    public void setUp() throws Exception {
        packageTypeIcon = new PackageTypeIcon();
    }

    @After
    public void tearDown() throws Exception {
        packageTypeIcon = null;
    }

    @Test
    public void testSetIconType() {
        packageTypeIcon.setIconType("My Icon");
        assertEquals("My Icon", packageTypeIcon.getIconType());
    }

    @Test
    public void testSetPackageTypes() {
        List<String> packageTypes = new ArrayList<String>(Arrays.asList("Package Type One"));
        packageTypeIcon.setPackageTypes(packageTypes);
        assertEquals(packageTypes, packageTypeIcon.getPackageTypes());
    }
}
