package dtd;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Field {
    String label;
    String type;
    Boolean required;
    String fieldName;
    String[] validations;
    Boolean otherAvailable;
    String linkedWith;
    String displayWhen;
    Boolean alphaSort;
    String[] values;
    @JsonRawValue
    String additionalProps = null;
    String constrainedBy = null;
    Map<String, String[]> constraints;
    @JsonIgnore
    Map<String, Boolean> dataTypes;
    @JsonIgnore
    String sectionName;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String[] getValidations() {
        if (validations == null) {
            return null;
        } else {
            return validations;
        }
    }

    public void setValidations(String[] validations) {
        this.validations = validations;
    }

    public Boolean getOtherAvailable() {
        return otherAvailable;
    }

    public void setOtherAvailable(Boolean otherAvailable) {
        this.otherAvailable = otherAvailable;
    }

    public Boolean getAlphaSort() {
        return alphaSort;
    }

    public void setAlphaSort(Boolean alphaSort) {
        this.alphaSort = alphaSort;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getAdditionalProps() {
            if (additionalProps == null || additionalProps == "") {
                return null;
            } else {
                return additionalProps;
            }

    }

    public void setAdditionalProps (String additionalProps) {
        this.additionalProps = additionalProps;
    }

    public String getConstrainedBy() {
        return constrainedBy;
    }

    public void setConstrainedBy(String constrainedBy) {
        this.constrainedBy = constrainedBy;
    }

    public Map<String, String[]> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, String[]> constraints) {
        this.constraints = constraints;
    }

    public String getLinkedWith() {
        return linkedWith;
    }

    public void setLinkedWith(String linkedWith) {
        this.linkedWith = linkedWith;
    }

    public String getDisplayWhen() {
        return displayWhen;
    }

    public void setDisplayWhen(String displayWhen) {
        this.displayWhen = displayWhen;
    }

    public Map<String, Boolean> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Map<String, Boolean> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
