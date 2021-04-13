package drzed.Data;

public class AbilityData {
    public String skillType;
    public String skillName;
    public String skillID;
    public AbilityData() {}

    public AbilityData(String name, String id, String type) {
        skillType = type;
        skillID = id;
        skillName = name;
    }
}
