package drzed;

import javafx.beans.property.*;

import java.text.DecimalFormat;

@SuppressWarnings("WeakerAccess")
public class DataAbility {
    Ability ab;
    DecimalFormat df = new DecimalFormat("#.##");

    public DataAbility() {}

    public DataAbility(Ability abil) {
        ab = abil;
    }

    public StringProperty getAbilityName() {
        return new SimpleStringProperty(ab.name);
    }
    public StringProperty getAbilityID() {
        return new SimpleStringProperty(ab.ID);
    }
    public DoubleProperty getAbilityDamage() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ab.Damage)));
    }
    public DoubleProperty getAbilityBaseDamage() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ab.baseDamage)));
    }
    public DoubleProperty getAbilityDPS() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ab.Damage / ab.hits)));
    }
    public LongProperty getAbilityHits() {
        return new SimpleLongProperty(ab.hits);
    }
}
