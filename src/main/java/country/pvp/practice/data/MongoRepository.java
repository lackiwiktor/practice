package country.pvp.practice.data;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MongoRepository<V extends DataObject> implements Repository<V> {

    protected final MongoDatabase database;

    @Override
    public void save(V entity) {
        database.getCollection(entity.getCollection()).replaceOne(Filters.eq("_id", entity.getId()), entity.get());
    }

    @Override
    public void load(V entity) {
        org.bson.Document document = database.getCollection(entity.getCollection()).find(Filters.eq("_id", entity.getId())).first();
        if (document == null) return;
        entity.apply(document);
    }
}
