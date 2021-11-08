package country.pvp.practice.board;

import com.google.common.collect.Lists;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.QueueData;
import country.pvp.practice.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor
public class BoardProvider {


    public List<String> provide(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.GRAY + "Queuing: " + ChatColor.WHITE + 0);
                break;
            case QUEUING:
                QueueData queueData = player.getStateData(PlayerState.QUEUING);
                lines.add("Time: " + TimeUtil.formatTimeMillisToClock(TimeUtil.elapsed(queueData.getJoinTimeStamp())));
                if (queueData.isRanked()) {
                    lines.add("Elo Range: " + (1000 - queueData.getEloRangeFactor()) + "-" + (1000 + queueData.getEloRangeFactor()));
                }
                lines.add("Ladder: " + queueData.getLadderDisplayName());
                break;
        }

        lines.add(0, "");
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add("");

        return lines;
    }
}
