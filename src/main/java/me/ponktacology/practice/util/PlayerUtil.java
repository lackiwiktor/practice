package me.ponktacology.practice.util;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PlayerUtil {

  public static void clearInventory(Player player) {
    player.getOpenInventory().close();
    player.getInventory().setArmorContents(new ItemStack[4]);
    player.getInventory().setContents(new ItemStack[36]);
    player.updateInventory();
  }


  public static void resetPlayer(PracticePlayer practicePlayer) {
    Preconditions.checkArgument(practicePlayer.isOnline(), "player is not online");
    Player player = practicePlayer.getPlayer();
    resetPlayer(player, true);
  }

  public static void resetPlayer(Player player) {
    resetPlayer(player, true);
  }

  public static void resetPlayer(Player player, boolean resetHeldSlot) {
    if (resetHeldSlot) player.getInventory().setHeldItemSlot(0);
    clearInventory(player);
    player.setHealth(20.0D);
    player.setSaturation(20.0F);
    player.setFallDistance(0.0F);
    player.setFoodLevel(20);
    player.setFireTicks(0);
    player.setMaximumNoDamageTicks(20);
    player.setExp(0.0F);
    player.setLevel(0);
    player.setAllowFlight(false);
    player.setFlying(false);
    player.setGameMode(GameMode.SURVIVAL);
    player.getActivePotionEffects().forEach(it -> player.removePotionEffect(it.getType()));
  }

  public void denyMovement(Player player) {
    if (player.hasMetadata("noDenyMove")) {
      player.removeMetadata("noDenyMove", Practice.getPractice());
      return;
    }

    player.setWalkSpeed(0.0F);
    player.setFlySpeed(0.0F);
    player.setFoodLevel(0);
    player.setSprinting(false);
    player.setMetadata("denyMove", new FixedMetadataValue(Practice.getPractice(), true));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, false, false));
  }

  public void allowMovement(Player player) {
    if (!player.hasMetadata("denyMove")) return;
    player.setWalkSpeed(0.2F);
    player.setFlySpeed(0.2F);
    player.setFoodLevel(20);
    player.setSprinting(true);
    player.removePotionEffect(PotionEffectType.JUMP);
    player.removeMetadata("denyMove", Practice.getPractice());
  }
}
