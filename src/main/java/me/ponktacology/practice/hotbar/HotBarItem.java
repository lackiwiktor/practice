package me.ponktacology.practice.hotbar;

import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.ItemBuilder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
public class HotBarItem {

    private final ItemStack item;
    private final String command;

    public HotBarItem(Material material, String displayName, String command) {
        this.item = new ItemBuilder(material)
                .name(displayName)
                .hideAll()
                .build();
        this.command = command;
    }

    void click(PracticePlayer player) {
        player.runCommand(command);
        player.getPlayer().updateInventory();
    }
}
