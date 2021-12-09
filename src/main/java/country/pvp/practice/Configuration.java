package country.pvp.practice;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class Configuration {

    private final FileConfiguration file;

    String getMongoString() {
        return file.getString("mongodb_link");
    }
}
