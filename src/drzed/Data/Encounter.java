package drzed.Data;

import drzed.Configs;
import drzed.Data.subtype.AbilityTypes;

import java.util.*;

import static drzed.Main.logToDebugFile;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
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
            "Pn.Nznelx",  //Deal Power In Damage (Goblin Supply Manifest)
            "Pn.Xlmmqg1" //Molten Shell (Orb of Shiv)
    ); //TODO Find more abilities that affect pets and should be considered their own abilities

    //TODO Exclude pet abilities from separation
    private static final List<String> petAbils = Arrays.asList(
        "Pn.0bwjon1", "Pn.0elwrm", "Pn.0o3poi1", "Pn.0v83f81", "Pn.118p4i1", "Pn.12ly9k", "Pn.17hg941", "Pn.188zhy1", "Pn.1i0iy6", "Pn.Lw0ped1",
        "Pn.1mnwuc1", "Pn.1q2rhr", "Pn.1srkyb", "Pn.203ngg1", "Pn.2124sh", "Pn.26o1v", "Pn.2fwq6g1", "Pn.2g9smk1", "Pn.2j7ikm1", "Pn.2wsj5y",
        "Pn.30fpxv", "Pn.34ti4n1", "Pn.3i0lgg1", "Pn.3od90f", "Pn.42rtah1", "Pn.49gcn01", "Pn.4hdmnv", "Pn.4lykum1", "Pn.4qvoq9", "Pn.5cd7kf",
        "Pn.5gmpuh1", "Pn.5uzr8e1", "Pn.5z4pq4", "Pn.69xj3v", "Pn.6bakzy", "Pn.6ku5kr1", "Pn.6uwljz", "Pn.6wgsxq", "Pn.7fwvy1", "Pn.7gfx1n",
        "Pn.7m4v6b1", "Pn.7nfqc8", "Pn.87lnck1", "Pn.87n4ci", "Pn.89fp1x", "Pn.95hvuc1", "Pn.99uf8t1", "Pn.9bygqe", "Pn.9dc2h61", "Pn.9jcvdv",
        "Pn.9unynx1", "Pn.9us6t91", "Pn.9v0qkh1", "Pn.9zscwd", "Pn.A9pnei1", "Pn.Abh7kk", "Pn.Ade9761", "Pn.B0hg1x", "Pn.B3v3f1", "Pn.B70dob",
        "Pn.Cas9af", "Pn.Caw1ol", "Pn.Cbz5af", "Pn.Cdl90r1", "Pn.Cdlg6l1", "Pn.Cq7e2d", "Pn.Crnnc3", "Pn.Cx7q64", "Pn.D6kq", "Pn.D8ak05",
        "Pn.Dfgb4g", "Pn.Di3xod1", "Pn.Diwg4i1", "Pn.Dmoo41", "Pn.Dqzzrm", "Pn.Dsx0nf1", "Pn.Dtrw41", "Pn.Dvm6f81", "Pn.Dybjs7", "Pn.E7zivx",
        "Pn.E8jcmp1", "Pn.Eazjyb", "Pn.Etln0h1", "Pn.Euabqe", "Pn.F3pjuh1", "Pn.F5jws91", "Pn.F7f26o", "Pn.Fci7st1", "Pn.Fhwfq9", "Pn.Fob5mu1",
        "Pn.G5hy82", "Pn.Gb0i8o1", "Pn.Gut96e", "Pn.Hgo0je1", "Pn.Hi49cu1", "Pn.Hn5o4i1", "Pn.Holhtz", "Pn.Hvk6z31", "Pn.Hvuq0h1", "Pn.I1roy6",
        "Pn.I31xe1", "Pn.I7zhh11", "Pn.J7qhdv", "Pn.Jt83mn", "Pn.Jvodyn1", "Pn.K98awd", "Pn.Kj0p3v", "Pn.Kzz06o", "Pn.L5xt5o", "Pn.L67cwp1",
        "Pn.L68ecu1", "Pn.L7co8j1", "Pn.Ljfrs2", "Pn.Lcmn8j1", "Pn.Llsah11", "Pn.Mc6tu5", "Pn.Mltj2p1", "Pn.Mnbtls", "Pn.Mol6eg", "Pn.Msus64",
        "Pn.Mx16t41", "Pn.N6smah1", "Pn.N8lnft", "Pn.Nlr3im", "Pn.Noondv", "Pn.Nsvdp31", "Pn.O0rax51", "Pn.O6mi4n1", "Pn.Ob34b", "Pn.Oomamd",
        "Pn.Op0mvs", "Pn.Opt7gv1", "Pn.Ow6rvs", "Pn.P4p2uk", "Pn.P5wkgl1", "Pn.Pr0oyi1", "Pn.Q6kpw3", "Pn.Q7go2k1", "Pn.Q8xq0h1", "Pn.Qjked01",
        "Pn.Qrrt8j1", "Pn.Qzi7mu1", "Pn.R2d1uk", "Pn.R5ue6e", "Pn.Rana6e", "Pn.Rjnmnv", "Pn.Rrez7m", "Pn.S2dwnq", "Pn.S2h0uk", "Pn.Sjmfos1",
        "Pn.Sp4mum1", "Pn.Svn8ih", "Pn.Sxcad51", "Pn.T6fooi1", "Pn.Tdp9581", "Pn.Tlqekm1", "Pn.Touc2p1", "Pn.U38m3v", "Pn.Ub6g351", "Pn.Ucnnxv",
        "Pn.Uy7nmk1", "Pn.Vc725d1", "Pn.Ve0p1s", "Pn.Vl37ar1", "Pn.Vtobcd", "Pn.Xfwlpy", "Pn.Xjhj6l1", "Pn.Xltoei1", "Pn.Xnw9es1", "Pn.Xo63kw1",
        "Pn.Xyi6761", "Pn.Ydss7r", "Pn.Zernyi1", "Pn.0admnv", "Pn.023t7r", "Pn.0bdr9p", "Pn.B7kz9w1", "Pn.Bbrsjp", "Pn.Bg8db71", "Pn.Bh100p"
    );
    public void updateEntity2(String ownerName, String ownerID, String petName, String petID, String targetName, String targetID, long t, String abilityName, String abilityID, String type, String flag, double magnitude, double baseMagnitude) {
        if (Configs.forceSomeAbilitiesToBeNonPet) {
            if (!petAbils.contains(abilityID) && !abilityName.contains("Attack") && !abilityName.contains("Tracker") && !abilityName.contains("Counter") && !abilityName.contains("]") && !abilityName.contains(":")) {
                petName = "";
                petID = "";
            }
            if (splitAbils.contains(abilityID) && !petID.isEmpty()) {
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
                bannedEntities.contains(enid) || enid.startsWith("R0_Tol_Ow_Rq_") || enid.startsWith("Mapmod") ;
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
