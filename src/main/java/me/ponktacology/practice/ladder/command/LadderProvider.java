package me.ponktacology.practice.ladder.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@RequiredArgsConstructor
public class LadderProvider implements ArgumentProvider<Ladder> {

  @Override
  public @Nullable Ladder provide(Context context, Argument argument) throws BladeExitMessage {
    String input = argument.getString();
    if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

    return Optional.ofNullable(Practice.getService(LadderService.class).getLadderByName(input))
        .orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Wrong ladder"));
  }
}
