package me.ponktacology.practice.match.participant;

import lombok.Data;
import me.ponktacology.practice.match.combatlogger.CombatLogger;
import me.ponktacology.practice.match.pearl_cooldown.PearlCooldown;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.statistics.PlayerMatchStatistics;
import me.ponktacology.practice.util.message.Recipient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
public class GameParticipant implements Recipient {

  private final UUID uuid;
  private final String name;
  private final PlayerMatchStatistics statistics = new PlayerMatchStatistics();
  private final PearlCooldown pearlCooldown = new PearlCooldown();
  private @Nullable GameParticipant lastAttacker;
  private @Nullable InventorySnapshot inventorySnapshot;
  private @Nullable CombatLogger combatLogger;
  private boolean dead;
  private boolean disconnected;

  public @Nullable Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public boolean isOnline() {
    return getPlayer() != null;
  }

  public boolean isAlive() {
    return !dead;
  }

  public void createInventorySnapshot() {
    if (!isOnline()) {
      if (combatLogger == null) throw new IllegalStateException("this should not happen nigga");

      inventorySnapshot =
          new InventorySnapshot(
              combatLogger.getName(),
              combatLogger.getInventory(),
              combatLogger.getArmor(),
              combatLogger.getHealth(),
              combatLogger.getFoodLevel(),
              combatLogger.getPotionEffects(),
              statistics);
      return;
    }

    Player player = getPlayer();
    inventorySnapshot =
        new InventorySnapshot(
            player.getName(),
            player.getInventory().getContents(),
            player.getInventory().getArmorContents(),
            player.getHealth(),
            player.getFoodLevel(),
            player.getActivePotionEffects(),
            statistics);
  }

  public void markAsDisconnected() {
    this.disconnected = true;
  }

  public void createCombatLogger() {
    if (!isOnline()) throw new IllegalStateException("nigger state exception");
    Player player = getPlayer();
    this.combatLogger =
        new CombatLogger(
            player.getUniqueId(),
            player.getName(),
            player.getHealth(),
            player.getFoodLevel(),
            player.getActivePotionEffects(),
            player.getInventory().getContents(),
            player.getInventory().getArmorContents());
    this.combatLogger.spawn(player.getLocation());
  }

  public void markAsDead() {
    this.dead = true;
  }

  @Override
  public void receive(String message) {
    if (!isOnline()) return;

    getPlayer().sendMessage(message);
  }
}
