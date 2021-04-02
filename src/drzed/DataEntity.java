package drzed;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@SuppressWarnings("WeakerAccess")
public class DataEntity {
    Entity ent;

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
    public StringProperty getEntityDamage() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.damageDealt));
    }
    public StringProperty getEntityDPS() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.damageDealt > 0 ? ent.damageDealt / ent.getLifetime() : 0));
    }
    public StringProperty getEntityHealing() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.healingTaken));
    }
    public StringProperty getEntityHPS() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.healingTaken > 0 ? ent.healingTaken / ent.getLifetime() : 0));
    }
    public StringProperty getEntityTaken() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.damageTaken));
    }
    public StringProperty getEntityDeaths() {
        return new SimpleStringProperty(String.valueOf(ent.deaths));
    }
    public StringProperty getEntityLifetime() {
        return new SimpleStringProperty(String.format("%1$,.1f", ent.getLifetime()));
    }
    public StringProperty getEntityHits() {
        return new SimpleStringProperty(String.valueOf(ent.hits));
    }
}