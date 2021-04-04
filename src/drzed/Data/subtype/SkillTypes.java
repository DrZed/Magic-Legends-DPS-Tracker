package drzed.Data.subtype;

import drzed.Data.Skill;
import hxckdms.hxcconfig.Config;

import java.util.LinkedHashMap;

@Config
@SuppressWarnings("WeakerAccess")
public class SkillTypes {
    public static LinkedHashMap<String, Skill> skillTypesList = new LinkedHashMap<>();

    public SkillTypes() { }

    public static String getType(String name, String id, double mag) {
        return getOrAddSkill(name, id, mag).skillType;
    }

//Pn.5b8h1s=[ name=power that gives a bonus when you drop below a pct of mana ]
    public static String getSkillName(String id) {
        if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
            return "Helm of the Harvester";
        }
        return skillTypesList.get(id).skillName;
    }

    private static Skill getOrAddSkill(String name, String id, double mag) {
        if (!skillTypesList.containsKey(id)) {
            if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
                name = "Helm of the Harvester";
            }
            skillTypesList.put(id, new Skill(name, id, (mag > 0 ? "DMG" : "HEAL")));
        }
        return skillTypesList.get(id);
    }
}
