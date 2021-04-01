package drzed;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataEntity {
    String entityID;
    String entityName;
    double entityDamage;
    double entityDPS;
    double entityHealing;
    double entityHPS;
    double entityTaken;
    int entityDeaths;
    float entityLifetime;
    long entityHits;

    public DataEntity() {}

    public DataEntity(Entity ent) {
        entityName = ent.name;
        entityID = ent.ID;
        entityDamage = ent.damageDealt;
        entityLifetime = (ent.lastSeen - ent.firstSeen) / 1000f;
        entityDPS = entityDamage > 0 ? entityDamage / entityLifetime : 0;
        entityHealing = ent.healingTaken;
        entityHPS = entityHealing > 0 ? entityHealing / entityLifetime : 0;
        entityTaken = ent.damageTaken;
        entityDeaths = ent.deathTime != 0 ? 1 : 0;
        entityHits = ent.hits;
    }

    public StringProperty getEntityName() {
        return new SimpleStringProperty(entityName);
    }
    public StringProperty getEntityID() {
        return new SimpleStringProperty(entityID);
    }
    public StringProperty getEntityDamage() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityDamage));
    }
    public StringProperty getEntityDPS() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityDPS));
    }
    public StringProperty getEntityHealing() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityHealing));
    }
    public StringProperty getEntityHPS() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityHPS));
    }
    public StringProperty getEntityTaken() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityTaken));
    }
    public StringProperty getEntityDeaths() {
        return new SimpleStringProperty(String.valueOf(entityDeaths));
    }
    public StringProperty getEntityLifetime() {
        return new SimpleStringProperty(String.format("%1$,.1f", entityLifetime));
    }
    public StringProperty getEntityHits() {
        return new SimpleStringProperty(String.valueOf(entityHits));
    }

    public void setEntityID(String n) {
//            entityID.set(n);
    }
    public void setEntityName(String n) {
//            entityName.set(n);
    }
    public void setEntityDamage(String n) {
//            entityDamage.set(n);
    }
    public void setEntityDPS(String n) {
//            entityDPS.set(n);
    }
    public void setEntityHealing(String n) {
//            entityHealing.set(n);
    }
    public void setEntityHPS(String n) {
//            entityHPS.set(n);
    }
    public void setEntityTaken(String n) {
//            entityTaken.set(n);
    }
    public void setEntityDeaths(String n) {
//            entityDeaths.set(n);
    }
    public void setEntityLifetime(String n) {
//            entityLifetime.set(n);
    }
    public void setEntityHits(String n) {
//            entityHits.set(n);
    }
}