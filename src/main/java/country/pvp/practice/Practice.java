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
import country.pvp.practice.duel.DuelRequestInvalidateTask;
import country.pvp.practice.duel.command.DuelCommand;
import country.pvp.practice.invitation.InvitationInvalidateTask;
import country.pvp.practice.invitation.command.InvitationCommand;
import country.pvp.practice.itembar.ItemBarListener;
import country.pvp.practice.kit.PlayerKitListener;
import country.pvp.practice.kit.editor.KitEditorListener;
import country.pvp.practice.kit.editor.command.KitEditorCommand;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderService;
import country.pvp.practice.ladder.command.LadderCommands;
import country.pvp.practice.ladder.command.provider.LadderProvider;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.MatchPlayerListener;
import country.pvp.practice.match.PearlCooldownTask;
import country.pvp.practice.match.command.MatchCommand;
import country.pvp.practice.match.command.SpectateCommand;
import country.pvp.practice.match.snapshot.InventorySnapshotInvalidateTask;
import country.pvp.practice.match.snapshot.command.ViewInventoryCommand;
import country.pvp.practice.menu.MenuListener;
import country.pvp.practice.party.PartyInviteRequestInvalidateTask;
import country.pvp.practice.party.PartyRemovePlayerListener;
import country.pvp.practice.party.command.PartyCommands;
import country.pvp.practice.player.*;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.QueueRemovePlayerListener;
import country.pvp.practice.queue.QueueTask;
import country.pvp.practice.queue.command.QueueCommand;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.settings.PracticeSettingsCommand;
import country.pvp.practice.settings.PracticeSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vaperion.blade.Blade;
import me.vaperion.blade.command.bindings.impl.BukkitBindings;
import me.vaperion.blade.command.container.impl.BukkitCommandContainer;
import me.vaperion.blade.completer.impl.DefaultTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@Slf4j
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
        register(PreparePlayerListener.class);
        register(PracticeBoard.class);
        register(PlayerProtectionListener.class);
        register(MenuListener.class);
        register(PlayerKitListener.class);
        register(MatchPlayerListener.class);
        register(QueueRemovePlayerListener.class);
        register(PartyRemovePlayerListener.class);
        register(KitEditorListener.class);

        schedule(QueueTask.class, 1000L, TimeUnit.MILLISECONDS, true);
        schedule(BoardTask.class, 500L, TimeUnit.MILLISECONDS, true);
        schedule(PlayerSaveTask.class, 1L, TimeUnit.MINUTES, true);
        schedule(PearlCooldownTask.class, 100L, TimeUnit.MILLISECONDS, true);
        schedule(InventorySnapshotInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(DuelRequestInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(InvitationInvalidateTask.class, 5L, TimeUnit.SECONDS, true);
        schedule(PartyInviteRequestInvalidateTask.class, 5L, TimeUnit.SECONDS, true);

        loadSettings();
        loadArenas();
        loadLadders();
        initPlayerQueues();

        setupBlade();
        registerCommand(ArenaCommands.class);
        registerCommand(LadderCommands.class);
        registerCommand(PracticeSettingsCommand.class);
        registerCommand(SpectateCommand.class);
        registerCommand(MatchCommand.class);
        registerCommand(ViewInventoryCommand.class);
        registerCommand(DuelCommand.class);
        registerCommand(QueueCommand.class);
        registerCommand(KitEditorCommand.class);
        registerCommand(PartyCommands.class);
        registerCommand(InvitationCommand.class);

        loadOnlinePlayers();
    }

    void onDisable() {
        for (Match<?> match : matchManager.getAll()) {
            match.cancel("Server is restarting");
        }

        for (PracticePlayer practicePlayer : playerManager.getAll()) {
            playerService.save(practicePlayer);
        }
    }

    private void loadOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                PracticePlayer practicePlayer = new PracticePlayer(player);
                playerService.loadAsync(practicePlayer);
                playerManager.add(practicePlayer);
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
                .bind(PracticePlayer.class, injector.getInstance(PracticePlayerProvider.class))
                .containerCreator(BukkitCommandContainer.CREATOR)
                .binding(new BukkitBindings())
                .helpGenerator(new PracticeHelpGenerator())
                .tabCompleter(new DefaultTabCompleter())
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
