package country.pvp.practice.util.message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessagePattern {

    private final String placeholder;
    private final Object value;

    public String replace(String message) {
        return message.replace(placeholder, value.toString());
    }
}
