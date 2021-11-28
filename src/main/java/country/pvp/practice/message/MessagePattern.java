package country.pvp.practice.message;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MessagePattern {

    private final @NotNull String placeholder;
    private final @NotNull Object value;

    public @NotNull String translate(@NotNull String message) {
        return message.replace(placeholder, value.toString());
    }
}
