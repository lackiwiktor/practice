package country.pvp.practice.kit;

import com.google.common.collect.Maps;
import country.pvp.practice.util.data.Callback;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.util.menu.Button;
import country.pvp.practice.util.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitChooseMenu extends Menu {

    private final LadderManager ladderManager;
    private final Callback<Ladder> callback;

    @Override
    public String getTitle(Player player) {
        return "Choose kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Ladder ladder : ladderManager.getAll()) {
            buttons.put(buttons.size(), new KitChoiceButton(ladder));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class KitChoiceButton extends Button {

        private final Ladder ladder;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ladder.getIcon();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                callback.call(ladder);
            }
        }
    }
}
