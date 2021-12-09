package country.pvp.practice.visibility;

import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public enum Visibility {
    SHOWN(Player::showPlayer),
    HIDDEN(Player::hidePlayer);

    private final BiConsumer<Player, Player> apply;

    public void apply(PlayerSession observer, PlayerSession observable) {
        this.apply.accept(observer.getPlayer(), observable.getPlayer());
    }

}
