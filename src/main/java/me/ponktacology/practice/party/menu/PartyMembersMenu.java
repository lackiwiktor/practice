package me.ponktacology.practice.party.menu;

import com.google.common.collect.Maps;
import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartyMembersMenu extends Menu {

    private final Party party;

    @Override
    public String getTitle(Player player) {
        return "Party Members";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (PracticePlayer session : party.getMembers().stream().sorted((o1, o2) -> {
            if (o1.isPartyLeader()) return Integer.MIN_VALUE;
            if (o2.isPartyLeader()) return Integer.MAX_VALUE;

            return o1.getName().compareTo(o2.getName());
        }).collect(Collectors.toList())) {
            buttons.put(buttons.size(), new MemberButton(session));
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @RequiredArgsConstructor
    private class MemberButton extends Button {

        private final PracticePlayer session;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .skull(session.getName())
                    .name(ChatColor.YELLOW + session.getName())
                    .build();
        }
    }
}
