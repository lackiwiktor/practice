package me.ponktacology.practice.party.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.kit.KitChooseMenu;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.MultiTeam;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyEvent;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class PartyEventMenu extends Menu {

    private final Party party;

    @Override
    public String getTitle(Player player) {
        return "Host Party Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(11, new EventButton(PartyEvent.FFA));
        buttons.put(15, new EventButton(PartyEvent.SPLIT));

        return buttons;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @RequiredArgsConstructor
    private class EventButton extends Button {

        private final PartyEvent event;

        @Override
        public ItemStack getButtonItem(Player player) {
            return event.getIcon();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!clickType.isLeftClick()) return;

            new KitChooseMenu((ladder) -> {
                List<PracticePlayer> players = Lists.newArrayList(party.getMembers());

                if (party.size() < 2) {
                    Messenger.messageError(player, "You must have at least 2 players in your party in order to start a party event.");
                    player.closeInventory();
                    return;
                }

                switch (event) {
                    case FFA:
                        SoloTeam[] soloTeams = new SoloTeam[players.size()];
                        for (int i = 0; i < players.size(); i++) {
                            soloTeams[i] = SoloTeam.of(players.get(i));
                        }

                        Practice.getService(MatchService.class).start(ladder, soloTeams);
                        break;
                    case SPLIT:
                        Set<PracticePlayer> playersA = Sets.newHashSet();
                        Set<PracticePlayer> playersB = Sets.newHashSet();

                        for (int i = 0; i < players.size(); i++) {
                            if (i % 2 == 0) playersA.add(players.get(i));
                            else playersB.add(players.get(i));
                        }

                        Practice.getService(MatchService.class).start(ladder, false, true, MultiTeam.of(playersA), MultiTeam.of(playersB));
                        break;
                }
            }).openMenu(player);
        }
    }
}
