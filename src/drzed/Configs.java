package drzed;

import hxckdms.hxcconfig.Config;

import java.util.ArrayList;
import java.util.List;

@Config
public class Configs {
    public static int combatLogPollRate = 1000;
    public static String combatLogFolder = "";
    public static String theme = "dark";
    public static int endEncounterTimer = 120000;
    public static String defaultFilter = "Keldon Warlord";
    public static int guiPollRate = 15;
    @Config.comment("you can remove N: %7$s from the front if you are going to use a smaller width")
    public static String miniDisplayFormat = "N: %7$s DPS: %1$s DMG: %2$s TS: \"%3$s\" DPS: %4$s DMG: %5$s T:%6$s";
    public static int miniDisplayWidth = 720;
    public static boolean miniMode = false;
    public static boolean condensedMode = true;
    public static List<String> bannedEntityIDs = new ArrayList<>();
    static {
        bannedEntityIDs.add("Modifier_Enemy_Augmented_Red_Enraged_Hazard");
        bannedEntityIDs.add("Dom_Shiv_Goblin_Minion_Goblinbombthrower_Bombhazard");
        bannedEntityIDs.add("Spell_Red_Sorcery_Elementalshatter");
        bannedEntityIDs.add("Object_Destructible_Goblin_Tower");
        bannedEntityIDs.add("Spell_Red_Sorcery_Magmavortex_Aoe");
        bannedEntityIDs.add("Spell_Blue_Sorcery_Magneticpulse");
        bannedEntityIDs.add("Spell_Blue_Sorcery_Tornado");
    }
}
