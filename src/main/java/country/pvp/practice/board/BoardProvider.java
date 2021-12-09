package country.pvp.practice.board;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.message.Bars;
import country.pvp.practice.message.MessageUtil;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.SessionQueueData;
import country.pvp.practice.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardProvider {

    private final MatchManager matchManager;

    public List<String> provide( PlayerSession player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.GRAY + "Playing: " + ChatColor.WHITE + matchManager.getPlayersInFightCount());
                break;
            case QUEUING:
                SessionQueueData queueData = player.getStateData();
                lines.add("  Time: " + TimeUtil.formatTimeMillisToClock(TimeUtil.elapsed(queueData.getJoinTimeStamp())));
                if (queueData.isRanked()) {
                    int baseElo = player.getElo(queueData.getLadder());
                    lines.add("  Elo Range: " + Math.max(baseElo - queueData.getEloRangeFactor(), 0) + "-" + (baseElo + queueData.getEloRangeFactor()));
                }
                lines.add("  Ladder: " + MessageUtil.color(queueData.getLadderDisplayName()));
                break;
            case IN_MATCH:
                Match match = player.getCurrentMatch();
                lines.addAll(match.getBoard(player));
                break;
            case SPECTATING:
            case EDITING_KIT:
                break;
        }

        lines.add(0, Bars.SB_BAR);
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add(Bars.SB_BAR);

        return lines;
    }
}
