package country.pvp.practice.visibility;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public enum Visibility {
  SHOWN(PracticePlayer::show),
  HIDDEN(PracticePlayer::hide);

  private final BiConsumer<PracticePlayer, PracticePlayer> apply;

  public void apply(PracticePlayer observer, PracticePlayer observable) {
    this.apply.accept(observer, observable);
  }
}
