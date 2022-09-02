package me.ponktacology.practice.ladder.command;

import com.google.common.base.Strings;
import me.ponktacology.practice.ladder.LadderType;
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

public class LadderTypeProvider implements ArgumentProvider<LadderType> {

    @Override
    public @Nullable LadderType provide(Context context, Argument argument) throws BladeExitMessage {
        String input = argument.getString();
        if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

        return LadderType.valueOf(input.toUpperCase(Locale.ROOT));
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        return Arrays.stream(LadderType.values())
                .map(it -> it.name().toUpperCase(Locale.ROOT))
                .collect(Collectors.toList());
    }
}
