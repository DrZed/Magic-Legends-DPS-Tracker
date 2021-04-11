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
        id = fixAbils(id);
        if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
            return "Helm of the Harvester";
        }
        return skillTypesList.get(id).skillName;
    }

    private static Skill getOrAddSkill(String name, String id, double mag) {
        id = fixAbils(id);
        if (!skillTypesList.containsKey(id)) {
            if (id.equalsIgnoreCase("Pn.5b8h1s")) { //Because go fk yourself long ass name
                name = "Helm of the Harvester";
            }
            skillTypesList.put(id, new Skill(name, id, (mag > 0 ? "DMG" : "HEAL")));
        }
        return skillTypesList.get(id);
    }

    public static String fixAbils(String abilityID) {
        String fx;
        fx = fixMindlash(abilityID);
        fx = fixShadowBladeThrow(fx);
        fx = fixFortyBlinks(fx);
        return fx;
    }

    private static String fixMindlash(String abilityID) {
        if (abilityID.equalsIgnoreCase("Pn.Amuy251") ||
                abilityID.equalsIgnoreCase("Pn.E19zs41") ||
                abilityID.equalsIgnoreCase("Pn.B6i4z31") ||
                abilityID.equalsIgnoreCase("Pn.67gyc51")) { //Because go fk yourself multiple ids
            return "Pn.Amuy251";
        }
        return abilityID;
    }

    private static String fixShadowBladeThrow(String abilityID) {
        if (abilityID.equalsIgnoreCase("Pn.366m7r") ||
            abilityID.equalsIgnoreCase("Pn.Qxw9eg") ||
            abilityID.equalsIgnoreCase("Pn.Zqrlhr") ||
            abilityID.equalsIgnoreCase("Pn.Ahl9og") ||
            abilityID.equalsIgnoreCase("Pn.J0pngl1") ||
            abilityID.equalsIgnoreCase("Pn.Otexse1") ||
            abilityID.equalsIgnoreCase("Pn.Jcenql1") ||
            abilityID.equalsIgnoreCase("Pn.Jo3n0m1") ||
            abilityID.equalsIgnoreCase("Pn.Vbdlrr") ||
            abilityID.equalsIgnoreCase("Pn.U0a9yg") ||
            abilityID.equalsIgnoreCase("Pn.8io7x51") ) { //Because go fk yourself multiple ids
            return "Pn.366m7r";
        }
        return abilityID;
    }
    private static String fixFortyBlinks(String abilityID) {
        if (abilityID.equalsIgnoreCase("Pn.V7q0n51") ||
            abilityID.equalsIgnoreCase("Pn.X9oug4") ||
            abilityID.equalsIgnoreCase("Pn.Kwy74g") ||
            abilityID.equalsIgnoreCase("Pn.Dobtyd1") ) { //Because go fk yourself multiple ids
            return "Pn.V7q0n51";
        }
        return abilityID;
    }
}
