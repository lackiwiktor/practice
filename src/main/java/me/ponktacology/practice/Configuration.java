package me.ponktacology.practice;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class Configuration {

    private final FileConfiguration file;

    public String getMongoString() {
        return file.getString("mongodb_link");
    }

    public boolean isDebug() {
        return file.getBoolean("debug");
    }
}
