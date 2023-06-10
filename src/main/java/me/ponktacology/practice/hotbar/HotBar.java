package me.ponktacology.practice.hotbar;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.follow.FollowService;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HotBar {

  public final ItemStack[] items;

  private HotBar(HotBarItem... items) {
    this.items = ItemStackUtil.convertNullToAirAndCloneItems(items);
  }

  static @Nullable HotBarItem getMatchingItemBarItem(ItemStack item) {
    Items hotBarItem = Items.getMatchingItem(item);
    if (hotBarItem == null) return null;
    return hotBarItem.item;
  }

  @Nullable
  static HotBar get(Player player) {
    switch (player.getState()) {
      case IN_LOBBY:
        HotBarItem[] items = new HotBarItem[9];
        FollowService followService = Practice.getService(FollowService.class);
        PartyService partyService = Practice.getService(PartyService.class);
        if (followService.isFollowing(player)) {
          items[0] = Items.STOP_FOLLOWING.item;
        } else if (partyService.hasParty(player)) {
          items[0] = Items.PARTY_MEMBERS.item;

          Party party = partyService.getPlayerParty(player);
          if (party.isLeader(player)) {
            items[1] = Items.PARTY_EVENT.item;
          }

          items[7] = Items.KIT_EDITOR.item;
          items[8] = Items.PARTY_LEAVE.item;
        } else {
          items[0] = Items.UNRANKED.item;
          items[1] = Items.RANKED.item;
          items[2] = player.hasRematch() ? Items.REMATCH.item : null;
          items[4] = Items.CREATE_PARTY.item;
          items[7] = Items.KIT_EDITOR.item;
        }

        return new HotBar(items);
      case QUEUING:
        return new HotBar(Items.LEAVE_QUEUE.item);
      case SPECTATING:
        return new HotBar(Items.STOP_SPECTATING.item);
      default:
        return null;
    }
  }

  @RequiredArgsConstructor
  private enum Items {
    UNRANKED(new HotBarItem(Material.IRON_SWORD, "&7Unranked", "unranked")),
    RANKED(new HotBarItem(Material.DIAMOND_SWORD, "&6Ranked", "ranked")),
    REMATCH(new HotBarItem(Material.BLAZE_POWDER, "&eRematch", "rematch")),
    KIT_EDITOR(new HotBarItem(Material.ANVIL, "&eKit Editor", "kiteditor")),
    CREATE_PARTY(new HotBarItem(Material.NAME_TAG, "&bCreate Party", "party create")),
    PARTY_EVENT(new HotBarItem(Material.IRON_AXE, "&aParty Event", "party event")),
    PARTY_MEMBERS(new HotBarItem(Material.SKULL_ITEM, "&aParty Members", "party members")),
    PARTY_LEAVE(new HotBarItem(Material.REDSTONE, "&cLeave Party", "party leave")),
    LEAVE_QUEUE(new HotBarItem(Material.REDSTONE, "&cLeave Queue", "leavequeue")),
    STOP_SPECTATING(new HotBarItem(Material.REDSTONE, "&cStop spectating", "stopspectating")),
    STOP_FOLLOWING(new HotBarItem(Material.REDSTONE, "&cStop followiung", "unfollow"));

    private final HotBarItem item;

    private static @Nullable Items getMatchingItem(ItemStack item) {
      for (Items items : Items.values()) {
        if (items.item.getItem().isSimilar(item)) return items;
        continue;
      }
      return null;
    }
  }
}
