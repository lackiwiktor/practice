package country.pvp.practice;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class Configuration {

    private final FileConfiguration configuration;

    String getMongoString() {
        return configuration.getString("mongodb_link");
    }
}
