package country.pvp.practice.player;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.mongo.MongoRepository;

public class PlayerService extends MongoRepository<PracticePlayer> {

    @Inject
    public PlayerService(MongoDatabase database) {
        super(database);
    }
}
