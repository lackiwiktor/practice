package country.pvp.practice.player;

import country.pvp.practice.data.DataRepository;
import country.pvp.practice.itembar.ItemBar;
import country.pvp.practice.visibility.VisibilityUpdater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PreparePlayerListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void joinEvent(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PracticePlayer practicePlayer = new PracticePlayer(player);
    practicePlayer.cache();
    DataRepository.loadAsync(practicePlayer);
    ItemBar.LOBBY.apply(practicePlayer);
    VisibilityUpdater.update(practicePlayer);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void joinEvent(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    Optional.ofNullable(PracticePlayer.remove(player))
            .ifPresent(DataRepository::save);
  }
}
