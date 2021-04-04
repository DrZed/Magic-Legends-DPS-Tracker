package drzed.Data;

public class Skill {
    public String skillType;
    public String skillName;
    public String skillID;
    public Skill() {}

    public Skill(String name, String id, String type) {
        skillType = type;
        skillID = id;
        skillName = name;
    }
}
