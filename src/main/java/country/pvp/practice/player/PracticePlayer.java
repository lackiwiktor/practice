package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.kit.Kit;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.data.PlayerKits;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.player.data.PlayerStateData;
import country.pvp.practice.player.data.PlayerStatistics;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.*;

@Data
public class PracticePlayer implements DataObject, Recipient {

    private final UUID uuid;
    private final PlayerStateData stateData = new PlayerStateData();
    private final PlayerStatistics statistics = new PlayerStatistics();
    private final PlayerKits kits = new PlayerKits();
    private String name;
    private PlayerState state = PlayerState.IN_LOBBY;

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

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isInMatch() {
        return state == PlayerState.IN_MATCH && hasStateData(PlayerState.IN_MATCH);
    }

    public boolean isInQueue() {
        return state == PlayerState.QUEUING && hasStateData(PlayerState.QUEUING);
    }

    public boolean isInLobby() {
        return state == PlayerState.IN_LOBBY;
    }

    public void setBar(ItemStack[] bar) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "Player must be online in order to change his item bar");
        for (int i = 0; i < bar.length; i++) {
            ItemStack item = bar[i].clone();
            player.getInventory().setItem(i, item);
        }
    }

    public void setStateData(PlayerState state, Object data) {
        stateData.setStateData(state, data);
    }

    public void removeStateData(PlayerState state) {
        stateData.removeStateData(state);
    }

    public <V> V getStateData(PlayerState state) {
        return stateData.getStateData(state);
    }

    public boolean hasStateData(PlayerState state) {
        return stateData.hasStateData(state);
    }

    public void clearPlayerData() {
        stateData.clear();
    }

    public int getRank(Ladder ladder) {
        return statistics.getRank(ladder);
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

    @Override
    public String getCollection() {
        return "players";
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public boolean hasPermission(String permission) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");

        return player.hasPermission(permission);
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

    public void teleport(Location location) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "player");
        player.teleport(location);
    }

    public Optional<? extends Kit> getMatchingKit(Ladder ladder, ItemStack itemStack) {
        Optional<? extends Kit> playerKit = Arrays.stream(kits.getKits(ladder)).filter(it -> it != null && it.getIcon().isSimilar(itemStack)).findFirst();

        if (!playerKit.isPresent()) {
            if (ladder.getKit().getIcon().isSimilar(itemStack)) {
                return Optional.of(ladder.getKit()); //Give default kit
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PracticePlayer that = (PracticePlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
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

    public boolean isInEditor() {
        return state == PlayerState.EDITING_KIT && hasStateData(PlayerState.EDITING_KIT);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }
}
