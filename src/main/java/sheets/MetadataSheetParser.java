package sheets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import dtd.Field;
import dtd.Section;
import dtd.TypeSpecificElement;

public class MetadataSheetParser {

    Sheets service;
    String spreadsheetId;
    public static final String GENERAL_INFORMATION_SHEET = "General Information";
    public static final String TRANSCRIPTOMICS_SHEET = "Transcriptomics";
    public static final String PROTEOMICS_SHEET = "Proteomics";
    public static final String VALUES_SHEET = "Drop-down Values";
    public static final String DATA_TYPE_SHEET = "Data Types";

    public static final String METADATA_MODULE_COLUMN_KEY = "Metadata Module";
    public static final String LABEL_COLUMN_KEY = "Property";
    public static final String TYPE_COLUMN_KEY = "Property Type";
    public static final String FIELD_NAME_COLUMN_KEY = "Field Name";
    public static final String REQUIRED_COLUMN_KEY = "Required?";
    public static final String VALIDATIONS_COLUMN_KEY = "Validations?";
    public static final String LINKED_WITH_COLUMN_KEY = "Linked With";
    public static final String DISPLAY_WHEN_COLUMN_KEY = "Display When";
    public static final String CONSTRAINTS_COLUMN_KEY = "Constraints";
    public static final String ADDITIONAL_PROPS_COLUMN_KEY = "Additional Props";


    public MetadataSheetParser(Sheets service, String spreadSheetId) throws IOException {
        this.service = service;
        this.spreadsheetId = spreadSheetId;
    }

    public Map<String, TypeSpecificElement> getTypeSpecificElements() throws IOException {
        Map typeSpecificElements = new HashMap<String, TypeSpecificElement>();
        String range = DATA_TYPE_SHEET + "!2:999";
        List<List<Object>> rows = getRows(range);
        for (List row: rows) {
            TypeSpecificElement element = new TypeSpecificElement();
            String dataType = (String) row.get(0);
            element.setDataType(dataType);
            element.setCategory((String) (row.get(1)));
            typeSpecificElements.put(dataType, element);
            element.setSectionMap(new HashMap<String, Section>());
        }
        return typeSpecificElements;
    }

    public List<Field> getStandardFields() throws IOException {
        Map<String, Object> metadataColumnMap = getColumnMap(GENERAL_INFORMATION_SHEET);
        String range = GENERAL_INFORMATION_SHEET + "!2:99";
        List fields = new ArrayList<Field>();
        ValueRange response = service.spreadsheets().values()
                .get(this.spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();
        //System.out.println("Found " + rows.size() + " rows in " + range);

        for (List row : rows) {
            Map metadataRow = populateMetadataMap(metadataColumnMap, row);
            fields.add(convertMapToField(metadataRow));
        }
        return fields;
    }

    public Map<String, TypeSpecificElement> populateTypeSpecificElements(Map<String, TypeSpecificElement> typeSpecificElements) throws IOException {
        getFieldsInSheet(TRANSCRIPTOMICS_SHEET, typeSpecificElements);
        getFieldsInSheet(PROTEOMICS_SHEET, typeSpecificElements);
        return typeSpecificElements;
    }

    public Map<String, TypeSpecificElement> getFieldsInSheet(String sheetName, Map<String, TypeSpecificElement> typeSpecificElements) throws IOException {
        Map<String, Object> metadataColumnMap = getColumnMap(sheetName);
        String range = sheetName + "!2:999";
        List<List<Object>> rows = getRows(range);

        //System.out.println("Found " + rows.size() + " rows in " + range);

        Map<String, Boolean> dataTypes = convertTypeSpecificElementsToDataTypes(typeSpecificElements);

        for (int i = 0; i < rows.size(); i++) {
            Map metadataRow = populateMetadataMap(metadataColumnMap, rows.get(i));
            Field field = convertMapToField(metadataRow);
            populateDataTypes(metadataRow, field, dataTypes);
            fillTypeSpecificElements(typeSpecificElements, field);
        }

        return typeSpecificElements;
    }

    private void fillTypeSpecificElements(Map<String, TypeSpecificElement> typeSpecificElements, Field field) {
        Map<String, Boolean> fieldDataTypes = field.getDataTypes();
        for (Map.Entry<String, Boolean> dataType: fieldDataTypes.entrySet()) {
            if (typeSpecificElements.containsKey(dataType.getKey()) && dataType.getValue()) {
                if (typeSpecificElements.get(dataType.getKey()).getSectionMap().containsKey(field.getSectionName())) {
                    typeSpecificElements.get(dataType.getKey()).getSectionMap().get(field.getSectionName()).getFields().add(field);
                } else {
                    Section section = new Section();
                    section.setSectionHeader(field.getSectionName());
                    List<Field> fieldList = new ArrayList<Field>();
                    fieldList.add(field);
                    section.setFields(fieldList);
                    typeSpecificElements.get(dataType.getKey()).getSectionMap().put(field.getSectionName(), section);
                }
            }
        }
    }

    private Map<String, ArrayList<String>> getDropdownValues() {
        return null;
    }

    private Field convertMapToField(Map<String, Object> metadataRow) {
        Field field = new Field();
        field.setType((String) metadataRow.get(TYPE_COLUMN_KEY));
        field.setLabel((String) metadataRow.get(LABEL_COLUMN_KEY));
        field.setAdditionalProps((String) metadataRow.get(ADDITIONAL_PROPS_COLUMN_KEY));
        field.setConstrainedBy((String) metadataRow.get(CONSTRAINTS_COLUMN_KEY));
        field.setLinkedWith((String) metadataRow.get(LINKED_WITH_COLUMN_KEY));
        field.setFieldName((String) metadataRow.get(FIELD_NAME_COLUMN_KEY));
        field.setRequired(convertToBoolean((String) metadataRow.get(REQUIRED_COLUMN_KEY)));
        field.setDisplayWhen((String) metadataRow.get(DISPLAY_WHEN_COLUMN_KEY));
        field.setSectionName((String) metadataRow.get(METADATA_MODULE_COLUMN_KEY));
        String validations = (String) metadataRow.get(VALIDATIONS_COLUMN_KEY);
        if (validations != null && validations != "") {
            field.setValidations(validations.split(","));
        }
        return field;
    }

    private void populateDataTypes(Map<String, Object> metadataRow, Field field, Map<String, Boolean> dataTypes) {
        for (Map.Entry<String, Boolean> dataType: dataTypes.entrySet()) {
            Boolean inDataType = convertToBoolean((String) metadataRow.get(dataType.getKey()));
            dataType.setValue(inDataType);
        }
        field.setDataTypes(dataTypes);
    }

    private Map<String, Object> getColumnMap(String sheetName) throws IOException {
        Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
        String range = sheetName + "!1:1";
        List<List<Object>> values = getRows(range);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            List row = values.get(0);
            for (Object cell : row) {
                columnMap.put((String) cell, null);
            }
        }
        return columnMap;
    }

    private Map populateMetadataMap(Map columnMap, List row) {
        Iterator it = columnMap.entrySet().iterator();
        int rowIndex = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (rowIndex < row.size()) {
                entry.setValue(row.get(rowIndex));
            } else {
                entry.setValue(null);
            }
            rowIndex++;
        }
        return columnMap;
    }

    private Boolean convertToBoolean(String value) {
        boolean booleanValue = false;
        if (value != null && (value.equals("Yes") || value.equals("Y"))) {
            booleanValue = true;
        }
        return booleanValue;
    }

    private List<List<Object>> getRows(String range) throws IOException {
        ValueRange response = service.spreadsheets().values()
                .get(this.spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();
        return rows;
    }

    private Map<String, Boolean> convertTypeSpecificElementsToDataTypes(Map<String, TypeSpecificElement> typeSpecificElements) {
        Map<String, Boolean> dataTypes = new HashMap<String, Boolean>();
        for (Map.Entry<String, TypeSpecificElement> element: typeSpecificElements.entrySet()) {
            dataTypes.put(element.getKey(), false);
        }
        return dataTypes;
    }

}
