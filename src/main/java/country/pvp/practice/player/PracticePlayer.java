package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.duel.DuelRequest;
import country.pvp.practice.duel.PlayerDuelRequest;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.kit.editor.PlayerEditingData;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.PlayerLobbyData;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.match.PlayerMatchStatistics;
import country.pvp.practice.match.RematchData;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.data.*;
import country.pvp.practice.queue.PlayerQueueData;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
public class PracticePlayer implements DataObject, Recipient {

    private final UUID uuid;
    private final PlayerStateData stateData = new PlayerStateData();
    private final PlayerStatistics statistics = new PlayerStatistics();
    private final PlayerKits kits = new PlayerKits();
    private final Set<PlayerDuelRequest> duelRequests = Sets.newConcurrentHashSet();
    private Party party;
    private String name;
    private PlayerState state = PlayerState.IN_LOBBY;
    private boolean loaded;

    public PracticePlayer(Player player) {
        this(player.getUniqueId());
        this.name = player.getName();
    }

    public PracticePlayer(UUID uuid, String name) {
        this(uuid);
        this.name = name;
    }

    public PracticePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getCollection() {
        return "players";
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
        loaded = true;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isInLobby() {
        Preconditions.checkNotNull(stateData, "data");
        return state == PlayerState.IN_LOBBY;
    }

    public boolean isInQueue() {
        Preconditions.checkNotNull(stateData, "data");
        return state == PlayerState.QUEUING && hasStateData();
    }

    public boolean isInMatch() {
        Preconditions.checkNotNull(stateData, "data");
        return state == PlayerState.IN_MATCH && hasStateData();
    }

    public boolean isInEditor() {
        Preconditions.checkNotNull(stateData, "data");
        return state == PlayerState.EDITING_KIT && hasStateData();
    }

    public void setBar(ItemStack[] bar) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "Player must be online in order to change his item bar");
        for (int i = 0; i < bar.length; i++) {
            ItemStack item = bar[i].clone();
            player.getInventory().setItem(i, item);
        }
    }

    public <V extends PlayerData> void setState(PlayerState state, PlayerData data) {
        this.state = state;
        stateData.setStateData(data);
    }

    public <V extends PlayerData> @Nullable V getStateData() {
        return stateData.get();
    }

    public boolean hasStateData() {
        return stateData.hasStateData();
    }

    public void setElo(Ladder ladder, int elo) {
        statistics.setElo(ladder, elo);
    }

    public int getElo(Ladder ladder) {
        return statistics.getElo(ladder);
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

    public boolean hasKits(Ladder ladder) {
        return kits.hasKits(ladder);
    }

    public NamedKit[] getKits(Ladder ladder) {
        return kits.getKits(ladder);
    }

    public void addDuelRequest(PlayerDuelRequest request) {
        duelRequests.add(request);
    }

    public boolean hasDuelRequest(PracticePlayer inviter) {
        return duelRequests.stream().anyMatch(it -> it.getInviter().equals(inviter));
    }

    public @Nullable PlayerDuelRequest getDuelRequest(PracticePlayer inviter) {
        return duelRequests.stream().filter(it -> it.getInviter().equals(inviter)).findFirst().orElse(null);
    }

    public void clearDuelRequests(PracticePlayer inviter) {
        duelRequests.removeIf(it -> it.getInviter().equals(inviter));
    }

    public void invalidateDuelRequests() {
        duelRequests.removeIf(DuelRequest::hasExpired);
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean isPartyLeader() {
        Preconditions.checkNotNull(party, "party");
        return party.getLeader().equals(this);
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

    public boolean hasRematchData() {
        PlayerLobbyData lobbyData = getStateData();
        Preconditions.checkNotNull(lobbyData, "data");
        return lobbyData.getRematchData() != null;
    }

    public PracticePlayer getRematchPlayer() {
        PlayerLobbyData lobbyData = getStateData();
        Preconditions.checkNotNull(lobbyData, "data");
        RematchData rematchData = lobbyData.getRematchData();
        Preconditions.checkNotNull(rematchData, "rematch data");
        return rematchData.getPlayer();
    }

    public Ladder getRematchLadder() {
        PlayerLobbyData lobbyData = getStateData();
        Preconditions.checkNotNull(lobbyData, "data");
        RematchData rematchData = lobbyData.getRematchData();
        Preconditions.checkNotNull(rematchData, "rematch data");
        return rematchData.getLadder();
    }

    public void teleport(Location location) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.teleport(location);
    }

    public Optional<NamedKit> getMatchingKit(Ladder ladder, ItemStack itemStack) {
        Optional<NamedKit> playerKit = Arrays.stream(kits.getKits(ladder)).filter(it -> it != null && it.getIcon().isSimilar(itemStack)).findFirst();

        if (!playerKit.isPresent()) {
            if (ladder.getKit().getIcon().isSimilar(itemStack)) {
                return Optional.of(NamedKit.from("Default Kit", ladder.getKit())); //Give default kit
            }
        } else return playerKit; //Give custom player kit

        return Optional.empty();
    }

    public void giveKits(Ladder ladder) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.addItem(ladder.getKit().getIcon());

        if (kits.hasKits(ladder)) {
            Arrays.stream(getKits(ladder)).filter(Objects::nonNull).forEach(it -> playerInventory.addItem(it.getIcon()));
        }
    }

    public Match<?> getCurrentMatch() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getMatch();
    }

    public void handleBeingHit(PracticePlayer attacker) {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.handleBeingHit(attacker);
    }

    public void respawn() {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.spigot().respawn();
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
        return getPlayer() != null;
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

    public int getPing() {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        return -1;
    }

    public void sendComponent(BaseComponent[] components) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.spigot().sendMessage(components);
    }

    public void removeFromParty() {
        this.party = null;
    }

    public void chat(String message) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.chat(message);
    }

    public void removeFromQueue(boolean left) {
        PlayerQueueData queueData = getStateData();
        Preconditions.checkNotNull(queueData, "data");
        queueData.removeFromQueue(left);
    }

    public void removeFromQueue() {
        removeFromQueue(false);
    }

    public void setDead(boolean dead) {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.setDead(dead);
    }

    public boolean hasLastAttacker() {
        return getLastAttacker() != null;
    }

    public PracticePlayer getLastAttacker() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getLastAttacker();
    }

    public void handleDisconnectInParty() {
        party.handleDisconnect(this);
    }

    public void handleDisconnectInMatch() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.setDisconnected(true);
    }

    public void stopSpectating(boolean broadcast) {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        Match<?> match = matchData.getMatch();
        match.stopSpectating(this, broadcast);
    }

    public PlayerMatchStatistics getMatchStatistics() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getStatistics();
    }

    public void handleHit() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.handleHit();
    }

    public boolean hasPearlCooldownExpired() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.hasPearlCooldownExpired();
    }

    public long getRemainingPearlCooldown() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getPearlCooldownRemaining();
    }

    public void increaseThrownPots() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.increaseThrownPotions();
    }

    public void increaseMissedPots() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.increaseMissedPotions();
    }

    public void resetPearlCooldown() {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.resetPearlCooldown();
    }

    public void notifyAboutPearlCooldownExpiration(PracticePlayer practicePlayer) {
        PlayerMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.notifyAboutPearlCooldownExpiration(practicePlayer);
    }

    public Ladder getCurrentlyEditingKit() {
        PlayerEditingData editingData = getStateData();
        Preconditions.checkNotNull(editingData, "data");
        return editingData.getLadder();
    }
}
