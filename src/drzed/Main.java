package drzed;

import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.Data.Entity;
import drzed.Data.AbilityData;
import drzed.Data.subtype.EncounterData;
import drzed.Data.subtype.EntityNames;
import drzed.Data.subtype.AbilityTypes;
import drzed.GUI.MainController;
import hxckdms.hxcconfig.HxCConfig;
import hxckdms.hxcconfig.handlers.SpecialHandlers;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("all")
public class Main extends Application {
    private static final String TITLE = "Magic Parser v2.0.0";
    public static Stage stage = null;
    public static Stage miniStage = null;
    public static File Directory;
    private static HxCConfig config;
    public static boolean DEBUG_MODE = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (Configs.miniMode) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI/ML_ParseMini.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Magic Legends Mini Parser");
            primaryStage.setScene(new Scene(root, Configs.miniDisplayWidth, 35));
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            miniStage = primaryStage;
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI/ML_ParserGUI.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Magic Legends Combat Parser");
            primaryStage.setScene(new Scene(root, 1280, 720));
            ((MainController) loader.getController()).menuD.setText(TITLE);
            primaryStage.initStyle(StageStyle.UNDECORATED);
        }
        stage = primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            String[] arg = args[0].split("=");
            if (arg[0].equalsIgnoreCase("-debug")) {
                DEBUG_MODE = Boolean.parseBoolean(arg[1]);
            }
        }
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
        SpecialHandlers.registerSpecialClass(ObservableList.class);
        SpecialHandlers.registerSpecialClass(Ability.class);
        SpecialHandlers.registerSpecialClass(Entity.class);
        SpecialHandlers.registerSpecialClass(AbilityData.class);
        SpecialHandlers.registerSpecialClass(AbilityTypes.class);
        SpecialHandlers.registerSpecialClass(Encounter.class);
    }

    private static void registerConfigs() {
        config = new HxCConfig(Configs.class, "MagicLegendsParser", new File("./"), "cfg", "MLParse");
    }

    public static void resaveConfig() {
        if (new File("./MagicLegendsParser.cfg").exists()) {
            new File("./MagicLegendsParser.cfg").delete();
            config.initConfiguration();
        }
    }

    private static HxCConfig dummy2 = new HxCConfig(AbilityTypes.class, "ML_Skill_Data", new File("./"), "cfg", "MLParse");
    private static HxCConfig dummy3 = new HxCConfig(EntityNames.class, "ML_Entity_Data", new File("./"), "cfg", "MLParse");
    private static void initialize() {
        config.initConfiguration(); //TODO avoid attempting this if OS != Windows
        String ddir = getLocation().replaceAll("\\\\", "/");
        String fdir = ddir + "/Magic Legends/Live/logs/GameClient";
        if (!ddir.equalsIgnoreCase("null") && !ddir.isEmpty()) {
            Configs.combatLogFolder = fdir;
            resaveConfig();
        }
        Directory = new File(Configs.combatLogFolder);
        File sk = new File("./ML_Skill_Data.cfg");
        if (sk.exists()) {
            dummy2.initConfiguration();
        }
        sk = new File("./ML_Entity_Data.cfg");
        if (sk.exists()) {
            dummy3.initConfiguration();
        }
    }

    static void export(String name) {
        if (EncounterData.encounter == null) {
            System.out.println("ENCOUNTER NULL");
            return;
        }
        if (new File("./data/" + name + ".log").exists()) {
            System.out.println("EXISTS");
            return;
        }
        HxCConfig dummy = new HxCConfig(EncounterData.class, name, new File("./data/"), "log", "MLParse");
        dummy.initConfiguration();
        File sk = new File("./ML_Skill_Data.cfg");
        if (sk.exists()) {
            sk.delete();
        }
        dummy2.initConfiguration();
        sk = new File("./ML_Entity_Data.cfg");
        if (sk.exists()) {
            sk.delete();
        }
        dummy3.initConfiguration();
        EncounterData.encounter = null; //To free up memory
    }

    public static void importEnc(String name) {
//        try {
//            EncounterData.loadEncounter(name);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        MagicParser.setCurrentEncounter(EncounterData.encounter);
    }


    /**
     *
     * @return registry value or null if not found
     */
    private static String getLocation(){
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " +
                    '"' + "HKEY_CURRENT_USER\\SOFTWARE\\Cryptic\\Magic: Legends" + "\" /v " + "InstallLocation");

            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();

            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException ignored) { }

            String output = sw.toString();

            // Output has the following format:
            // \n<Version information>\n\n<key>    <registry type>    <value>\r\n\r\n
            int i = output.indexOf("REG_SZ");
            if (i == -1) {
                return "null";
            }

            sw = new StringBuilder();
            i += 6; // skip REG_SZ

            // skip spaces or tabs
            for (; ; ) {
                if (i > output.length())
                    break;
                char c = output.charAt(i);
                if (c != ' ' && c != '\t')
                    break;
                ++i;
            }

            // take everything until end of line
            for (; ; ) {
                if (i > output.length())
                    break;
                char c = output.charAt(i);
                if (c == '\r' || c == '\n')
                    break;
                sw.append(c);
                ++i;
            }

            return sw.toString();
        } catch (Exception e) {
            return "null";
        }
    }
}
