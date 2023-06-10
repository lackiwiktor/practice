package me.ponktacology.practice.player.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.Practice;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
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
public class PracticePlayerProvider implements ArgumentProvider<PracticePlayer> {


    @Override
    public @Nullable PracticePlayer provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        String input = argument.getString();
        if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

        return Practice.getService(PlayerService.class)
                .get(input)
                .orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Player not found."));
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        return Practice.getService(PlayerService.class)
                .getAll()
                .stream()
                .map(PracticePlayer::getName)
                .filter(name -> name.startsWith(argument.getString()))
                .collect(Collectors.toList());
    }
}
