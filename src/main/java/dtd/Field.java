package dtd;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRawValue;

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
    String additionalProps;
    String constrainedBy;
    Map<String, String[]> constraints;

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
        return validations;
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
        return additionalProps;
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
}
