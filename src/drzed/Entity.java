package drzed;

import java.net.IDN;
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
    public long deathTime;
    public long lastSeen;
    public long deaths;
    public String internalName;
    public long IDNum;
    public List<String> petIds;
    public Entity ownerEntity;

    public Entity() {}

    public Entity(String EntityName, String Identifier, long first) {
        name = EntityName;
        ID = Identifier;
        firstSeen = first;
        abilities = new HashMap<>();
        petIds = new ArrayList<>();
        healingSources = new HashMap<>();
        internalName = getID(Identifier);
        IDNum = getIDNumber(Identifier);
        hits = 0;
    }

    public void updateSeen(long t) {
        lastSeen = t;
    }

    public void updateAbility(String abilityID, double damage, double base) {
        if (!abilities.containsKey(abilityID)) {
            abilities.put(abilityID, new Ability(SkillTypes.getSkillName(abilityID), abilityID));
        }
        abilities.get(abilityID).updateDamage(damage);
        abilities.get(abilityID).updateBaseDamage(base);

        hits++;
        damageDealt += damage;
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
        return (lastSeen - firstSeen) / 1000D;
    }

    public static String getID(Entity ent) {
        return getID(ent.ID);
    }

    //C[310876 Zen_Vastwood_Forest_Hordeling_Goblin_Ranged] //Creature ID
    //P[440730@31580857 Keldon Warlord@KeldonSlayer#31282] //Epic Account great for testing id permutations
    //P[31719@618666 Keldon@DeathDemon18] //Arc Alt great for testing id permutations
    public static String getID(String id) {
        if (id.equalsIgnoreCase("*") || id.isEmpty()) return "";
        if (id.contains("@")) {
            String[] tmp = id.replaceAll("\\w\\[", "").replaceAll("\\]", "").trim().split("@");
            return tmp[tmp.length - 1];
        }
        return id.replaceAll("\\w\\[", "").replaceAll("\\]", "").trim().split(" ")[1];
    }

    //Replaces @ with 1234 because @ isn't a number
    public static long getIDNumber(String id) {
        return Long.parseLong(id.replaceAll("\\w\\[", "").replaceAll("\\]", "").replaceAll("@", "1234").trim().split(" ")[0]);
    }
}
