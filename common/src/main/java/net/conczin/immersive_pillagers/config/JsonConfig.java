package net.conczin.immersive_pillagers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.conczin.immersive_pillagers.ImmersivePillagers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int version = 0;

    int getVersion() {
        return 1;
    }

    public static File getConfigFile() {
        return new File("./config/" + ImmersivePillagers.MOD_ID + ".json");
    }

    public void save() {
        File configFile = getConfigFile();
        File parent = configFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            ImmersivePillagers.LOGGER.error("Failed to create config directory for {}", configFile);
            return;
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            version = getVersion();
            writer.write(toJsonString());
        } catch (IOException e) {
            ImmersivePillagers.LOGGER.error("Failed to save config", e);
        }
    }

    public String toJsonString() {
        return GSON.toJson(this);
    }

    public static Config loadOrCreate() {
        if (getConfigFile().exists()) {
            try (FileReader reader = new FileReader(getConfigFile())) {
                Config config = GSON.fromJson(reader, Config.class);
                if (config == null || config.version != config.getVersion()) {
                    config = new Config();
                }
                config.save();
                return config;
            } catch (Exception e) {
                ImmersivePillagers.LOGGER.error("Failed to load Immersive Pillagers config. Default config is used for now. Delete the file to reset.", e);
                return new Config();
            }
        }

        Config config = new Config();
        config.save();
        return config;
    }
}
