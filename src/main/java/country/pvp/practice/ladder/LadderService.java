package country.pvp.practice.ladder;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.MongoRepository;

import java.util.Set;

public class LadderService extends MongoRepository<Ladder> {

    @Inject
    public LadderService(MongoDatabase database) {
        super(database);
    }

    public Set<Ladder> loadAll() {
        Set<Ladder> ladders = Sets.newHashSet();

        database.getCollection("ladders").find().forEach(it -> {
            Ladder ladder = new Ladder(it.getString("_id"));
            ladder.applyDocument(it);
            ladders.add(ladder);
        });

        return ladders;
    }
}
