package country.pvp.practice.board;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import country.pvp.practice.kit.editor.SessionEditingData;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.SessionQueueData;
import country.pvp.practice.util.TimeUtil;
import country.pvp.practice.util.message.Bars;
import country.pvp.practice.util.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class BoardProvider {

    private final MatchManager matchManager;

    public List<String> provide(PlayerSession player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.WHITE + "Online: " + ChatColor.YELLOW + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.WHITE + "Playing: " + ChatColor.YELLOW + matchManager.getPlayersInFightCount());
                lines.add("");
                break;
            case EDITING_KIT:
                SessionEditingData editingData = player.getStateData();
                lines.add(ChatColor.WHITE + "Online: " + ChatColor.YELLOW + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.WHITE + "Playing: " + ChatColor.YELLOW + matchManager.getPlayersInFightCount());
                lines.add("");
                lines.add(ChatColor.GRAY + "Kit Editor:");
                lines.add(ChatColor.WHITE + "  " + MessageUtil.color(editingData.getLadder().getDisplayName()));
                lines.add("");
                break;
            case QUEUING:
                SessionQueueData queueData = player.getStateData();
                lines.add(ChatColor.WHITE + "  Time: "  + ChatColor.YELLOW + TimeUtil.formatTimeMillisToClock(TimeUtil.elapsed(queueData.getJoinTimeStamp())));

                if (queueData.isRanked()) {
                    int baseElo = player.getElo(queueData.getLadder());
                    lines.add(ChatColor.WHITE + "  Elo Range: "  + ChatColor.YELLOW + Math.max(baseElo - queueData.getEloRangeFactor(), 0) + "-" + (baseElo + queueData.getEloRangeFactor()));
                }

                lines.add(ChatColor.WHITE + "  Ladder: "  + ChatColor.YELLOW + MessageUtil.color(queueData.getLadderDisplayName()));
                lines.add("");
                break;
            case IN_MATCH:
                Match match = player.getCurrentMatch();
                lines.addAll(match.getBoard(player));
                break;
            case SPECTATING:
                Match spectatingMatch = player.getCurrentlySpectatingMatch();
                lines.addAll(spectatingMatch.getBoard(player));
                break;
        }

        lines.add(0, Bars.SB_BAR);
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add(Bars.SB_BAR);

        return lines;
    }
}
