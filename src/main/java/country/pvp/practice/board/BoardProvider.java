package country.pvp.practice.board;

import com.google.common.collect.Lists;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.MatchType;
import country.pvp.practice.queue.Queue;
import country.pvp.practice.queue.QueueData;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor
public class BoardProvider {

    private final QueueManager queueManager;

    public List<String> provide(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.GRAY + "Queuing: " + ChatColor.WHITE + 0);
                break;
            case QUEUING:
                Queue queue = queueManager.getQueue(player);
                QueueData<?> queueData = queue.get(player);
                lines.add("Time: " + TimeUtil.elapsed(queueData.getJoinTimeStamp()));
                if (queue.getType() == MatchType.RANKED) {
                    lines.add("Elo Range: " + (1000 - queueData.getEloRangeFactor()) + "-" + (1000 + queueData.getEloRangeFactor()));
                }
                lines.add("Ladder: " + queue.getLadder().getDisplayName());
                break;
        }

        lines.add(0, "");
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add("");

        return lines;
    }
}
