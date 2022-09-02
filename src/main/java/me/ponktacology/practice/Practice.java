package me.ponktacology.practice;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaService;
import me.ponktacology.practice.arena.ArenaType;
import me.ponktacology.practice.arena.command.ArenaProvider;
import me.ponktacology.practice.arena.command.ArenaTypeProvider;
import me.ponktacology.practice.arena.command.MatchArenaProvider;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.board.ScoreboardService;
import me.ponktacology.practice.commands.HelpGenerator;
import me.ponktacology.practice.database.DatabaseService;
import me.ponktacology.practice.event.EventService;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.ladder.LadderType;
import me.ponktacology.practice.ladder.command.LadderProvider;
import me.ponktacology.practice.ladder.command.LadderTypeProvider;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.snapshot.InventorySnapshotService;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.party.command.PartyProvider;
import me.ponktacology.practice.party.duel.PartyDuelService;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.command.PracticePlayerProvider;
import me.ponktacology.practice.player.duel.PlayerDuelService;
import me.ponktacology.practice.queue.QueueService;
import me.ponktacology.practice.util.menu.MenuService;
import me.ponktacology.practice.util.visibility.VisibilityService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.Blade;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public class Practice extends JavaPlugin {

  @Getter private static Practice practice;
  private BukkitAudiences audience;
  private final Configuration configuration = new Configuration(this.getConfig());

  private boolean debug = false;

  private final ClassToInstanceMap<Service> services =
      ImmutableClassToInstanceMap.<Service>builder()
          .put(DatabaseService.class, new DatabaseService(configuration))
          .put(ArenaService.class, new ArenaService())
          .put(EventService.class, new EventService())
          .put(HotBarService.class, new HotBarService())
          .put(LadderService.class, new LadderService())
          .put(InvitationService.class, new InvitationService())
          .put(LobbyService.class, new LobbyService())
          .put(MatchService.class, new MatchService())
          .put(PartyDuelService.class, new PartyDuelService())
          .put(PlayerDuelService.class, new PlayerDuelService())
          .put(PlayerService.class, new PlayerService())
          .put(QueueService.class, new QueueService())
          .put(VisibilityService.class, new VisibilityService())
          .put(PartyService.class, new PartyService())
          .put(MenuService.class, new MenuService())
          .put(InventorySnapshotService.class, new InventorySnapshotService())
          .put(ScoreboardService.class, new ScoreboardService())
          .build();

  public static <V extends Service> V getService(Class<V> clazz) {
    return (V)
        Preconditions.checkNotNull(
            practice.services.get(clazz), clazz.getSimpleName() + " is not initialized");
  }

  @Override
  public void onEnable() {
    practice = this;
    audience = BukkitAudiences.create(this);

    saveDefaultConfig();

    for (Service service : services.values()) {
      service.start();
      service
          .getListeners()
          .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    registerCommands();
  }

  @Override
  public void onDisable() {
    for (Service service : services.values()) {
      service.stop();
      service.getListeners().forEach(HandlerList::unregisterAll);
    }

    audience.close();
  }

  private void registerCommands() {
    Blade blade =
        Blade.forPlatform(new BladeBukkitPlatform(this))
            .config(
                config -> {
                  config.setFallbackPrefix("practice");
                  config.setHelpGenerator(new HelpGenerator());
                })
            .bind(
                binder -> {
                  binder.bind(Arena.class, new ArenaProvider());
                  binder.bind(ArenaType.class, new ArenaTypeProvider());
                  binder.bind(Ladder.class, new LadderProvider());
                  binder.bind(Party.class, new PartyProvider());
                  binder.bind(PracticePlayer.class, new PracticePlayerProvider());
                  binder.bind(LadderType.class, new LadderTypeProvider());
                  binder.bind(MatchArena.class, new MatchArenaProvider());
                })
            .build();

    for (Service service : services.values()) {
      service.getCommands().forEach(blade::register);
    }
  }
}
