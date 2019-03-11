package dtd;

import java.util.Map;

public class DTD {

    Section standardFields;
    Map<String, TypeSpecificElement> typeSpecificElements;

    public Map<String, TypeSpecificElement> getTypeSpecificElements() {
        return typeSpecificElements;
    }

    public void setTypeSpecificElements(Map<String, TypeSpecificElement> typeSpecificElements) {
        this.typeSpecificElements = typeSpecificElements;
    }

    public Section getStandardFields() {
        return standardFields;
    }

    public void setStandardFields(Section standardFields) {
        this.standardFields = standardFields;
    }
}
