package drzed;

import javafx.beans.property.*;

import java.text.DecimalFormat;

@SuppressWarnings("WeakerAccess")
public class DataEntity {
    Entity ent;
    DecimalFormat df = new DecimalFormat("#.##");

    public DataEntity() {}

    public DataEntity(Entity entity) {
        ent = entity;
    }

    public StringProperty getEntityName() {
        return new SimpleStringProperty(ent.name);
    }
    public StringProperty getEntityID() {
        return new SimpleStringProperty(ent.ID);
    }
    public DoubleProperty getEntityDamage() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.damageDealt)));
    }
    public DoubleProperty getEntityDPS() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.damageDealt > 0 ? ent.damageDealt / ent.getLifetime() : 0)));
    }
    public DoubleProperty getEntityHealing() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.healingTaken)));
    }
    public DoubleProperty getEntityHPS() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.healingTaken > 0 ? ent.healingTaken / ent.getLifetime() : 0)));
    }
    public DoubleProperty getEntityTaken() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.damageTaken)));
    }
    public LongProperty getEntityDeaths() {
        return new SimpleLongProperty(ent.deaths);
    }
    public DoubleProperty getEntityLifetime() {
        return new SimpleDoubleProperty(Double.parseDouble(df.format(ent.getLifetime())));
    }
    public LongProperty getEntityHits() {
        return new SimpleLongProperty(ent.hits);
    }
}