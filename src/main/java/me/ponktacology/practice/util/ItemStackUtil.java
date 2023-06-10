package me.ponktacology.practice.util;

import me.ponktacology.practice.hotbar.HotBarItem;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemStackUtil {

    public static ItemStack[] convertNullToAirAndCloneItems(ItemStack[] items) {
        ItemStack[] convertedItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                convertedItems[i] = new ItemStack(Material.AIR);

            } else convertedItems[i] = items[i].clone();
        }

        return convertedItems;
    }

    public static ItemStack[] convertNullToAirAndCloneItems(HotBarItem[] items) {
        ItemStack[] convertedItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                convertedItems[i] = new ItemStack(Material.AIR);

            } else convertedItems[i] = items[i].getItem().clone();
        }

        return convertedItems;
    }
}
