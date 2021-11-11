package country.pvp.practice.player;

import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void resetPlayer(Player player) {
        player.getOpenInventory().close();
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.setFallDistance(0F);
        player.setExhaustion(0F);
        player.setSaturation(12F);
        player.getActivePotionEffects().forEach(it -> player.removePotionEffect(it.getType()));
    }
}
