package country.pvp.practice.visibility;

import country.pvp.practice.player.PracticePlayer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VisibilityUpdater {

  public static void update(PracticePlayer player) {
    for (PracticePlayer other : PracticePlayer.players()) {
      update(player, other);
      update(other, player);
    }
  }

  public static void update(PracticePlayer observer, PracticePlayer observable) {
    Visibility visibility = VisibilityProvider.provide(observer, observable);
    visibility.apply(observer, observable);
  }
}
