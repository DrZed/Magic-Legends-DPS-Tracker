package drzed;

import drzed.Data.Encounter;
import drzed.Data.subtype.EncounterData;
import drzed.Data.subtype.EntityNames;
import drzed.GUI.MainController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings({"WeakerAccess","unused"})
public class MagicParser {
    private static boolean inEncounter = false;
    private static BufferedReader in;
    private static File currentFile = null;
    private static boolean retry = false;
    private static Encounter currentEncounter;
    public static String myID;
    private static long lastEncUpT = 0;

    public static boolean ParseFile() throws IOException {
        String line;
        if (tryAndSetNewFile()) {
            for (int i = 0; i < Configs.linesPerPoll; i++) {
                if ((line = in.readLine()) != null) {
                    inEncounter = true;
                    parseLine(line);
                } else {
                    if (System.currentTimeMillis() - lastEncUpT > Configs.endEncounterTimer && inEncounter)
                        endEncounter(lastEncUpT);
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean tryAndSetNewFile() throws IOException {
        File f1 = getFile();
        if (f1 == null) return false;
        if (currentFile == null) setFile(f1);
        if (!currentFile.getName().equals(f1.getName())) setFile(f1);
        return currentFile != null && in != null;
    }

    private static void setFile(File f1) throws IOException {
        currentFile = f1;
        in = new BufferedReader(new InputStreamReader(Files.newInputStream(currentFile.toPath(), StandardOpenOption.READ), StandardCharsets.UTF_8));
    }

    private static File getFile() {
        if (Main.Directory.listFiles() != null) {
            ArrayList<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(Main.Directory.listFiles())));
            fileList.removeIf(f -> !f.getName().startsWith("Combatlog"));
            fileList.sort((a, b) -> (int) (getFileTime(a) - getFileTime(b)));
            fileList.sort(Comparator.reverseOrder());
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

    private static void parseLine(String line) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yy:MM:dd:HH:mm:ss.S");
        LocalDateTime time = LocalDateTime.parse(line.split("::")[0], format);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.systemDefault());
        long t = zonedDateTime.toInstant().toEpochMilli();
        String[] parts = line.split("::")[1].split(",");

        if (parts.length > 12) {
            LinkedList<String> newarr = new LinkedList<>();
            int ffs = 0;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equalsIgnoreCase("*") || parts[i].isEmpty()) {
                    newarr.add(parts[i]);
                    continue;
                }
                if (i == 0 && !parts[i + 1].contains("]")) {
                    newarr.add(parts[i] + parts[i + 1]);
                    i++;
                    ffs++;
                } else if (i == (2 + ffs) && !parts[i + 1].contains("]")) {
                    newarr.add(parts[i] + parts[i + 1]);
                    i++;
                    ffs++;
                } else if (i == (4 + ffs) && !parts[i + 1].contains("]")) {
                    newarr.add(parts[i] + parts[i + 1]);
                    i++;
                    ffs++;
                } else {
                    newarr.add(parts[i]);
                }
            }
            for (int i = 0; i < newarr.size(); i++) {
                parts[i] = newarr.get(i);
            }
        }

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

        if (ownerName.equalsIgnoreCase(Configs.selfPlayerName)) {
            myID = ownerID;
        }

        float mag = Float.parseFloat(magnitude);
        float baseMag = Float.parseFloat(magnitudeBase);
        if (currentEncounter == null) {
            currentEncounter = new Encounter(t);
        } else if (lastEncUpT != 0 && t - lastEncUpT > Configs.endEncounterTimer) {
            endEncounter(lastEncUpT);
            currentEncounter = new Encounter(t);
        }

        EntityNames.addEntityName(targetName, targetID);
        EntityNames.addEntityName(ownerName, ownerID);
        EntityNames.addEntityName(petName, petID);

        currentEncounter.updateEntity2(ownerName, ownerID, petName, petID, targetName, targetID, t, eventName, eventID, type, flags, mag, baseMag);
        lastEncUpT = t;
    }

    private static void endEncounter(long t) {
        if (inEncounter) {
            inEncounter = false;
            currentEncounter.end(t);
            EncounterData.encounter = currentEncounter;
            Main.export("CombatLog_" + Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss")));
//        currentEncounter = null;
        }
    }

    public static long getFileTime(File f) {
        try {
            return Files.readAttributes(f.toPath(), BasicFileAttributes.class).creationTime().toMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setCurrentEncounter(Encounter encounter) {
        currentEncounter = encounter;
    }

    public static Encounter getCurrentEncounter() {
        return currentEncounter;
    }
}
