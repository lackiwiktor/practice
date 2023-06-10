package me.ponktacology.practice.match.snapshot;

import com.google.common.base.Preconditions;
import me.ponktacology.practice.util.Expiring;
import me.ponktacology.practice.match.statistics.PlayerMatchStatistics;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.ItemStackUtil;
import me.ponktacology.practice.util.TimeUtil;
import lombok.Data;
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
    private long createdAt;
    private UUID opponent;

    public static InventorySnapshot create(PracticePlayer player, PlayerMatchStatistics statistics) {
        Player bukkitPlayer = player.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "player");
        PlayerInventory playerInventory = bukkitPlayer.getInventory();

        return new InventorySnapshot(
                player.getName(),
                playerInventory.getContents(),
                playerInventory.getArmorContents(),
                bukkitPlayer.getHealth(),
                bukkitPlayer.getFoodLevel(),
                bukkitPlayer.getActivePotionEffects(),
                statistics);
    }

    public Optional<UUID> getOpponent() {
        return Optional.ofNullable(opponent);
    }

    public ItemStack[] getArmor() {
        return ItemStackUtil.convertNullToAirAndCloneItems(armor);
    }

    public ItemStack[] getInventory() {
        return ItemStackUtil.convertNullToAirAndCloneItems(inventory);
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
