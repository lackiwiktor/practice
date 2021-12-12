package country.pvp.practice.match.snapshot;

import com.google.common.base.Preconditions;
import country.pvp.practice.expiring.Expiring;
import country.pvp.practice.match.PlayerMatchStatistics;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Data
public class InventorySnapshot implements Expiring {

    private final UUID id = UUID.randomUUID();
    private final String name;
    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final double health;
    private final int hunger;
    private final Collection<PotionEffect> effects;
    private final PlayerMatchStatistics statistics;
    private long createdAt = System.currentTimeMillis();
    private UUID opponent;

    public static InventorySnapshot create(PlayerSession player) {
        Player bukkitPlayer = player.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "player");
        PlayerInventory playerInventory = bukkitPlayer.getInventory();
        return new InventorySnapshot(player.getName(), playerInventory.getContents(), playerInventory.getArmorContents(), bukkitPlayer.getHealth(), bukkitPlayer.getFoodLevel(), bukkitPlayer.getActivePotionEffects(), player.getMatchStatistics());
    }

    public Optional<UUID> getOpponent() {
        return Optional.ofNullable(opponent);
    }

    public ItemStack[] getArmor() {
        return Arrays.stream(armor).map(it -> it == null ? new ItemStack(Material.AIR) : it).toArray(ItemStack[]::new);
    }

    public ItemStack[] getInventory() {
        return Arrays.stream(inventory).map(it -> it == null ? new ItemStack(Material.AIR) : it).toArray(ItemStack[]::new);
    }

    public boolean hasExpired() {
        return TimeUtil.elapsed(createdAt) > 120_000L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventorySnapshot that = (InventorySnapshot) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
