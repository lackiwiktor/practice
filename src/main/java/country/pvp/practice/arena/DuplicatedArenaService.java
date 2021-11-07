package country.pvp.practice.arena;

import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.MongoRepository;

public class DuplicatedArenaService extends MongoRepository<DuplicatedArena> {

    public DuplicatedArenaService(MongoDatabase database) {
        super(database);
    }


}
