package country.pvp.practice.player;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import country.pvp.practice.data.mongo.MongoRepository;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerService extends MongoRepository<PlayerSession> {

    @Inject
    public PlayerService(MongoDatabase database) {
        super(database);
    }

    @Override
    public void load(PlayerSession entity) {
        org.bson.Document document = database
                .getCollection(entity.getCollection())
                .find(Filters.eq("_id", entity.getId()))
                .first();

        if (document == null) {
            save(entity);
            return;
        }

        entity.applyDocument(document);
    }

    public List<PlayerSession> get(Bson filter, Bson sort, int count) {
        List<PlayerSession> sessions = Lists.newArrayList();
        PlayerSession playerSession = new PlayerSession(UUID.randomUUID()); //temporary work-around

        List<Document> documents = database
                .getCollection(playerSession.getCollection())
                .find(filter)
                .sort(sort)
                .limit(count)
                .into(new ArrayList<>());

        for (Document document : documents) {
            UUID uuid = UUID.fromString(document.getString("_id"));
            PlayerSession session = new PlayerSession(uuid);
            session.applyDocument(document);
            sessions.add(session);
        }

        return sessions;
    }
}
