package country.pvp.practice.queue.menu;

import com.google.common.collect.Maps;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.Queue;
import country.pvp.practice.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends Menu {

    private final @NotNull QueueManager queueManager;
    private final @NotNull MatchManager matchManager;

    private final boolean ranked;
    private final @NotNull PracticePlayer practicePlayer;

    @Override
    public @NotNull String getTitle(Player player) {
        return "Select kit...";
    }

    @Override
    public @NotNull Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Queue queue : queueManager.getQueues(ranked)) {
            buttons.put(buttons.size(), new QueueButton(matchManager, practicePlayer, queue));
        }

        return buttons;
    }
}
