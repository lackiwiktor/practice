package country.pvp.practice.kit.editor;

import com.google.common.collect.Maps;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitEditorMenu extends Menu {

    private final PracticePlayer practicePlayer;
    private final Ladder ladder;

    @Override
    public String getTitle(Player player) {
        return "Kit editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (int i = 0; i < 7; i++) {
            NamedKit kit = practicePlayer.getKit(ladder, i);

            final int index = i;
            final int slot = index + 1;
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.CHEST).name("Save kit " + ladder.getName() + " #" + slot).build();
                }

                @Override
                public boolean shouldUpdate(Player player, ClickType clickType) {
                    if (clickType.isLeftClick()) {
                        NamedKit newKit = kit;

                        if (newKit == null) {
                            newKit = new NamedKit(ladder.getName() + " #" + slot);
                            newKit.setArmor(ladder.getKit().getArmor());
                            newKit.setInventory(ladder.getKit().getInventory());
                            practicePlayer.setKit(ladder, newKit, index);
                        }

                        newKit.setArmor(player.getInventory().getArmorContents());
                        newKit.setInventory(player.getInventory().getContents());
                        return true;
                    }

                    return false;
                }
            });

            if (kit != null) {
                buttons.put(slot + 9, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.BOOK).name("Load kit " + ladder.getName() + " #" + slot).build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            kit.apply(practicePlayer);
                            player.getOpenInventory().close();
                        }
                    }
                });

                buttons.put(slot + 18, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.REDSTONE_BLOCK).name("Remove kit " + ladder.getName() + " #" + slot).build();
                    }

                    @Override
                    public boolean shouldUpdate(Player player, ClickType clickType) {
                        if (clickType.isLeftClick()) {
                            practicePlayer.removeKit(ladder, index);
                            return true;
                        }

                        return false;
                    }
                });
            }
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
