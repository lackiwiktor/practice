package country.pvp.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

public class PracticePlugin extends JavaPlugin {

    private final Injector injector = Guice.createInjector(new PracticeModule());

    @Override
    public void onEnable() {
        Practice practice = injector.getInstance(Practice.class);
        practice.onEnable();
    }
}
