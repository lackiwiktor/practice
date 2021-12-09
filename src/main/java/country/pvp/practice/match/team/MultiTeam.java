package country.pvp.practice.match.team;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.SessionMatchData;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiTeam extends Team {

    private final List<PlayerSession> players = Lists.newArrayList();

    MultiTeam(Set<PlayerSession> players) {
        this.players.addAll(players);
    }

    public static MultiTeam of(Set<PlayerSession> players) {
        return new MultiTeam(players);
    }

    @Override
    public String getName() {
        Preconditions.checkArgument(players.size() > 0);
        return players.get(0).getName();
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public int getPing() {
        return -1;
    }

    @Override
    public boolean isDead() {
        return getOnlinePlayers().stream().noneMatch(this::isAlive);
    }

    @Override
    public void clearRematchData() {
        for (PlayerSession player : getOnlinePlayers()) {
            player.setRematchData(null);
        }
    }

    @Override
    public void createMatchSession(Match match) {
        for (PlayerSession session : getOnlinePlayers()) {
            session.setState(PlayerState.IN_MATCH, new SessionMatchData(match));
        }
    }

    @Override
    public SessionMatchData getMatchSession(PlayerSession player) {
        return player.getStateData();
    }

    @Override
    public boolean hasPlayer(PlayerSession player) {
        return players.contains(player);
    }

    @Override
    public void teleport(Location location) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.teleport(location);
        }
    }

    @Override
    public void giveKits(Ladder ladder) {
        for (PlayerSession player : getOnlinePlayers()) {
            player.giveKits(ladder);
        }
    }

    @Override
    public void reset() {
        for (PlayerSession player : getOnlinePlayers()) {
            PlayerUtil.resetPlayer(player.getPlayer());
        }
    }

    @Override
    public void receive(String message) {
        for (PlayerSession session : getOnlinePlayers()) {
            if (session.isOnline())
                session.receive(message);
        }
    }

    @Override
    public List<PlayerSession> getOnlinePlayers() {
        return players.stream().filter(PlayerSession::isOnline).collect(Collectors.toList());
    }

    @Override
    public List<PlayerSession> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }
}
