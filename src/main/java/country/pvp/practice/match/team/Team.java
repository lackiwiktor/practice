package country.pvp.practice.match.team;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.SessionMatchData;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.Location;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Team implements Recipient {

    public int size() {
        return getPlayers().size();
    }

    public SessionMatchData getMatchData(PlayerSession player) {
        return player.getStateData();
    }

    public boolean hasPlayer(PlayerSession player) {
        return getPlayers().contains(player);
    }

    public void setPlayersState(PlayerState state) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.setState(state);
        }
    }

    public void teleport(Location location) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.teleport(location);
        }
    }

    public void giveKits( Ladder ladder) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.giveKits(ladder);
        }
    }

    public void resetPlayers() {
        for (PlayerSession player : getOnlinePlayers()) {
            PlayerUtil.resetPlayer(player.getPlayer());
        }
    }

    public void setMatchData(Match match) {
        for (PlayerSession player : getPlayers()) {
            player.setState(PlayerState.IN_MATCH, new SessionMatchData(match));
        }
    }

    @Override
    public void receive(String message) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.receive(message);
        }
    }

    public boolean isAlive( PlayerSession player) {
        SessionMatchData matchData = getMatchData(player);

        return !matchData.isDead();
    }

    public boolean hasDisconnected( PlayerSession player) {
        SessionMatchData matchData = getMatchData(player);

        return matchData.isDisconnected();
    }

    public Set<PlayerSession> getAlivePlayers() {
        return getPlayers().stream()
                .filter(this::isAlive)
                .collect(Collectors.toSet());
    }

    public boolean isDead() {
        return getAlivePlayers().size() == 0;
    }

    public List<PlayerSession> getOnlinePlayers() {
        return getPlayers().stream().filter(it -> !hasDisconnected(it)).collect(Collectors.toList());
    }

    public abstract String getName();

    public abstract List<PlayerSession> getPlayers();

    public abstract int getPing();
}
