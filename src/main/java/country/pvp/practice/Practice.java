package country.pvp.practice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.arena.ArenaService;
import country.pvp.practice.commands.*;
import country.pvp.practice.commands.provider.ArenaProvider;
import country.pvp.practice.board.BoardTask;
import country.pvp.practice.board.PracticeBoard;
import country.pvp.practice.duel.DuelRequestInvalidateTask;
import country.pvp.practice.invitation.InvitationInvalidateTask;
import country.pvp.practice.listeners.*;
import country.pvp.practice.kit.editor.KitEditorListener;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderService;
import country.pvp.practice.commands.provider.LadderProvider;
import country.pvp.practice.leaderboards.LeaderBoardsFetchTask;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.PearlCooldownTask;
import country.pvp.practice.match.snapshot.InventorySnapshotInvalidateTask;
import country.pvp.practice.match.snapshot.command.ViewInventoryCommand;
import country.pvp.practice.menu.MenuListener;
import country.pvp.practice.party.PartyInviteRequestInvalidateTask;
import country.pvp.practice.player.*;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.QueueTask;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.settings.PracticeSettingsCommand;
import country.pvp.practice.settings.PracticeSettingsService;
import country.pvp.practice.concurrent.TaskDispatcher;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.Blade;
import me.vaperion.blade.command.bindings.impl.BukkitBindings;
import me.vaperion.blade.command.container.impl.BukkitCommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class Practice {

    private final Injector injector;
    private final PlayerManager playerManager;
    private final PlayerService playerService;
    private final LadderManager ladderManager;
    private final LadderService ladderService;
    private final ArenaManager arenaManager;
    private final ArenaService arenaService;
    private final QueueManager queueManager;
    private final PracticeSettings practiceSettings;
    private final PracticeSettingsService practiceSettingsService;
    private final MatchManager matchManager;

    private Blade blade;

    void onEnable() {
        register(ItemBarListener.class);
        register(PlayerSessionListener.class);
        register(PracticeBoard.class);
        register(PlayerLobbyListener.class);
        register(MenuListener.class);
        register(PlayerKitListener.class);
        register(MatchPlayerListener.class);
        register(PlayerQueueListener.class);
        register(PlayerPartyListener.class);
        register(KitEditorListener.class);

        schedule(QueueTask.class, 1000L, TimeUnit.MILLISECONDS, true);
        schedule(BoardTask.class, 500L, TimeUnit.MILLISECONDS, true);
        schedule(PlayerSaveTask.class, 1L, TimeUnit.MINUTES, true);
        schedule(PearlCooldownTask.class, 100L, TimeUnit.MILLISECONDS, true);
        schedule(InventorySnapshotInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(DuelRequestInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(InvitationInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(PartyInviteRequestInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(LeaderBoardsFetchTask.class, 15L, TimeUnit.SECONDS, true);

        loadSettings();
        loadArenas();
        loadLadders();
        initPlayerQueues();

        setupBlade();
        registerCommand(ArenaCommands.class);
        registerCommand(LadderCommands.class);
        registerCommand(PracticeSettingsCommand.class);
        registerCommand(MatchCommands.class);
        registerCommand(ViewInventoryCommand.class);
        registerCommand(QueueCommands.class);
        registerCommand(KitEditorCommands.class);
        registerCommand(PartyCommands.class);
        registerCommand(InvitationCommands.class);

        loadOnlinePlayers();
    }

    void onDisable() {
        for (Match match : matchManager.getAll()) {
            match.cancel("Server is restarting");
        }

        for (PlayerSession playerSession : playerManager.getAll()) {
            playerService.save(playerSession);
        }
    }

    private void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                PlayerSession playerSession = new PlayerSession(player);
                playerService.loadAsync(playerSession);
                playerManager.add(playerSession);
            } catch (Exception e) {
                player.kickPlayer("Server is restarting...");
            }
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
                .bind(PlayerSession.class, injector.getInstance(PracticePlayerProvider.class))
                .containerCreator(BukkitCommandContainer.CREATOR)
                .binding(new BukkitBindings())
                .helpGenerator(new HelpGenerator())
                .build();
    }

    private void registerCommand(Class<?> command) {
        blade.register(injector.getInstance(command));
    }

    private void initPlayerQueues() {
        for (Ladder ladder : ladderManager.getAll()) {
            queueManager.initQueue(ladder);
        }
    }

    private void loadLadders() {
        ladderManager.addAll(ladderService.loadAll());
    }

    private void loadArenas() {
        arenaManager.addAll(arenaService.loadAll());
    }

    private void loadSettings() {
        practiceSettingsService.load(practiceSettings);
    }
}
