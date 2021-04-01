package drzed;

import hxckdms.hxcconfig.Config;

import java.util.LinkedHashMap;

@Config
@SuppressWarnings("WeakerAccess")
public class SkillTypes {
    public static LinkedHashMap<String, Skill> skillTypesList = new LinkedHashMap<>();

    public SkillTypes() { }

    public static String getType(String name, String id, double mag) {
//        System.out.println("adding skill " + name + " by id " + id);
        return getOrAddSkill(name, id, mag).skillType;
    }

    public static String getSkillName(String id) {
        return skillTypesList.get(id).skillName;
    }

    private static Skill getOrAddSkill(String name, String id, double mag) {
        if (!skillTypesList.containsKey(id)) {
            System.out.println("Adding skill to list: " + name);
            skillTypesList.put(id, new Skill(name, id, (mag > 0 ? "DMG" : "HEAL")));
        }
        return skillTypesList.get(id);
    }
}
