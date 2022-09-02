package me.ponktacology.practice.party;

import me.ponktacology.practice.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public enum PartyEvent {

    SPLIT(new ItemBuilder(Material.DIAMOND_SWORD)
            .name("Party Split")
            .lore("Split your party into", "two teams and fight.", "", "Click to host!")
            .build()),
    FFA(new ItemBuilder(Material.DIAMOND_SWORD)
            .name("Party FFA")
            .lore("Everyone in the party", "fights everybody else.", "", "Click to host!")
            .build());

    private final ItemStack icon;
}
