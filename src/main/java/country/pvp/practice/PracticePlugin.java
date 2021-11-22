package country.pvp.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

public class PracticePlugin extends JavaPlugin {

    private final Injector injector = Guice.createInjector(new PracticeModule());
    private final Practice practice = injector.getInstance(Practice.class);

    @Override
    public void onEnable() {
        practice.onEnable();
    }

    @Override
    public void onDisable() {
        practice.onDisable();
    }
}
