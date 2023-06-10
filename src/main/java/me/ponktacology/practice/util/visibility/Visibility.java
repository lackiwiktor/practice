package me.ponktacology.practice.util.visibility;

import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public enum Visibility {
    SHOWN(Player::showPlayer),
    HIDDEN(Player::hidePlayer);

    private final BiConsumer<Player, Player> apply;

    public void apply(PracticePlayer observer, PracticePlayer observable) {
        this.apply.accept(observer.getPlayer(), observable.getPlayer());
    }

}
