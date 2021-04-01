package drzed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public List<String> petIds;

    public Entity() {}

    public Entity(String EntityName, String IdentityNumber, long first) {
        name = EntityName;
        ID = IdentityNumber;
        firstSeen = first;
        abilities = new HashMap<>();
        petIds = new ArrayList<>();
        healingSources = new HashMap<>();
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
        healing *= -1;
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
    }
}
