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
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RequiredArgsConstructor
public class KitChooseMenu extends Menu {

    private final @NotNull LadderManager ladderManager;
    private final @NotNull KitEditorService kitEditorService;

    private final @NotNull PracticePlayer practicePlayer;

    @Override
    public @NotNull String getTitle(Player player) {
        return "Choose kit";
    }

    @Override
    public @NotNull Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Ladder ladder : ladderManager.getAll()) {
            buttons.put(buttons.size(), new KitChoiceButton(kitEditorService, practicePlayer, ladder));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    public static class KitChoiceButton extends Button {

        private final @NotNull KitEditorService kitEditorService;

        private final @NotNull PracticePlayer practicePlayer;
        private final @NotNull Ladder ladder;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ladder.getIcon();
        }

        @Override
        public void clicked(Player player, @NotNull ClickType clickType) {
            if (clickType.isLeftClick()) {
                kitEditorService.moveToEditor(practicePlayer, ladder);
            }
        }
    }
}
