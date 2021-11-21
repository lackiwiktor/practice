package country.pvp.practice.visibility;

import com.google.common.base.Preconditions;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Slf4j
public enum Visibility {
    SHOWN(Player::showPlayer),
    HIDDEN(Player::hidePlayer);

    private final BiConsumer<Player, Player> apply;

    public void apply(PracticePlayer observer, PracticePlayer observable) {
        Preconditions.checkNotNull(observer.getPlayer(), "Player must be online in order to hide other players.");
        Preconditions.checkNotNull(observable.getPlayer(), "Player must be online in order to be hidden.");
        this.apply.accept(observer.getPlayer(), observable.getPlayer());
        log.info(observer.getName() + (this == SHOWN ? " shown " : " hide ") + observable.getName());
    }

}
