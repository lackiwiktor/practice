package me.ponktacology.practice.match.combatlogger;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

@Data
public class CombatLogger {
  private final UUID player;
  private final String name;
  private double health;
  private int foodLevel;
  private Collection<PotionEffect> potionEffects;
  private ItemStack[] inventory;
  private ItemStack[] armor;
  private @Nullable Villager entity;

  public CombatLogger(
      UUID player,
      String name,
      double health,
      int foodLevel,
      Collection<PotionEffect> potionEffects,
      ItemStack[] inventory,
      ItemStack[] armor) {
    this.player = player;
    this.name = name;
    this.health = health;
    this.foodLevel = foodLevel;
    this.potionEffects = potionEffects;
    this.inventory = inventory;
    this.armor = armor;
  }

  public void spawn(Location location) {
    System.out.println("SPAWNED VILLAGER");
    entity = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
    entity.setHealth(health);
    entity.setCustomName("CombatLogger");
    entity.setCustomNameVisible(true);
    entity.getEquipment().setArmorContents(armor);
    entity.getEquipment().setItemInHand(inventory[0]);
  }

  public void destroy() {
    entity.remove();
  }
}
