package me.ponktacology.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.gesundkrank.jskills.Rating;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.event.Event;
import me.ponktacology.practice.event.EventParticipant;
import me.ponktacology.practice.invitation.duel.DuelInvitable;
import me.ponktacology.practice.invitation.duel.Request;
import me.ponktacology.practice.kit.NamedKit;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.RematchData;
import me.ponktacology.practice.match.StateMatchData;
import me.ponktacology.practice.match.StateSpectatingData;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.player.data.PlayerKits;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.player.data.PlayerStatistics;
import me.ponktacology.practice.player.data.StateData;
import me.ponktacology.practice.player.duel.PlayerDuelRequest;
import me.ponktacology.practice.queue.StateQueueData;
import me.ponktacology.practice.util.data.DataObject;
import me.ponktacology.practice.util.message.Recipient;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
public class PracticePlayer
    implements DataObject,
        DuelInvitable<PracticePlayer, PlayerDuelRequest>,
        Recipient,
        EventParticipant {

  private final UUID uuid;
  private final PlayerStatistics statistics = new PlayerStatistics();
  private final PlayerKits kits = new PlayerKits();
  private final Set<PlayerDuelRequest> duelRequests = Sets.newConcurrentHashSet();

  private String name;
  private PlayerState state = PlayerState.IN_LOBBY;

  private @Nullable Event<?> event;
  private @Nullable Party party;
  private @Nullable RematchData rematchData;
  private @Nullable StateData stateData;

  public PracticePlayer(UUID uuid, String name) {
    this(uuid);
    this.name = name;
  }

  public PracticePlayer(UUID uuid) {
    this.uuid = uuid;
  }

  public void setState(PlayerState state) {
    this.state = state;
    if (state == PlayerState.IN_LOBBY) stateData = null;
  }

  @Override
  public String getId() {
    return uuid.toString();
  }

  @Override
  public Document getDocument() {
    org.bson.Document document = new org.bson.Document("_id", getId());
    document.put("name", name);
    document.put("nameLowerCase", name.toLowerCase(Locale.ROOT));
    document.put("statistics", statistics.getDocument());
    document.put("kits", kits.getDocument());

    return document;
  }

  @Override
  public void receive(String message) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.sendMessage(message);
  }

  @Override
  public void applyDocument(Document document) {
    if (name == null) name = document.getString("name");
    statistics.applyDocument(document.get("statistics", Document.class));
    kits.applyDocument(document.get("kits", Document.class));
  }

  public @Nullable Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public boolean isInLobby() {
    return state == PlayerState.IN_LOBBY;
  }

  public boolean isInEvent() {
    return getEvent() != null;
  }

  public boolean isInQueue() {
    return state == PlayerState.QUEUING;
  }

  public boolean isInMatch() {
    return state == PlayerState.IN_MATCH;
  }

  public boolean isSpectating() {
    return state == PlayerState.SPECTATING;
  }

  public boolean isInEditor() {
    return state == PlayerState.EDITING_KIT;
  }

  public boolean isSelecting() {
    return state == PlayerState.SELECTING;
  }

  public void setBar(ItemStack... bar) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "Player must be online in order to change his item bar");
    player.getInventory().setContents(bar);
    player.updateInventory();
  }

  public boolean hasRematch() {
    return rematchData != null;
  }

  public <V extends StateData> void setState(PlayerState state, V data) {
    this.state = state;
    this.stateData = data;
  }

  public <V extends StateData> @Nullable V getStateData() {
    return (V) stateData;
  }

  public void setElo(Ladder ladder, Rating rating) {
    statistics.setElo(ladder, rating);
  }

  public int getElo(Ladder ladder) {
    return (int) statistics.getElo(ladder).getMean();
  }

  public void removeKit(Ladder ladder, int index) {
    kits.removeKit(ladder, index);
  }

  public void setKit(Ladder ladder, NamedKit newKit, int index) {
    kits.setKit(ladder, newKit, index);
  }

  public NamedKit getKit(Ladder ladder, int index) {
    return kits.getKit(ladder, index);
  }

  public NamedKit[] getKits(Ladder ladder) {
    return kits.getKits(ladder);
  }

  public boolean hasParty() {
    return party != null && !party.isDisbanded();
  }

  public boolean isPartyLeader() {
    Preconditions.checkNotNull(party, "party");
    return party.isLeader(this);
  }

  public void addToParty(Party party) {
    this.party = party;
  }

  public void enableFlying() {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.setAllowFlight(true);
    player.setFlying(true);
  }

  public void disableFlying() {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.setFlying(false);
    player.setAllowFlight(false);
  }

  public boolean hasPermission(String permission) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");

    return player.hasPermission(permission);
  }

  public void teleport(Location location) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.teleport(location);
  }

  @Nullable
  public NamedKit getMatchingKit(Ladder ladder, ItemStack itemStack) {
    if (ladder.getKit().getIcon().isSimilar(itemStack)) {
      return NamedKit.from("Default Kit", ladder.getKit()); // Give default kit
    }

    if (kits.hasKits(ladder)) {
      Optional<NamedKit> playerKit =
          Arrays.stream(kits.getKits(ladder))
              .filter(it -> it != null && it.getIcon().isSimilar(itemStack))
              .findFirst();

      if (!playerKit.isPresent()) {
        return null;
      }
    }

    return null;
  }

  public void giveKits(Ladder ladder) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    PlayerInventory playerInventory = player.getInventory();

    if (kits.hasKits(ladder)) {
      playerInventory.addItem(ladder.getKit().getIcon());
      Arrays.stream(getKits(ladder))
          .filter(Objects::nonNull)
          .forEach(it -> playerInventory.addItem(it.getIcon()));
    } else {
      ladder.getKit().apply(this);
    }

    player.updateInventory();
  }

  public Match getCurrentMatch() {
    Preconditions.checkArgument(isInMatch(), "player is not in a match");
    StateMatchData matchData = getStateData();
    return matchData.getMatch();
  }

  public Match getCurrentlySpectatingMatch() {
    StateSpectatingData spectatingData = getStateData();
    Preconditions.checkNotNull(spectatingData, "data");
    return spectatingData.getMatch();
  }

  public void setVelocity(Vector vector) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.setVelocity(vector);
  }

  public Location getLocation() {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    return player.getLocation();
  }

  public boolean isOnline() {
    return getPlayer() != null && getPlayer().isOnline();
  }

  public int getPing() {
    Player player = getPlayer();

    if (player == null) {
      return -1;
    }

    return ((CraftPlayer) player).getHandle().ping;
  }

  public void removeFromParty() {
    this.party = null;
  }

  public void runCommand(String command) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    Bukkit.dispatchCommand(player, command);
  }

  public void removeFromQueue(boolean left) {
    StateQueueData queueData = getStateData();
    Preconditions.checkNotNull(queueData, "data");
    queueData.removeFromQueue(left);
  }

  public void removeFromQueue() {
    removeFromQueue(false);
  }

  public void stopSpectating(boolean broadcast) {
    if (event != null) {
      event.removeSpectator(this);
      return;
    }

    StateSpectatingData spectatingData = getStateData();
    Preconditions.checkNotNull(spectatingData, "data");
    Match match = spectatingData.getMatch();
    match.stopSpectating(this, broadcast, true);
  }

  @Override
  public void addDuelRequest(PlayerDuelRequest request) {
    duelRequests.add(request);
  }

  @Override
  public boolean hasDuelRequest(DuelInvitable inviter) {
    return duelRequests.stream().anyMatch(it -> it.getInviter().equals(inviter));
  }

  @Override
  public void clearDuelRequests(DuelInvitable inviter) {
    duelRequests.removeIf(it -> it.getInviter().equals(inviter));
  }

  @Override
  public void invalidateDuelRequests() {
    duelRequests.removeIf(Request::hasExpired);
  }

  @Override
  public @Nullable PlayerDuelRequest getDuelRequest(DuelInvitable inviter) {
    return duelRequests.stream()
        .filter(it -> it.getInviter().equals(inviter))
        .findFirst()
        .orElse(null);
  }

  public double getHealth() {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    return player.getHealth();
  }

  public void die() {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    player.setHealth(0);
  }

  @Override
  public void receiveInvite(Component component) {
    Player player = getPlayer();
    Preconditions.checkNotNull(player, "player");
    Practice.getPractice().getAudience().player(player).sendMessage(component);
  }

  @Override
  public void setCurrentEvent(Event<?> event) {
    this.event = event;
  }

  @Override
  public void returnToLobby() {
    Practice.getService(LobbyService.class).moveToLobby(this);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PracticePlayer that = (PracticePlayer) o;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
