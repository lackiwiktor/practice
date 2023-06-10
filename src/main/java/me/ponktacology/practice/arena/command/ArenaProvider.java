package me.ponktacology.practice.arena.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaService;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ArenaProvider implements ArgumentProvider<Arena> {

  @Override
  public @Nullable Arena provide(Context context, Argument argument) throws BladeExitMessage {
    String input = argument.getString();
    if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

    return Optional.ofNullable(Practice.getService(ArenaService.class).getArenaByName(input))
        .orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Wrong arena"));
  }
}
