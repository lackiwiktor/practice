package country.pvp.practice.player;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, PlayerSession> players = Maps.newConcurrentMap();

    public PlayerSession get(Player player) {
        return players.get(player.getUniqueId());
    }

    public Optional<PlayerSession> get(String name) {
        return players.values().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void add(PlayerSession player) {
        players.put(player.getUuid(), player);
    }

    public PlayerSession remove(Player player) {
        return players.remove(player.getUniqueId());
    }

    public Set<PlayerSession> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

}
