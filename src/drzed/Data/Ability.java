package drzed.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ability {
    public String name = "";
    public String ID = "";
    public int baseMagnitude = 0;
    public int totalDamage = 0;
    public int baseHealingMagnitude = 0;
    public int totalHealing = 0;
    public int hits = 0;
    public int taken = 0;
    public String type = "";

    public Ability() {}

    public Ability(String nm, String id) {
        name = nm;
        ID = id;
    }

    void update(double dam, double bas, double tkn) {
        if (dam > 0) {
            if (bas == 0)
                updateBaseDamage(dam);
            else
                updateBaseDamage(bas);
            updateDamage(dam);
        } else if (dam < 0) {
            if (bas == 0)
                updateBaseHealing(dam);
            else
                updateBaseHealing(bas);
            updateHealing(dam);
        }
        taken += (int) Math.round(tkn);
        hits++;
    }

    private void updateBaseDamage(double dm) {
        baseMagnitude = (int) Math.round((baseMagnitude + dm) / 2f);
    }

    private void updateDamage(double dm) {
        totalDamage += (int) Math.round(dm);
    }
    private void updateBaseHealing(double dm) {
        dm *= -1;
        baseHealingMagnitude = (int) Math.round((baseMagnitude + dm) / 2f);
    }

    private void updateHealing(double dm) {
        dm *= -1;
        totalHealing += (int) Math.round(dm);
    }

    public int getTotalDamage() {
        return totalDamage;
    }
    public int getDPH() {
        return totalDamage / hits;
    }
    private static final List<String> prims = Arrays.asList("Pn.419cwd","Pn.Buehls", "Pn.6ccekm1", "Pn.Zf9cbs", "Pn.90wbfy", "Pn.Cp3ahw",
    "Pn.H9nmk5", "Pn.Pyy1l21", "Pd.41y76q1", "Pd.Gioakm1", "Pd.H2w56q1", "Pd.J5ucg9", "Pd.L23hyi1", "Pd.Ov5vzj", "Pd.Q6c3qq1",
    "Pl.00ajwk1", "Pl.1dyu", "Pl.2845gv1", "Pl.30nu7m", "Pl.44euwa1", "Pl.5zzrfo", "Pl.6n21iy1", "Pl.71fl3v", "Pl.8jfh3v",
    "Pl.9hbt8e1", "Pl.9w3gnv", "Pl.Crnhfy", "Pl.Dq0v", "Pl.Fxwqi2", "Pl.G7txnl", "Pl.Gxzkls", "Pl.Hvy5gq1", "Pl.Hvy9yn1",
    "Pl.Iasi5t", "Pl.Mjmnhr", "Pl.Q2crzo", "Pl.Qjbmgg1", "Pl.Qilxhy1", "Pl.Te6qyd1", "Pl.Uptngg1", "Pl.Uqgeua", "Pl.Wn0s2f1",
    "Pl.X3lukc1", "Pl.Yon12n", "Pl.Zgtred1", "Pn.0x76h11", "Pn.2y4l3v", "Pn.31e4ys1", "Pn.33qn2f1", "Pn.344iw3", "Pn.34bli2",
    "Pn.366m7r", "Pn.3zmmwf1"); //Pulled from translation files, may need to add more, and eventually Gear/Artifact/Q/W abilities
    //Primaries, Primaries upgraded, Old Primaries, New Primaries, and even Unreleased Class Primaries (Pyromancer/Cryomancer)
    public String getType() {
        if (prims.contains(ID)) {
            return "Ability";
        }
        if (ID.contains("Spell")) {
            return "Spell";
        }
        if (ID.contains("Ability")) {
            return "Ability";
        }
        if (taken > 0) {
            return ID.contains("Token") ? "Token" : "Summon";
        }
        return "Sorcery";
    }
}
