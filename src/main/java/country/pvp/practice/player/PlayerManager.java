package country.pvp.practice.player;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, PracticePlayer> players = Maps.newConcurrentMap();

    public PracticePlayer get(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    public @NotNull Optional<PracticePlayer> get(String name) {
        return players.values().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void add(@NotNull PracticePlayer player) {
        players.put(player.getUuid(), player);
    }

    public PracticePlayer remove(@NotNull Player player) {
        return players.remove(player.getUniqueId());
    }

    public @NotNull Set<PracticePlayer> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

}
