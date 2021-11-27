package country.pvp.practice.team;

import com.mongodb.assertions.Assertions;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.Location;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class Team implements Recipient {

    public int size() {
        return getPlayers().size();
    }

    public PlayerMatchData getMatchData(PracticePlayer player) {
        return player.getStateData();
    }

    public boolean hasPlayer(PracticePlayer player) {
        return getPlayers().contains(player);
    }

    public void setPlayersState(PlayerState state) {
        for (PracticePlayer player : getOnlinePlayers()) {
            player.setState(state);
        }
    }

    public void teleport(Location location) {
        for (PracticePlayer player : getOnlinePlayers()) {
            player.teleport(location);
        }
    }

    public void giveKits(Ladder ladder) {
        for (PracticePlayer player : getOnlinePlayers()) {
            player.giveKits(ladder);
        }
    }

    public void resetPlayers() {
        for (PracticePlayer player : getOnlinePlayers()) {
            PlayerUtil.resetPlayer(player.getPlayer());
        }
    }

    public void setMatchData(Match match) {
        for (PracticePlayer player : getPlayers()) {
            player.setState(PlayerState.IN_MATCH, new PlayerMatchData(match));
        }
    }

    @Override
    public void receive(String message) {
        for (PracticePlayer player : getOnlinePlayers()) {
            player.receive(message);
        }
    }

    public boolean isAlive(PracticePlayer player) {
        PlayerMatchData matchData = getMatchData(player);

        return !matchData.isDead();
    }

    public boolean hasDisconnected(PracticePlayer player) {
        PlayerMatchData matchData = getMatchData(player);

        Assertions.assertFalse(matchData == null);

        return matchData.isDisconnected();
    }

    public Set<PracticePlayer> getAlivePlayers() {
        return getPlayers().stream()
                .filter(this::isAlive)
                .collect(Collectors.toSet());
    }

    public boolean isDead() {
        return getAlivePlayers().size() == 0;
    }

    public Set<PracticePlayer> getOnlinePlayers() {
        return getPlayers().stream().filter(it -> !hasDisconnected(it)).collect(Collectors.toSet());
    }

    public abstract String getName();

    public abstract Set<PracticePlayer> getPlayers();

    public abstract int getPing();

    public abstract int getElo(Ladder ladder);

    public abstract void setElo(Ladder ladder, int elo);
}
