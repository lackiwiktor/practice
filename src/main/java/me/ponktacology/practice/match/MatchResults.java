package me.ponktacology.practice.match;

import com.google.common.collect.Lists;
import ga.windpvp.windspigot.commons.ClickableBuilder;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MatchResults {

  private final Team winner;
  private final Team[] losers;
  private final Map<PracticePlayer, InventorySnapshot> snapshots;
  private final List<PracticePlayer> spectators;


}
