package country.pvp.practice.match.snapshot;

import com.google.common.base.Preconditions;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.match.PlayerMatchStatistics;
import country.pvp.practice.player.PracticePlayer;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Data
public class InventorySnapshot {

    private final PracticePlayer player;
    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final double health;
    private final int hunger;
    private final Collection<PotionEffect> effects;
    private final PlayerMatchStatistics statistics;
    private UUID opponent;

    public static InventorySnapshot create(PracticePlayer player) {

        Player bukkitPlayer = player.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "player");
        PlayerInventory playerInventory = bukkitPlayer.getInventory();
        PlayerMatchData matchData = player.getStateData();
        Preconditions.checkNotNull(matchData, "matchData");

        return new InventorySnapshot(player, playerInventory.getContents(), playerInventory.getArmorContents(), bukkitPlayer.getHealth(), bukkitPlayer.getFoodLevel(), bukkitPlayer.getActivePotionEffects(), matchData.getStatistics());
    }

    public String getName() {
        return player.getName();
    }

    public UUID getId() {
        return player.getUuid();
    }

    public Optional<UUID> getOpponent() {
        return Optional.ofNullable(opponent);
    }

    public ItemStack[] getArmor() {
        return Arrays.stream(armor).map(it -> it == null ? new ItemStack(Material.AIR) : it).toArray((n) -> new ItemStack[n]);
    }

    public ItemStack[] getInventory() {
        return Arrays.stream(inventory).map(it -> it == null ? new ItemStack(Material.AIR) : it).toArray((n) -> new ItemStack[n]);
    }
}
