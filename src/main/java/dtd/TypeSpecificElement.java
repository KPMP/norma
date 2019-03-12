package dtd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TypeSpecificElement {
    @JsonIgnore
    String dataType;
    @JsonIgnore
    String category;
    @JsonIgnore
    HashMap<String, Section> sectionMap;

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

    public HashMap<String, Section> getSectionMap() {
        return sectionMap;
    }

    public void setSectionMap(HashMap<String, Section> sectionMap) {
        this.sectionMap = sectionMap;
    }
}
