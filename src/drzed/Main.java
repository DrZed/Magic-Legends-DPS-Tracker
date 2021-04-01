package drzed;

import hxckdms.hxcconfig.HxCConfig;
import hxckdms.hxcconfig.handlers.SpecialHandlers;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static Stage stage = null;
    public static Stage miniStage = null;
    static File Directory;
    private static HxCConfig config;
    public static Main instance;

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (Configs.miniMode) {
            Parent root = FXMLLoader.load(getClass().getResource("ML_ParseMini.fxml"));
            primaryStage.setTitle("Magic Legends Mini Parser");
            primaryStage.setScene(new Scene(root, Configs.miniDisplayWidth, 35));
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            miniStage = primaryStage;
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("ML_ParserGUI.fxml"));
            primaryStage.setTitle("Magic Legends Combat Parser");
            primaryStage.setScene(new Scene(root, 1280, 720));
            primaryStage.initStyle(StageStyle.UNDECORATED);
        }
        stage = primaryStage;
        primaryStage.show();
    }

    public Main() {
        super();
        instance = this;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        registerHandlers();
        registerConfigs();
        initialize();
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private static void registerHandlers() {
        SpecialHandlers.registerSpecialClass(Ability.class);
        SpecialHandlers.registerSpecialClass(Entity.class);
        SpecialHandlers.registerSpecialClass(Encounter.class);
        SpecialHandlers.registerSpecialClass(Skill.class);
        SpecialHandlers.registerSpecialClass(SkillTypes.class);
    }

    private static void registerConfigs() {
        config = new HxCConfig(Configs.class, "MagicLegendsParser", new File("./"), "cfg", "MLParse");
    }

    static HxCConfig dummy2 = new HxCConfig(SkillTypes.class, "ML_Skill_Data", new File("./"), "cfg", "MLParse");
    private static void initialize() {
        Directory = new File(Configs.combatLogFolder);
        File sk = new File("./ML_Skill_Data.cfg");
        if (sk.exists()) {
            dummy2.initConfiguration();
            sk.delete();
        }
        config.initConfiguration();
    }

    public static void export(String name) {
        HxCConfig dummy = new HxCConfig(EncounterData.class, name, new File("./data/"), "log", "MLParse");
        dummy.initConfiguration();
        File sk = new File("./ML_Skill_Data.cfg");
        if (sk.exists()) {
            sk.delete();
        }
        dummy2.initConfiguration();
    }
}
