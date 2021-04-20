package drzed.Data;

import drzed.Configs;
import drzed.Data.subtype.AbilityTypes;

import java.util.*;

import static drzed.Main.logToDebugFile;

@SuppressWarnings({"WeakerAccess","unused"})
public class Encounter {
    public int duration = 0;
    public LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
    public long globalDamageByPlayers, globalDamageToPlayers;

    private static final List<String> bannedEntities = Arrays.asList("R0_Tol_Ow_Rq_R3_Q2_Defend_Artifact", "R0_Event_Mana_Rig_Mayhem_Falling_Crystal_Phase_Hazard",
            "R0_Event_Mana_Rig_Mayhem_Crystal_Telegraph", "R0_Event_Mana_Rig_Mayhem_Crystal_Spawner", "R0_Event_Mana_Rig_Mayhem_Crystal");

    private HashMap<String, Entity> petsOfOwners = new HashMap<>();
    private Entity filterEntity;
    private long startTimeLong = 0;

    public Encounter() {
    }

    public Encounter(long StartTime) {
        startTimeLong = StartTime;
        System.out.println("STARTING ENCOUNTER Time = " + startTimeLong);
    }

    public void end(long endTime) {
        System.out.println("ENDING ENCOUNTER Time = " + endTime);
        checkOrphans();
        duration = (int) Math.round(((endTime - startTimeLong) / 1000D));
        System.out.println("ENCOUNTER HAS ENDED! Duration = " + formatTime(duration));
    }

    public static String formatTime(int seconds) {
        int s = seconds % 60;
        int m = (seconds - s) / 60;
        return String.format("%d:%02d", m, s);
    }

    private HashMap<String, Ability> orphans = new HashMap<>();
    private static final List<String> splitAbils = Arrays.asList(
            "Pn.Oyq2na1", //Necrotic Burst
            "Pn.Onpxds1", //Mark of Immolation
            "Pn.Nznelx"  //Deal Power In Damage (Goblin Supply Manifest)
    ); //TODO Find more abilities that affect pets and should be considered their own abilities
    public void updateEntity2(String ownerName, String ownerID, String petName, String petID, String targetName, String targetID, long t, String abilityName, String abilityID, String type, String flag, double magnitude, double baseMagnitude) {
        if (Configs.forceSomeAbilitiesToBeNonPet) {
            if (splitAbils.contains(abilityID)) {
                petName = "";
                petID = "";
            }
        }
        if (ownerName.isEmpty() && ownerID.isEmpty() && !petName.isEmpty() && !petID.isEmpty()) {
            ownerName = petName;
            ownerID = petID;
        }
        if (isBannedEntity(ownerID)) {
            if (isBannedEntity(targetID)) {
                logToDebugFile("REJECTED DATA : ");
                logToDebugFile("=========================================");
                logToDebugFile("Owner : " + ownerName + "  " + ownerID);
                logToDebugFile("Target : " + targetName + "  " + targetID);
                logToDebugFile("Pet : " + petName + "  " + petID);
                logToDebugFile("Ability : " + abilityName + "  " + abilityID);
                logToDebugFile("Type/Flag : " + type + " | " + flag);
                logToDebugFile("Magnitudes : " + magnitude + "  " + baseMagnitude);
                logToDebugFile("=========================================");
                return;
            } else {
                if (targetID.startsWith("P")) {
                    Entity player = getOrAddEntity(targetName, targetID, t);
                    if (magnitude < 0 && !type.equalsIgnoreCase("Shield")) {
                        player.updateAbility(abilityName, abilityID, magnitude, baseMagnitude);
                    } else {
                        globalDamageToPlayers += magnitude;
                        player.updateDamageTaken(magnitude, type.equalsIgnoreCase("Shield"));
                    }
                } else {
                    if (targetID.contains("Token")) {
                        if (petsOfOwners.containsKey(targetID) && magnitude > 0) {
                            petsOfOwners.get(targetID).updateDamageTaken(magnitude, type.equalsIgnoreCase("Shield"));
                            globalDamageToPlayers += magnitude;
                        } else {
                            if (orphans.containsKey(targetID)) {
                                orphans.get(targetID).update(0, 0, magnitude);
                            } else {
                                orphans.put(targetID, AbilityTypes.makeAbility(targetName, Entity.getID(targetID), magnitude));
                            }
                        }
                    }
                }
            }
            return;
        }
        if (ownerID.startsWith("P")) {
            //Player Ent Damage Dealt
            Entity player = getOrAddEntity(ownerName, ownerID, t);
            if (!targetName.isEmpty()) {
                if (isBannedEntity(targetID) || targetID.contains("Token")) {
                    return; //Don't add damage to banned entities (Spells/Non-Enemy objects)
                }
                Entity target = getOrAddEntity(targetName, targetID, t);
                if (!petName.isEmpty()) {
                    //Pet Ability Dealt
                    String pnid = Entity.getID(petID);
                    AbilityTypes.getType(petName, pnid, magnitude);
                    petsOfOwners.put(petID, player);
                    player.updateAbility(petName, pnid, magnitude, baseMagnitude);
                    //In this case pet is the ability, abilityName/abilityID would be the pets ability
                } else {
                    AbilityTypes.getType(abilityName, abilityID, magnitude);
                    player.updateAbility(abilityName, abilityID, magnitude, baseMagnitude);
                    //No Pet Found so add damage as from ability as usual
                }
                //Other Ent Taken
                if (magnitude < 0 && !type.equalsIgnoreCase("Shield")) {
                    target.updateAbility(player.name, player.internalName, magnitude, baseMagnitude);
                } else {
                    target.updateDamageTaken(magnitude, type.equalsIgnoreCase("Shield"));
                    globalDamageByPlayers += magnitude;
                }
                //Add damage to target entity
                if (flag.contains("Kill")) {
                    player.addKill();
                    target.kill();
                }
            }
        } else {
            //Other Ent Dealt
            Entity otherEnt = getOrAddEntity(ownerName, ownerID, t);
            if (!targetName.isEmpty()) {
                String tnid = Entity.getID(targetID);
                //Player Ent Taken
                if (targetID.startsWith("P")) {
                    Entity player = getOrAddEntity(targetName, targetID, t);
                    if (magnitude < 0 && !type.equalsIgnoreCase("Shield")) {
                        logToDebugFile("DAMAGE IS NEGATIVE!");
                        logToDebugFile("=========================================");
                        logToDebugFile("Source " + otherEnt.name);
                        logToDebugFile("Target " + player.name);
                        logToDebugFile("Amount " + magnitude);
                        logToDebugFile("=========================================");
                    }
                    player.updateDamageTaken(magnitude, type.equalsIgnoreCase("Shield"));
                    globalDamageToPlayers += magnitude;
                    otherEnt.updateAbility(abilityName, abilityID, magnitude, baseMagnitude);
                    if (flag.contains("Kill")) {
                        otherEnt.addKill();
                        player.kill(); //Yes this happens
                    }
                    return;
                }
                //Pet Ability Taken
                if (petsOfOwners.containsKey(targetID)) {
                    petsOfOwners.get(targetID).updateAbility(targetName, tnid, 0, 0, magnitude);
                    globalDamageToPlayers += magnitude;
                    if (flag.contains("Kill")) {
                        otherEnt.addKill();
//                        target.kill(); //Maybe add this at some point if needed
                    }
                    return;
                    //If target is pet, add damage taken to pet "ability"
                }
                if (isBannedEntity(targetID)) {
                    return; //Don't add damage to banned entities (Spells/Non-Enemy objects)
                    //Some pets are classified as banned entities, so circumventing my own negation
                }
                if (targetID.contains("Token")) {
                    if (orphans.containsKey(targetID)) {
                        orphans.get(targetID).update(0, 0, magnitude);
                    } else {
                        orphans.put(targetID, AbilityTypes.makeAbility(targetName, tnid, magnitude));
                    }
                    otherEnt.updateAbility(abilityName, abilityID, magnitude, baseMagnitude);
                    if (flag.contains("Kill")) {
                        otherEnt.addKill();
                    }
                    return;
                }
                //In Theory this should never call
                logToDebugFile("ENTITIES THAT SHOULDN'T BE DAMAGING EACH-OTHER");
                logToDebugFile("=========================================");
                logToDebugFile("Damage Source Entity " + otherEnt.name + " ID = " + otherEnt.fullID);
                logToDebugFile("Damage Target Entity " + targetName + " ID = " + targetID);
                logToDebugFile("Amount : " + magnitude);
                logToDebugFile("=========================================");
                //At least I hope?
                //UPDATE: It has triggered when an enemy damages another enemy, WTF Cryptic
            }
        }
    }

    private void checkOrphans() {
        if (!orphans.isEmpty()) {
            List<String> purgeQ = new ArrayList<>();
            for (String s : orphans.keySet()) {
                String pnid = Entity.getID(s);
                for (String s1 : petsOfOwners.keySet()) {
                    String p2nid = Entity.getID(s1);
                    if (s.equalsIgnoreCase(s1)) {
                        Ability ab = orphans.get(s);
                        petsOfOwners.get(s1).updateAbility(ab.name, ab.ID, 0, 0, ab.totalDamage);
                        globalDamageToPlayers += ab.totalDamage;
                        purgeQ.add(s);
                        break; // This is guaranteed to be the pet of Owner
                    } else if (pnid.equalsIgnoreCase(p2nid)) {
                        Ability ab = orphans.get(s);
                        petsOfOwners.get(s1).updateAbility(ab.name, ab.ID, 0, 0, ab.totalDamage);
                        globalDamageToPlayers += ab.totalDamage;
                        purgeQ.add(s);
                        break; // This is Possibly the pet of owner
                    }
                }
            }
            for (String s : purgeQ) {
                orphans.remove(s);
            }
        }
    }

    private static boolean isBannedEntity(String id) {
        String enid = Entity.getID(id);
        return enid.isEmpty() || enid.startsWith("Ability_") || enid.startsWith("Spell_") ||
                enid.startsWith("Object_") || enid.startsWith("Modifier_") || enid.startsWith("Regionmechanic_") ||
                bannedEntities.contains(enid) || enid.startsWith("R0_Tol_Ow_Rq_");
    }

    public Entity getOrAddEntity(String nm, String id, long t) {
        String enid = Entity.getID(id);
        if (enid.isEmpty() || enid.startsWith("Ability_") || enid.startsWith("Spell_") ||
                enid.startsWith("Object_") || enid.startsWith("Modifier_") || enid.startsWith("Regionmechanic_") ||
            bannedEntities.contains(enid) || enid.startsWith("R0_Tol_Ow_Rq_")) {
            return null;
        }
        if (id.contains("Token")) {
            System.out.println("TOKEN BEING RETURNED");
        }
        Entity e = getOrAddEntityNoDupe(nm, id, t);
        if (e.name.equalsIgnoreCase(Configs.selfPlayerName)) filterEntity = e;
        return e;
    }

    public Entity getFilterEntity() {
        return filterEntity;
    }

    private Entity getOrAddEntityNoDupe(String nm, String id, long t) {
        String realID = Entity.getID(id);
        if (!entities.containsKey(realID))
            entities.put(realID, new Entity(nm, id, t));
        entities.get(realID).updateSeen(t);
        return entities.get(realID);
    }

    public LinkedHashMap<String, Entity> getEntities() {
        return entities;
    }

    public Entity getEntity(String id) {
        return getEntities().get(Entity.getID(id));
    }

    private void sortEntities() {
        entities.entrySet().stream().sorted(Map.Entry.comparingByValue((a,b) -> -(a.damageDealt - b.damageDealt)));
    }
}
