package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.kit.PlayerKit;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Recipient;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Data
public class PracticePlayer implements DataObject, Recipient {

    private final UUID uuid;
    private final PlayerStateData stateData = new PlayerStateData();
    private final PlayerStatistics statistics = new PlayerStatistics();
    private final PlayerKits kits = new PlayerKits(this);
    private String name;
    private PlayerState state = PlayerState.IN_LOBBY;

    public PracticePlayer(Player player) {
        this(player.getUniqueId());
        this.name = player.getName();
    }

    public PracticePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isFighting() {
        return state == PlayerState.IN_MATCH;
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

    public int getRank(Ladder ladder) {
        return statistics.getRank(ladder);
    }

    public boolean hasKit(Ladder ladder) {
        return kits.hasKits(ladder);
    }

    public List<PlayerKit> getKits(Ladder ladder) {
        return kits.getKits(ladder);
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
        Preconditions.checkNotNull(player, "Player must be online in order to check his permissions");

        return player.hasPermission(permission);
    }

    @Override
    public Document getDocument() {
        org.bson.Document document = new org.bson.Document("_id", getId());
        document.put("name", name);
        document.put("nameLowerCase", name.toLowerCase(Locale.ROOT));
        document.put("ranks", statistics.getDocument());

        return document;
    }

    @Override
    public void receive(String message) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "Player must be online in order to send him a message");
        player.sendMessage(message);
    }

    @Override
    public void applyDocument(Document document) {
        if (name == null) name = document.getString("name");
        statistics.applyDocument(document.get("ranks", Document.class));
    }

    public void teleport(Location location) {
        Player player = getPlayer();
        Preconditions.checkNotNull(player, "Player must be online in order to teleport him");
        player.teleport(location);
    }

    public PlayerKit getMatchingKit(Ladder ladder, ItemStack itemStack) {
        return kits.getKits(ladder).stream().filter(it -> it.getIcon().isSimilar(itemStack)).findFirst().orElse(null);
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
}
