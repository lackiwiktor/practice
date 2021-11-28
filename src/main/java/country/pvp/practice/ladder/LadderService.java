package country.pvp.practice.ladder;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.mongo.MongoRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LadderService extends MongoRepository<Ladder> {

    @Inject
    public LadderService(MongoDatabase database) {
        super(database);
    }

    public @NotNull Set<Ladder> loadAll() {
        Set<Ladder> ladders = Sets.newHashSet();

        database.getCollection("ladders").find().forEach(it -> {
            Ladder ladder = new Ladder(it.getString("_id"));
            ladder.applyDocument(it);
            ladders.add(ladder);
        });

        return ladders;
    }
}
