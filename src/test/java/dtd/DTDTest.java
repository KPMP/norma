package dtd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DTDTest {

    private DTD dtd;

    @Before
    public void setUp() throws Exception {
        dtd = new DTD();
    }

    @After
    public void tearDown() throws Exception {
        dtd = null;
    }

    @Test
    public void testSetVersion() {
        dtd.setVersion("Version 0.1");
        assertEquals("Version 0.1", dtd.getVersion());
    }

    @Test
    public void testSetTypeSpecificElements() {
        Map<String, TypeSpecificElement> typeSpecificElementMap = new HashMap();
        TypeSpecificElement typeSpecificElement = new TypeSpecificElement();
        typeSpecificElementMap.put("Element1", typeSpecificElement);
        List<Object> typeSpecificElementList = new ArrayList<Object>();
        typeSpecificElementList.add(typeSpecificElementMap.entrySet().iterator().next());
        dtd.setTypeSpecificElements(typeSpecificElementMap);
        assertEquals(typeSpecificElementList, dtd.getTypeSpecificElements());
    }

    @Test
    public void testSetSection() {
        Section section = new Section();
        dtd.setStandardFields(section);
        assertEquals(section, dtd.getStandardFields());
    }

    @Test
    public void testGenerateJSON() throws JsonProcessingException {
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
        field.setValidations(new String[]{"Validation1", "Validation2"});
        field.setValues(new ArrayList<String>(Arrays.asList("Value1", "Value2")));
        HashMap<String, Boolean> dataTypes = new HashMap<String, Boolean>();
        dataTypes.put("Data Type", true);
        field.setDataTypes(dataTypes);
        field.setAlphaSort(true);
        field.setOtherAvailable(true);
        field.setSectionName("Section");

        Section section = new Section();
        section.setSectionHeader("Section Header");
        section.setFields(new ArrayList<Field>(Arrays.asList(field)));

        TypeSpecificElement typeSpecificElement = new TypeSpecificElement();
        Map<String, Section> sectionMap = new HashMap<String, Section>();
        sectionMap.put("Section 1", section);
        typeSpecificElement.setSectionMap(sectionMap);

        Map<String, TypeSpecificElement> typeSpecificElementMap = new HashMap<String, TypeSpecificElement>();
        typeSpecificElementMap.put("Data Type 1", typeSpecificElement);

        dtd.setVersion("1.0");
        dtd.setStandardFields(section);
        dtd.setTypeSpecificElements(typeSpecificElementMap);

        String expected = "{\"version\":\"1.0\",\"standardFields\":{\"sectionHeader\":\"Section Header\",\"fields\":[{\"label\":\"Label\",\"type\":\"Type\",\"required\":true,\"fieldName\":\"Field Name\",\"validations\":[\"Validation1\",\"Validation2\"],\"otherAvailable\":true,\"linkedWith\":\"Linked With\",\"displayWhen\":\"Display When\",\"alphaSort\":true,\"values\":[\"Value1\",\"Value2\"],\"additionalProps\":{\"additionalProp\":\"myProp\"},\"constrainedBy\":\"Constrained By\"}]},\"typeSpecificElements\":[{\"Data Type 1\":{\"sections\":[{\"sectionHeader\":\"Section Header\",\"fields\":[{\"label\":\"Label\",\"type\":\"Type\",\"required\":true,\"fieldName\":\"Field Name\",\"validations\":[\"Validation1\",\"Validation2\"],\"otherAvailable\":true,\"linkedWith\":\"Linked With\",\"displayWhen\":\"Display When\",\"alphaSort\":true,\"values\":[\"Value1\",\"Value2\"],\"additionalProps\":{\"additionalProp\":\"myProp\"},\"constrainedBy\":\"Constrained By\"}]}]}}]}";
        assertEquals(expected, dtd.generateJSON());

    }

    @Test
    public void testGenerateJSONWithNullsAndEmpty() throws JsonProcessingException {
        Field field = new Field();
        field.setType("Type");
        field.setLabel("Label");
        field.setAdditionalProps("");
        field.setConstrainedBy("Constrained By");
        field.setLinkedWith("");
        field.setFieldName("Field Name");
        field.setRequired(true);
        field.setDisplayWhen(null);
        field.setSectionName("Section Name");
        field.setValidations(new String[]{"Validation1", "Validation2"});
        field.setValues(new ArrayList<String>(Arrays.asList("Value1", "Value2")));
        HashMap<String, Boolean> dataTypes = new HashMap<String, Boolean>();
        dataTypes.put("Data Type", true);
        field.setDataTypes(dataTypes);
        field.setAlphaSort(true);
        field.setOtherAvailable(true);
        field.setSectionName("Section");

        Section section = new Section();
        section.setSectionHeader("Section Header");
        section.setFields(new ArrayList<Field>(Arrays.asList(field)));

        TypeSpecificElement typeSpecificElement = new TypeSpecificElement();
        Map<String, Section> sectionMap = new HashMap<String, Section>();
        sectionMap.put("Section 1", section);
        typeSpecificElement.setSectionMap(sectionMap);

        Map<String, TypeSpecificElement> typeSpecificElementMap = new HashMap<String, TypeSpecificElement>();
        typeSpecificElementMap.put("Data Type 1", typeSpecificElement);

        dtd.setVersion("1.0");
        dtd.setStandardFields(section);
        dtd.setTypeSpecificElements(typeSpecificElementMap);

        String expected = "{\"version\":\"1.0\",\"standardFields\":{\"sectionHeader\":\"Section Header\",\"fields\":[{\"label\":\"Label\",\"type\":\"Type\",\"required\":true,\"fieldName\":\"Field Name\",\"validations\":[\"Validation1\",\"Validation2\"],\"otherAvailable\":true,\"alphaSort\":true,\"values\":[\"Value1\",\"Value2\"],\"constrainedBy\":\"Constrained By\"}]},\"typeSpecificElements\":[{\"Data Type 1\":{\"sections\":[{\"sectionHeader\":\"Section Header\",\"fields\":[{\"label\":\"Label\",\"type\":\"Type\",\"required\":true,\"fieldName\":\"Field Name\",\"validations\":[\"Validation1\",\"Validation2\"],\"otherAvailable\":true,\"alphaSort\":true,\"values\":[\"Value1\",\"Value2\"],\"constrainedBy\":\"Constrained By\"}]}]}}]}";
        assertEquals(expected, dtd.generateJSON());

    }


}

