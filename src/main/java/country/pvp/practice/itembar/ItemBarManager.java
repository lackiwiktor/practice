package country.pvp.practice.itembar;

import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBarManager {

    public ItemBar get(PlayerSession player) {
        ItemBarItem[] items = new ItemBarItem[9];
        switch (player.getState()) {
            case IN_LOBBY:
                items[0] = new ItemBarItem(new ItemBuilder(Material.IRON_SWORD).name("&7Unranked").unbreakable().build(),
                        ((practicePlayer, interact) -> practicePlayer.chat("/unranked")));
                items[1] = new ItemBarItem(new ItemBuilder(Material.DIAMOND_SWORD).name("&6Ranked").unbreakable().build(),
                        ((practicePlayer, interact) -> practicePlayer.chat("/ranked")));
                items[2] = player.hasRematchData() ? new ItemBarItem(new ItemBuilder(Material.BLAZE_ROD).name("&eRematch").unbreakable().build(),
                        ((practicePlayer, interact) -> practicePlayer.chat("/duel " + practicePlayer.getRematchPlayer().getName() + " " + practicePlayer.getRematchLadder().getName()))) : null;
                items[4] = new ItemBarItem(new ItemBuilder(Material.ANVIL).name("&eKit Editor").unbreakable().build(),
                        ((practicePlayer, interact) -> practicePlayer.chat("/kiteditor")));
                items[5] = player.hasParty() ? new ItemBarItem(new ItemBuilder(Material.IRON_AXE).build(), ((practicePlayer, interact) -> {
                })) : new ItemBarItem(new ItemBuilder(Material.NAME_TAG).name("&bCreate Party").build(), ((practicePlayer, interact) -> practicePlayer.chat("/party create")));

                return new ItemBar(items);
            case QUEUING:
                return new ItemBar(
                        new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cLeave Queue").unbreakable().build(),
                                ((practicePlayer, interact) -> practicePlayer.removeFromQueue(true))));
            case SPECTATING:
                return new ItemBar(
                        new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cStop spectating").unbreakable().build(),
                                ((practicePlayer, interact) -> practicePlayer.stopSpectating(true))));
        }

        return null;
    }

    public boolean click(PlayerSession player, ItemStack item, BarInteract interact) {
        for (ItemBarItem itemBarItem : get(player).getItems()) {
            if (itemBarItem == null || itemBarItem.getItem().getType() == Material.AIR) continue;

            if (itemBarItem.isSimilar(item)) {
                itemBarItem.click(player, interact);
                return true;
            }
        }

        return false;
    }

    public void apply(PlayerSession player) {
        PlayerUtil.resetPlayer(player.getPlayer());
        get(player).apply(player);
    }
}
