package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class MatchBuildListener extends PracticePlayerListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if (!practicePlayer.isInMatch()) return;

        Match match = practicePlayer.getCurrentMatch();
        Block block = event.getBlock();

        if (!match.canDestroyBlock(block)) {
            Messenger.messageError(practicePlayer, "You can only destroy blocks placed by a player.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());
        if (!practicePlayer.isInMatch()) return;

        Match match = practicePlayer.getCurrentMatch();

        Block block = event.getBlockPlaced();

        if (match.isOutsideArena(block.getLocation())) {
            Messenger.messageError(practicePlayer, "You can't build outside the arena.");
            event.setCancelled(true);
            return;
        }

        match.addDestroyableBlock(event.getBlockPlaced());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if (!practicePlayer.isInMatch()) return;

        Match match = practicePlayer.getCurrentMatch();

        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (match.isOutsideArena(block.getLocation())) {
            Messenger.messageError(practicePlayer, "You can't build outside the arena.");
            event.setCancelled(true);
            return;
        }

        match.addDestroyableBlock(block);
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().getEntities().clear();
        event.getWorld().setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void weatherEvent(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void spawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void igniteEvent(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING)
            event.setCancelled(true);
    }

    @EventHandler
    public void decayEvent(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void hangingEvent(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void burnEvent(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void primeEvent(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }
}
