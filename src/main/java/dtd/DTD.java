package dtd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DTD {

    Section standardFields;
    Map<String, TypeSpecificElement> typeSpecificElements;

    public List<Object> getTypeSpecificElements() {
        List<Object> typeSpecificElementList = new ArrayList<Object>();
        for (Map.Entry<String, TypeSpecificElement> entry : typeSpecificElements.entrySet()) {
            typeSpecificElementList.add(entry);
        }
        return typeSpecificElementList;
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

    public String generateJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
