package me.ponktacology.practice.event.type;

import me.ponktacology.practice.arena.thimble.ThimbleArena;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.Logger;
import me.ponktacology.practice.util.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ThimbleListener extends PracticePlayerListener {

  @EventHandler
  public void playerMoveEvent(PlayerMoveEvent event) {
    PracticePlayer player = get(event);

/*
    if (!player.isInEvent()) return;
    if (!(player.getEvent() instanceof Thimble)) return;
 */

    Thimble thimble = null; // (Thimble) player.getEvent();

    // We check if player is currently jumping
    if (player.equals(thimble.getCurrentJumper()) && thimble.getState() == ThimbleState.JUMPING) {
      ThimbleArena arena = thimble.getArena();
      Region jumpingArea = arena.getWaterRegion();
      Block block = event.getTo().getBlock();
      // TODO: Optimize


      if (jumpingArea.isIn(block.getLocation())) {
        Logger.log("(in arena) On ground: %b", event.getPlayer().isOnGround());
        thimble.onJump(player, true, event.getTo());
      } else {
        Location location = event.getTo().getBlock().getLocation().clone().subtract(0, 1, 0);

        if (jumpingArea.isIn(location)) {

         if(!location.getBlock().getType().toString().contains("WATER")) {
           thimble.onJump(player, false, event.getTo());
         }
        }
      }
    }
  }
}
