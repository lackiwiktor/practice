package me.ponktacology.practice.util.data.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.ponktacology.practice.database.DatabaseService;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.util.data.DataObject;
import me.ponktacology.practice.util.data.Repository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

@RequiredArgsConstructor
public abstract class MongoRepositoryImpl<V extends DataObject> implements Repository<V> {

  protected abstract String getCollectionName();

  @Override
  public void save(V entity) {
    getCollection()
        .replaceOne(
            Filters.eq("_id", entity.getId()),
            entity.getDocument(),
            new ReplaceOptions().upsert(true));
  }

  @Override
  public void load(V entity) {
    Document document =
            getCollection().find(Filters.eq("_id", entity.getId())).first();
    if (document == null) return;
    entity.applyDocument(document);
  }

  @Override
  public void delete(V entity) {
    getCollection().deleteOne(Filters.eq("_id", entity.getId()));
  }

  public void createIndex(Bson index) {
    getCollection().createIndex(index);
  }

  public MongoCollection<Document> getCollection() {
    return Practice.getService(DatabaseService.class)
        .getMongoDatabase()
        .getCollection(getCollectionName());
  }
}
