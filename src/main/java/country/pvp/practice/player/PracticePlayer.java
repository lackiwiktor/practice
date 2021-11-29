package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.duel.DuelRequest;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.data.*;
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
    private final Set<DuelRequest> duelRequests = Sets.newConcurrentHashSet();
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
        return state == PlayerState.IN_LOBBY;
    }

    public boolean isInQueue() {
        return state == PlayerState.QUEUING && hasStateData();
    }

    public boolean isInMatch() {
        return state == PlayerState.IN_MATCH && hasStateData();
    }

    public boolean isInEditor() {
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

    public void clearSateData() {
        stateData.removeStateData();
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

    public void addDuelRequest(DuelRequest request) {
        duelRequests.add(request);
    }

    public boolean hasDuelRequest(PracticePlayer player) {
        return duelRequests.stream().anyMatch(it -> it.getPlayer().equals(player));
    }

    public @Nullable DuelRequest getDuelRequest(PracticePlayer player) {
        return duelRequests.stream().filter(it -> it.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public void invalidateDuelRequests() {
        duelRequests.removeIf(DuelRequest::hasExpired);
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

    public void invalidateRequest(DuelRequest request) {
        duelRequests.remove(request);
    }
}
