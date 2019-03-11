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

    public List<Field> getStandardFields() throws IOException {
        Map<String, Object> metadataColumnMap = getColumnMap(GENERAL_INFORMATION_SHEET);
        String range = GENERAL_INFORMATION_SHEET + "!2:99";
        List fields = new ArrayList<Field>();
        ValueRange response = service.spreadsheets().values()
                .get(this.spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();
        System.out.println("Found " + rows.size() + " rows in " + range);

        for (List row : rows) {
            Map metadataRow = populateMetadataMap(metadataColumnMap, row);
            fields.add(convertMapToField(metadataRow));
        }
        return fields;
    }

    public Map<String, TypeSpecificElement> getTypeSpecificElements() throws IOException {
        Map typeSpecificElements = new HashMap<String, TypeSpecificElement>();
        TypeSpecificElement typeSpecificElement = new TypeSpecificElement();
        typeSpecificElement.setSections(getTypeSpecificSections(TRANSCRIPTOMICS_SHEET));
        typeSpecificElements.put("Transcriptomics", typeSpecificElement);
        typeSpecificElement = new TypeSpecificElement();
        typeSpecificElement.setSections(getTypeSpecificSections(PROTEOMICS_SHEET));
        typeSpecificElements.put("Proteomics", typeSpecificElement);
        return typeSpecificElements;
    }

    public List<Section> getTypeSpecificSections(String sheetName) throws IOException {
        Map<String, Object> metadataColumnMap = getColumnMap(GENERAL_INFORMATION_SHEET);
        List<Section> sections = new ArrayList<Section>();
        String range = sheetName + "!2:999";
        ValueRange response = service.spreadsheets().values()
                .get(this.spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();
        System.out.println("Found " + rows.size() + " rows in " + range);

        List<Field> fields = new ArrayList<Field>();
        Section section = new Section();

        List row = rows.get(0);
        Map metadataRow = populateMetadataMap(metadataColumnMap, row);
        fields.add(convertMapToField(metadataRow));
        String lastRowModule = (String) metadataRow.get(METADATA_MODULE_COLUMN_KEY);
        section.setSectionHeader(lastRowModule);

        for (int i = 1; i < rows.size(); i++) {
            metadataRow = populateMetadataMap(metadataColumnMap, rows.get(i));
            String thisRowModule = (String) metadataRow.get(METADATA_MODULE_COLUMN_KEY);
            if (!thisRowModule.equals(lastRowModule)) {
                section.setFields(fields);
                sections.add(section);
                section = new Section();
                section.setSectionHeader(thisRowModule);
                fields = new ArrayList<Field>();
            }
            fields.add(convertMapToField(metadataRow));
            lastRowModule = thisRowModule;
        }
        section.setFields(fields);
        sections.add(section);
        return sections;
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
        String validations = (String) metadataRow.get(VALIDATIONS_COLUMN_KEY);
        field.setValidations(validations.split(","));
        return field;

    }

    private Map<String, Object> getColumnMap(String sheetName) throws IOException {
        Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
        String range = sheetName + "!1:1";
        ValueRange response = service.spreadsheets().values()
                .get(this.spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
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
            }
            rowIndex++;
        }
        return columnMap;
    }

    private Boolean convertToBoolean(String value) {
        boolean booleanValue = false;
        if (value == "Yes" || value == "Y") {
            booleanValue = true;
        }
        return booleanValue;
    }

}
