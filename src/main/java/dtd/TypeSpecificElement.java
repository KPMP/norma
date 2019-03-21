package dtd;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TypeSpecificElement {
    @JsonIgnore
    private String dataType;
    @JsonIgnore
    private String category;
    @JsonIgnore
    private Map<String, Section> sectionMap;
    private Double version;


    public List<Section> getSections() {
        return new ArrayList<Section>(sectionMap.values());
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Section> getSectionMap() {
        return sectionMap;
    }

    public void setSectionMap(Map<String, Section> sectionMap) {
        this.sectionMap = sectionMap;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }
}
