package drzed;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings({"WeakerAccess","unused"})
public class MagicParser {
    private static int polls = 0;
    private static boolean inEncounter = false;
    static RandomAccessFile in;
    /*static void ParseFile() throws IOException, InterruptedException {
        String line;
        if (in == null) {
             in = new RandomAccessFile(getFile(), "r");
        }
//        while(true) {
            if ((line = in.readLine()) != null) {
//                System.out.println(line);
                polls = 0;
                inEncounter = true;
                parseLine(line);
            } else {
//                Thread.sleep(combatLogPollRate);
                polls++;
            }
            if (polls >= 4 && inEncounter) endEncounter();
//        }
    }*/

    static void ParseFile() throws IOException, InterruptedException {
        String line;
        if (in == null) {
            in = new RandomAccessFile(getFile(), "r");
        }
        if ((line = in.readLine()) != null) {
            polls = 0;
            inEncounter = true;
            parseLine(line);
        } else {
            polls++;
        }
        if (polls >= (Configs.endEncounterTimer / Configs.guiPollRate) && inEncounter) endEncounter();
    }

    private static File getFile() {
        if (Main.Directory.listFiles() != null) {
            ArrayList<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(Main.Directory.listFiles())));
            fileList.removeIf(f -> !f.getName().startsWith("Combatlog"));
            fileList.sort(Comparator.reverseOrder());
            return fileList.get(0);
        } return null;
    }
//21:03:28:13:37:09.4::
// Keldon Warlord,P[440730@31580857 Keldon Warlord@KeldonSlayer#31282],,*,,*,[Sorcery risk/reward],Pn.0xgzi41,Physical,ShieldBreak,40999.3,0
//    Time  ::   ownDsp  ,   ownInt    ,     srcDsp  ,   srcInt   ,  tgtDsp  ,   tgtInt  ,evtDsp,  evtInt  ,type ,flags,mag,magBase

    private static Encounter currentEncounter;
    private static long lastEncUpT = 0;

    private static void parseLine(String line) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yy:MM:dd:HH:mm:ss.S");
        LocalDateTime time = LocalDateTime.parse(line.split("::")[0], format);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.systemDefault());
        long t = zonedDateTime.toInstant().toEpochMilli();
        String[] parts = line.split("::")[1].split(",");
//        System.out.println(parts.length); ALWAYS 12
//        System.out.println(line.split("::")[0] + " -> " + time.toString());

//        System.out.println(zonedDateTime.toInstant().toEpochMilli());

        String selfName = parts[0]; //Source of Event Name
        String selfID = parts[1]; //Source of Event ID
        String sourceName = parts[2]; //Owner Name
        String sourceID = parts[3]; //Owner ID
        String targetName = parts[4]; //Damage Recipient Name
        String targetID = parts[5]; //Damage Recipient ID
        String eventName = parts[6]; //Skill Name
        String eventID = parts[7]; //Skill ID
        String type = parts[8]; //Type
        String flags = parts[9]; //Flag
        String magnitude = parts[10]; //Magnitude Dealt (after vulnerability calculation)
        String magnitudeBase = parts[11]; //Magnitude Base (before vulnerability calculation)

        float mag = Float.parseFloat(magnitude);
        float baseMag = Float.parseFloat(magnitudeBase);

        if (currentEncounter == null) {
            currentEncounter = new Encounter(t);
        } else if (lastEncUpT != 0 && t - lastEncUpT > Configs.endEncounterTimer) {
            endEncounter();
            currentEncounter = new Encounter(t);
        }

        currentEncounter.updateEntity(selfName, selfID, sourceName, sourceID, targetName, targetID, t, eventName, eventID, flags, mag, baseMag);
        if (!flags.trim().isEmpty()) {
//            System.out.println(flags);
        }

        lastEncUpT = t;
    }

    private static void endEncounter() {
        System.out.println("Encounter has Ended!");
        inEncounter = false;
        currentEncounter.end(System.currentTimeMillis());
        EncounterData.encounter = currentEncounter;
        currentEncounter = null;
        Main.export("CombatLog_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss")));
    }

    public static Encounter getCurrentEncounter() {
        return currentEncounter;
    }
}
