package drzed;

import java.util.Comparator;

public class Ability {
    public String name;
    public String ID;
    public double baseDamage;
    public double Damage;
    public int hits;

    public Ability() {}

    public Ability(String nm, String id) {
        name = nm;
        ID = id;
        hits = 0;
    }

    public void updateBaseDamage(double dm) {
        baseDamage = (baseDamage + dm) / 2f;
    }

    public void updateDamage(double dm) {
        Damage += dm;
        hits++;
    }

    public double getDamage() {
        return Damage;
    }
}
