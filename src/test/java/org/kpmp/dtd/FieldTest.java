package org.kpmp.dtd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {

    private Field field;

    @Before
    public void setUp() throws Exception {
        field = new Field();
    }

    @After
    public void tearDown() throws Exception {
        field = null;
    }

    @Test
    public void testSettersAndGetters() throws Exception {

        Field field = new Field();
        field.setType("Type");
        field.setLabel("Label");
        field.setAdditionalProps("{\"additionalProp\":\"myProp\"}");
        field.setConstrainedBy("Constrained By");
        field.setLinkedWith("Linked With");
        field.setFieldName("Field Name");
        field.setRequired(true);
        field.setDisplayWhen("Display When");
        field.setSectionName("Section Name");
        String[] validations = new String[]{"Validation1", "Validation2"};
        field.setValidations(validations);
        List<String> values = new ArrayList<String>(Arrays.asList("Value1", "Value2"));
        field.setValues(values);
        HashMap<String, Boolean> dataTypes = new HashMap<String, Boolean>();
        dataTypes.put("Data Type", true);
        field.setDataTypes(dataTypes);
        field.setAlphaSort(true);
        field.setOtherAvailable(true);

        assertEquals("Type", field.getType());
        assertEquals("Label", field.getLabel());
        assertEquals("{\"additionalProp\":\"myProp\"}", field.getAdditionalProps());
        assertEquals("Constrained By", field.getConstrainedBy());
        assertEquals("Linked With", field.getLinkedWith());
        assertEquals("Field Name", field.getFieldName());
        assertTrue(field.getRequired());
        assertEquals("Section Name", field.getSectionName());
        assertEquals(validations, field.getValidations());
        assertEquals(values, field.getValues());
        assertEquals(dataTypes, field.getDataTypes());
        assertTrue(field.getAlphaSort());
        assertTrue(field.getOtherAvailable());

    }

    @Test
    public void testSettersAndGettersWithEmptys() throws Exception {

        Field field = new Field();
        field.setType("Type");
        field.setLabel("Label");
        field.setAdditionalProps("");
        field.setConstrainedBy("Constrained By");
        field.setLinkedWith("Linked With");
        field.setFieldName("Field Name");
        field.setRequired(true);
        field.setDisplayWhen("Display When");
        field.setSectionName("Section Name");
        field.setValidations(null);
        List<String> values = new ArrayList<String>(Arrays.asList("Value1", "Value2"));
        field.setValues(values);
        HashMap<String, Boolean> dataTypes = new HashMap<String, Boolean>();
        dataTypes.put("Data Type", true);
        field.setDataTypes(dataTypes);
        field.setAlphaSort(true);
        field.setOtherAvailable(true);

        assertEquals("Type", field.getType());
        assertEquals("Label", field.getLabel());
        assertEquals(null, field.getAdditionalProps());
        assertEquals("Constrained By", field.getConstrainedBy());
        assertEquals("Linked With", field.getLinkedWith());
        assertEquals("Field Name", field.getFieldName());
        assertTrue(field.getRequired());
        assertEquals("Section Name", field.getSectionName());
        assertEquals(null, field.getValidations());
        assertEquals(values, field.getValues());
        assertEquals(dataTypes, field.getDataTypes());
        assertTrue(field.getAlphaSort());
        assertTrue(field.getOtherAvailable());

    }
}
