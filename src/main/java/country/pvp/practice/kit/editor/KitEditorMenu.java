package country.pvp.practice.kit.editor;

import com.google.common.collect.Maps;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitEditorMenu extends Menu {

    private final PlayerService playerService;

    private final PlayerSession playerSession;
    private final Ladder ladder;

    @Override
    public String getTitle(Player player) {
        return "Kit editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (int i = 0; i < 7; i++) {
            buttons.put(i + 1, new SaveKitButton(playerService, playerSession, ladder, i));

            if (playerSession.getKit(ladder, i) != null) {
                buttons.put(i + 10, new LoadKitButton(playerSession, ladder, i));
                buttons.put(i + 19, new RemoveKitButton(playerSession, ladder, i));
            }
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }


    private static class SaveKitButton extends Button {

        private final PlayerService playerService;

        private final PlayerSession playerSession;
        private final Ladder ladder;
        private final int index;
        private final int slot;

        public SaveKitButton(PlayerService playerService, PlayerSession playerSession, Ladder ladder, int index) {
            this.playerService = playerService;
            this.playerSession = playerSession;
            this.ladder = ladder;
            this.index = index;
            this.slot = index + 1;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.CHEST).name("&eSave kit &d" + ladder.getName() + " #" + slot).build();
        }

        @Override
        public boolean shouldUpdate( Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                NamedKit newKit = playerSession.getKit(ladder, index);

                if (newKit == null) {
                    newKit = new NamedKit(ladder.getName() + " #" + slot);
                    newKit.setArmor(ladder.getKit().getArmor());
                    newKit.setInventory(ladder.getKit().getInventory());
                    playerSession.setKit(ladder, newKit, index);
                }

                newKit.setArmor(player.getInventory().getArmorContents());
                newKit.setInventory(player.getInventory().getContents());

                playerService.saveAsync(playerSession);
                return true;
            }

            return false;
        }
    }

    private static class LoadKitButton extends Button {

        private final PlayerSession playerSession;
        private final Ladder ladder;
        private final int index;
        private final int slot;

        public LoadKitButton(PlayerSession playerSession, Ladder ladder, int index) {
            this.playerSession = playerSession;
            this.ladder = ladder;
            this.index = index;
            this.slot = index + 1;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK).name("&eLoad kit &d" + ladder.getName() + " #" + slot).build();
        }

        @Override
        public void clicked( Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                NamedKit kit = playerSession.getKit(ladder, index);
                kit.apply(playerSession);
                player.getOpenInventory().close();
            }
        }
    }

    private static class RemoveKitButton extends Button {

        private final PlayerSession playerSession;
        private final Ladder ladder;
        private final int index;
        private final int slot;

        public RemoveKitButton(PlayerSession playerSession, Ladder ladder, int index) {
            this.playerSession = playerSession;
            this.ladder = ladder;
            this.index = index;
            this.slot = index + 1;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.REDSTONE).name("&eRemove kit &d" + ladder.getName() + " #" + slot).build();
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                playerSession.removeKit(ladder, index);
                return true;
            }

            return false;
        }
    }
}
