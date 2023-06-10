package me.ponktacology.practice.match.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;


@RequiredArgsConstructor
public class MatchKitListener extends PracticePlayerListener {

    private final MatchService matchService;

    @EventHandler(priority = EventPriority.MONITOR)
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK && action != Action.PHYSICAL) return;

        PracticePlayer practicePlayer = get(event);
        if (!matchService.isInMatch(practicePlayer)) return;
        Match match = matchService.getPlayerMatch(practicePlayer);

        Optional.ofNullable(practicePlayer.getMatchingKit(match.getLadder(), item))
                .ifPresent(it -> {
                    Messenger.message(practicePlayer, Messages.MATCH_PLAYER_EQUIP_KIT.match("{kit}", it.getName()));
                    it.apply(practicePlayer);
                });
    }


    @EventHandler(ignoreCancelled = true)
    public void dropEvent(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PracticePlayer practicePlayer = get(event);

        if (!matchService.isInMatch(practicePlayer)) return;
        Match match = matchService.getPlayerMatch(practicePlayer);

        Optional.ofNullable(practicePlayer.getMatchingKit(match.getLadder(), item))
                .ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        PracticePlayer practicePlayer = get((Player) event.getWhoClicked());
        if (!matchService.isInMatch(practicePlayer)) return;

        Match match = matchService.getPlayerMatch(practicePlayer);

        PlayerInventory playerInventory = event.getWhoClicked().getInventory();
        if (event.getClick().isKeyboardClick() && event.getHotbarButton() > 0) {
            ItemStack hotbarItem = playerInventory.getItem(event.getHotbarButton());

            if (hotbarItem == null) return;

            Optional.ofNullable(practicePlayer.getMatchingKit(match.getLadder(), hotbarItem))
                    .ifPresent(it -> event.setCancelled(true));
            return;
        }

        Optional.ofNullable(practicePlayer.getMatchingKit(match.getLadder(), item))
                .ifPresent(it -> event.setCancelled(true));
    }

}
