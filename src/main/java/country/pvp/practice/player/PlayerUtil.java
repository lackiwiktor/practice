package country.pvp.practice.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerUtil {

    public static void resetPlayer(@NotNull Player player) {
        player.getOpenInventory().close();
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.setFallDistance(0F);
        player.setExhaustion(0F);
        player.setSaturation(12F);
        player.getInventory().setHeldItemSlot(0);
        player.getActivePotionEffects().forEach(it -> player.removePotionEffect(it.getType()));
    }
}
