package drzed;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.IOException;

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
    public TableColumn<DataEntity, String> damageCol = new TableColumn("Damage");
    @FXML
    public TableColumn<DataEntity, String> dpsCol = new TableColumn("DPS");
    @FXML
    public TableColumn<DataEntity, String> healCol = new TableColumn("Heal");
    @FXML
    public TableColumn<DataEntity, String> hpscol = new TableColumn("HPS");
    @FXML
    public TableColumn<DataEntity, String> takenCol = new TableColumn("Taken");
    @FXML
    public TableColumn<DataEntity, String> deathCol = new TableColumn("Death");
    @FXML
    public TableColumn<DataEntity, String> durationCol = new TableColumn("Time");
    @FXML
    public TableColumn<DataEntity, String> hitsCol = new TableColumn("Hits");

    @FXML
    public TableColumn<DataAbility, String> abilityCol = new TableColumn<>("Ability");
    @FXML
    public TableColumn<DataAbility, String> basedmgCol = new TableColumn<>("Base DMG");
    @FXML
    public TableColumn<DataAbility, String> damageCol2 = new TableColumn<>("Damage");
    @FXML
    public TableColumn<DataAbility, String> dpsCol2 = new TableColumn<>("DPH");
    @FXML
    public TableColumn<DataAbility, String> hitsCol2 = new TableColumn<>("Hits");
    @FXML
    public Button quitBtn;

    public static ObservableList<DataEntity> dataEntities = FXCollections.observableArrayList();
    public static ObservableList<DataAbility> dataAbilities = FXCollections.observableArrayList();
    private static ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    public Controller() {
        table.setEditable(false);
        statsTbl.setEditable(false);

        table.getColumns().addAll(nameCol, damageCol, dpsCol, healCol, hpscol, takenCol, deathCol, durationCol, hitsCol);
        statsTbl.getColumns().addAll(abilityCol, basedmgCol, damageCol2, dpsCol2, hitsCol2);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        statsTbl.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(dataEntities);
        statsTbl.setItems(dataAbilities);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(180);
        pieChart.setClockwise(true);
    }

    @FXML
    private void initialize() {
        Task task = new Task<Void>() {
            @Override public Void call() {
                while (true) {
                    update();
                }
            }
        };
        new Thread(task).start();
    }

    private static Encounter current;
    private static long tmin = 0;
    private static DataEntity fil;
    public void update() {
        if (System.currentTimeMillis() - tmin < Configs.guiPollRate) return;
        try {
            MagicParser.ParseFile();
            if (current == null && MagicParser.getCurrentEncounter() != null) {
                current = MagicParser.getCurrentEncounter();
                Platform.runLater(() -> pieChart.getData().clear());
            } else if (current != null && MagicParser.getCurrentEncounter() != null && current != MagicParser.getCurrentEncounter()) {
                current = MagicParser.getCurrentEncounter();
                Platform.runLater(() -> pieChart.getData().clear());
            }
            if (current == null) return;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (dataEntities.size() != current.entities.size()) {
            dataEntities = FXCollections.observableArrayList();
            dataAbilities = FXCollections.observableArrayList();
            for (Entity value : current.entities.values()) {
                DataEntity de = new DataEntity(value);
                if (value.name.equalsIgnoreCase(Configs.defaultFilter)) {
                    fil = de;
                }
                dataEntities.add(de);
            }

            table.getItems().removeAll();

            nameCol.setCellValueFactory(cellData -> cellData.getValue().getEntityName());
            damageCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDamage());
            dpsCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDPS());
            healCol.setCellValueFactory(cellData -> cellData.getValue().getEntityHealing());
            hpscol.setCellValueFactory(cellData -> cellData.getValue().getEntityHPS());
            takenCol.setCellValueFactory(cellData -> cellData.getValue().getEntityTaken());
            deathCol.setCellValueFactory(cellData -> cellData.getValue().getEntityDeaths());
            durationCol.setCellValueFactory(cellData -> cellData.getValue().getEntityLifetime());
            hitsCol.setCellValueFactory(cellData -> cellData.getValue().getEntityHits());

            table.setItems(dataEntities);

            statsTbl.getItems().removeAll();
            if (fil != null) {
                table.getSelectionModel().select(fil);
//                System.out.println(table.getSelectionModel().getSelectedIndex());
            }
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                Entity ent = current.getEntity(table.getSelectionModel().getSelectedItem().entityID);
                if (ent.abilities.size() > 0) {
                    for (Ability value : ent.abilities.values()) {
                        dataAbilities.add(new DataAbility(value));
                    }
                    abilityCol.setCellValueFactory(cellData -> cellData.getValue().getAbilityName());
                    basedmgCol.setCellValueFactory(cellData -> cellData.getValue().getAbilityBaseDamage());
                    damageCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityDamage());
                    dpsCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityDPS());
                    hitsCol2.setCellValueFactory(cellData -> cellData.getValue().getAbilityHits());
                    statsTbl.setItems(dataAbilities);
                }

                if (dataAbilities.size() > 0) {
                    for (DataAbility dataAbility : dataAbilities) {
                        Platform.runLater(() -> addData(dataAbility.abilityName, dataAbility.abilityDamage));
//                        if (pieChart.getData().isEmpty()) {
//                            pieChart.setData(pieChartData);
//                        }
                    }
                } else {
//                    pieChartData.clear();
                    Platform.runLater(() -> pieChart.getData().clear());
                }
            }
        }

        tmin = System.currentTimeMillis();
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
        Main.Directory = chooser.showOpenDialog(Main.stage);
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
    public void onMouseDrag(MouseEvent mouseEvent) {
        Main.stage.setX(mouseEvent.getScreenX() + xOffset);
        Main.stage.setY(mouseEvent.getScreenY() + yOffset);
    }

    public void mousePress(MouseEvent mouseEvent) {
        xOffset = Main.stage.getX() - mouseEvent.getScreenX();
        yOffset = Main.stage.getY() - mouseEvent.getScreenY();
    }
}
