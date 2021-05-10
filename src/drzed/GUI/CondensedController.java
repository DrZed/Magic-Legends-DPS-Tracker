package drzed.GUI;

import drzed.Configs;
import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.MagicParser;
import drzed.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

@SuppressWarnings("all")
public class CondensedController {
    @FXML public MenuItem fileB;
    @FXML public Menu menuD;
    @FXML public TableView<Ability> statsTbl;
    @FXML public TableColumn<Ability, String> abilityCol;
    @FXML public TableColumn<Ability, Number> damageCol2;
    @FXML public TableColumn<Ability, Number> dpsCol2;
    @FXML public TableColumn<Ability, Number> hitsCol2;
    @FXML public TableColumn<Ability, Number> abilityShareCol;

    @FXML
    private void initialize() {
        setFactories();

        Task task = new Task<Void>() {
            @Override public Void call() {
                byte frms = 0;
                while (true) {
                    try {
                        update();
                        Thread.sleep(Configs.guiPollRate);
                        if (frms > 4) {
                            frms = 0;
                        }
                        frms++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(task).start();
    }


    private static Encounter current;
    public void update() {
        updateAllData();
    }

    private void updateAllData() {
        if ((current == null && MagicParser.getCurrentEncounter() != null) || current != MagicParser.getCurrentEncounter()) {
            if (MagicParser.getCurrentEncounter() != null)
                resetData();
        }

        if (current != null) {
            updateAbilityData();
        }
    }

    private void resetData() {
        System.out.println("RESETTING DATA");
        current = MagicParser.getCurrentEncounter();
        Platform.runLater(() -> statsTbl.getItems().clear());
    }

    public void loadEncounter(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ENCOUNTERLOG files (CombatLog*.log)", "CombatLog*.log");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setSelectedExtensionFilter(extFilter);
        chooser.setInitialDirectory(new File("./data/"));
        String enc = chooser.showOpenDialog(Main.stage).getName();
        if (!enc.isEmpty()) {
            Main.importEnc(enc);
            Platform.runLater(() -> resetData());
            Platform.runLater(() -> updateAllData());
        }
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    double xOffset, yOffset;
    public void onDrag(MouseEvent mouseEvent) {
        Main.streamStage.setX(mouseEvent.getScreenX() + xOffset);
        Main.streamStage.setY(mouseEvent.getScreenY() + yOffset);
    }

    public void onPress(MouseEvent mouseEvent) {
        xOffset = Main.streamStage.getX() - mouseEvent.getScreenX();
        yOffset = Main.streamStage.getY() - mouseEvent.getScreenY();
    }

    private void setFactories() {
        abilityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));
        damageCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalDamage()));
        dpsCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDPH()));
        abilityShareCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(current != null && current.getFilterEntity() != null ? current.getFilterEntity().getEffectiveness(cellData.getValue().getTotalDamage()) : 0));
        hitsCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().hits));

        damageCol2.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        dpsCol2.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        hitsCol2.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        abilityShareCol.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()) + "%");
            }
        });
    }

    private void updateAbilityData() {
        if (current.getFilterEntity() != null) {
            Platform.runLater(() -> statsTbl.setItems(FXCollections.observableArrayList(current.getFilterEntity().abilityList)));
        }
        if (current.getFilterEntity() != null && !statsTbl.getItems().isEmpty() && !current.getFilterEntity().abilityList.isEmpty()) {
            for (Ability ability : current.getFilterEntity().abilityList) {
                if (!statsTbl.getItems().contains(ability)) {
                    statsTbl.getItems().add(ability);
                }
            }
            statsTbl.getItems().sort((a,b) -> -(a.totalDamage - b.totalDamage));
        }
        statsTbl.sort();
        statsTbl.refresh();
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

    public void onClick(MouseEvent mouseEvent) {
        updateAllData();
    }
}
