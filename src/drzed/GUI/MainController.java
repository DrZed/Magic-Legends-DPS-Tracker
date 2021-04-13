package drzed.GUI;

import drzed.Configs;
import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.Data.Entity;
import drzed.MagicParser;
import drzed.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

@SuppressWarnings("all")
public class MainController {
    @FXML public MenuItem fileB;
    @FXML public Menu menuD;
    @FXML public PieChart pieChart;

    @FXML public TableView<Entity> table;
    @FXML public TableColumn<Entity, String> nameCol;
    @FXML public TableColumn<Entity, Number> damageCol;
    @FXML public TableColumn<Entity, Number> dpsCol;
    @FXML public TableColumn<Entity, Number> healCol;
    @FXML public TableColumn<Entity, Number> hpscol;
    @FXML public TableColumn<Entity, Number> takenCol;
    @FXML public TableColumn<Entity, Number> deathCol;
    @FXML public TableColumn<Entity, Number> durationCol;
    @FXML public TableColumn<Entity, Number> hitsCol;
    @FXML public TableColumn<Entity, Number> shieldCol;
    @FXML public TableColumn<Entity, Number> killCol;
    @FXML public TableColumn<Entity, Number> effCol;

    @FXML public TableView<Ability> statsTbl;
    @FXML public TableColumn<Ability, String> abilityCol;
    @FXML public TableColumn<Ability, String> typeCol;
    @FXML public TableColumn<Ability, Number> damageCol2;
    @FXML public TableColumn<Ability, Number> dpsCol2;
    @FXML public TableColumn<Ability, Number> hitsCol2;
    @FXML public TableColumn<Ability, Number> abilityShareCol;
    @FXML public TableColumn<Ability, Number> abTaken;
    @FXML public TableColumn<Ability, Number> healCol1;

    private static ObservableList<Entity> ents = FXCollections.observableArrayList();
    private static Entity curFiltEnt;

    public MainController() {
        ents = FXCollections.observableArrayList();
    }

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
    private static boolean reviewMode;
    public void update() {
        try {
            if (!reviewMode) {
                MagicParser.ParseFile();
            }
        } catch (IOException e) { e.printStackTrace(); }

        if ((current == null && MagicParser.getCurrentEncounter() != null) || current != MagicParser.getCurrentEncounter()) {
            if (MagicParser.getCurrentEncounter() != null)
                resetData();
        }

        if (current != null) {
            updateDataEntities();
            if (ents.size() > 0) {
                updateTable();
            }
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                updateAbilityData();
                updatePie();
            }
        }
    }

    private void resetData() {
        System.out.println("RESETTING DATA");
        current = MagicParser.getCurrentEncounter();
        ents = FXCollections.observableArrayList();
        Platform.runLater(() -> pieChart.getData().clear());
        Platform.runLater(() -> table.getItems().clear());
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
            reviewMode = true;
            Main.importEnc(enc);
            Platform.runLater(() -> resetData());
            Platform.runLater(() -> table.getItems().clear());
            Platform.runLater(() -> statsTbl.getItems().clear());
        }
    }

    public void closeEncounter(ActionEvent actionEvent) {
        reviewMode = false;
    }

    public void onFile(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("COMBATLOG files (Combatlog*.log)", "Combatlog*.log");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setSelectedExtensionFilter(extFilter);
        Main.Directory = chooser.showOpenDialog(Main.stage).getParentFile();
        System.out.println(Main.Directory.getAbsolutePath().replaceAll("\\\\", "/"));
        Configs.combatLogFolder = Main.Directory.getAbsolutePath().replaceAll("\\\\", "/");
        Main.resaveConfig();
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
        Main.stage.setX(mouseEvent.getScreenX() + xOffset);
        Main.stage.setY(mouseEvent.getScreenY() + yOffset);
    }

    public void onPress(MouseEvent mouseEvent) {
        xOffset = Main.stage.getX() - mouseEvent.getScreenX();
        yOffset = Main.stage.getY() - mouseEvent.getScreenY();
    }

    public void openMini(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ML_ParseMini.fxml"));
            Stage secondStage = new Stage();
            secondStage.setTitle("Magic Legends Mini Parser");
            secondStage.setScene(new Scene(root, Configs.miniDisplayWidth, 35));
            secondStage.initStyle(StageStyle.TRANSPARENT);
            secondStage.setAlwaysOnTop(true);
            secondStage.setX(0);
            secondStage.setY(0);
            Main.miniStage = secondStage;
            secondStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFactories() {
        /* SET CELL VALUE FACTORY TO SPECIFIED DATA TYPES */
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));
        damageCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().damageDealt));
        dpsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDPS()));
        healCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().healingTaken));
        hpscol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getHPS()));
        takenCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().damageTaken));
        deathCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().deaths));
        durationCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLifetime()));
        hitsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().hits));
//        ownerCol.setCellValueFactory(cellData -> new SimpleStringProperty(current != null && !cellData.getValue().ownerEntityID.isEmpty() ? current.getEntity(cellData.getValue().ownerEntityID).name : ""));
        shieldCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().shieldTaken));
        killCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().kills));
        effCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getEff()));

        abilityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));
        damageCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalDamage()));
        dpsCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDPH()));
        abilityShareCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(current != null && curFiltEnt != null ? curFiltEnt.getEffectiveness(cellData.getValue().getTotalDamage()) : 0));
        hitsCol2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().hits));
        abTaken.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().taken));
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        healCol1.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().totalHealing));

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
        abTaken.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        healCol1.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        dpsCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        healCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        hpscol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        takenCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        deathCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        durationCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        hitsCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        shieldCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        killCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });
        damageCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(DecimalFormat.getNumberInstance().format(value.intValue()));
            }
        });

        effCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()) + "%");
            }
        });
        abilityShareCol.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()) + "%");
            }
        });

        /* SET ROW FACTORY TO COLORIZE ROWS WITH OWNERS (PETS) */
        table.setRowFactory(tv -> new TableRow<Entity>() {
            @Override
            protected void updateItem(Entity item, boolean empty) {
                super.updateItem(item, empty);
                if (current == null) return;
                if (item != null && item != null) {
                    if (item.name.equalsIgnoreCase(Configs.selfPlayerName) && curFiltEnt != item)
                        setStyle("-fx-background-color:" + Configs.selfColor);
                    if (item.isPlayer && curFiltEnt != item)
                        setStyle("-fx-background-color:" + Configs.playerColor);
                }
            }
        });
    }

    private void updateDataEntities() {
        for (Entity value : current.getEntities().values()) {
            if (!ents.contains(value)) {
                ents.add(value);
                if (value.name.equalsIgnoreCase(Configs.selfPlayerName)) {
                    if (current.getFilterEntity()  != null && table.getSelectionModel() != null) {
                        try {
                            table.getSelectionModel().select(current.getFilterEntity());
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    private void updateTable() {
        table.setItems(ents);
        table.sort();
        table.refresh();
    }

    private void updateAbilityData() {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Entity ent = table.getSelectionModel().getSelectedItem();
        if (curFiltEnt == null || !ent.name.equalsIgnoreCase(curFiltEnt.name)) {
            curFiltEnt = ent;
            Platform.runLater(() -> pieChart.getData().clear());
            Platform.runLater(() -> statsTbl.setItems(ent.abilityList));
        }
        statsTbl.sort();
        statsTbl.refresh();
    }

    private void updatePie() {
        if (statsTbl.getItems().size() > 0) {
            for (int i = 0; i < statsTbl.getItems().size(); i++) {
                Ability ab = statsTbl.getItems().get(i);
                if (i < 10) {
                    Platform.runLater(() -> addData(ab.name, ab.totalDamage));
                } else {
                    Platform.runLater(() -> removeData(ab.name));
                }
            }
        } else {
            Platform.runLater(() -> pieChart.getData().clear());
        }
        if (pieChart.getData().size() > 10) {
            Platform.runLater(() -> pieChart.getData().clear());
        }
    }

    //adds new Data to the list
    public void naiveAddData(String name, double value) {
        pieChart.getData().add(new PieChart.Data(name, value));
        pieChart.setLabelsVisible(Configs.pieChartLabels);
//        pieChart.setLabelLineLength(0);
//        pieChart.setLegendSide(Side.LEFT);
    }

    //updates existing Data-Object if name matches
    public void addData(String name, double value) {
        for(PieChart.Data d : pieChart.getData()) {
            if(d.getName().equals(name)) {
                d.setPieValue(value);
//                pieChart.setLabelsVisible(false);
//                pieChart.setLabelLineLength(0);
                return;
            }
        }
        naiveAddData(name, value);
    }
    public void removeData(String name) {
        for(PieChart.Data d : pieChart.getData()) {
            if(d.getName().equals(name)) {
                pieChart.getData().remove(d);
                return;
            }
        }
    }

    private int getSelDmg() {
        if (table.getSelectionModel().getSelectedItem() != null) {
            return table.getSelectionModel().getSelectedItem().damageDealt;
        }
        return 1;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
}
