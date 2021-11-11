package country.pvp.practice.team;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.Location;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class Team implements Recipient {

    public abstract Set<PracticePlayer> getPlayers();

    public int size() {
        return getPlayers().size();
    }

    public MatchData getMatchData(PracticePlayer player) {
        return player.getStateData(PlayerState.IN_MATCH);
    }

    public boolean hasPlayer(PracticePlayer player) {
        return getPlayers().contains(player);
    }

    public void setPlayersState(PlayerState state) {
        for (PracticePlayer player : getPlayers()) {
            player.setState(state);
        }
    }

    public void teleport(Location location) {
        for (PracticePlayer player : getPlayers()) {
            player.teleport(location);
        }
    }

    public void giveKits(Ladder ladder) {
        for (PracticePlayer player : getPlayers()) {
            player.giveKits(ladder);
        }
    }

    public void resetPlayers() {
        for (PracticePlayer player : getPlayers()) {
            PlayerUtil.resetPlayer(player.getPlayer());
        }
    }

    public void setMatchData(Match match) {
        for (PracticePlayer player : getPlayers()) {
            player.setStateData(PlayerState.IN_MATCH, new MatchData(match));
        }
    }

    @Override
    public void receive(String message) {
        for (PracticePlayer player : getPlayers()) {
            player.receive(message);
        }
    }

    public boolean isAlive(PracticePlayer player) {
        MatchData matchData = getMatchData(player);

        return !matchData.isDead();
    }

    public boolean hasDisconnected(PracticePlayer player) {
        MatchData matchData = getMatchData(player);

        return matchData.isDisconnected();
    }

    public Set<PracticePlayer> getAlivePlayers() {
        return getPlayers().stream()
                .filter(this::isAlive)
                .collect(Collectors.toSet());
    }

    public boolean isAnyPlayerAlive() {
        return getAlivePlayers().size() > 0;
    }

    public Set<PracticePlayer> getOnlinePlayers() {
        return getPlayers().stream().filter(it -> !hasDisconnected(it)).collect(Collectors.toSet());
    }

    public abstract String getName();
}
