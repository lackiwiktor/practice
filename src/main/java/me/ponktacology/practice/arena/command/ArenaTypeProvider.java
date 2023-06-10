package me.ponktacology.practice.arena.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.arena.ArenaType;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ArenaTypeProvider implements ArgumentProvider<ArenaType> {

    @Override
    public @Nullable ArenaType provide(Context context, Argument argument) throws BladeExitMessage {
        String input = argument.getString();
        if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

        return ArenaType.valueOf(input.toUpperCase(Locale.ROOT));
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        return Arrays.stream(ArenaType.values())
                .map(it -> it.name().toUpperCase(Locale.ROOT))
                .collect(Collectors.toList());
    }
}
