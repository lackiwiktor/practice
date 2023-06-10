package me.ponktacology.practice.match.listener;

import com.google.common.collect.ImmutableList;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.event.MatchEndEvent;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Bars;
import me.ponktacology.practice.util.message.Messenger;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class MatchEndingMessageListener implements Listener {

  @EventHandler
  public void onMatchEnd(MatchEndEvent event) {
    Match match = event.getMatch();
    BaseComponent[][] matchResultComponents =
        sendMatchComponent(
            match.getWinner(),
            match.getLosers(),
            match.getSpectators(),
            match.getInventorySnapshotTracker().get());
    for (PracticePlayer player : match.getOnlinePlayers()) {
      Messenger.message(player, Bars.CHAT_BAR);
      for (BaseComponent[] components : matchResultComponents) {
        if (components == null) continue;
        player.sendComponent(components);
      }
      Messenger.message(player, Bars.CHAT_BAR);
    }
  }

  private BaseComponent[][] sendMatchComponent(
      Team winner,
      List<Team> losers,
      List<PracticePlayer> spectators,
      Map<PracticePlayer, InventorySnapshot> snapshots) {
    BaseComponent[] matchOverviewHeader =
        TextComponent.fromLegacyText(Messages.MATCH_RESULT_OVERVIEW.get());

    BaseComponent[] matchOverviewPlayerNames =
        TextComponent.fromLegacyText(Messages.MATCH_RESULT_OVERVIEW_WINNER.get());

    if (winner != null) {
      BaseComponent[] winnerComponents = getComponents(snapshots, ImmutableList.of(winner));
      for (int i = 0; i < winnerComponents.length; i++) {
        matchOverviewPlayerNames =
            concatenate(matchOverviewPlayerNames, new BaseComponent[] {winnerComponents[i]});
        if (i < winnerComponents.length - 1) {
          matchOverviewPlayerNames =
              concatenate(
                  matchOverviewPlayerNames, TextComponent.fromLegacyText(ChatColor.GRAY + ", "));
        }
      }

      matchOverviewPlayerNames =
          concatenate(
              matchOverviewPlayerNames,
              TextComponent.fromLegacyText(Messages.MATCH_RESULT_OVERVIEW_SPLITTER.get()));
    }
    matchOverviewPlayerNames =
        concatenate(
            matchOverviewPlayerNames,
            TextComponent.fromLegacyText(Messages.MATCH_RESULT_OVERVIEW_LOSER.get()));

    BaseComponent[] loserComponents = getComponents(snapshots, losers);
    for (int i = 0; i < loserComponents.length; i++) {
      matchOverviewPlayerNames =
          concatenate(matchOverviewPlayerNames, new BaseComponent[] {loserComponents[i]});
      if (i < loserComponents.length - 1) {
        matchOverviewPlayerNames =
            concatenate(
                matchOverviewPlayerNames, TextComponent.fromLegacyText(ChatColor.GRAY + ", "));
      }
    }

    BaseComponent[] matchOverviewSpectators = null;
    if (!spectators.isEmpty()) {
      matchOverviewSpectators =
          TextComponent.fromLegacyText(Messages.MATCH_RESULT_SPECTATORS.get());

      for (int i = 0; i < spectators.size(); i++) {
        matchOverviewSpectators =
            concatenate(
                matchOverviewSpectators,
                TextComponent.fromLegacyText(ChatColor.YELLOW + spectators.get(i).getName()));
        if (i < spectators.size() - 1) {
          matchOverviewSpectators =
              concatenate(
                  matchOverviewSpectators, TextComponent.fromLegacyText(ChatColor.GRAY + ", "));
        }
      }
    }

    return new BaseComponent[][] {
      matchOverviewHeader, matchOverviewPlayerNames, matchOverviewSpectators
    };
  }

  private BaseComponent[] getComponents(
      Map<PracticePlayer, InventorySnapshot> snapshots, List<Team> teams) {
    BaseComponent[] components = new BaseComponent[0];

    for (Team team : teams) {
      for(PracticePlayer player : team.getPlayers()) {
      components = concatenate(components, new ComponentBuilder(player.getName())
              .color(net.md_5.bungee.api.ChatColor.YELLOW)
              .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewsnapshot " + snapshots.get(player).getId()))
              .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Messages.MATCH_RESULT_OVERVIEW_HOVER.match("{player}", player.getName()))))
              .create());
      }
    }

    return components;
  }

  private static  <T> T[] concatenate(T[] a, T[] b) {
    int aLen = a.length;
    int bLen = b.length;

    @SuppressWarnings("unchecked")
    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);

    return c;
  }
}
