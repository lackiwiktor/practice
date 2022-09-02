package me.ponktacology.practice.match;

import com.google.common.collect.Lists;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MatchResults {

  private final Team winner;
  private final Team[] losers;
  private final Map<PracticePlayer, InventorySnapshot> snapshots;
  private final List<PracticePlayer> spectators;

  private static final TextColor PLAYER_COLOR = TextColor.color(255, 238, 43);
  private static final Component SEPARATOR = Component.text(", ", TextColor.color(16777215));

  Component createMatchResultMessage() {
    TextComponent.Builder matchResultOverview =
        Component.text(Messages.MATCH_RESULT_OVERVIEW.get()).toBuilder();

    matchResultOverview.append(Component.newline());
    matchResultOverview.append(Component.text(Messages.MATCH_RESULT_OVERVIEW_WINNER.get()));

    List<Component> winnerComponents = getComponents(winner);
    List<Component> loserComponents = getComponents(losers);

    for (int i = 0; i < winnerComponents.size(); i++) {
      matchResultOverview.append(winnerComponents.get(i));
      if (i < winnerComponents.size() - 1) {
        matchResultOverview.append(SEPARATOR);
      }
    }

    matchResultOverview.append(Component.text(Messages.MATCH_RESULT_OVERVIEW_SPLITTER.get()));
    matchResultOverview.append(Component.text(Messages.MATCH_RESULT_OVERVIEW_LOSER.get()));

    for (int i = 0; i < loserComponents.size(); i++) {
      matchResultOverview.append(loserComponents.get(i));
      if (i < loserComponents.size() - 1) {
        matchResultOverview.append(SEPARATOR);
      }
    }

    if (!spectators.isEmpty()) {
      matchResultOverview.append(Component.newline());
      matchResultOverview.append(Component.text(Messages.MATCH_RESULT_SPECTATORS.get()));

      for (int i = 0; i < spectators.size(); i++) {
        matchResultOverview.append(
            Component.text(spectators.get(i).getName(), Style.style(PLAYER_COLOR)));
        if (i < spectators.size() - 1) {
          matchResultOverview.append(SEPARATOR);
        }
      }
    }

    return matchResultOverview.build();
  }

  private List<Component> getComponents(Team... teams) {
    List<Component> components = Lists.newArrayList();

    for (Team team : teams) {
      components.addAll(
          team.getPlayers().stream()
              .map(
                  it ->
                      Component.text(it.getName(), Style.style(PLAYER_COLOR))
                          .clickEvent(
                              ClickEvent.clickEvent(
                                  ClickEvent.Action.RUN_COMMAND,
                                  "/viewsnapshot " + snapshots.get(it).getId()))
                          .hoverEvent(
                              HoverEvent.showText(
                                  Component.text(
                                      Messages.MATCH_RESULT_OVERVIEW_HOVER.match(
                                          "{player}", it.getName())))))
              .collect(Collectors.toList()));
    }

    return components;
  }
}
