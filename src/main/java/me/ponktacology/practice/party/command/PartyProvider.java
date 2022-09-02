package me.ponktacology.practice.party.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartyProvider implements ArgumentProvider<Party> {

  @Override
  public @Nullable Party provide(Context context, Argument argument) throws BladeExitMessage {
    String input = argument.getString();
    if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

    PracticePlayer session =
        Practice.getService(PlayerService.class)
            .get(input)
            .orElseThrow(
                () ->
                    new BladeExitMessage(
                        ChatColor.RED + "Error: Couldn't find a party with this player."));

    if (!session.hasParty()) {
      throw new BladeExitMessage(ChatColor.RED + "Error: This player doesn't have a party.");
    }

    return session.getParty();
  }

  @Override
  public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument)
      throws BladeExitMessage {
    return Practice.getService(PlayerService.class).getAll().stream()
        .map(PracticePlayer::getName)
        .collect(Collectors.toList());
  }
}
