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
        id = fixMindlash(id);
        if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
            return "Helm of the Harvester";
        }
        return skillTypesList.get(id).skillName;
    }

    private static Skill getOrAddSkill(String name, String id, double mag) {
        id = fixMindlash(id);
        if (!skillTypesList.containsKey(id)) {
            if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
                name = "Helm of the Harvester";
            }
            skillTypesList.put(id, new Skill(name, id, (mag > 0 ? "DMG" : "HEAL")));
        }
        return skillTypesList.get(id);
    }

    //Pn.Amuy251=[ name=Mindlash ]
//Pn.E19zs41=[ name=Mindlash ]
//Pn.B6i4z31=[ name=Mindlash ]
//Pn.67gyc51=[ name=Mindlash ]
    private static String fixMindlash(String abilityID) {
        if (abilityID.equalsIgnoreCase("Pn.Amuy251") ||
                abilityID.equalsIgnoreCase("Pn.E19zs41") ||
                abilityID.equalsIgnoreCase("Pn.B6i4z31") ||
                abilityID.equalsIgnoreCase("Pn.67gyc51")) { //Because go fk yourself multiple ids
            return "Pn.Amuy251";
        }
        return abilityID;
    }
}
