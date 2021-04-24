package drzed.Data;

import drzed.Data.subtype.AbilityTypes;
import drzed.MagicParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

@SuppressWarnings({"WeakerAccess","unused"})
public class Entity {
    public String name = "";
    public String fullID = "";
    public String internalName = "";
    public String ownerEntityID = "";

    public boolean isPlayer = false;

    public int hits = 0;
    public int healingTaken = 0;
    public int damageTaken = 0;
    public int damageDealt = 0;
    public int deaths = 0;
    public int lifetime = 0;
    public int shieldTaken = 0;
    public int kills = 0;

    private long firstSeen = 0;
    public LinkedList<Ability> abilityList = new LinkedList<>();

    public Entity() {}

    public Entity(String EntityName, String Identifier, long first) {
        name = EntityName;
        fullID = Identifier;
        firstSeen = first;
        isPlayer = Identifier.startsWith("P");
        internalName = getID(Identifier);
    }

    public void updateSeen(long t) {
        lifetime = Math.round((t - firstSeen) / 1000f);
    }

    public void updateDamageTaken(double damage, boolean shield) {
        if (damage < 0 && !shield) {
            System.out.println("DAMAGE NEGATIVE AND NOT SHIELD");
        }
        if (shield) {
            shieldTaken += damage < 0 ? -1 * damage : damage;
        } else {
            damageTaken += damage;
        }
    }

    public double getEffectiveness(double dm) {
        return dm / damageDealt * 100;
    }

    public void kill() {
        deaths++;
    }

    public Ability getBestAbility() {
        sortAbils();
        return abilityList.get(0);
    }

    private void sortAbils() {
//        abilityList.sort(Comparator.comparingDouble(Ability::getTotalDamage));
        abilityList.sort((o1, o2) -> -(o1.totalDamage - o2.totalDamage));
    }



    public void updateAbility(String abilityName, String abilityID, double damage, double base) {
        updateAbility(abilityName, abilityID, damage, base, 0);
    }

    public void updateAbility(String abilityName, String abilityID, double damage, double base, double dtaken) {
        Ability ab = null;
        abilityID = AbilityTypes.fixAbils(abilityName, abilityID);

        for (Ability ability : abilityList) {
            if (ability.ID.equalsIgnoreCase(abilityID)) {
                ab = ability;
                break;
            }
        }
        if (ab == null) {
            ab = AbilityTypes.makeAbility(abilityName, abilityID, damage);
            abilityList.add(ab);
        }
        ab.update(damage, base, dtaken);

        if (damage > 0) {
            hits++;
            damageDealt += damage;
        } else {
            healingTaken -= damage;
        }
        sortAbils();
    }

    public int getLifetime() {
        return Math.max(1, lifetime);
    }

    public static String getID(Entity ent) {
        return getID(ent.fullID);
    }

    public int getDPS() {
        return damageDealt / getLifetime();
    }
    public int getHPS() {
        return healingTaken / getLifetime();
    }

    public static String getID(String id) {
        if (id.equalsIgnoreCase("*") || id.isEmpty()) return "";
        if (!id.contains("]")) {
            return id;
        }
        String trim = id.replaceAll("\\w\\[|]", "").trim();
        if (id.contains("@")) {
            String[] tmp = trim.split("@");
            return tmp[tmp.length - 1];
        }
        return trim.split(" ")[1];
    }

    public void addKill() {
        kills++;
    }

    public double getEff() {
        if (!isPlayer) return 0;
        long tdam = MagicParser.getCurrentEncounter().globalDamageByPlayers + MagicParser.getCurrentEncounter().globalDamageToPlayers;
        long mdam = damageTaken + shieldTaken;
        for (Ability ability : abilityList) {
            mdam += ability.taken;
            mdam += ability.totalDamage;
        }
        return ((double) mdam / (double) tdam) * 100;
    }
}
