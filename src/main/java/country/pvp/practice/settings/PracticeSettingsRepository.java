package country.pvp.practice.settings;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.util.data.mongo.MongoRepositoryImpl;

public class PracticeSettingsRepository extends MongoRepositoryImpl<PracticeSettings> {

    @Inject
    public PracticeSettingsRepository(MongoDatabase database) {
        super(database);
    }
}
