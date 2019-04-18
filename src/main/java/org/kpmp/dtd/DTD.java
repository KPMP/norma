package org.kpmp.dtd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DTD {

    private Double version;
    private StandardFields standardFields;
    private Map<String, TypeSpecificElement> typeSpecificElements;

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

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

    public StandardFields getStandardFields() {
        return standardFields;
    }

    public void setStandardFields(StandardFields standardFields) {
        this.standardFields = standardFields;
    }

    public String generateJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        return mapper.writeValueAsString(this);
    }
}
