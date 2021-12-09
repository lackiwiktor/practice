package country.pvp.practice.match.team;

import com.google.common.base.Preconditions;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.SessionMatchData;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.data.PlayerState;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data(staticConstructor = "of")
public class SoloTeam extends Team implements Ranked {

    private final PlayerSession playerSession;

    @Override
    public int getElo(Ladder ladder) {
        return playerSession.getElo(ladder);
    }

    @Override
    public void setElo(Ladder ladder, int elo) {
        playerSession.setElo(ladder, elo);
    }

    @Override
    public String getName() {
        return playerSession.getName();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public SessionMatchData getMatchSession(PlayerSession playerSession) {
        return playerSession.getStateData();
    }

    @Override
    public boolean hasPlayer(PlayerSession playerSession) {
        return this.playerSession.equals(playerSession);
    }

    @Override
    public int getPing() {
        return playerSession.getPing();
    }

    @Override
    public boolean isDead() {
        SessionMatchData matchData = getMatchSession(playerSession);
        Preconditions.checkNotNull(matchData, "data");

        return matchData.isDead();
    }

    @Override
    public void clearRematchData() {
        playerSession.setRematchData(null);
    }

    @Override
    public void createMatchSession(Match match) {
        playerSession.setState(PlayerState.IN_MATCH, new SessionMatchData(match));
    }

    @Override
    public void teleport(Location location) {
        playerSession.teleport(location);
    }

    @Override
    public void giveKits(Ladder ladder) {
        playerSession.giveKits(ladder);
    }

    @Override
    public void reset() {
        Player bukkitPlayer = playerSession.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "online");

        PlayerUtil.resetPlayer(bukkitPlayer);
    }

    @Override
    public List<PlayerSession> getPlayers() {
        return Collections.singletonList(playerSession);
    }

    @Override
    public void receive(String message) {
        if (playerSession.isOnline())
            playerSession.receive(message);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoloTeam soloTeam = (SoloTeam) o;
        return Objects.equals(playerSession, soloTeam.playerSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerSession);
    }
}
