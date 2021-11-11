package country.pvp.practice.player;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, PracticePlayer> players = Maps.newConcurrentMap();

    public PracticePlayer get(Player player) {
        return players.get(player.getUniqueId());
    }

    public void add(PracticePlayer player) {
        players.put(player.getUuid(), player);
    }

    public PracticePlayer remove(Player player) {
        return players.remove(player.getUniqueId());
    }

    public Set<PracticePlayer> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

}
