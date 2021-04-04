package drzed;

import java.util.LinkedHashMap;

@SuppressWarnings({"WeakerAccess","unused"})
public class Encounter {
    public long time = 0;
    public long duration = 0;
    public LinkedHashMap<String, Entity> allEntities;
    public LinkedHashMap<String, Long> entityDeaths;
    public LinkedHashMap<String, Entity> noDupedEntities;

    public Encounter() {}

    public Encounter(long StartTime) {
        time = StartTime;
        allEntities = new LinkedHashMap<>();
        entityDeaths = new LinkedHashMap<>();
        noDupedEntities = new LinkedHashMap<>();
    }

    public void end(long endTime) {
        duration = time - endTime;
        allEntities.forEach((a, b) -> {
            b.kill(endTime);
        });
    }

    public void updateEntity(String ownerName, String ownerID, String petName, String petID, String targetName, String targetID, long t, String abilityName, String abilityID, String flag, double magnitude, double baseMagnitude) {
        if (ownerName.trim().isEmpty()) return;
        String tp = SkillTypes.getType(abilityName, abilityID, magnitude);
        if (tp.equals("DMG")) {
            addDamageToEntity(targetName, targetID, t, ownerName, ownerID, magnitude);
            if (!petID.isEmpty() && !petID.equalsIgnoreCase("*")) {
                String enid = Entity.getID(petID);
                if (enid.startsWith("Spell_")) {
                    SkillTypes.getType(petName, petID, magnitude);
                    updateAbility(ownerName, ownerID, t, petID, magnitude, baseMagnitude);
                } else {
                    updateAbility(petName, petID, t, abilityID, magnitude, baseMagnitude);
                    addPetToEntity(ownerName, ownerID, t, petName, petID);
                }
            } else {
                updateAbility(ownerName, ownerID, t, abilityID, magnitude, baseMagnitude);
            }
        }
        if (tp.equals("HEAL")) {
            addHealingToEntity(ownerName, ownerID, t, abilityName, abilityID, magnitude, baseMagnitude);
        }
        if (!flag.trim().isEmpty() && flag.trim().equalsIgnoreCase("kill")) {
            killEntity(targetName, targetID, t);
        }
    }

    private void updateAbility(String name, String id, long t, String abilityID, double damage, double baseMag) {
        Entity e = getOrAddEntity(name, id, t);
        if (e == null) return;
        e.updateAbility(abilityID, damage, baseMag);
    }

    private void addPetToEntity(String ownerName, String ownerID, long t, String petName, String petID) {
        if (petID.trim().equalsIgnoreCase("*") || petID.trim().isEmpty()) return;
        Entity e = getOrAddEntity(ownerName, ownerID, t);
        Entity e2 = getOrAddEntity(petName, petID, t);
        if (e == null || e2 == null) return;
        e.addPetID(e2.ID);
        e2.ownerEntity = e;
    }

    private void addDamageToEntity(String targetName, String targetID, long t, String sourceName, String sourceID, double damage) {
        Entity e = getOrAddEntity(targetName, targetID, t);
        if (e == null) return;
        e.updateDamageTaken(damage);
    }

    private void addHealingToEntity(String targetName, String targetID, long t, String abilityName, String abilityID, double heal, double baseMag) {
        Entity e = getOrAddEntity(targetName, targetID, t);
        if (e == null) return;
        e.healEntity(abilityID, heal, baseMag);
    }

    private void killEntity(String targetName, String targetID, long t) {
        Entity e = getOrAddEntity(targetName, targetID, t);
        if (e == null) return;
        e.kill(t);
        String enid = Entity.getID(targetID);
        entityDeaths.replace(enid, entityDeaths.get(enid) + 1);
    }

    private Entity getOrAddEntity(String nm, String id, long t) {
        String enid = Entity.getID(id);
     /*   if (Configs.bannedEntityIDs.contains(enid)) ||
                enid.isEmpty()) {
//            System.out.println("Returning null on entity name: " + nm + " ID " + id);
            return null;
        }*/
        if (!entityDeaths.containsKey(enid) || enid.startsWith("Spell_") || enid.startsWith("Object_") || enid.startsWith("Modifier_")) {
            entityDeaths.put(enid, 0L);
        }
        return Configs.condensedMode ? getOrAddEntityNoDupe(nm, id, t) : getOrAddEntityDupe(nm, id, t);
    }


    private Entity getOrAddEntityDupe(String nm, String id, long t) {
        if (!allEntities.containsKey(id))
            allEntities.put(id, new Entity(nm, id, t));
        allEntities.get(id).updateSeen(t);
        return allEntities.get(id);
    }

    private Entity getOrAddEntityNoDupe(String nm, String id, long t) {
        String realID = Entity.getID(id);
        if (!noDupedEntities.containsKey(realID))
            noDupedEntities.put(realID, new Entity(nm, id, t));
        noDupedEntities.get(realID).updateSeen(t);
        return noDupedEntities.get(realID);
    }

    public LinkedHashMap<String, Entity> getEnts() {
        return Configs.condensedMode ? noDupedEntities : allEntities;
    }

    public LinkedHashMap<String, Long> getEntityDeaths() {
        return entityDeaths;
    }

    public long getDeaths(String id) {
        return entityDeaths.get(id);
    }

    public Entity getEntity(String id) {
        return getEnts().get(Configs.condensedMode ? Entity.getID(id) : id);
    }
}
