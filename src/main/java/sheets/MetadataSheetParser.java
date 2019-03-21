package sheets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import dtd.Field;
import dtd.PackageTypeIcon;
import dtd.Section;
import dtd.TypeSpecificElement;

public class MetadataSheetParser {

    private Sheets service;
    private String spreadsheetId;
    private static final String GENERAL_INFORMATION_SHEET = "Dataset Information";
    private static final String TRANSCRIPTOMICS_SHEET = "Transcriptomics";
    private static final String PROTEOMICS_SHEET = "Proteomics";
    private static final String VALUES_SHEET = "Drop-down Values";
    private static final String DATA_TYPE_SHEET = "Data Types";
    private static final String VERSION_SHEET = "Version";

    private static final String METADATA_MODULE_COLUMN_KEY = "Metadata Module";
    private static final String LABEL_COLUMN_KEY = "Property";
    private static final String TYPE_COLUMN_KEY = "Property Type";
    private static final String FIELD_NAME_COLUMN_KEY = "Field Name";
    private static final String REQUIRED_COLUMN_KEY = "Required?";
    private static final String VALIDATIONS_COLUMN_KEY = "Validations?";
    private static final String LINKED_WITH_COLUMN_KEY = "Linked With";
    private static final String DISPLAY_WHEN_COLUMN_KEY = "Display When";
    private static final String CONSTRAINTS_COLUMN_KEY = "Constraints";
    private static final String ADDITIONAL_PROPS_COLUMN_KEY = "Additional Props";

    private static final String DROP_DOWN_TYPE_VALUE = "Drop-down";
    private static final String MULTI_SELECT_TYPE_VALUE = "Multi-select";
    private static final String OTHER_VALUE = "Other";

    private static final String CELL_RANGE_NOHEADER = "1:999";
    private static final String CELL_RANGE_HEADER = "2:999";

    private static final String METADATA_VERSION_RANGE = "A2:A2";
    private static final String DATASET_INFORMATION_VERSION_RANGE = "B2:B2";



    public MetadataSheetParser(Sheets service, String spreadSheetId) throws IOException {
        this.service = service;
        this.spreadsheetId = spreadSheetId;
    }

    public List<PackageTypeIcon> getPackageTypeIcons() throws IOException {
        Map<String, PackageTypeIcon> packageTypeIconsMap = new HashMap<String, PackageTypeIcon>();
        String range = getRange(DATA_TYPE_SHEET, CELL_RANGE_HEADER);
        List <List<Object>> rows = getRows(range);
        for (List row: rows) {
            String packageType = (String) row.get(0);
            String iconType = (String) row.get(3);
            if (packageTypeIconsMap.containsKey(iconType)) {
                packageTypeIconsMap.get(iconType).getPackageTypes().add(packageType);
            } else {
                PackageTypeIcon packageTypeIcon = new PackageTypeIcon();
                packageTypeIcon.setIconType(iconType);
                packageTypeIcon.setPackageTypes((new ArrayList<String>(Arrays.asList(packageType))));
                packageTypeIconsMap.put(iconType, packageTypeIcon);
            }
        }
        return new ArrayList(packageTypeIconsMap.values());
    }

    public Double getMetadataVersion() throws IOException {
        String range = getRange(VERSION_SHEET, METADATA_VERSION_RANGE);
        List <List<Object>> rows = getRows(range);
        Double version = Double.parseDouble((String)rows.get(0).get(0));
        return version;
    }

    public Double getDatasetInformationVersion() throws IOException {
        String range = getRange(VERSION_SHEET, DATASET_INFORMATION_VERSION_RANGE);
        List <List<Object>> rows = getRows(range);
        Double version = Double.parseDouble((String)rows.get(0).get(0));
        return version;
    }

    public Map<String, TypeSpecificElement> getTypeSpecificElements() throws IOException {
        Map typeSpecificElements = new HashMap<String, TypeSpecificElement>();
        String range = getRange(DATA_TYPE_SHEET, CELL_RANGE_HEADER);
        List <List<Object>> rows = getRows(range);
        for (List row: rows) {
            String versionRow = (String) row.get(2);
            if (!versionRow.isEmpty()) {
                TypeSpecificElement element = new TypeSpecificElement();
                String dataType = (String) row.get(0);
                element.setDataType(dataType);
                element.setCategory((String) (row.get(1)));
                element.setVersion(Double.parseDouble((String) row.get(2)));
                typeSpecificElements.put(dataType, element);
                element.setSectionMap(new LinkedHashMap<String, Section>());
            }
        }
        return typeSpecificElements;
    }

    public List<Field> getStandardFields() throws IOException {
        String dropdownRange = getRange(VALUES_SHEET, CELL_RANGE_NOHEADER);
        Map<String, List<String>> dropdownValueMap = getDropdownValues(dropdownRange);
        return getFieldsInSheet(GENERAL_INFORMATION_SHEET, new ArrayList<String>(), dropdownValueMap);
    }

    public List<Field> getAllFields(List<String> dataTypes) throws IOException {
        Map<String, List<String>> dropdownValueMap = getDropdownValues(getRange(VALUES_SHEET, CELL_RANGE_NOHEADER));
        List<Field> fields = new ArrayList<dtd.Field>();
        fields.addAll(getFieldsInSheet(TRANSCRIPTOMICS_SHEET, dataTypes, dropdownValueMap));
        fields.addAll(getFieldsInSheet(PROTEOMICS_SHEET, dataTypes, dropdownValueMap));
        return fields;
    }

    public void populateTypeSpecificElements(Map<String, TypeSpecificElement> typeSpecificElements, List<Field> fields) throws IOException {
        for (Field field : fields) {
            for (Map.Entry<String, Boolean> dataType : field.getDataTypes().entrySet()) {
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
    }

    public List<Field> getFieldsInSheet(String sheetName, List<String> dataTypes, Map<String, List<String>> dropdownValueMap) throws IOException {
        List<Field> fields = new ArrayList<Field>();
        Map<String, Object> metadataColumnMap = getColumnMap(sheetName);
        List<List<Object>> rows = getRows(getRange(sheetName, CELL_RANGE_HEADER));

        for (int i = 0; i < rows.size(); i++) {
            Map metadataRow = fillMetadataRow(metadataColumnMap, rows.get(i));
            Field field = convertMapToField(metadataRow);
            Map<String, Boolean> dataTypeMembership = getDataTypeMembership(metadataRow, dataTypes);
            field.setDataTypes(dataTypeMembership);
            if (field.getType().equals(DROP_DOWN_TYPE_VALUE) || field.getType().equals(MULTI_SELECT_TYPE_VALUE) && dropdownValueMap.containsKey(field.getLabel())) {
                List<String> values = dropdownValueMap.get(field.getLabel());
                if(values.contains(OTHER_VALUE)) {
                    field.setOtherAvailable(true);
                    values.remove(OTHER_VALUE);
                }
                field.setValues(values);
            }
            fields.add(field);
        }

        for (Field field : fields) {
            if (field.getLinkedWith() != null) {
                for (Field field2: fields) {
                    if (field.getLinkedWith().equals(field2.getLabel())) {
                        field.setLinkedWith(field2.getFieldName());
                    }
                }
            }
        }

        return fields;
    }

    public Map<String, List<String>> getDropdownValues(String range) throws IOException {
        Map<String, List<String>> dropDownValueMap = new HashMap<String, List<String>>();
        List<List<Object>> rows = getRows(range);
        for (List row : rows) {
            String propertyName = (String) row.get(0);
            String value = (String) row.get(2);
            if (dropDownValueMap.containsKey(propertyName)) {
                dropDownValueMap.get(propertyName).add(value);
            } else {
                dropDownValueMap.put(propertyName, new ArrayList<String>(Arrays.asList(value)));
            }
        }
        return dropDownValueMap;
    }

    public Field convertMapToField(Map<String, Object> metadataRow) {
        Field field = new Field();
        field.setType((String) metadataRow.get(TYPE_COLUMN_KEY));
        field.setLabel((String) metadataRow.get(LABEL_COLUMN_KEY));
        field.setAdditionalProps((String) metadataRow.get(ADDITIONAL_PROPS_COLUMN_KEY));
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

    public Map<String, Boolean> getDataTypeMembership(Map<String, Object> metadataRow, List<String> dataTypes) {
        Map<String, Boolean> dataTypeMembership = new HashMap<String, Boolean>();
        for (String dataType: dataTypes) {
            Boolean inDataType = convertToBoolean((String) metadataRow.get(dataType));
            dataTypeMembership.put(dataType, inDataType);
        }
        return dataTypeMembership;
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

    public Map<String, Object> fillMetadataRow(Map<String, Object> columnMap, List row) {
        Map<String, Object> metaDataMap = new HashMap<String, Object>();
        Iterator it = columnMap.entrySet().iterator();
        int rowIndex = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (rowIndex < row.size()) {
                metaDataMap.put((String) entry.getKey(), row.get(rowIndex));
            } else {
                metaDataMap.put((String) entry.getKey(), null);
            }
            rowIndex++;
        }
        return metaDataMap;
    }

    public Boolean convertToBoolean(String value) {
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

    public List<String> convertTypeSpecificElementsToDataTypes(Map<String, TypeSpecificElement> typeSpecificElements) {
        List<String> dataTypes = new ArrayList<String>();
        for (Map.Entry<String, TypeSpecificElement> element: typeSpecificElements.entrySet()) {
            dataTypes.add(element.getKey());
        }
        return dataTypes;
    }

    public String getRange(String sheetName, String cellRange) {
        return sheetName + "!" + cellRange;
    }



}
