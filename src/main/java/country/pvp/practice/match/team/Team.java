package country.pvp.practice.match.team;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.SessionMatchData;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Recipient;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Team implements Recipient {

    public boolean isAlive(PlayerSession player) {
        SessionMatchData matchData = getMatchSession(player);

        return !matchData.isDead();
    }

    public boolean hasDisconnected(PlayerSession player) {
        SessionMatchData matchData = getMatchSession(player);

        return matchData.isDisconnected();
    }

    public abstract String getName();

    public abstract int size();

    public abstract SessionMatchData getMatchSession(PlayerSession playerSession);

    public abstract boolean hasPlayer(PlayerSession playerSession);

    public abstract int getPing();

    public abstract boolean isDead();

    public abstract void clearRematchData();

    public abstract void createMatchSession(Match match);

    public abstract void teleport(Location location);

    public abstract void giveKits(Ladder ladder);

    public abstract void reset();

    public List<PlayerSession> getOnlinePlayers() {
        return getPlayers()
                .stream()
                .filter(PlayerSession::isOnline)
                .collect(Collectors.toList());
    }

    public List<PlayerSession> getAlivePlayers() {
        return getPlayers()
                .stream()
                .filter(this::isAlive)
                .collect(Collectors.toList());
    }

    public int getAlivePlayersCount() {
        return getAlivePlayers().size();
    }

    public abstract List<PlayerSession> getPlayers();
}
