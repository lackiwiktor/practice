package country.pvp.practice.player;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.MongoRepository;


public class PlayerRepository extends MongoRepository<PracticePlayer> {

    @Inject
    public PlayerRepository(MongoDatabase database) {
        super(database);
    }
}
