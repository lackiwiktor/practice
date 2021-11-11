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
import country.pvp.practice.match.MatchKitListener;
import country.pvp.practice.match.MatchPlayerListener;
import country.pvp.practice.menu.MenuListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.PreparePlayerListener;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.menu.QueueMenuProvider;
import country.pvp.practice.queue.QueueRemovePlayerListener;
import country.pvp.practice.queue.QueueTask;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.settings.PracticeSettingsCommand;
import country.pvp.practice.settings.PracticeSettingsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.vaperion.blade.Blade;
import me.vaperion.blade.command.bindings.impl.BukkitBindings;
import me.vaperion.blade.command.container.impl.BukkitCommandContainer;
import me.vaperion.blade.completer.impl.DefaultTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    private final PlayerManager playerManager;
    private final PlayerService playerService;
    private final LadderManager ladderManager;
    private final LadderService ladderService;
    private final ArenaManager arenaManager;
    private final ArenaService arenaService;
    private final QueueManager queueManager;
    private final QueueMenuProvider queueMenuProvider;
    private final PracticeSettings practiceSettings;
    private final PracticeSettingsService practiceSettingsService;
    private Blade blade;

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
        register(MatchKitListener.class);
        register(MatchPlayerListener.class);
        register(QueueRemovePlayerListener.class);

        schedule(BoardTask.class, 1L, TimeUnit.SECONDS, true);
        schedule(QueueTask.class, 1L, TimeUnit.SECONDS, false);

        loadAll();

        setupBlade();
        registerCommand(ArenaCommands.class);
        registerCommand(LadderCommands.class);
        registerCommand(PracticeSettingsCommand.class);

        loadOnlinePlayers();
    }

    public void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PracticePlayer practicePlayer = new PracticePlayer(player);
            playerService.loadAsync(practicePlayer);
            playerManager.add(practicePlayer);
        }
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
        CompletableFuture<Boolean> settingsLoaded = new CompletableFuture<>();
        CompletableFuture<Boolean> laddersLoaded = new CompletableFuture<>();
        CompletableFuture<Boolean> arenasLoaded = new CompletableFuture<>();

        TaskDispatcher.async(() -> loadSettings(settingsLoaded));
        TaskDispatcher.async(() -> loadLadders(laddersLoaded));
        TaskDispatcher.async(() -> loadArenas(arenasLoaded));

        if (settingsLoaded.get()) {
            log.info("Loaded settings");
        }

        if (laddersLoaded.get()) {
            log.info("Loaded arenas and ladders!");
            initPlayerQueues();
            log.info("Initialized queues!");
        }

        if (arenasLoaded.get()) {
            log.info("Loaded arenas!");
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

    public void loadSettings(CompletableFuture<Boolean> future) {
        try {
            practiceSettingsService.load(practiceSettings);
            future.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            future.complete(false);
        }
    }
}
