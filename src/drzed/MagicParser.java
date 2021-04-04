package drzed;

import drzed.Data.Encounter;
import drzed.Data.subtype.EncounterData;
import drzed.Data.subtype.EntityNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings({"WeakerAccess","unused"})
public class MagicParser {
    private static int polls = 0;
    private static boolean inEncounter = false;
    private static RandomAccessFile in;
    private static File currentFile = null;
    private static boolean retry = false;
    private static Encounter currentEncounter;
    private static long lastEncUpT = 0;
    public static String myID;

    public static void ParseFile() throws IOException {
        String line;
        if (tryAndSetNewFile()) {
            if ((line = in.readLine()) != null) {
                polls = 0;
                inEncounter = true;
                parseLine(line);
            } else {
                polls++;
            }
            if (polls >= (Configs.endEncounterTimer / Configs.guiPollRate) && inEncounter) endEncounter();
        }
    }

    private static boolean tryAndSetNewFile() throws FileNotFoundException {
        File f1 = getFile();
        if (f1 == null) return false;
        if (currentFile == null) setFile(f1);
        if (!currentFile.getName().equals(f1.getName())) setFile(f1);
        return currentFile != null && in != null;
    }

    private static void setFile(File f1) throws FileNotFoundException {
        currentFile = f1;
        in = new RandomAccessFile(currentFile, "r");
    }


    // Gets newest File
    private static File getFile() {
        if (Main.Directory.listFiles() != null) {
            ArrayList<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(Main.Directory.listFiles())));
            fileList.removeIf(f -> !f.getName().startsWith("Combatlog"));
            fileList.sort((a, b) -> (int) (getFileTime(a) - getFileTime(b)));
            fileList.sort(Comparator.reverseOrder());
//            fileList.forEach(System.out::println);
            return fileList.get(0);
        } return null;
    }

    static List<String> knownFlags = new ArrayList<>();
    static {
        knownFlags.add("Kill");
        knownFlags.add("NoFloater");
        knownFlags.add("Immune");
        knownFlags.add("CombatNotification");
        knownFlags.add("ShieldBreak");
    }

    //21:03:28:13:37:09.4::
// Keldon Warlord,P[440730@31580857 Keldon Warlord@KeldonSlayer#31282],,*,,*,[Sorcery risk/reward],Pn.0xgzi41,Physical,ShieldBreak,40999.3,0
//    Time  ::   self , selfID, pet, petID, target, targetID, ability, abilityID,type ,flags, magnitude, magnitudeBase
    private static void parseLine(String line) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yy:MM:dd:HH:mm:ss.S");
        LocalDateTime time = LocalDateTime.parse(line.split("::")[0], format);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.systemDefault());
        long t = zonedDateTime.toInstant().toEpochMilli();
        String[] parts = line.split("::")[1].split(",");

        String ownerName = parts[0]; //Source of Event Trigger
        String ownerID = parts[1]; //Source of Event Trigger
        String petName = parts[2]; //Source if Pet
        String petID = parts[3]; //Source if Pet
        String targetName = parts[4]; //Damage Recipient Name
        String targetID = parts[5]; //Damage Recipient ID
        String eventName = parts[6]; //Skill Name
        String eventID = parts[7]; //Skill ID
        String type = parts[8]; //Type
        String flags = parts[9]; //Flag
        String magnitude = parts[10]; //Magnitude Dealt (after vulnerability calculation)
        String magnitudeBase = parts[11]; //Magnitude Base (before vulnerability calculation)


        if (ownerName.equalsIgnoreCase(Configs.defaultFilter)) {
            myID = ownerID;
        }

        float mag = Float.parseFloat(magnitude);
        float baseMag = Float.parseFloat(magnitudeBase);
        if (currentEncounter == null) {
            currentEncounter = new Encounter(t);
        } else if (lastEncUpT != 0 && t - lastEncUpT > Configs.endEncounterTimer) {
            endEncounter();
            currentEncounter = new Encounter(t);
        }

        EntityNames.addEntityName(targetName, targetID);
        EntityNames.addEntityName(ownerName, ownerID);
        EntityNames.addEntityName(petName, petID);

        currentEncounter.updateEntity(ownerName, ownerID, petName, petID, targetName, targetID, t, eventName, eventID, flags, mag, baseMag);

        if (!flags.trim().isEmpty() && !knownFlags.contains(flags)) {
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

    public static long getFileTime(File f) {
        try {
            return Files.readAttributes(f.toPath(), BasicFileAttributes.class).creationTime().toMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
