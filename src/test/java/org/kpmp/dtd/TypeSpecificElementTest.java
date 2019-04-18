package org.kpmp.dtd;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypeSpecificElementTest {

    private TypeSpecificElement typeSpecificElement;

    @Before
    public void setUp() throws Exception {
        typeSpecificElement = new TypeSpecificElement();
    }

    @After
    public void tearDown() throws Exception {
        typeSpecificElement = null;
    }

    @Test
    public void testSetDataType() {
        String dataType = "Data Type";
        typeSpecificElement.setDataType(dataType);
        assertEquals(dataType, typeSpecificElement.getDataType());
    }

    @Test
    public void testSetCategory() {
        String category = "Category";
        typeSpecificElement.setCategory(category);
        assertEquals(category, typeSpecificElement.getCategory());
    }

    @Test
    public void testSetSectionMap() {
        Map<String, Section> sectionMap = new HashMap<String, Section>();
        Section section = new Section();
        sectionMap.put("Section 1", section);
        typeSpecificElement.setSectionMap(sectionMap);
        assertEquals(sectionMap, typeSpecificElement.getSectionMap());
    }

    @Test
    public void testSetVersion() {
        typeSpecificElement.setVersion(1.0);
        assertEquals(1.0, typeSpecificElement.getVersion(), 0.001);
    }
}
