package country.pvp.practice.match;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MatchMenu extends Menu {

    private final MatchManager matchManager;

    @Override
    public String getTitle(Player player) {
        return "Match List";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Match match : matchManager.getAll()) {
            buttons.put(buttons.size(), new MatchOverviewButton(match));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    static class MatchOverviewButton extends Button {

        private final Match match;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();

            match.getTeamA().getPlayers().forEach(it -> lore.add(formatLore(it)));
            lore.add(ChatColor.GRAY + "      vs");
            match.getTeamB().getPlayers().forEach(it -> lore.add(formatLore(it)));

            return new ItemBuilder(match.isRanked() ? Material.DIAMOND_SWORD : Material.IRON_SWORD)
                    .amount(match.getPlayersCount())
                    .name(ChatColor.YELLOW.toString()
                            .concat(match.getTeamADisplayName()
                                    .concat(ChatColor.WHITE.toString())
                                    .concat(" vs ")
                                    .concat(ChatColor.YELLOW.toString()))
                            .concat(match.getTeamBDisplayName()))
                    .lore(lore)
                    .build();
        }

        private String formatLore( PlayerSession player) {
            String lore;
            if (match.isAlive(player)) {
                lore = ChatColor.WHITE.toString()
                        .concat(player.getName());
            } else {
                lore = ChatColor.STRIKETHROUGH.toString()
                        .concat(player.getName());
            }

            if (match.isRanked()) {
                lore += match.isRanked() ?
                        ChatColor.GRAY.toString().concat("(" + ChatColor.LIGHT_PURPLE + player.getElo(match.getLadder()) + ChatColor.GRAY.toString().concat(")"))
                        : "";
            }

            return lore;
        }
    }
}
