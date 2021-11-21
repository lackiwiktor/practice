package country.pvp.practice.kit.editor;

import com.google.common.collect.Maps;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitEditorChest extends Menu {

    private final Ladder ladder;

    @Override
    public String getTitle(Player player) {
        return "Editor items";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        ItemStack[] items = ladder.getEditorItems();

        for (ItemStack item : items) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return item;
                }

                @Override
                public boolean shouldCancel(Player player, ClickType clickType) {
                    return false;
                }
            });
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }
}
