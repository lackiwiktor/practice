package country.pvp.practice.party.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.type.MultiTeam;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.party.Party;
import country.pvp.practice.party.PartyEvent;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class PartyEventMenu extends Menu {

    private final MatchProvider matchProvider;
    private final KitChooseMenuProvider kitChooseMenuProvider;
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


            kitChooseMenuProvider.provide((ladder) -> {
                List<PlayerSession> players = Lists.newArrayList(party.getMembers());

                switch (event) {
                    case FFA:
                        SoloTeam[] soloTeams = new SoloTeam[players.size()];
                        for (int i = 0; i < players.size(); i++) {
                            soloTeams[i] = SoloTeam.of(players.get(i));
                        }

                        matchProvider.provide(ladder, soloTeams).init();
                        break;
                    case SPLIT:
                        Set<PlayerSession> playersA = Sets.newHashSet();
                        Set<PlayerSession> playersB = Sets.newHashSet();

                        for (int i = 0; i < players.size(); i++) {
                            if (i % 2 == 0) playersA.add(players.get(i));
                            else playersB.add(players.get(i));
                        }

                        matchProvider.provide(ladder, false, true, MultiTeam.of(playersA), MultiTeam.of(playersB)).init();
                        break;
                }
            }).openMenu(player);
        }
    }
}
