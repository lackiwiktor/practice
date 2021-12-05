package country.pvp.practice.itembar;

import country.pvp.practice.player.PlayerSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Data
public class ItemBar {

    private final ItemBarItem[] items;

    ItemBar(ItemBarItem... items) {
        this.items = items;
    }

    void apply(PlayerSession player) {
        player.setBar(bar());
    }

    ItemStack[] bar() {
        return Arrays.stream(items).map(it -> it == null ? new ItemStack(Material.AIR) : it.getItem()).toArray(ItemStack[]::new);
    }

    @Nullable
    static ItemBar get(PlayerSession player) {
        ItemBarItem[] items = new ItemBarItem[9];

        switch (player.getState()) {
            case IN_LOBBY:

                items[0] = Items.UNRANKED.get();
                items[1] = Items.RANKED.get();
                items[2] = player.hasRematch() ? Items.REMATCH.get() : null;
                items[4] = Items.KIT_EDITOR.get();
                items[5] = Items.CREATE_PARTY.get();

                return new ItemBar(items);
            case QUEUING:
                return new ItemBar(Items.LEAVE_QUEUE.get());
            case SPECTATING:
                return new ItemBar(Items.STOP_SPECTATING.get());
        }

        return null;
    }

    @RequiredArgsConstructor
    private enum Items {

        UNRANKED(new ItemBarItem(new ItemBuilder(Material.IRON_SWORD).name("&7Unranked").unbreakable().build(),
                (practicePlayer -> practicePlayer.chat("/unranked")))),
        RANKED(new ItemBarItem(new ItemBuilder(Material.DIAMOND_SWORD).name("&6Ranked").unbreakable().build(),
                (practicePlayer -> practicePlayer.chat("/ranked")))),
        REMATCH(new ItemBarItem(new ItemBuilder(Material.BLAZE_ROD).name("&eRematch").unbreakable().build(),
                (practicePlayer -> practicePlayer.chat("/duel " + practicePlayer.getRematchPlayer().getName() + " " + practicePlayer.getRematchLadder().getName())))),
        KIT_EDITOR(new ItemBarItem(new ItemBuilder(Material.ANVIL).name("&eKit Editor").unbreakable().build(),
                (practicePlayer -> practicePlayer.chat("/kiteditor")))),
        CREATE_PARTY(new ItemBarItem(new ItemBuilder(Material.NAME_TAG).name("&bCreate Party").build(),
                (practicePlayer -> practicePlayer.chat("/party create")))),
        LEAVE_QUEUE(new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cLeave Queue").unbreakable().build(),
                (practicePlayer -> practicePlayer.removeFromQueue(true)))),
        STOP_SPECTATING(new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("&cStop spectating").unbreakable().build(),
                (practicePlayer -> practicePlayer.stopSpectating(true))));

        private final ItemBarItem item;

        public ItemBarItem get() {
            return item;
        }
    }
}
