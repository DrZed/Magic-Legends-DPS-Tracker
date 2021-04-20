package drzed.Data.subtype;

import drzed.Data.Ability;
import drzed.Data.AbilityData;
import hxckdms.hxcconfig.Config;

import java.util.LinkedHashMap;

@Config
@SuppressWarnings("WeakerAccess")
public class AbilityTypes {
    public static LinkedHashMap<String, AbilityData> skillTypesList = new LinkedHashMap<>();

    public AbilityTypes() { }

    public static String getType(String name, String id, double mag) {
        return getOrAddSkill(name, id, mag).skillType;
    }

    public static Ability makeAbility(String name, String id, double d) {
        AbilityData abdat = getOrAddSkill(name, id, d);
        return new Ability(abdat.skillName, abdat.skillID);
    }

    private static AbilityData getOrAddSkill(String abilityName, String abilityID, double mag) {
        abilityID = fixAbils(abilityName, abilityID);
        if (abilityID.equalsIgnoreCase("Ability_Ravnica_Assassin_Primary_Ultimate") ||
                abilityID.equalsIgnoreCase("Ability_Ravnica_Assassin_Passive_Ultimate")) {
            abilityName = "Shadow Assassin";
            abilityID = "Ability_Ravnica_Assassin_Passive_Ultimate";
        }
        if (!skillTypesList.containsKey(abilityID)) {
            if (abilityID.equalsIgnoreCase("Pn.5b8h1s")) {
                abilityName = "Helm of the Harvester";
            }
            if (abilityID.equalsIgnoreCase("Pn.Nznelx")) {
                abilityName = "Goblin Supply Manifest";
            }
            skillTypesList.put(abilityID, new AbilityData(abilityName, abilityID, (mag > 0 ? "DMG" : "HEAL")));
        }
        if (abilityID.equalsIgnoreCase("Pn.5b8h1s")) {
            skillTypesList.get(abilityID).skillName = "Helm of the Harvester";
            skillTypesList.replace(abilityID, skillTypesList.get(abilityID));
        }
        if (abilityID.equalsIgnoreCase("Pn.Nznelx")) {
            skillTypesList.get(abilityID).skillName = "Goblin Supply Manifest";
            skillTypesList.replace(abilityID, skillTypesList.get(abilityID));
        }
        return skillTypesList.get(abilityID);
    }

    public static String fixAbils(String abilityName, String abilityID) {
        String fx = abilityID;

        for (AbilityData value : skillTypesList.values()) {
            if (value.skillName.equalsIgnoreCase(abilityName)) {
                fx = value.skillID;
                break;
            }
        }

        if (fx.equalsIgnoreCase("Ability_Ravnica_Assassin_Primary_Ultimate") ||
                fx.equalsIgnoreCase("Ability_Ravnica_Assassin_Passive_Ultimate")) {
            fx = "Ability_Ravnica_Assassin_Passive_Ultimate";
        }

        return fx;
    }
}
