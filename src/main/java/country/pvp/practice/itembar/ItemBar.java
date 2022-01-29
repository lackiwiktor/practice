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

    @Nullable static ItemBar get(PlayerSession player) {
        switch (player.getState()) {
            case IN_LOBBY:
                ItemBarItem[] items = new ItemBarItem[9];

                if (player.hasParty()) {
                    items[0] = Items.PARTY_MEMBERS.get();

                    if (player.isPartyLeader()) {
                        items[1] = Items.PARTY_EVENT.get();
                    }

                    items[8] = Items.PARTY_LEAVE.get();
                } else {
                    items[0] = Items.UNRANKED.get();
                    items[1] = Items.RANKED.get();
                    items[2] = player.hasRematch() ? Items.REMATCH.get() : null;
                    items[4] = Items.CREATE_PARTY.get();
                }

                items[7] = Items.KIT_EDITOR.get();

                return new ItemBar(items);
            case QUEUING:
                return new ItemBar(Items.LEAVE_QUEUE.get());
            case SPECTATING:
                return new ItemBar(Items.STOP_SPECTATING.get());
            default:
                return null;
        }
    }

    @RequiredArgsConstructor
    private enum Items {

        UNRANKED(new ItemBarItem(new ItemBuilder(Material.IRON_SWORD)
                .name("&7Unranked")
                .hideAll()
                .build(),
                (practicePlayer -> practicePlayer.runCommand("unranked")))),
        RANKED(new ItemBarItem(new ItemBuilder(Material.DIAMOND_SWORD)
                .name("&6Ranked")
                .hideAll()
                .build(),
                (practicePlayer -> practicePlayer.runCommand("ranked")))),
        REMATCH(new ItemBarItem(new ItemBuilder(Material.BLAZE_POWDER)
                .name("&eRematch")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("duel " + practicePlayer.getRematchPlayer().getName() + " " + practicePlayer.getRematchLadder().getName())))),
        KIT_EDITOR(new ItemBarItem(new ItemBuilder(Material.ANVIL)
                .name("&eKit Editor")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("kiteditor")))),
        CREATE_PARTY(new ItemBarItem(new ItemBuilder(Material.NAME_TAG)
                .name("&bCreate Party")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("party create")))),
        PARTY_EVENT(new ItemBarItem(new ItemBuilder(Material.IRON_AXE)
                .name("&aParty Event")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("party event")))),
        PARTY_MEMBERS(new ItemBarItem(new ItemBuilder(Material.SKULL_ITEM)
                .name("&aParty Members")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("party members")))),
        PARTY_LEAVE(new ItemBarItem(new ItemBuilder(Material.REDSTONE)
                .name("&cLeave Party")
                .build(),
                (practicePlayer -> practicePlayer.runCommand("party leave")))),
        LEAVE_QUEUE(new ItemBarItem(new ItemBuilder(Material.REDSTONE)
                .name("&cLeave Queue")
                .build(),
                (practicePlayer -> practicePlayer.removeFromQueue(true)))),
        STOP_SPECTATING(new ItemBarItem(new ItemBuilder(Material.REDSTONE)
                .name("&cStop spectating")
                .build(),
                (practicePlayer -> practicePlayer.stopSpectating(true))));

        private final ItemBarItem item;

        public ItemBarItem get() {
            return item;
        }
    }
}
