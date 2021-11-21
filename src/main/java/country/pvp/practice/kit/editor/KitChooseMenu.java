package country.pvp.practice.kit.editor;

import com.google.common.collect.Maps;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitChooseMenu extends Menu {

    private final PracticePlayer practicePlayer;
    private final LadderManager ladderManager;
    private final KitEditorService kitEditorService;

    @Override
    public String getTitle(Player player) {
        return "Choose kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Ladder ladder : ladderManager.getAll()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ladder.getIcon();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        kitEditorService.moveToEditor(practicePlayer, ladder);
                    }
                }
            });
        }

        return buttons;
    }
}
