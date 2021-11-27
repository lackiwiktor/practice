package country.pvp.practice.board;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.message.MessageUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.PlayerQueueData;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardProvider {

    private static final String BAR = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------";

    private final QueueManager queueManager;
    private final MatchManager matchManager;

    public List<String> provide(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        if (player.isInQueue()) {
            PlayerQueueData queueData = player.getStateData();
            lines.add("  Time: " + TimeUtil.formatTimeMillisToClock(TimeUtil.elapsed(queueData.getJoinTimeStamp())));
            if (queueData.isRanked()) {
                int baseElo = player.getElo(queueData.getLadder());
                lines.add("  Elo Range: " + Math.max(baseElo - queueData.getEloRangeFactor(), 0) + "-" + (baseElo + queueData.getEloRangeFactor()));
            }
            lines.add("  Ladder: " + MessageUtil.color(queueData.getLadderDisplayName()));
        } else if (player.isInMatch()) {
            PlayerMatchData matchData = player.getStateData();
            Match match = matchData.getMatch();

            switch (match.getState()) {
                case COUNTDOWN:
                    lines.add(ChatColor.GRAY + "Opponent: " + ChatColor.WHITE + match.getOpponent(player).getName());
                case FIGHT:
                    lines.add(ChatColor.GRAY + "Your Ping: " + ChatColor.WHITE + player.getPing());
                    lines.add(ChatColor.GRAY + "Their Ping: " + ChatColor.WHITE + match.getOpponent(player).getPing());
                    break;
                case END:
                    lines.add(ChatColor.GRAY + "Winner: " + ChatColor.WHITE + (match.getWinner().isPresent() ? match.getWinner().get().getName() : "none"));
                    break;
            }
        } else {
            lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
            lines.add(ChatColor.GRAY + "Playing: " + ChatColor.WHITE + matchManager.getPlayersInFightCount());
        }

        lines.add(0, BAR);
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add(BAR);

        return lines;
    }
}
