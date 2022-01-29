package country.pvp.practice.arena;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Sender;
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

public class ArenaBlockListener extends PlayerListener {

    private final MatchManager matchManager;

    @Inject
    public ArenaBlockListener(PlayerManager playerManager, MatchManager matchManager) {
        super(playerManager);
        this.matchManager = matchManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();
        Block block = event.getBlock();

        if (!match.hasBeenPlacedByPlayer(block)) {
            Sender.messageError(playerSession, "You can only destroy blocks placed by a player.");
            event.setCancelled(true);
            return;
        }

        match.removePlacedBlock(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PlayerSession playerSession = get(event.getPlayer());
        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        Block block = event.getBlockPlaced();

        if (!match.isInArena(block.getLocation())) {
            Sender.messageError(playerSession, "You can't build outside the arena.");
            event.setCancelled(true);
            return;
        }

        match.addPlacedBlock(event.getBlockPlaced());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bucketEmpty(PlayerBucketEmptyEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (!match.isInArena(block.getLocation())) {
            Sender.messageError(playerSession, "You can't build outside the arena.");
            event.setCancelled(true);
            return;
        }

        match.addPlacedBlock(block);
    }

    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent event) {
        event.setCancelled(true);
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
    public void spreadEvent(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void primeEvent(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }
}
