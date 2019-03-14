package sheets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Get;
import com.google.api.services.sheets.v4.model.ValueRange;

import dtd.Field;
import dtd.Section;
import dtd.TypeSpecificElement;

public class MetadataSheetParserTest {

    @Mock
    private Sheets sheets;
    private MetadataSheetParser parser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        parser = new MetadataSheetParser(sheets, "");
    }

    @Test
    public void testConvertMapToField() {
        Map<String, Object> metadataRow = new HashMap<String, Object>();
        metadataRow.put("Metadata Module", "A Module");
        metadataRow.put("Property", "A Property");
        metadataRow.put("Bulk total/mRNA", "Y");
        metadataRow.put("Bulk microRNA", "N");
        metadataRow.put("Segmental miRNA", "Y");
        metadataRow.put("Sub-segmental RNA-Seq", "Y");
        metadataRow.put("Single-cell RNA-Seq", "N");
        metadataRow.put("Single-nucleus RNA-Seq", "N");
        metadataRow.put("Property Type", "Type");
        metadataRow.put("Required?", "Y");
        metadataRow.put("Validations?", "valid1,valid2");
        metadataRow.put("Linked With", "Linky");
        metadataRow.put("Display When", "When?");
        metadataRow.put("Constraints", "Straints");
        metadataRow.put("Field Name", "Namey Name");
        metadataRow.put("Additional Props", "{\"Proppy Prop\": \"Proppy\"}");
        metadataRow.put("Notes", "Lotsa Notes");

        Field field = parser.convertMapToField(metadataRow);

        assertEquals("A Module", field.getSectionName());
        assertEquals("A Property", field.getLabel());
        assertEquals("Type", field.getType());
        assertEquals(true, field.getRequired());
        assertEquals(new String[]{"valid1","valid2"}, field.getValidations());
        assertEquals("Linky", field.getLinkedWith());
        assertEquals("When?", field.getDisplayWhen());
        assertEquals("Straints", field.getConstrainedBy());
        assertEquals("Namey Name", field.getFieldName());
        assertEquals("{\"Proppy Prop\": \"Proppy\"}", field.getAdditionalProps());

    }

    @Test
    public void getDataTypeMembership() {
        Map<String, Object> metadataRow = new HashMap<String, Object>();
        metadataRow.put("Metadata Module", "A Module");
        metadataRow.put("Property", "A Property");
        metadataRow.put("Bulk total/mRNA", "Y");
        metadataRow.put("Bulk microRNA", "N");
        metadataRow.put("Segmental miRNA", "Y");
        metadataRow.put("Sub-segmental RNA-Seq", "Y");
        metadataRow.put("Single-cell RNA-Seq", "N");
        metadataRow.put("Single-nucleus RNA-Seq", "N");
        metadataRow.put("Property Type", "Type");
        metadataRow.put("Required?", "Y");
        metadataRow.put("Validations?", "valid1,valid2");
        metadataRow.put("Linked With", "Linky");
        metadataRow.put("Display When", "When?");
        metadataRow.put("Constraints", "Straints");
        metadataRow.put("Field Name", "Namey Name");
        metadataRow.put("Additional Props", "{\"Proppy Prop\": \"Proppy\"}");
        metadataRow.put("Notes", "Lotsa Notes");
        List<String> dataTypes = new ArrayList<String>(Arrays.asList("Bulk total/mRNA","Bulk microRNA","Segmental miRNA","Sub-segmental RNA-Seq","Single-cell RNA-Seq","Single-nucleus RNA-Seq"));
        Map<String, Boolean> dataTypeMembership = parser.getDataTypeMembership(metadataRow, dataTypes);
        assertTrue(dataTypeMembership.get("Bulk total/mRNA"));
        assertFalse(dataTypeMembership.get("Bulk microRNA"));
        assertTrue(dataTypeMembership.get("Segmental miRNA"));
        assertTrue(dataTypeMembership.get("Sub-segmental RNA-Seq"));
        assertFalse(dataTypeMembership.get("Single-cell RNA-Seq"));
        assertFalse(dataTypeMembership.get("Single-nucleus RNA-Seq"));
    }

    @Test
    public void testFillMetadataRow() {
        List<Object> row = new ArrayList<Object>(Arrays.asList("Col1Val", "Col2Val", "Col3Val"));
        Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
        columnMap.put("Column 1", "");
        columnMap.put("Column 2", "");
        columnMap.put("Column 3", "");
        Map<String, Object> metadataRow = parser.fillMetadataRow(columnMap, row);
        assertEquals("Col1Val", metadataRow.get("Column 1"));
        assertEquals("Col2Val", metadataRow.get("Column 2"));
        assertEquals("Col3Val", metadataRow.get("Column 3"));
    }

    @Test
    public void testFillMetadataRowWithEmptyCells() {
        List<Object> row = new ArrayList<Object>(Arrays.asList("Col1Val"));
        Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
        columnMap.put("Column 1", "");
        columnMap.put("Column 2", "");
        columnMap.put("Column 3", "");
        Map<String, Object> metadataRow = parser.fillMetadataRow(columnMap, row);
        assertEquals("Col1Val", metadataRow.get("Column 1"));
        assertEquals(null, metadataRow.get("Column 2"));
        assertEquals(null, metadataRow.get("Column 3"));
    }

    @Test
    public void testConvertToBoolean() {
        assertTrue(parser.convertToBoolean("Y"));
        assertTrue(parser.convertToBoolean("Yes"));
        assertTrue(parser.convertToBoolean("Yes"));
        assertFalse(parser.convertToBoolean(null));
        assertFalse(parser.convertToBoolean(""));
    }

    @Test
    public void testConvertTypeSpecificElementsToDataTypes() {
        Map<String, TypeSpecificElement> typeSpecificElements = new LinkedHashMap<String, TypeSpecificElement>();
        typeSpecificElements.put("Data Type 1", new TypeSpecificElement());
        typeSpecificElements.put("Data Type 2", new TypeSpecificElement());
        List<String> dataTypes = parser.convertTypeSpecificElementsToDataTypes(typeSpecificElements);
        assertEquals(dataTypes.get(0), "Data Type 1");
        assertEquals(dataTypes.get(1), "Data Type 2");
    }

    @Test
    public void testGetDropdownValues() throws IOException {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row1 = new ArrayList<Object>(Arrays.asList("Property1", "", "Value1"));
        List<Object> row2 = new ArrayList<Object>(Arrays.asList("Property1", "", "Value2"));
        List<Object> row3 = new ArrayList<Object>(Arrays.asList("Property2", "", "Value1"));
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        Spreadsheets spreadsheets = mock(Spreadsheets.class);
        ValueRange valueRange = new ValueRange();
        valueRange.setValues(rows);
        Values values = mock(Values.class);
        Get get = mock(Get.class);

        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        when(values.get("", "Range")).thenReturn(get);
        when(get.execute()).thenReturn(valueRange);

        Map<String, List<String>> dropDownValues = parser.getDropdownValues("Range");
        assertEquals(dropDownValues.get("Property1").get(0), "Value1");
        assertEquals(dropDownValues.get("Property1").get(1), "Value2");
        assertEquals(dropDownValues.get("Property2").get(0), "Value1");
    }

    @Test
    public void testGetFieldsInSheet() throws IOException {
        List<List<Object>> headerRows = new ArrayList<List<Object>>();
        List<Object> headerRow1 = new ArrayList<Object>(Arrays.asList("Property", "Property Type"));
        headerRows.add(headerRow1);

        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row1 = new ArrayList<Object>(Arrays.asList("Field 1", ""));
        List<Object> row2 = new ArrayList<Object>(Arrays.asList("Field 2", ""));
        List<Object> row3 = new ArrayList<Object>(Arrays.asList("Field 3", ""));
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        Spreadsheets spreadsheets = mock(Spreadsheets.class);
        ValueRange valueRange = new ValueRange();
        valueRange.setValues(rows);
        ValueRange valueRange2 = new ValueRange();
        valueRange2.setValues(headerRows);
        Values values = mock(Values.class);
        Get get = mock(Get.class);
        Get get2 = mock(Get.class);

        List<String> dataTypes = new ArrayList<String>();
        Map<String, List<String>> dropDownMap = new HashMap<String, List<String>>();
        dropDownMap.put("Field 1", new ArrayList<String>(Arrays.asList("One", "Two", "Other")));

        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        when(values.get("",  "Sheet!1:1")).thenReturn(get2);
        when(values.get("",  "Sheet!2:999")).thenReturn(get);
        when(get2.execute()).thenReturn(valueRange2);
        when(get.execute()).thenReturn(valueRange);

        List<Field> fields = parser.getFieldsInSheet("Sheet", dataTypes, dropDownMap);
        assertEquals(fields.get(0).getLabel(), "Field 1");
        assertTrue(fields.get(0).getOtherAvailable());
        assertEquals(fields.get(1).getLabel(), "Field 2");
        assertEquals(fields.get(2).getLabel(), "Field 3");
    }


    @Test
    public void testGetTypeSpecificElements() throws IOException {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row1 = new ArrayList<Object>(Arrays.asList("Data Type 1", "Category 1"));
        List<Object> row2 = new ArrayList<Object>(Arrays.asList("Data Type 2", "Category 1"));
        List<Object> row3 = new ArrayList<Object>(Arrays.asList("Data Type 3", "Category 2"));
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        Spreadsheets spreadsheets = mock(Spreadsheets.class);
        ValueRange valueRange = new ValueRange();
        valueRange.setValues(rows);
        Values values = mock(Values.class);
        Get get = mock(Get.class);

        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        when(values.get("", "Data Types!2:999")).thenReturn(get);
        when(get.execute()).thenReturn(valueRange);

        Map<String, TypeSpecificElement> typeSpecificElementMap = parser.getTypeSpecificElements();
        assertEquals("Data Type 1", typeSpecificElementMap.get("Data Type 1").getDataType());
        assertEquals("Data Type 2", typeSpecificElementMap.get("Data Type 2").getDataType());
        assertEquals("Data Type 3", typeSpecificElementMap.get("Data Type 3").getDataType());

    }

    @Test
    public void testPopulateTypeSpecificElements() throws IOException {
        Map<String, TypeSpecificElement> typeSpecificElements = new LinkedHashMap<String, TypeSpecificElement>();

        TypeSpecificElement typeSpecificElement1 = new TypeSpecificElement();
        TypeSpecificElement typeSpecificElement2 = new TypeSpecificElement();

        Map<String, Section> sectionMap1 = new LinkedHashMap<String, Section>();
        Map<String, Section> sectionMap2 = new LinkedHashMap<String, Section>();

        Section section2 = new Section();

        section2.setFields(new ArrayList<Field>());

        sectionMap2.put("Section 2", section2);
        typeSpecificElement1.setSectionMap(sectionMap1);
        typeSpecificElement2.setSectionMap(sectionMap2);
        typeSpecificElements.put("Data Type 1", typeSpecificElement1);
        typeSpecificElements.put("Data Type 2", typeSpecificElement2);

        List<Field> fields = new ArrayList<Field>();
        Field field1 = new Field();
        Field field2 = new Field();
        Map<String, Boolean> dataTypes1 = new HashMap<String, Boolean>();
        Map<String, Boolean> dataTypes2 = new HashMap<String, Boolean>();
        dataTypes1.put("Data Type 1", true);
        dataTypes1.put("Data Type 2", false);
        dataTypes2.put("Data Type 1", false);
        dataTypes2.put("Data Type 2", true);
        field1.setDataTypes(dataTypes1);
        field1.setSectionName("Section 1");
        field2.setDataTypes(dataTypes2);
        field2.setSectionName("Section 2");
        fields.add(field1);
        fields.add(field2);
        parser.populateTypeSpecificElements(typeSpecificElements, fields);
        assertEquals(field1, typeSpecificElements.get("Data Type 1").getSectionMap().get("Section 1").getFields().get(0));
        assertEquals(field2, typeSpecificElements.get("Data Type 2").getSectionMap().get("Section 2").getFields().get(0));
    }

    @Test
    public void testGetRange() {
        assertEquals("SheetName!1:100", parser.getRange("SheetName", "1:100"));
    }

    @After
    public void tearDown() throws Exception {
        parser = null;
    }


}
