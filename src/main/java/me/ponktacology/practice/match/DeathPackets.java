package me.ponktacology.practice.match;

import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DeathPackets {

    private final Match match;
    private final PracticePlayer deadPlayer;

    public void sendDeathPackets() {
        Location location = deadPlayer.getLocation();

        PacketPlayOutSpawnEntityWeather lightningPacket = new PacketPlayOutSpawnEntityWeather(
                new EntityLightning(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        true)
        );

        for (PracticePlayer practicePlayer : match.getOnlinePlayers()) {
            Player player = practicePlayer.getPlayer();
            // We do not have to check again if player is not null
            // since getAllOnlinePlayers ensures that
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(lightningPacket);
        }
    }
}
