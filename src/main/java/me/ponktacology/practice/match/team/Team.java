package me.ponktacology.practice.match.team;

import com.google.common.collect.Lists;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.StateMatchData;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.PlayerUtil;
import me.ponktacology.practice.util.message.Recipient;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public abstract class Team implements Recipient {

    protected final List<PracticePlayer> players = Lists.newArrayList();

    public abstract String getName();

    public int size() {
        return players.size();
    }

    public boolean hasPlayer(PracticePlayer player) {
        return players.contains(player);
    }

    public void clearRematchData() {
        forEachPlayer(player -> player.setRematchData(null));
    }

    public void createMatchSession(Match match) {
        forEachPlayer(player -> player.setState(PlayerState.IN_MATCH, new StateMatchData(match)));
        clearRematchData();
    }

    public void teleport(Location location) {
        forEachPlayer(player -> player.teleport(location));
    }

    public void giveKits(Ladder ladder) {
        forEachPlayer(player -> {
            if (!player.isOnline()) {
                return;
            }

            player.giveKits(ladder);
        });
    }

    public void reset() {
        forEachPlayer(player -> {
            if (!player.isOnline()) {
                return;
            }
            Player bukkitPlayer = player.getPlayer();
            PlayerUtil.resetPlayer(bukkitPlayer);
        });
    }

    public List<PracticePlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public void receive(String message) {
        forEachPlayer(player -> player.receive(message));
    }

    public List<PracticePlayer> getOnlinePlayers() {
        return players
                .stream()
                .filter(PracticePlayer::isOnline)
                .collect(Collectors.toList());
    }

    private void forEachPlayer(Consumer<PracticePlayer> consumer) {
        for (PracticePlayer player : players) {
            consumer.accept(player);
        }
    }
}
