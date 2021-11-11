package country.pvp.practice.settings;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.mongo.MongoRepository;

public class PracticeSettingsService extends MongoRepository<PracticeSettings> {

    @Inject
    public PracticeSettingsService(MongoDatabase database) {
        super(database);
    }
}
