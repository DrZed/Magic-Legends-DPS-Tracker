package drzed;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@SuppressWarnings("WeakerAccess")
public class DataAbility {
    public double abilityDamage;
    public double abilityDPS;
    public double abilityHits;
    public String abilityName;
    public String abilityID;
    public double abilityBaseDamage;

    public DataAbility() {}

    public DataAbility(Ability ab) {
        abilityDamage = ab.Damage;
        abilityHits = ab.hits;
        abilityDPS = ab.Damage / abilityHits;
        abilityName = ab.name;
        abilityID = ab.ID;
        abilityBaseDamage = ab.baseDamage;
    }

    public StringProperty getAbilityName() {
        return new SimpleStringProperty(abilityName);
    }
    public StringProperty getAbilityID() {
        return new SimpleStringProperty(abilityID);
    }
    public StringProperty getAbilityDamage() {
        return new SimpleStringProperty(String.format("%1$,.1f", abilityDamage));
    }
    public StringProperty getAbilityBaseDamage() {
        return new SimpleStringProperty(String.format("%1$,.1f", abilityBaseDamage));
    }
    public StringProperty getAbilityDPS() {
        return new SimpleStringProperty(String.format("%1$,.1f", abilityDPS));
    }
    public StringProperty getAbilityHits() {
        return new SimpleStringProperty(String.valueOf(abilityHits));
    }
}
