package country.pvp.practice.board;

import com.google.common.collect.Lists;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

public class BoardProvider {

    public List<String> provide(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        switch (player.getState()) {
            case IN_LOBBY:
                lines.add(ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getServer().getOnlinePlayers().size());
                lines.add(ChatColor.GRAY + "Queuing: " + ChatColor.WHITE + 0);
                break;
        }

        lines.add(0, "");
        lines.add("");
        lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.pvp.country");
        lines.add("");

        return lines;
    }
}
