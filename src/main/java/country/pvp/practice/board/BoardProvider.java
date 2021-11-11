package country.pvp.practice.board;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.message.MessageUtil;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.QueueData;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardProvider {

    private final QueueManager queueManager;

    public List<String> provide(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.GRAY + "Queuing: " + ChatColor.WHITE + queueManager.getPlayersInQueueCount());
                break;
            case QUEUING:
                QueueData queueData = player.getStateData(PlayerState.QUEUING);
                if (queueData == null) break;
                lines.add("  Time: " + TimeUtil.formatTimeMillisToClock(TimeUtil.elapsed(queueData.getJoinTimeStamp())));
                if (queueData.isRanked()) {
                    lines.add("  Elo Range: " + (1000 - queueData.getEloRangeFactor()) + "-" + (1000 + queueData.getEloRangeFactor()));
                }
                lines.add("  Ladder: " + MessageUtil.color(queueData.getLadderDisplayName()));
                break;
            case IN_MATCH:
                MatchData matchData = player.getStateData(PlayerState.IN_MATCH);
                if (matchData == null) break;
                Match match = matchData.getMatch();
                lines.add("State: " + match.getState());
                break;
        }

        lines.add(0, "");
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add("");

        return lines;
    }
}
