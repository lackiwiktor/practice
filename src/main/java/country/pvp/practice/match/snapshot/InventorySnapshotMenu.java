package country.pvp.practice.match.snapshot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.message.FormatUtil;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.match.PlayerMatchStatistics;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.time.TimeUtil;
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

        buttons.put(47, new HealthButton(snapshot.getHealth()));
        buttons.put(48, new HungerButton(snapshot.getHunger()));
        buttons.put(50, new PotionEffectsButton(snapshot.getEffects()));
        buttons.put(51, new StatisticsButton(snapshot.getStatistics(), snapshot.getInventory()));

        Optional<UUID> opponentOptional = snapshot.getOpponent();

        if (opponentOptional.isPresent()) {
            Optional<InventorySnapshot> snapshotOptional = snapshotManager.get(opponentOptional.get());

            snapshotOptional.ifPresent(playerInventorySnapshot -> buttons.put(49, new OpponentSnapshotButton(snapshotManager, playerInventorySnapshot)));
        }

        return buttons;
    }

    private ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    @RequiredArgsConstructor
    static class PlaceholderButton extends Button {

        private final ItemStack item;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(item).build();
        }
    }

    @RequiredArgsConstructor
    static class HealthButton extends Button {

        private final double health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SPECKLED_MELON)
                    .amount((int) FormatUtil.formatHealth(health))
                    .name(health == 0 ? ChatColor.RED + "Dead" : (ChatColor.GREEN.toString() + FormatUtil.formatHealth(health) + "/10 Health"))
                    .build();
        }
    }

    @RequiredArgsConstructor
    static class HungerButton extends Button {

        private final int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .amount(hunger)
                    .name(ChatColor.GREEN.toString() + hunger + "/10 Hunger")
                    .build();
        }
    }

    @RequiredArgsConstructor
    static class PotionEffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();

            for (PotionEffect effect : effects) {
                lore.add(formatPotionEffect(effect));
            }

            return new ItemBuilder(Material.POTION)
                    .name(ChatColor.GREEN + "Potion Effects")
                    .lore(lore)
                    .build();
        }

        private String formatPotionEffect(PotionEffect effect) {
            return ChatColor.BLUE.toString().concat(getName(effect.getType()).concat(" ") + effect.getAmplifier() + ChatColor.GRAY.toString().concat(" - " + TimeUtil.formatTimeMillisToClock(effect.getDuration() * 50L)));
        }

        public static String getName(PotionEffectType type) {
            if (type.getName().equalsIgnoreCase("fire_resistance")) {
                return "Fire Resistance";
            } else if (type.getName().equalsIgnoreCase("speed")) {
                return "Speed";
            } else if (type.getName().equalsIgnoreCase("weakness")) {
                return "Weakness";
            } else if (type.getName().equalsIgnoreCase("slowness")) {
                return "Slowness";
            } else {
                return "Unknown";
            }
        }
    }

    @RequiredArgsConstructor
    static class OpponentSnapshotButton extends Button {

        private final InventorySnapshotManager snapshotManager;
        private final InventorySnapshot snapshot;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .skull(snapshot.getName())
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
    static class StatisticsButton extends Button {

        private final PlayerMatchStatistics statistics;
        private final ItemStack[] inventory;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();
            lore.add("Hits: " + statistics.getHits());
            lore.add("Longest Combo: " + statistics.getLongestCombo());
            lore.add("Potions Left: " + countHealthPotions());
            lore.add("Potion Accuracy: " + statistics.getPotionAccuracy() + "%");

            return new ItemBuilder(Material.PAPER)
                    .name(ChatColor.GREEN.toString().concat("Match Stats"))
                    .lore(lore)
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
