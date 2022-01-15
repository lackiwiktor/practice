package country.pvp.practice.arena;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.util.data.mongo.MongoRepositoryImpl;
import org.bson.Document;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DuplicatedArenaService extends MongoRepositoryImpl<DuplicatedArena> {

    private final ArenaManager arenaManager;

    @Inject
    public DuplicatedArenaService(MongoDatabase database, ArenaManager arenaManager) {
        super(database);
        this.arenaManager = arenaManager;
    }

    public Map<Arena, Set<DuplicatedArena>> loadAll() {
        Map<Arena, Set<DuplicatedArena>> arenas = Maps.newHashMap();

        for (Document document : database.getCollection("duplicated_arenas").find()) {
            String parentName = document.getString("parent");
            Arena parent = arenaManager.get(parentName);

            if (parent == null) {
                System.out.println("Parent of arena not found, skipping loading duplicates.");
                continue;
            }

            DuplicatedArena arena = new DuplicatedArena(UUID.fromString(document.getString("_id")), parent);
            arena.applyDocument(document);
            Set<DuplicatedArena> copies = arenas.computeIfAbsent(arena, (a) -> Sets.newHashSet());
            copies.add(arena);
        }

        return arenas;
    }
}
