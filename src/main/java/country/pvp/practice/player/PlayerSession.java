package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.duel.DuelInvitable;
import country.pvp.practice.duel.Request;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.kit.editor.SessionEditingData;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.*;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.data.PlayerKits;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.player.data.PlayerStatistics;
import country.pvp.practice.player.data.SessionData;
import country.pvp.practice.player.duel.PlayerDuelRequest;
import country.pvp.practice.queue.SessionQueueData;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
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
public class PlayerSession implements DataObject, DuelInvitable<PlayerSession, PlayerDuelRequest>, Recipient {

    private final UUID uuid;
    private String name;
    private @Nullable Party party;
    private PlayerState state = PlayerState.IN_LOBBY;
    private @Nullable RematchData rematchData;
    private @Nullable SessionData sessionData;
    private boolean loaded;
    private final PlayerStatistics statistics = new PlayerStatistics();
    private final PlayerKits kits = new PlayerKits();
    private final Set<PlayerDuelRequest> duelRequests = Sets.newConcurrentHashSet();

    public PlayerSession(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public PlayerSession(UUID uuid, String name) {
        this(uuid);
        this.name = name;
    }

    public PlayerSession(UUID uuid) {
        this.uuid = uuid;
    }

    public void setState(PlayerState state) {
        this.state = state;
        if (state == PlayerState.IN_LOBBY) sessionData = null;
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
        return state == PlayerState.IN_LOBBY;
    }

    public boolean isInQueue() {
        return state == PlayerState.QUEUING && sessionData != null;
    }

    public boolean isInMatch() {
        return state == PlayerState.IN_MATCH && sessionData != null;
    }

    public boolean isInEditor() {
        return state == PlayerState.EDITING_KIT && sessionData != null;
    }

    public void setBar(ItemStack... bar) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "Player must be online in order to change his item bar");
        for (int i = 0; i < bar.length; i++) {
            ItemStack item = bar[i].clone();
            player.getInventory().setItem(i, item);
        }
    }

    public boolean hasRematch() {
        return rematchData != null;
    }

    public <V extends SessionData> void setState(PlayerState state, V data) {
        this.state = state;
        this.sessionData = data;
    }

    public <V extends SessionData> @Nullable V getStateData() {
        return (V) sessionData;
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

    public NamedKit[] getKits(Ladder ladder) {
        return kits.getKits(ladder);
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

    public PlayerSession getRematchPlayer() {
        Preconditions.checkNotNull(rematchData, "rematch data");
        return rematchData.getPlayer();
    }

    public Ladder getRematchLadder() {
        Preconditions.checkNotNull(rematchData, "rematch data");
        return rematchData.getLadder();
    }

    public void teleport(Location location) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.teleport(location);
    }

    @Nullable
    public NamedKit getMatchingKit(Ladder ladder, ItemStack itemStack) {
        Optional<NamedKit> playerKit = Arrays.stream(kits.getKits(ladder)).filter(it -> it != null && it.getIcon().isSimilar(itemStack)).findFirst();

        if (!playerKit.isPresent()) {
            if (ladder.getKit().getIcon().isSimilar(itemStack)) {
                return NamedKit.from("Default Kit", ladder.getKit()); //Give default kit
            }
        } else return playerKit.get(); //Give custom player kit

        return null;
    }

    public void giveKits(Ladder ladder) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.addItem(ladder.getKit().getIcon());

        if (kits.hasKits(ladder)) {
            Arrays.stream(getKits(ladder)).filter(Objects::nonNull).forEach(it -> playerInventory.addItem(it.getIcon()));
        }

        player.updateInventory();
    }

    public Match getCurrentMatch() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getMatch();
    }

    public Match getCurrentlySpectatingMatch() {
        SessionSpectatingData spectatingData = getStateData();
        Preconditions.checkNotNull(spectatingData, "data");
        return spectatingData.getMatch();
    }

    public void handleBeingHit(PlayerSession attacker) {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.handleBeingHit(attacker);
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

    public int getPing() {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        return ((CraftPlayer) player).getHandle().ping;
    }

    public void sendComponent(BaseComponent... components) {
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
        SessionQueueData queueData = getStateData();
        Preconditions.checkNotNull(queueData, "data");
        queueData.removeFromQueue(left);
    }

    public void removeFromQueue() {
        removeFromQueue(false);
    }

    public void setDead(boolean dead) {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.setDead(dead);
    }

    public boolean hasLastAttacker() {
        return getLastAttacker() != null;
    }

    public PlayerSession getLastAttacker() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getLastAttacker();
    }

    public void handleDisconnectInParty() {
        Preconditions.checkNotNull(party);
        party.handleDisconnect(this);
    }

    public void handleDisconnectInMatch() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.setDisconnected(true);
    }

    public void stopSpectating(boolean broadcast) {
        SessionSpectatingData spectatingData = getStateData();
        Preconditions.checkNotNull(spectatingData, "data");
        Match match = spectatingData.getMatch();
        match.stopSpectating(this, broadcast);
    }

    public PlayerMatchStatistics getMatchStatistics() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getStatistics();
    }

    public void handleHit() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.handleHit();
    }

    public boolean hasPearlCooldownExpired() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.hasPearlCooldownExpired();
    }

    public long getRemainingPearlCooldown() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        return matchData.getPearlCooldownRemaining();
    }

    public void increaseThrownPots() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.increaseThrownPotions();
    }

    public void increaseMissedPots() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.increaseMissedPotions();
    }

    public void resetPearlCooldown() {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.resetPearlCooldown();
    }

    public void notifyAboutPearlCooldownExpiration(PlayerSession playerSession) {
        SessionMatchData matchData = getStateData();
        Preconditions.checkNotNull(matchData, "data");
        matchData.notifyAboutPearlCooldownExpiration(playerSession);
    }

    public Ladder getCurrentlyEditingKit() {
        SessionEditingData editingData = getStateData();
        Preconditions.checkNotNull(editingData, "data");
        return editingData.getLadder();
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
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

    @Override
    public void receiveInvite(BaseComponent[] component) {
        sendComponent(component);
    }

    public int getEntityId() {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        return player.getEntityId();
    }
}
