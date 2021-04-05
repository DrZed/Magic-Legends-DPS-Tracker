package drzed.Data;

import drzed.Configs;
import drzed.Data.subtype.SkillTypes;

import java.util.LinkedHashMap;

import static drzed.Main.DEBUG_ALL_STEPS_MODE;

@SuppressWarnings({"WeakerAccess","unused"})
public class Encounter {
    public long time = 0;
    public long duration = 0;
    public LinkedHashMap<String, Entity> allEntities;
    public LinkedHashMap<String, Long> entityDeaths;
    public LinkedHashMap<String, Entity> noDupedEntities;

    private Entity filterEntity;

    public Encounter() {
        allEntities = new LinkedHashMap<>();
        entityDeaths = new LinkedHashMap<>();
        noDupedEntities = new LinkedHashMap<>();
    }

    public Encounter(long StartTime) {
        time = StartTime;
        allEntities = new LinkedHashMap<>();
        entityDeaths = new LinkedHashMap<>();
        noDupedEntities = new LinkedHashMap<>();
    }

    public void end(long endTime) {
        duration = endTime - time;
        allEntities.forEach((a, b) -> {
            b.kill(endTime);
        });
    }

    public void updateEntity(String ownerName, String ownerID, String petName, String petID, String targetName, String targetID, long t, String abilityName, String abilityID, String flag, double magnitude, double baseMagnitude) {
        if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity BEGIN");
        Entity owner = getOrAddEntity(ownerName, ownerID, t);
        Entity pet = getOrAddEntity(petName, petID, t);
        Entity target = getOrAddEntity(targetName, targetID, t);
        String tp = SkillTypes.getType(abilityName, abilityID, magnitude);
        if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity ENTITIES SET");
        if (tp.equals("DMG")) {
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity DMG");
            if (target != null)
                addDamageToEntity(target, owner, magnitude);
            if (pet != null) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity PET");
                String enid = pet.internalName;
                if (enid.startsWith("Spell_") || enid.contains("Token")) {
                    if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity SPELL/TOKEN");
                    SkillTypes.getType(petName, enid, magnitude);
                    updateAbility(pet, abilityID, magnitude, baseMagnitude); //Now Pets are listed in both so you can see their DPS

                    if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity ADDED AS ABILITY");
                    if (owner != null) {//                                     compared to other entities, and damage taken
                        if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity ADDING PET TO OWNER");
                        addPetToEntity(owner, pet);
                        updateAbility(owner, enid, magnitude, baseMagnitude);
                        if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity ADDED TO OWNER");
                    }
                }
                if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity PET DONE");
            } else {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity UPDATE ABILITY");
                if (owner != null)
                    updateAbility(owner, abilityID, magnitude, baseMagnitude);
                if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity UPDATED");
            }
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity END DMG");
        }
        if (tp.equals("HEAL")) {
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity HEAL");
            if (owner != null)
                addHealingToEntity(owner, abilityName, abilityID, magnitude, baseMagnitude);
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity END HEAL");
        }
        if (!flag.trim().isEmpty() && flag.trim().contains("Kill")) {
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity KILL");
            if (target != null)
                killEntity(target, t);
            if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity END KILL");
        }
        if (DEBUG_ALL_STEPS_MODE) System.out.println("Encounter.updateEntity END");
    }

    private void updateAbility(Entity e, String abilityID, double damage, double baseMag) {
        e.updateAbility(abilityID, damage, baseMag);
    }

    private void addPetToEntity(Entity owner, Entity pet) {
        owner.addPetID(pet.ID);
        pet.ownerEntity = owner.ID;
    }
    private void addDamageToEntity(Entity target, Entity source, double damage) {
        target.updateDamageTaken(damage);
    }

    private void addHealingToEntity(Entity e, String abilityName, String abilityID, double heal, double baseMag) {
        e.healEntity(abilityID, heal, baseMag);
    }

    private void killEntity(Entity e, long t) {
        e.kill(t);
        entityDeaths.replace(e.internalName, entityDeaths.get(e.internalName) + 1);
    }

    public Entity getOrAddEntity(String nm, String id, long t) {
        String enid = Entity.getID(id);
        if (enid.isEmpty()) {
            return null;
        }
        if (!entityDeaths.containsKey(enid) || enid.startsWith("Spell_") || enid.startsWith("Object_") || enid.startsWith("Modifier_")) {
            entityDeaths.put(enid, 0L);
        }
        Entity e = Configs.condensedMode ? getOrAddEntityNoDupe(nm, id, t) : getOrAddEntityDupe(nm, id, t);
        if (e.name.equalsIgnoreCase(Configs.defaultFilter)) filterEntity = e;
//        System.out.println("Returning entity : " + nm);
        return e;
    }

    public Entity getFilterEntity() {
        return filterEntity;
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
