package me.ponktacology.practice.hotbar.listener;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class HotBarListener extends PracticePlayerListener {

  private final HotBarService hotBarService;

  @EventHandler(priority = EventPriority.LOWEST)
  public void clickEvent(PlayerInteractEvent event) {
    ItemStack item = event.getItem();
    if (item == null) return;

    Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

    PracticePlayer practicePlayer = get(event);
    if (!Practice.getService(MatchService.class).isInMatch(practicePlayer)) {
      event.setCancelled(hotBarService.handleInteract(practicePlayer, item));
    }
  }
}
