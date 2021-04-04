package drzed.Data;

import drzed.Data.subtype.SkillTypes;

import java.util.*;

@SuppressWarnings({"WeakerAccess","unused"})
public class Entity {
    public String name;
    public String ID;
    public HashMap<String, Ability> abilities;
    public HashMap<String, Ability> healingSources;
    public long hits;
    public double healingTaken;
    public double damageTaken;
    public double damageDealt;
    public long firstSeen;
    public long lastSeen;
    public long deathTime;
    public long deaths;
    public String internalName;
    public long IDNum;
    public List<String> petIds;
    public String ownerEntity;
    public boolean isPlayer;
    public LinkedList<Integer> dpsOT;
    public long DOTT;
    public long lDOTT;
    public double DOT;

    public Entity() {}

    public Entity(String EntityName, String Identifier, long first) {
        name = EntityName;
        ID = Identifier;
        firstSeen = first;
        isPlayer = Identifier.startsWith("P");
        if (Identifier.contains("Token")) {
            name += " Token";
        }
        abilities = new HashMap<>();
        petIds = new ArrayList<>();
        healingSources = new HashMap<>();
        dpsOT = new LinkedList<>();
        internalName = getID(Identifier);
        IDNum = getIDNumber(Identifier);
        lDOTT = first;
    }

    public void updateSeen(long t) {
        lastSeen = t;
        DOTT = t;
    }

    public void updateAbility(String abilityID, double damage, double base) {
        abilityID = fixMindlash(abilityID);
        if (!abilities.containsKey(abilityID)) {
            abilities.put(abilityID, new Ability(SkillTypes.getSkillName(abilityID), abilityID));
        }
        abilities.get(abilityID).updateDamage(damage);
        abilities.get(abilityID).updateBaseDamage(base);

        hits++;
        damageDealt += damage;
        DOT += damage;
        if (DOTT - lDOTT > 30000) {
            updateDOT();
        }
    }

//Pn.Amuy251=[ name=Mindlash ]
//Pn.E19zs41=[ name=Mindlash ]
//Pn.B6i4z31=[ name=Mindlash ]
//Pn.67gyc51=[ name=Mindlash ]
    private String fixMindlash(String abilityID) {
        if (abilityID.equalsIgnoreCase("Pn.Amuy251") ||
                abilityID.equalsIgnoreCase("Pn.E19zs41") ||
                abilityID.equalsIgnoreCase("Pn.B6i4z31") ||
                abilityID.equalsIgnoreCase("Pn.67gyc51")) { //Because go fk yourself multiple ids
            return "Pn.Amuy251";
        }
        return abilityID;
    }

    private void updateDOT() {
        long dt = DOTT - lDOTT;
        dpsOT.add(Math.toIntExact(Math.round((DOT / dt) * 1000D)));
        lDOTT = DOTT;
        DOT = 0;
    }

    public void healEntity(String abilityID, double healing, double base) {
        if (healing < 0) {
            healing *= -1;
        }
        if (!healingSources.containsKey(abilityID)) {
            healingSources.put(abilityID, new Ability(SkillTypes.getSkillName(abilityID), abilityID));
        }
        healingSources.get(abilityID).updateDamage(healing);
        healingSources.get(abilityID).updateBaseDamage(base);
        healingTaken += healing;
    }

    public void updateDamageTaken(double damage) {
        damageTaken += damage;
    }

    public void addPetID(String petd) {
        if (!petIds.contains(petd)) {
            petIds.add(petd);
        }
    }

    public void kill(long last) {
        deathTime = last;
        deaths++;
    }

    public Ability getBestAbility() {
        LinkedList<Ability> abs = new LinkedList<>(abilities.values());
        abs.sort(Comparator.comparingDouble(Ability::getDamage));
        return abs.getLast();
    }

    public double getLifetime() {
        return Math.max(1.0D, (lastSeen - firstSeen) / 1000D);
    }

    public static String getID(Entity ent) {
        return getID(ent.ID);
    }

    public double getDPS() {
        return damageDealt / getLifetime();
    }
    public double getHPS() {
        return healingTaken / getLifetime();
    }

    //C[310876 Zen_Vastwood_Forest_Hordeling_Goblin_Ranged] //Creature ID
    //P[440730@31580857 Keldon Warlord@KeldonSlayer#31282] //Epic Account great for testing id permutations
    //P[31719@618666 Keldon@DeathDemon18] //Arc Alt great for testing id permutations
    public static String getID(String id) {
        if (id.equalsIgnoreCase("*") || id.isEmpty()) return "";
        String trim = id.replaceAll("\\w\\[|]", "").trim();
        if (id.contains("@")) {
            String[] tmp = trim.split("@");
            return tmp[tmp.length - 1];
        }
        String eid = trim.split(" ")[1];
        if (eid.equalsIgnoreCase("Regionmechanic_Tolaria_Bubble_Controller") ||
            eid.equalsIgnoreCase("Regionmechanic_Tolaria_Bubble")) {
            return "";
        }
        return eid;
    }

    public static long getIDNumber(String id) {
        return Long.parseLong(id.replaceAll("\\w\\[|]|@", "").trim().split(" ")[0]);
    }
}
