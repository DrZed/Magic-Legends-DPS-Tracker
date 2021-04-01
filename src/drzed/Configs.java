package drzed;

import hxckdms.hxcconfig.Config;

@Config
public class Configs {
    public static int combatLogPollRate = 1000;
    public static String combatLogFolder = "A:/Games/Magic Legends_en/Magic Legends/Live/logs/GameClient";
    public static String theme = "dark";
    public static int endEncounterTimer = 120000;
    public static String defaultFilter = "Keldon Warlord";
    public static int guiPollRate = 15;
    @Config.comment("you can remove N: %7$s from the front if you are going to use a smaller width")
    public static String miniDisplayFormat = "N: %7$s DPS: %1$s DMG: %2$s TS: \"%3$s\" DPS: %4$s DMG: %5$s T:%6$s";
    public static int miniDisplayWidth = 720;
    public static boolean miniMode = false;
}
