package country.pvp.practice.kit.editor;

import com.google.common.collect.Maps;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RequiredArgsConstructor
public class KitEditorChest extends Menu {

    private final @NotNull Ladder ladder;

    @Override
    public @NotNull String getTitle(Player player) {
        return "Editor items";
    }

    @Override
    public @NotNull Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        ItemStack[] items = ladder.getEditorItems();

        for (ItemStack item : items) {
            buttons.put(buttons.size(), new KitEditorChestButton(item));
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    @RequiredArgsConstructor
    public static class KitEditorChestButton extends Button {

        private final @NotNull ItemStack item;

        @Override
        public @NotNull ItemStack getButtonItem(Player player) {
            return item;
        }

        @Override
        public boolean shouldCancel(Player player, ClickType clickType) {
            return false;
        }
    }
}
