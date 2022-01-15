package country.pvp.practice.settings;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.util.data.mongo.MongoRepositoryImpl;

public class PracticeSettingsService extends MongoRepositoryImpl<PracticeSettings> {

    @Inject
    public PracticeSettingsService(MongoDatabase database) {
        super(database);
    }
}
