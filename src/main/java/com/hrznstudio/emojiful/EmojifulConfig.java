package com.hrznstudio.emojiful;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

public class EmojifulConfig {
    private static EmojifulConfig instance;
    @Expose @SerializedName("render") public boolean renderEmoji = true;

    private EmojifulConfig() {
    }

    public static void init() {
        try {
            File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "emojiful.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (!file.exists()) {
                FileUtils.writeStringToFile(file, gson.toJson(new EmojifulConfig(), EmojifulConfig.class), Charset.defaultCharset());
            }
            instance = gson.fromJson(IOUtils.toString(new FileInputStream(file), Charset.defaultCharset()), EmojifulConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EmojifulConfig getInstance() {
        if (instance == null) {
            init();
        }
        return Objects.requireNonNull(instance, "Called for Config before it's Initialization");
    }
}
