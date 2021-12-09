package country.pvp.practice.arena;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.mongo.MongoRepository;

import java.util.Set;

public class ArenaService extends MongoRepository<Arena> {

    @Inject
    public ArenaService(MongoDatabase database) {
        super(database);
    }

    public Set<Arena> loadAll() {
        Set<Arena> arenas = Sets.newHashSet();

        database.getCollection("arenas").find().forEach(it -> {
            Arena arena = new Arena(it.getString("_id"));
            arena.applyDocument(it);
            arenas.add(arena);
        });

        return arenas;
    }
}
