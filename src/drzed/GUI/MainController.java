package drzed.GUI;

import drzed.Configs;
import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.Data.Entity;
import drzed.MagicParser;
import drzed.Main;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import static drzed.Main.DEBUG_ALL_STEPS_MODE;

@SuppressWarnings("all")
public class MainController {
    @FXML
    public LineChart lineChart;
    @FXML
    public MenuItem fileB;
    @FXML
    public PieChart pieChart = new PieChart();
    @FXML
    public TableView<Entity> table = new TableView<>();
    @FXML
    public TableView<Ability> statsTbl = new TableView<>();

    @FXML
    public TableColumn<Entity, String> nameCol = new TableColumn("Name");
    @FXML
    public TableColumn<Entity, Number> damageCol = new TableColumn("Damage");
    @FXML
    public TableColumn<Entity, Number> dpsCol = new TableColumn("DPS");
    @FXML
    public TableColumn<Entity, Number> healCol = new TableColumn("Heal");
    @FXML
    public TableColumn<Entity, Number> hpscol = new TableColumn("HPS");
    @FXML
    public TableColumn<Entity, Number> takenCol = new TableColumn("Taken");
    @FXML
    public TableColumn<Entity, Number> deathCol = new TableColumn("Death");
    @FXML
    public TableColumn<Entity, Number> durationCol = new TableColumn("Time");
    @FXML
    public TableColumn<Entity, Number> hitsCol = new TableColumn("Hits");

    @FXML
    public TableColumn<Ability, String> abilityCol = new TableColumn<>("Ability");
    @FXML
    public TableColumn<Ability, Number> damageCol2 = new TableColumn<>("Damage");
    @FXML
    public TableColumn<Ability, Number> dpsCol2 = new TableColumn<>("DPH");
    @FXML
    public TableColumn<Ability, Number> hitsCol2 = new TableColumn<>("Hits");
    @FXML
    public TableColumn<Ability, Number> abilityShareCol = new TableColumn<>("%");
    @FXML
    public Menu menuD;

    static NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
    public static ObservableList<Entity> ents = FXCollections.observableArrayList();
    public static ObservableList<Ability> abils = FXCollections.observableArrayList();
    private static ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static Entity curFiltEnt;

    public MainController() {
        table.setEditable(false);
        statsTbl.setEditable(false);
        format.setMaximumFractionDigits(1);
        damageCol.setSortType(TableColumn.SortType.DESCENDING);
        damageCol2.setSortType(TableColumn.SortType.DESCENDING);
        table.getColumns().addAll(nameCol, damageCol, dpsCol, healCol, hpscol, takenCol, deathCol, durationCol, hitsCol);
        statsTbl.getColumns().addAll(abilityCol, damageCol2, dpsCol2, hitsCol2, abilityShareCol);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        statsTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        table.setItems(ents);
        table.getSortOrder().add(damageCol);
        statsTbl.setItems(abils);
        statsTbl.getSortOrder().add(damageCol2);

        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(180);
        pieChart.setClockwise(true);

        ents = FXCollections.observableArrayList();
        abils = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.init SET FACTORIES");
        setFactories();

        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.init CREATE THREAD");
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
    private static boolean reviewMode;
    public void update() {
        try {
            if (!reviewMode) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update CALL PARSE FILE");
                    MagicParser.ParseFile();
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update POST FILE");
            }
        } catch (IOException e) { e.printStackTrace(); }

        if ((current == null && MagicParser.getCurrentEncounter() != null) || current != MagicParser.getCurrentEncounter()) {
            if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update RESET DATA");
            resetData();
        }

        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update UPDATE DATA");
        if (current != null) {
            if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update UPDATE DATA ENTITIES");
            updateDataEntities();
            if (ents.size() > 0) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update UPDATE TABLE");
                updateTable();
            }
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update UPDATE ABILITY DATA");
                updateAbilityData();
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update UPDATE PIE");
                updatePie();
//                updateLineChart();
            }
        }

        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.update END");
    }

    private void updateLineChart() {
        Entity ent = table.getSelectionModel().getSelectedItem();
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Damage");

        XYChart.Series<Number, Number> l = new XYChart.Series();
        int uplimit = 100, dlimit = 2000;
        l.setName("DPS Overtime");
        for (int i = 0; i < Math.min(ent.dpsOT.size(), 10); i++) {
            if (ent.dpsOT.get(i) > uplimit) uplimit = ent.dpsOT.get(i) + 500;
            l.getData().add(new XYChart.Data(30 * i, ent.dpsOT.get(i)));
        }
        yAxis.setUpperBound(uplimit);
        yAxis.setLowerBound(dlimit);
        xAxis.setLowerBound(30);
        xAxis.setUpperBound(3000);
        lineChart = new LineChart(xAxis, yAxis);

        Platform.runLater(() -> lineChart.getData().add(l));
    }

    private void resetData() {
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.resetData RESET BEGIN");
        current = MagicParser.getCurrentEncounter();
        ents = FXCollections.observableArrayList();
        abils = FXCollections.observableArrayList();
        Platform.runLater(() -> pieChart.getData().clear());
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.resetData RESET TABLES");
        table.getItems().clear();
        statsTbl.getItems().clear();
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.resetData END");
    }

    private void updateTable() {
        table.setItems(ents);
        table.sort();
        table.refresh();
    }

    private void updateDataEntities() {
//        ents = FXCollections.observableArrayList(current.getEnts().values());
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateDataEntities UPDATE LOOP START");
        for (Entity value : current.getEnts().values()) {
            if (!ents.contains(value)) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateDataEntities ADDING ENT " + value.name);
                ents.add(value);
                if (value.name.equalsIgnoreCase(Configs.defaultFilter)) {
                    if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateDataEntities SETTING FILTER ENT TO :" + value.name);
                    if (current.getFilterEntity()  != null && table.getSelectionModel() != null) {
                        try {
                            table.getSelectionModel().select(current.getFilterEntity());
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateDataEntities UPDATE LOOP END");
    }

    private void updateAbilityData() {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateAbilityData BEGIN");
        Entity ent = table.getSelectionModel().getSelectedItem();
        if (curFiltEnt == null || !ent.name.equalsIgnoreCase(curFiltEnt.name)) {
            curFiltEnt = ent;
            abils = FXCollections.observableArrayList(ent.abilities.values());
            Platform.runLater(() -> statsTbl.setItems(abils));
        } else {
            for (Ability ability : ent.abilities.values()) {
                if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateAbilityData ADDING ABILITY : " + ability.name);
                if (!statsTbl.getItems().contains(ability)) {
                    statsTbl.getItems().add(ability);
                }
            }
        }
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateAbilityData SORTING");
        statsTbl.sort();
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateAbilityData REFRESHING DATA");
        statsTbl.refresh();
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updateAbilityData END");
    }

    private void updatePie() {
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updatePie BEGIN");
        if (statsTbl.getItems().size() > 0) {
            for (Ability ab : statsTbl.getItems()) {
                Platform.runLater(() -> addData(ab.name, ab.Damage));
            }
        } else {
            Platform.runLater(() -> pieChart.getData().clear());
        }
        if (DEBUG_ALL_STEPS_MODE) System.out.println("MainController.updatePie END");
    }

    //adds new Data to the list
    public void naiveAddData(String name, double value) {
        pieChart.getData().add(new PieChart.Data(name, value));
        pieChart.setLabelsVisible(false);
        pieChart.setLabelLineLength(0);
        pieChart.setLegendSide(Side.LEFT);
    }

    //updates existing Data-Object if name matches
    public void addData(String name, double value) {
        for(PieChart.Data d : pieChart.getData()) {
            if(d.getName().equals(name)) {
                d.setPieValue(value);
                pieChart.setLabelsVisible(false);
                pieChart.setLabelLineLength(0);
                return;
            }
        }
        naiveAddData(name, value);
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
        //TODO
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
/*
    public static String fixDisplayStr(String str) {
        System.out.println(str);
        return new String(str.getBytes(), Charset.forName("UTF-8"));
    }*/

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
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));
        damageCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().damageDealt)));
        dpsCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getDPS())));
        healCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().healingTaken)));
        hpscol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getHPS())));
        takenCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().damageTaken)));
        deathCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().deaths));
        durationCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getLifetime())));
        hitsCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().hits));

        abilityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));
        damageCol2.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getDamage())));
        dpsCol2.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getDPH())));
        abilityShareCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(getFormattedDoubleLocalized(cellData.getValue().getDamage() / getSelDmg()) * 100D));
        hitsCol2.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().hits));

        /* SET DISPLAY FACTORY SO NO NUMBERS ARE FORMATTED IN SCIENTIFIC NOTATION */
     /*   nameCol.setCellFactory(tc -> new TableCell<Entity, String>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(fixDisplayStr(value));
            } //I give up trying to forcibly fix Tsidé's name string encoding é in java sucks nuts
        });*/
        damageCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        dpsCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        healCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        hpscol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        takenCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        durationCol.setCellFactory(tc -> new TableCell<Entity, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        damageCol2.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        dpsCol2.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())));
            }
        });
        abilityShareCol.setCellFactory(tc -> new TableCell<Ability, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (!empty) setText(String.format("%1$,.1f", getFormattedDoubleLocalized(value.doubleValue())) + "%");
            }
        });

        /* SET ROW FACTORY TO COLORIZE ROWS WITH OWNERS (PETS) */
        table.setRowFactory(tv -> new TableRow<Entity>() {
            @Override
            protected void updateItem(Entity item, boolean empty) {
                super.updateItem(item, empty);
                if (current == null) return;
                if (item != null && item != null && item.isPlayer && table.getSelectionModel().getSelectedItem() != null && !table.getSelectionModel().getSelectedItem().name.equalsIgnoreCase(item.name)) {
                    setStyle("-fx-background-color:" + Configs.playerColor);
                }
                if (item != null && item != null && item.ownerEntity != null && !item.ownerEntity.isEmpty()) {
                    Entity owner = current.getEntity(item.ownerEntity);
                    if (owner == null) return;
                    if (owner.name == null) return;
                    if (owner.name.equalsIgnoreCase(Configs.defaultFilter)) {
                        setStyle("-fx-background-color:" + Configs.ownPetColor);
                    } else if (table.getSelectionModel().getSelectedIndex() != -1) {
                        Entity ent = current.getEntity(table.getSelectionModel().getSelectedItem().ID);
                        if (owner.name.equalsIgnoreCase(ent.name)) {
                            setStyle("-fx-background-color:" + Configs.selectedPetColor);
                        }
                    }
                }
            }
        });
    }
    
    private static Double getFormattedDoubleLocalized(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {return 0.0D;}
        String localizedString = FORMAT.format(d);

        return Double.parseDouble(localizedString);
//        if (localizedString.isEmpty()) {
//            return d;
//        }
//        System.out.println(d);
//        try {
//            return format.parse(localizedString).doubleValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//            int count = localizedString.length() - localizedString.replace(".", "").length();
//            int count2 = localizedString.length() - localizedString.replace(",", "").length();
//            return Double.parseDouble(count == 0 && count2 > 1 ? replaceLast(localizedString, ",", ".") : localizedString);
//        }
    }

    private double getSelDmg() {
        if (table.getSelectionModel().getSelectedItem() != null) {
            return table.getSelectionModel().getSelectedItem().damageDealt;
        }
        return 1;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
}
