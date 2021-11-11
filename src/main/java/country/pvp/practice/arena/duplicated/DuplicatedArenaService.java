package country.pvp.practice.arena.duplicated;

import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.mongo.MongoRepository;

public class DuplicatedArenaService extends MongoRepository<DuplicatedArena> {

    public DuplicatedArenaService(MongoDatabase database) {
        super(database);
    }


}
