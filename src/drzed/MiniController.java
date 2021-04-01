package drzed;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@SuppressWarnings("all")
public class MiniController {
    @FXML
    public TextField infoBox = new TextField();

    public MiniController() {
        infoBox.setEditable(false);
        infoBox.setText("Testing");
        infoBox.setVisible(true);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setMaxSize(600, 60);
    }

    @FXML
    private void initialize() {
        Task task = new Task<Void>() {
            @Override public Void call() {
                while (true) {
                    update();
                    try {
                        Thread.sleep(Configs.guiPollRate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(task).start();
    }

    public void update() {
        try {
            MagicParser.ParseFile();
            Platform.runLater(() -> infoBox.setText(getDisplayText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String getDisplayText() {
        Entity me = MagicParser.getCurrentEncounter().getEntity(MagicParser.myID);
        if (me == null) { return "Waiting for Encounter..."; }
        String DPS = format((long) (me.damageDealt / me.getLifetime()));
        String Damage = format((long) me.damageDealt);
        Ability ab = me.getBestAbility();
        String Spell = ab.name;
        String SpellDPS = format((long) (ab.Damage / ab.hits));
        String SpellDMG = format((long) ab.Damage);
        String TimeActive = String.format("%1$,.1f", me.getLifetime()) + "s";

        if (!me.name.equalsIgnoreCase(Configs.defaultFilter)) {
            System.out.println("Something went horrendously wrong.");
            return "Something went horrendously wrong.";
        }

        return String.format(Configs.miniDisplayFormat, DPS, Damage, Spell, SpellDPS, SpellDMG, TimeActive, me.name);
    }


    double xOffset, yOffset;
    public void mDrag(MouseEvent mouseEvent) {
        Main.miniStage.setX(mouseEvent.getScreenX() + xOffset);
        Main.miniStage.setY(mouseEvent.getScreenY() + yOffset);
    }

    public void mPress(MouseEvent mouseEvent) {
        xOffset = Main.miniStage.getX() - mouseEvent.getScreenX();
        yOffset = Main.miniStage.getY() - mouseEvent.getScreenY();
    }

    public void close(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }
}
