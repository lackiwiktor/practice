package country.pvp.practice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.arena.ArenaService;
import country.pvp.practice.arena.command.ArenaCommands;
import country.pvp.practice.arena.command.provider.ArenaProvider;
import country.pvp.practice.board.BoardTask;
import country.pvp.practice.board.PracticeBoard;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarListener;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderService;
import country.pvp.practice.ladder.command.LadderCommands;
import country.pvp.practice.ladder.command.provider.LadderProvider;
import country.pvp.practice.lobby.LobbyPlayerListener;
import country.pvp.practice.menu.MenuListener;
import country.pvp.practice.player.PreparePlayerListener;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.QueueMenuProvider;
import country.pvp.practice.queue.QueueTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.vaperion.blade.Blade;
import me.vaperion.blade.command.bindings.impl.BukkitBindings;
import me.vaperion.blade.command.container.impl.BukkitCommandContainer;
import me.vaperion.blade.completer.impl.DefaultTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class Practice {

    @Getter
    private static Practice instance;

    private final Injector injector;
    private final LadderManager ladderManager;
    private final LadderService ladderService;
    private final ArenaManager arenaManager;
    private final ArenaService arenaService;
    private final QueueManager queueManager;
    private final QueueMenuProvider queueMenuProvider;
    private Blade blade;

    public static QueueManager getQueueManager() {
        return instance.queueManager;
    }

    public static QueueMenuProvider getQueueMenuProvider() {
        return instance.queueMenuProvider;
    }

    public void onEnable() {
        instance = this;
        register(ItemBarListener.class);
        register(PreparePlayerListener.class);
        register(PracticeBoard.class);
        register(LobbyPlayerListener.class);
        register(MenuListener.class);
        schedule(BoardTask.class, 1L, TimeUnit.SECONDS, true);
        schedule(QueueTask.class, 1L, TimeUnit.SECONDS, false);
        loadAll();
        setupBlade();
        registerCommand(ArenaCommands.class);
        registerCommand(LadderCommands.class);
    }

    private void register(Class<? extends Listener> listener) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(listener), JavaPlugin.getPlugin(PracticePlugin.class));
    }

    private void schedule(Class<? extends Runnable> runnable, long duration, TimeUnit unit, boolean async) {
        if (async)
            TaskDispatcher.scheduleAsync(injector.getInstance(runnable), duration, unit);
        else TaskDispatcher.scheduleSync(injector.getInstance(runnable), duration, unit);
    }

    private void setupBlade() {
        blade = Blade.of()
                .fallbackPrefix("practice")
                .overrideCommands(true)
                .bind(Arena.class, injector.getInstance(ArenaProvider.class))
                .bind(Ladder.class, injector.getInstance(LadderProvider.class))
                .containerCreator(BukkitCommandContainer.CREATOR)
                .binding(new BukkitBindings())
                .helpGenerator(new PracticeHelpGenerator())
                .tabCompleter(new DefaultTabCompleter())
                .build();
    }

    private void registerCommand(Class<?> command) {
        blade.register(injector.getInstance(command));
    }

    @SneakyThrows
    private void loadAll() {
        CompletableFuture<Boolean> laddersLoaded = new CompletableFuture<>();
        CompletableFuture<Boolean> arenasLoaded = new CompletableFuture<>();

        TaskDispatcher.async(() -> loadLadders(laddersLoaded));
        TaskDispatcher.async(() -> loadArenas(arenasLoaded));

        if (laddersLoaded.get() && arenasLoaded.get()) {
            log.info("Loaded arenas and ladders!");
            initPlayerQueues();
            log.info("Initialized queues!");
        }
    }

    private void initPlayerQueues() {
        for (Ladder ladder : ladderManager.getAll()) {
            queueManager.initQueue(ladder);
        }
    }

    private void loadLadders(CompletableFuture<Boolean> future) {
        try {
            ladderManager.addAll(ladderService.loadAll());
            future.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            future.complete(false);
        }
    }

    private void loadArenas(CompletableFuture<Boolean> future) {
        try {
            arenaManager.addAll(arenaService.loadAll());
            future.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            future.complete(false);
        }
    }


}
