package drzed;

import java.util.LinkedHashMap;

@SuppressWarnings({"WeakerAccess","unused"})
public class Encounter {
    public long time = 0;
    public long duration = 0;
    public LinkedHashMap<String, Entity> entities;

    public Encounter() {}

    public Encounter(long StartTime) {
        time = StartTime;
        entities = new LinkedHashMap<>();
    }

    public void end(long endTime) {
        duration = time - endTime;
        entities.forEach((a,b) -> {
            b.kill(endTime);
        });
    }

    public void updateEntity(String selfName, String selfID, String ownerName, String ownerID, String targetName, String targetID, long t, String abilityName, String abilityID, String flag, double magnitude, double baseMagnitude) {
        if (selfName.trim().isEmpty()) return;
        String tp = SkillTypes.getType(abilityName, abilityID, magnitude);

        if (tp.equals("DMG")) {
            addDamageToEntity(targetName, targetID, t, selfName, selfID, magnitude);
            updateAbility(selfName, selfID, t, abilityID, magnitude, baseMagnitude);
            if (!ownerID.trim().isEmpty() && !ownerID.trim().equalsIgnoreCase("*")) {
                addPetToEntity(ownerName, ownerID, t, selfName, selfID);
            }
        }
        if (tp.equals("HEAL")) {
            addHealingToEntity(selfName, selfID, t, abilityName, abilityID, magnitude, baseMagnitude);
        }
        if (!flag.trim().isEmpty() && flag.trim().equalsIgnoreCase("kill")) {
            killEntity(targetName, targetID, t);
        }
    }

    private void updateAbility(String name, String id, long t, String abilityID, double damage, double baseMag) {
        getOrAddEntity(name, id, t).updateAbility(abilityID, damage, baseMag);
    }

    private void addPetToEntity(String ownerName, String ownerID, long t, String petName, String petID) {
        if (ownerID.trim().equalsIgnoreCase("*") || ownerID.trim().isEmpty()) return;
        getOrAddEntity(ownerName, ownerID, t).addPetID(getOrAddEntity(petName, petID, t).ID);
    }

    private void addDamageToEntity(String targetName, String targetID, long t, String sourceName, String sourceID, double damage) {
        getOrAddEntity(targetName, targetID, t).updateDamageTaken(damage);
    }

    private void addHealingToEntity(String targetName, String targetID, long t, String abilityName, String abilityID, double heal, double baseMag) {
        getOrAddEntity(targetName, targetID, t).healEntity(abilityID, heal, baseMag);
    }

    private void killEntity(String targetName, String targetID, long t) {
        getOrAddEntity(targetName, targetID, t).kill(t);
    }

    private Entity getOrAddEntity(String nm, String id, long t) {
        if (!entities.containsKey(id))
            entities.put(id, new Entity(nm, id, t));
        entities.get(id).updateSeen(t);
        return entities.get(id);
    }

    public Entity getEntity(String id) {
        return entities.get(id);
    }
}
