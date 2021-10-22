package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.message.Recipient;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PracticePlayer implements DataObject, Recipient {

  private static final Map<UUID, PracticePlayer> players = Maps.newHashMap();

  private final UUID id;
  private String name;
  private PlayerState state = PlayerState.IN_LOBBY;

  public PracticePlayer(Player player) {
    this(player.getUniqueId());
    this.name = player.getName();
  }

  public PracticePlayer(UUID uuid) {
    this.id = uuid;
  }

  public static PracticePlayer get(Player player) {
    return players.get(player.getUniqueId());
  }

  public static PracticePlayer remove(Player player) {
    return players.remove(player.getUniqueId());
  }

  public static Set<PracticePlayer> players() {
    return Collections.unmodifiableSet(new HashSet<>(players.values()));
  }

  @Override
  public void save() {
    // TODO: save data
  }

  @Override
  public void load(Document document) {
    // TODO: load data from a document
  }

  @Override
  public void receive(String message) {
    Player player = player();
    Preconditions.checkNotNull(player, "Player must be online in order to send him a message");
    player.sendMessage(message);
  }

  public void cache() {
    players.put(id, this);
  }

  public void show(PracticePlayer practicePlayer) {
    Player observer = player();
    Player observable = practicePlayer.player();
    Preconditions.checkNotNull(observer, "Player must be online in order to hide other players.");
    Preconditions.checkNotNull(observable, "Player must be online in order to be hidden.");

    observer.showPlayer(observable);
  }

  public void hide(PracticePlayer practicePlayer) {
    Player observer = player();
    Player observable = practicePlayer.player();
    Preconditions.checkNotNull(observer, "Player must be online in order to hide other players.");
    Preconditions.checkNotNull(observable, "Player must be online in order to be hidden.");

    observer.hidePlayer(observable);
  }

  public Player player() {
    return Bukkit.getPlayer(id);
  }

  public boolean isFighting() {
    return state == PlayerState.IN_MATCH;
  }

  public boolean isInLobby() {
    return state == PlayerState.IN_LOBBY;
  }

  public void setBar(ItemStack[] bar) {
    Player player = player();
    Preconditions.checkNotNull(player, "Player must be online in order to change his item bar");
    for (int i = 0; i < bar.length; i++) {
      ItemStack item = bar[i].clone();
      player.getInventory().setItem(i, item);
    }
  }
}
