package country.pvp.practice.match.team;

import com.google.common.collect.Sets;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.SessionMatchData;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyTeam extends Team {

    private final Party party;
    private final Set<PlayerSession> players = Sets.newConcurrentHashSet();

    private PartyTeam(Party party) {
        this.party = party;
        players.addAll(party.getMembers());
    }

    public static PartyTeam of(Party party) {
        return new PartyTeam(party);
    }

    @Override
    public String getName() {
        return party.getName();
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public int getPing() {
        return -1;
    }


    public boolean isDead() {
        return getOnlinePlayers().stream().noneMatch(this::isAlive);
    }

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
