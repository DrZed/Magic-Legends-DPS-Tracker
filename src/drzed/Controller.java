package drzed;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Controller {

    @FXML
    public LineChart lineChart;
    @FXML
    public MenuItem themeB;
    @FXML
    public MenuItem aboutB;
    @FXML
    public MenuItem fileB;
    @FXML
    public MenuItem closeB;
    @FXML
    public PieChart pieChart = new PieChart();
    @FXML
    public TableView<DataEntity> table = new TableView<DataEntity>();
    @FXML
    public TableView<DataAbility> statsTbl = new TableView<DataAbility>();

    @FXML
    public TableColumn<DataEntity, String> nameCol = new TableColumn("Name");
    @FXML
    public TableColumn<DataEntity, Number> damageCol = new TableColumn("Damage");
    @FXML
    public TableColumn<DataEntity, Number> dpsCol = new TableColumn("DPS");
    @FXML
    public TableColumn<DataEntity, Number> healCol = new TableColumn("Heal");
    @FXML
    public TableColumn<DataEntity, Number> hpscol = new TableColumn("HPS");
    @FXML
    public TableColumn<DataEntity, Number> takenCol = new TableColumn("Taken");
    @FXML
    public TableColumn<DataEntity, Number> deathCol = new TableColumn("Death");
    @FXML
    public TableColumn<DataEntity, Number> durationCol = new TableColumn("Time");
    @FXML
    public TableColumn<DataEntity, Number> hitsCol = new TableColumn("Hits");

    @FXML
    public TableColumn<DataAbility, String> abilityCol = new TableColumn<>("Ability");
//    @FXML
//    public TableColumn<DataAbility, String> basedmgCol = new TableColumn<>("Base DMG");
    @FXML
    public TableColumn<DataAbility, Number> damageCol2 = new TableColumn<>("Damage");
    @FXML
    public TableColumn<DataAbility, Number> dpsCol2 = new TableColumn<>("DPH");
    @FXML
    public TableColumn<DataAbility, Number> hitsCol2 = new TableColumn<>("Hits");
    @FXML
    public Button quitBtn;

    public static ObservableList<DataEntity> dataEntities = FXCollections.observableArrayList();
    public static List<Entity> dataEntitiesAdded = new ArrayList<>();
    public static ObservableList<DataAbility> dataAbilities = FXCollections.observableArrayList();
    private static ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    public Controller() {
        table.setEditable(false);
        statsTbl.setEditable(false);

        table.getColumns().addAll(nameCol, damageCol, dpsCol, healCol, hpscol, takenCol, deathCol, durationCol, hitsCol);
        statsTbl.getColumns().addAll(abilityCol, damageCol2, dpsCol2, hitsCol2);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        statsTbl.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(dataEntities);
        statsTbl.setItems(dataAbilities);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(180);
        pieChart.setClockwise(true);

        dataEntities = FXCollections.observableArrayList();
        dataEntitiesAdded = new ArrayList<>();
        dataAbilities = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setFactories();

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


    private static Encounter current;
    private static DataEntity fil;
    public void update() {
        try {
            MagicParser.ParseFile();
            if ((current == null && MagicParser.getCurrentEncounter() != null) || current != MagicParser.getCurrentEncounter()) {
                resetData();
            }

            if (current == null) return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateDataEntities();
        if (dataEntities.size() > 0 && dataEntitiesAdded.size() > 0) {
            updateTable();
            updateAbilityData();
        }
    }

    private void resetData() {
        current = MagicParser.getCurrentEncounter();
        Platform.runLater(() -> pieChart.getData().clear());
        dataEntities = FXCollections.observableArrayList();
        dataEntitiesAdded = new ArrayList<>();
        dataAbilities = FXCollections.observableArrayList();
        table.getItems().removeAll();
        inited = false;
    }

    boolean inited = false;
    private void updateTable() {
        if (!inited) {
            table.setItems(dataEntities);
            inited = true;
        }
        table.refresh();
    }

    private void updateDataEntities() {
        for (Entity value : current.getEnts().values()) {
            DataEntity de = new DataEntity(value);
            if (!dataEntitiesAdded.contains(value)) {
                dataEntities.add(de);
                dataEntitiesAdded.add(value);
                if (value.name.equalsIgnoreCase(Configs.defaultFilter)) {
                    fil = de;
                    if (fil != null) {
                        table.getSelectionModel().select(fil);
                    }
                }
            }
        }
    }

    private void updateAbilityData() {
        dataAbilities = FXCollections.observableArrayList();
        statsTbl.getItems().removeAll();
        if (table.getSelectionModel().getSelectedIndex() != -1) {
            Entity ent = current.getEntity(table.getSelectionModel().getSelectedItem().ent.ID);
            if (ent.abilities.size() > 0) {
                for (Ability value : ent.abilities.values()) {
                    dataAbilities.add(new DataAbility(value));
                }
                Platform.runLater(() -> statsTbl.setItems(dataAbilities));
            }

            if (dataAbilities.size() > 0) {
                for (DataAbility dataAbility : dataAbilities) {
                    Platform.runLater(() -> addData(dataAbility.ab.name, dataAbility.ab.Damage));
                }
            } else {
                Platform.runLater(() -> pieChart.getData().clear());
            }
        }
    }

    //adds new Data to the list
    public void naiveAddData(String name, double value) {
        pieChart.getData().add(new PieChart.Data(name, value));
        pieChart.setLabelsVisible(false);
        pieChart.setLabelLineLength(0);
        pieChart.setLegendSide(Side.LEFT);
    }

    //updates existing Data-Object if name matches
    public void addData(String name, double value)
    {
        for(PieChart.Data d : pieChart.getData())
        {
            if(d.getName().equals(name))
            {
                d.setPieValue(value);
                pieChart.setLabelsVisible(false);
                pieChart.setLabelLineLength(0);
                return;
            }
        }
        naiveAddData(name, value);
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

    public void onTheme(ActionEvent actionEvent) {

    }

    public void onAbout(ActionEvent actionEvent) {

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
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getEntityName());
        damageCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDamage());
        dpsCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDPS());
        healCol.setCellValueFactory(cellData -> cellData.getValue().getEntityHealing());
        hpscol.setCellValueFactory(cellData -> cellData.getValue().getEntityHPS());
        takenCol.setCellValueFactory(cellData -> cellData.getValue().getEntityTaken());
        deathCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDeaths());
        durationCol.setCellValueFactory(cellData -> cellData.getValue().getEntityLifetime());
        hitsCol.setCellValueFactory(cellData -> cellData.getValue().getEntityHits());

        abilityCol.setCellValueFactory(cellData -> cellData.getValue().getAbilityName());
//                basedmgCol.setCellValueFactory(cellData -> cellData.getValue().getAbilityBaseDamage());
        damageCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityDamage());
        dpsCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityDPS());
        hitsCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityHits());

        /* SET DISPLAY FACTORY SO NO NUMBERS ARE FORMATTED IN SCIENTIFIC NOTATION */
        damageCol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        dpsCol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        healCol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        hpscol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        takenCol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        durationCol.setCellFactory(tc -> new TableCell<DataEntity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        damageCol2.setCellFactory(tc -> new TableCell<DataAbility, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });
        dpsCol2.setCellFactory(tc -> new TableCell<DataAbility, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", value.doubleValue()));
            }
        });

        /* SET ROW FACTORY TO COLORIZE ROWS WITH OWNERS (PETS) */
        table.setRowFactory(tv -> new TableRow<DataEntity>() {
            @Override
            protected void updateItem(DataEntity item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.ent != null && item.ent.isPlayer && !table.getSelectionModel().getSelectedItem().ent.name.equalsIgnoreCase(item.ent.name)) {
                    setStyle("-fx-background-color:" + Configs.playerColor);
                }
                if (item != null && item.ent != null && item.ent.ownerEntity != null) {
                    Entity owner = item.ent.ownerEntity;
                    if (owner.name.equalsIgnoreCase(Configs.defaultFilter)) {
                        setStyle("-fx-background-color:" + Configs.ownPetColor);
                    } else if (table.getSelectionModel().getSelectedIndex() != -1) {
                        Entity ent = current.getEntity(table.getSelectionModel().getSelectedItem().ent.ID);
                        if (owner.name.equalsIgnoreCase(ent.name)) {
                            setStyle("-fx-background-color:" + Configs.selectedPetColor);
                        }
                    }
                }
            }
        });
    }
}
