package me.ponktacology.practice.kit;

import com.google.common.base.Preconditions;
import me.ponktacology.practice.util.ItemStackUtil;
import me.ponktacology.practice.util.data.SerializableObject;
import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Kit implements SerializableObject {

    private ItemStack [] inventory = new ItemStack[36];
    private ItemStack [] armor = new ItemStack[4];

    @Override
    public Document getDocument() {
        Document document = new Document();
        document.put(
                "inventory",
                Arrays.stream(inventory).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        document.put(
                "armor", Arrays.stream(armor).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        inventory = (ItemStack[]) document.get("inventory", List.class).stream().map(it -> ItemStackAdapter.fromJson((String) it)).toArray(ItemStack[]::new);
        armor = (ItemStack[]) document.get("armor", List.class).stream().map(it -> ItemStackAdapter.fromJson((String) it)).toArray(ItemStack[]::new);
    }

    public void apply(PracticePlayer player) {
        Player bukkitPlayer = player.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "player");
        PlayerInventory playerInventory = bukkitPlayer.getInventory();
        playerInventory.setArmorContents(getArmor());
        playerInventory.setContents(getInventory());
        bukkitPlayer.updateInventory();
    }

    public ItemStack [] getInventory() {
        return ItemStackUtil.convertNullToAirAndCloneItems(inventory);
    }

    public ItemStack [] getArmor() {
        return ItemStackUtil.convertNullToAirAndCloneItems(armor);
    }

    public ItemStack getIcon() {
        return new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("&eDefault Kit")
                .build();
    }

}
