package country.pvp.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.bukkit.plugin.java.JavaPlugin;

public class PracticePlugin extends JavaPlugin {

    private Practice practice;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Configuration configuration = new Configuration(getConfig());
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new Bindings(configuration));
        practice = injector.getInstance(Practice.class);
        practice.onEnable();
    }

    @Override
    public void onDisable() {
        practice.onDisable();
    }
}
