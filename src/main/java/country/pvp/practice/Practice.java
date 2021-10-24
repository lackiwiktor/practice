package country.pvp.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import country.pvp.practice.board.PracticeBoard;
import country.pvp.practice.board.BoardTask;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarListener;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderRepository;
import country.pvp.practice.lobby.LobbyPlayerListener;
import country.pvp.practice.player.PreparePlayerListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Practice extends JavaPlugin {

    private final Injector injector = Guice.createInjector(new PracticeModule());

    @Override
    public void onEnable() {
        register(ItemBarListener.class);
        register(PreparePlayerListener.class);
        register(PracticeBoard.class);
        register(LobbyPlayerListener.class);
        schedule(BoardTask.class, 1L, TimeUnit.SECONDS, true);
        loadAll();
    }

    private void register(Class<? extends Listener> listener) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(listener), this);
    }

    private void schedule(Class<? extends Runnable> runnable, long duration, TimeUnit unit, boolean async) {
        if (async)
            TaskDispatcher.scheduleAsync(injector.getInstance(runnable), duration, unit);
        else TaskDispatcher.scheduleSync(injector.getInstance(runnable), duration, unit);
    }

    @SneakyThrows
    public void loadAll() {
        CompletableFuture<Boolean> kitsLoaded = new CompletableFuture<>();
        TaskDispatcher.async(() -> loadKits(kitsLoaded));

        if (kitsLoaded.get()) {
            log.info("Loaded kits!");
        }
    }

    private void loadKits(CompletableFuture<Boolean> future) {
        try {
            LadderRepository repository = injector.getInstance(LadderRepository.class);
            LadderManager manager = injector.getInstance(LadderManager.class);
            manager.addAll(repository.loadAll());
            future.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            future.complete(false);
        }
    }
}
