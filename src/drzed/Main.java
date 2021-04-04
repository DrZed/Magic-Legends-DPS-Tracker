package drzed;

import drzed.Data.Ability;
import drzed.Data.Encounter;
import drzed.Data.Entity;
import drzed.Data.Skill;
import drzed.Data.subtype.EncounterData;
import drzed.Data.subtype.EntityNames;
import drzed.Data.subtype.SkillTypes;
import hxckdms.hxcconfig.HxCConfig;
import hxckdms.hxcconfig.handlers.SpecialHandlers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Main extends Application {
    public static Stage stage = null;
    public static Stage miniStage = null;
    public static File Directory;
    private static HxCConfig config;
    public static Main instance;

    /*
    Due to a user on reddit asking I use default install directory, here's the registry key
    Computer\HKEY_CURRENT_USER\SOFTWARE\Cryptic\Magic: Legends
    InstallLocation
    A:\Games\Magic Legends_en
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (Configs.miniMode) {
            Parent root = FXMLLoader.load(getClass().getResource("GUI/ML_ParseMini.fxml"));
            primaryStage.setTitle("Magic Legends Mini Parser");
            primaryStage.setScene(new Scene(root, Configs.miniDisplayWidth, 35));
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            miniStage = primaryStage;
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("GUI/ML_ParserGUI.fxml"));
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
        SpecialHandlers.registerSpecialClass(Skill.class);
        SpecialHandlers.registerSpecialClass(SkillTypes.class);
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

    static HxCConfig dummy2 = new HxCConfig(SkillTypes.class, "ML_Skill_Data", new File("./"), "cfg", "MLParse");
    static HxCConfig dummy3 = new HxCConfig(EntityNames.class, "ML_Entity_Data", new File("./"), "cfg", "MLParse");
    private static void initialize() {
        config.initConfiguration();
        String ddir = readRegistry("HKEY_CURRENT_USER\\SOFTWARE\\Cryptic\\Magic: Legends", "InstallLocation").replaceAll("\\\\", "/");
        String fdir = ddir + "/Magic Legends/Live/logs/GameClient";
        if (ddir != null && !ddir.equalsIgnoreCase("null") && !ddir.isEmpty()) {
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

    public static void export(String name) {
        if (EncounterData.encounter != null) {
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
    }


    /**
     *
     * @param location path in the registry
     * @param key registry key
     * @return registry value or null if not found
     */
    public static final String readRegistry(String location, String key){
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " +
                    '"' + location + "\" /v " + key);

            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();

            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.append((char) c);
            } catch (IOException e) {
            }

            String output = sw.toString();

            // Output has the following format:
            // \n<Version information>\n\n<key>    <registry type>    <value>\r\n\r\n
            int i = output.indexOf("REG_SZ");
            if (i == -1) {
                return null;
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
            return null;
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw= new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException e) {
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }
}
