package country.pvp.practice.itembar;

import country.pvp.practice.Practice;
import country.pvp.practice.duel.RematchData;
import country.pvp.practice.lobby.PlayerLobbyData;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.PlayerSpectatingData;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.PlayerQueueData;
import country.pvp.practice.queue.Queue;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBarManager {

    public ItemBar get(PracticePlayer player) {
        ItemBarItem[] items = new ItemBarItem[9];
        switch (player.getState()) {
            case IN_LOBBY:
                PlayerLobbyData lobbyData = player.getStateData();
                RematchData rematchData = lobbyData.getRematchData();

                items[0] = new ItemBarItem(new ItemBuilder(Material.IRON_SWORD).name("&7Unranked").unbreakable().build(),
                        ((practicePlayer, interact) -> Practice.getQueueMenuProvider().provide(false, practicePlayer).openMenu(practicePlayer.getPlayer())));
                items[1] = new ItemBarItem(new ItemBuilder(Material.DIAMOND_SWORD).name("&6Ranked").unbreakable().build(),
                        ((practicePlayer, interact) -> Practice.getQueueMenuProvider().provide(true, practicePlayer).openMenu(practicePlayer.getPlayer())));
                items[2] = rematchData == null ? null : new ItemBarItem(new ItemBuilder(Material.BLAZE_ROD).name("&eRematch").unbreakable().build(),
                        ((practicePlayer, interact) -> Practice.getDuelService().invite(player, rematchData)));
                items[4] = new ItemBarItem(new ItemBuilder(Material.ANVIL).name("&eKit Editor").unbreakable().build(),
                        (((practicePlayer, interact) -> Practice.getKitChooseMenuProvider().provide((ladder) -> Practice.getKitEditorService().moveToEditor(practicePlayer, ladder)).openMenu(practicePlayer.getPlayer()))));

                return new ItemBar(items);
            case QUEUING:
                return new ItemBar(
                        new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cLeave Queue").unbreakable().build(),
                                ((practicePlayer, interact) -> {
                                    PlayerQueueData queueData = practicePlayer.getStateData();
                                    Queue queue = queueData.getQueue();
                                    queue.removePlayer(practicePlayer, true);
                                })));
            case SPECTATING:
                return new ItemBar(
                        new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cStop spectating").unbreakable().build(),
                                ((practicePlayer, interact) -> {
                                    PlayerSpectatingData spectatingData = player.getStateData();
                                    Match<?> match = spectatingData.getMatch();
                                    match.stopSpectating(player, true);
                                })));
        }

        return null;
    }

    public boolean click(PracticePlayer player, ItemStack item, BarInteract interact) {
        for (ItemBarItem itemBarItem : get(player).getItems()) {
            if (itemBarItem == null || itemBarItem.getItem().getType() == Material.AIR) continue;

            if (itemBarItem.isSimilar(item)) {
                itemBarItem.click(player, interact);
                return true;
            }
        }

        return false;
    }

    public void apply(PracticePlayer player) {
        PlayerUtil.resetPlayer(player.getPlayer());
        get(player).apply(player);
    }
}
