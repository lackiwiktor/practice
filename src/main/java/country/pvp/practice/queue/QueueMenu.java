package country.pvp.practice.queue;

import com.google.common.collect.Maps;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.team.PlayerTeam;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends Menu {

    private final QueueManager queueManager;
    private final MatchType type;
    private final PlayerTeam team;

    @Override
    public String getTitle(Player player) {
        return "Select kit...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (SoloQueue queue : queueManager.getSoloQueues(type)) {
            buttons.put(buttons.size(), new QueueButton(team, queue, queueManager));
        }

        return buttons;
    }
}
