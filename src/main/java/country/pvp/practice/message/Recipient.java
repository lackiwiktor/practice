package country.pvp.practice.message;

import net.md_5.bungee.api.chat.BaseComponent;

public interface Recipient {
    void receive(String message);
    void receive(BaseComponent[] components);
}
