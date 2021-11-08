package country.pvp.practice.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.kit.PlayerKit;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerKits {

    private final Map<String, List<PlayerKit>> kits = Maps.newHashMap();
    private final PracticePlayer player;

    public List<PlayerKit> getKits(Ladder ladder) {
        return kits.getOrDefault(ladder.getName(), Collections.emptyList());
    }

    public boolean hasKits(Ladder ladder) {
        return kits.containsKey(ladder.getName());
    }

    public void addKit(Ladder ladder, PlayerKit kit) {
        kits.computeIfAbsent(ladder.getName(), (k) -> Lists.newLinkedList()).add(kit);
    }

    public void removeKit(Ladder ladder, PlayerKit kit) {
        kits.getOrDefault(ladder.getName(), Collections.emptyList()).remove(kit);
    }

    public void prepareKits(Ladder ladder) {
        Player bukkitPlayer = player.getPlayer();
        Preconditions.checkNotNull(bukkitPlayer, "player");

        PlayerInventory playerInventory = bukkitPlayer.getInventory();
        playerInventory.setItem(0, ladder.getKit().getIcon());
    }
}
