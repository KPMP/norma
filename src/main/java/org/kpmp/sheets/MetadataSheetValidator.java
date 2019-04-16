package org.kpmp.sheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import org.kpmp.dtd.Field;
import org.kpmp.dtd.TypeSpecificElement;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataSheetValidator {

    private MetadataSheetParser parser;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String DATA_TYPE_DROPDOWN = "Package Type";
    private static final String TYPE_DROPDOWN = "Drop-down";
    private static final List<String> INPUT_TYPES = new ArrayList<>(Arrays.asList("Date", TYPE_DROPDOWN, "Multi-select", "Numeric", "Submitter Information", "Text Area", "Text Field"));
    private static final MessageFormat fieldNameCheck = new MessageFormat("Field \"{0}\" has no fieldName.");
    private static final MessageFormat fieldValidationCheck = new MessageFormat("Field \"{0}\" has a validation but no additional props.");
    private static final MessageFormat inputTypeCheck = new MessageFormat("Field \"{0}\" has an invalid input type: {1}.");
    private static final MessageFormat linkedWithCheck = new MessageFormat("Field \"{0}\" is missing a Linked With value.");
    private static final MessageFormat dropdownCheck = new MessageFormat("Field \"{0}\" is a drop-down but missing values.");
    private static final MessageFormat displayWhenCheck = new MessageFormat("Field \"{0}\" Display When value ({1}) missing for \"{2}\".");
    private static final MessageFormat fieldNameDuplicateCheck = new MessageFormat("The fieldName \"{0}\" in \"{1}\" already exists.");
    private static final MessageFormat dataTypeSheetCheck = new MessageFormat("No sheet exists for data type category {0}.");
    private static final MessageFormat dataTypeSheetColumnCheck = new MessageFormat("No column exists in sheet {0} for data type {1}.");

    public MetadataSheetValidator(MetadataSheetParser parser) throws IOException {
        this.parser = parser;
    }

    public void runValidator() throws IOException {
        Map<String, TypeSpecificElement> typeSpecificElements = parser.getTypeSpecificElements();
        List<String> dataTypes = parser.convertTypeSpecificElementsToDataTypes(typeSpecificElements);
        List<Field> fields = parser.getAllFields(dataTypes);
        fields.addAll(parser.getStandardFields());
        log.info("Starting Metadata Sheet Validator");
        checkFields(fields);
        checkDataTypes();
    }


    public void checkFields(List<Field> fields) throws IOException {
        Set<String> fieldNames = new HashSet<>();
        Map<String, List<String>> dropDowns = parser.getDropdownValues();

        for (Field field: fields) {
            String fieldName = field.getFieldName();
            String label = field.getLabel();

            if (fieldName == null || fieldName.equals("")) {
                log.error(fieldNameCheck.format(new Object[] { label }));
            }

            if (!fieldNames.add(fieldName)) {
                log.error(fieldNameDuplicateCheck.format(new Object[] { fieldName, label }));
            }

            if (field.getLinkedWith() != null && !field.getLinkedWith().equals("")) {
                if (field.getDisplayWhen() == null || field.getDisplayWhen().equals("")) {
                    log.error(linkedWithCheck.format(new Object[]{label}));
                } else {
                    List<String> dropdownValues = dropDowns.get(field.getLinkedWithLabel());
                    if (dropdownValues == null || !dropdownValues.contains(field.getDisplayWhen())) {
                        log.error(displayWhenCheck.format(new Object[]{label, field.getDisplayWhen(), field.getLinkedWithLabel()}));
                    }
                }
            }

            if (field.getType().equals(TYPE_DROPDOWN) && field.getValues() == null) {
                log.error(dropdownCheck.format(new Object[] { label }));
            }

            if (!INPUT_TYPES.contains(field.getType())) {
                log.error(inputTypeCheck.format(new Object[] { label, field.getType() }));
            }

            if (field.getValidations() != null && field.getAdditionalProps() == null) {
                log.error(fieldValidationCheck.format(new Object[] { label }));
            }

        }

    }

    public void checkDataTypes() throws IOException {
        Map<String, TypeSpecificElement> typeSpecificElements = parser.getTypeSpecificElements();
        Set dataTypes = typeSpecificElements.keySet();

        Map<String, List<String>> dropDowns = parser.getDropdownValues();
        Set<String> dropdownValues = new HashSet<>(dropDowns.get(DATA_TYPE_DROPDOWN));

        if (!dataTypes.equals(dropdownValues)) {
            log.error("The data types do not match the Package Type dropdown values.");
        }

        Set dataCategories = new HashSet();
        for (Map.Entry<String, TypeSpecificElement> entry : typeSpecificElements.entrySet()) {
            dataCategories.add(entry.getValue().getCategory());
        }

        for (Object dataTypeCategory: dataCategories) {
            String dataTypeCategoryString = (String) dataTypeCategory;
            try {
                Map<String, Object> columnMap = parser.getColumnMap(dataTypeCategoryString);
                for (Map.Entry<String, TypeSpecificElement> entry : typeSpecificElements.entrySet()) {
                    String dataType = entry.getKey();
                    if (entry.getValue().getCategory().equals(dataTypeCategory) && !columnMap.containsKey(dataType)) {
                        log.error(dataTypeSheetColumnCheck.format(new Object[] { dataTypeCategoryString, dataType }));
                    }
                }
            } catch (GoogleJsonResponseException e) {
                log.error(dataTypeSheetCheck.format(new Object[] { dataTypeCategoryString }));
            }
        }
    }


}
