package drzed;

import hxckdms.hxcconfig.Config;

import java.util.ArrayList;
import java.util.List;

@Config
@SuppressWarnings("WeakerAccess")
public class Configs {
    @Config.comment("Combat log folder")
    public static String combatLogFolder = "";
    @Config.comment("How long between damage taken/dealt should count as a new encounter/fight")
    public static int endEncounterTimer = 60000;
    @Config.comment("This is your player name")
    public static String selfPlayerName = "Keldon Warlord";
    @Config.comment("How long to wait before looking for updates to gui in milliseconds, 15 = 66fps")
    public static int guiPollRate = 15;
    @Config.comment("you can remove N: %7$s from the front if you are going to use a smaller width")
    public static String miniDisplayFormat = "N: %7$s DPS: %1$s DMG: %2$s TS: \"%3$s\" DPS: %4$s DMG: %5$s T:%6$s";
    @Config.comment("This is overlay mode's window width")
    public static int miniDisplayWidth = 720;
    @Config.comment("This is overlay mode shows only DPS, DMG, Top Skill")
    public static boolean miniMode = false;
    @Config.comment("This color is used for the cell background of your own pets")
    public static String selfColor = "#004400";
    @Config.comment("This color is used for the cell background of other players")
    public static String playerColor = "#004444";
    @Config.comment("This increases the number of lines parsed per display refresh")
    public static int linesPerPoll = 20;
    @Config.comment("This enables displaying labels on the pie slices of the pie chart")
    public static boolean pieChartLabels = false;
}
