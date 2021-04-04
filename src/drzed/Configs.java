package drzed;

import hxckdms.hxcconfig.Config;

import java.util.ArrayList;
import java.util.List;

@Config
public class Configs {
    @Config.comment("Combat log folder")
    public static String combatLogFolder = "";
    @Config.comment("How long between damage taken/dealt should count as a new encounter/fight")
    public static int endEncounterTimer = 120000;
    @Config.comment("This is your player name")
    public static String defaultFilter = "Keldon Warlord";
    @Config.comment("How long to wait before looking for updates to gui in milliseconds, 15 = 66fps")
    public static int guiPollRate = 15;
    @Config.comment("you can remove N: %7$s from the front if you are going to use a smaller width")
    public static String miniDisplayFormat = "N: %7$s DPS: %1$s DMG: %2$s TS: \"%3$s\" DPS: %4$s DMG: %5$s T:%6$s";
    @Config.comment("This is overlay mode's window width")
    public static int miniDisplayWidth = 720;
    @Config.comment("This is overlay mode shows only DPS, DMG, Top Skill")
    public static boolean miniMode = false;
    @Config.comment("This puts entities of the same entity IDs as same entity in damage list also reduces RAM usage")
    public static boolean condensedMode = true;
    @Config.comment("This color is used for the cell background of your own pets")
    public static String ownPetColor = "#004400";
    @Config.comment("This color is used for the cell background of other player pets")
    public static String selectedPetColor = "#000044";
    @Config.comment("This color is used for the cell background of other players")
    public static String playerColor = "#004444";
    @Config.comment("This blocks certain entities as being listed in Entity List as they're dummy entities")
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
