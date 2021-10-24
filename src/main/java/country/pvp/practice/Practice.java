package country.pvp.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import country.pvp.practice.itembar.ItemBarListener;
import country.pvp.practice.kit.KitManager;
import country.pvp.practice.kit.KitRepository;
import country.pvp.practice.player.PreparePlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    private final Injector injector = Guice.createInjector(new PracticeModule());

    @Override
    public void onEnable() {
        register(ItemBarListener.class);
        register(PreparePlayerListener.class);
        loadKits();

    }

    private void register(Class<? extends Listener> listener) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(listener), this);
    }

    private void loadKits() {
        KitRepository repository = injector.getInstance(KitRepository.class);
        KitManager manager = injector.getInstance(KitManager.class);
        manager.addAll(repository.loadAll());
    }
}
