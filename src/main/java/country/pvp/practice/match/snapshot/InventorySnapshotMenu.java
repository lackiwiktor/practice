package country.pvp.practice.match.snapshot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.match.PlayerMatchStatistics;
import country.pvp.practice.util.TimeUtil;
import country.pvp.practice.util.menu.Button;
import country.pvp.practice.util.menu.Menu;
import country.pvp.practice.util.message.FormatUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@RequiredArgsConstructor
public class InventorySnapshotMenu extends Menu {

    private final InventorySnapshotManager snapshotManager;

    private final InventorySnapshot snapshot;

    @Override
    public String getTitle(Player player) {
        return "Inventory of ".concat(snapshot.getName());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (ItemStack stack : fixInventoryOrder(snapshot.getInventory())) {
            buttons.put(buttons.size(), new PlaceholderButton(stack));
        }

        for (int i = 0; i < 4; i++) {
            ItemStack item = snapshot.getArmor()[i];
            buttons.put(36 + i, new PlaceholderButton(item));
        }

        buttons.put(45, new HealthButton(snapshot.getHealth()));
        buttons.put(46, new HungerButton(snapshot.getHunger()));
        buttons.put(47, new PotionEffectsButton(snapshot.getEffects()));
        buttons.put(48, new HealthPotionsButton(snapshot.getInventory()));
        buttons.put(49, new StatisticsButton(snapshot.getStatistics()));

        Optional<UUID> opponentOptional = snapshot.getOpponent();

        if (opponentOptional.isPresent()) {
            Optional<InventorySnapshot> snapshotOptional = snapshotManager.get(opponentOptional.get());

            snapshotOptional.ifPresent(playerInventorySnapshot -> buttons.put(53, new OpponentSnapshotButton(snapshotManager, playerInventorySnapshot)));
        }

        return buttons;
    }

    private ItemStack[] fixInventoryOrder(ItemStack... source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    @RequiredArgsConstructor
    class PlaceholderButton extends Button {

        private final ItemStack item;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(item).build();
        }
    }

    @RequiredArgsConstructor
    class HealthButton extends Button {

        private final double health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SPECKLED_MELON)
                    .amount((int) FormatUtil.formatHealth(health))
                    .hideAll()
                    .name(ChatColor.GREEN + FormatUtil.formatHealthWithHeart(health))
                    .build();
        }
    }

    @RequiredArgsConstructor
    class HungerButton extends Button {

        private final int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .amount(hunger)
                    .hideAll()
                    .name(ChatColor.GREEN.toString() + hunger + "/20 Hunger")
                    .build();
        }
    }

    @RequiredArgsConstructor
    class PotionEffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();

            for (PotionEffect effect : effects) {
                lore.add(formatPotionEffect(effect));
            }

            return new ItemBuilder(Material.POTION)
                    .hideAll()
                    .name(ChatColor.GREEN + "Potion Effects")
                    .lore(lore)
                    .build();
        }

        private String formatPotionEffect(PotionEffect effect) {
            return ChatColor.BLUE.toString().concat(getName(effect.getType()).concat(" ") + (effect.getAmplifier() + 1) + ChatColor.GRAY.toString().concat(" - " + TimeUtil.formatTimeMillisToClock(effect.getDuration() * 50L)));
        }

        //TODO: Make this into map
        public String getName(PotionEffectType type) {
            if (type.getName().equalsIgnoreCase("fire_resistance")) {
                return "Fire Resistance";
            } else if (type.getName().equalsIgnoreCase("speed")) {
                return "Speed";
            } else if (type.getName().equalsIgnoreCase("weakness")) {
                return "Weakness";
            } else if (type.getName().equalsIgnoreCase("slowness")) {
                return "Slowness";
            } else if (type.getName().equalsIgnoreCase("regeneration")) {
                return "Regeneration";
            } else if (type.getName().equalsIgnoreCase("damage_resistance")) {
                return "Resistance";
            } else if (type.getName().equalsIgnoreCase("absorption")) {
                return "Absorption";
            } else return "Unknown";
        }
    }

    @RequiredArgsConstructor
    class OpponentSnapshotButton extends Button {

        private final InventorySnapshotManager snapshotManager;
        private final InventorySnapshot snapshot;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.LEVER)
                    .hideAll()
                    .name(ChatColor.GREEN + "View ".concat(snapshot.getName()).concat(" inventory."))
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                new InventorySnapshotMenu(snapshotManager, snapshot).openMenu(player);
            }
        }
    }

    @RequiredArgsConstructor
    class StatisticsButton extends Button {

        private final PlayerMatchStatistics statistics;


        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();
            lore.add(ChatColor.GRAY + "Hits: " + ChatColor.WHITE + statistics.getHits());
            lore.add(ChatColor.GRAY + "Longest Combo: " + ChatColor.WHITE + statistics.getLongestCombo());
            lore.add(ChatColor.GRAY + "Potion Accuracy: " + ChatColor.WHITE + statistics.getPotionAccuracy() + "%");

            return new ItemBuilder(Material.PAPER)
                    .name(ChatColor.GREEN.toString().concat("Match Stats"))
                    .hideAll()
                    .lore(lore)
                    .build();
        }
    }


    @RequiredArgsConstructor
    private class HealthPotionsButton extends Button {

        private final ItemStack[] inventory;

        @Override
        public ItemStack getButtonItem(Player player) {
            int healthPotionsCount = countHealthPotions();
            return new ItemBuilder(Material.POTION)
                    .amount(healthPotionsCount)
                    .durability(16421)
                    .hideAll()
                    .name(ChatColor.GREEN.toString().concat(healthPotionsCount + " health potions"))
                    .build();
        }

        private int countHealthPotions() {
            int count = 0;

            for (ItemStack item : inventory) {
                if (item.getType() == Material.POTION && item.getDurability() == 16421) count++;
            }

            return count;
        }
    }
}
