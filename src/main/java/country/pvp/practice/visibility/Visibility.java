package country.pvp.practice.visibility;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Slf4j
public enum Visibility {
    SHOWN(Player::showPlayer),
    HIDDEN(Player::hidePlayer);

    private final @NotNull BiConsumer<Player, Player> apply;

    public void apply(@NotNull PracticePlayer observer, @NotNull PracticePlayer observable) {
        this.apply.accept(observer.getPlayer(), observable.getPlayer());
        log.info(observer.getName() + (this == SHOWN ? " shown " : " hide ") + observable.getName());
    }

}
