package country.pvp.practice.data;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import country.pvp.practice.TaskDispatcher;
import org.bson.Document;

public class DataRepository {

  private static MongoDatabase database;

  public static void connect(String connectionUrl, String dbName) {
    database = MongoClients.create(connectionUrl).getDatabase(dbName);
  }

  public static MongoCollection<Document> collection(String collection) {
    Preconditions.checkNotNull(database, "Database is not connected");
    return database.getCollection(collection);
  }

  public static void save(DataObject data) {
    collection(data.getCollection()).replaceOne(Filters.eq("_id", data.getId()), data.toDocument());
  }

  public static boolean load(DataObject data) {
    Document document =
        collection(data.getCollection()).find(Filters.eq("_id", data.getId())).first();

    if (document == null) return false;
    data.load(document);

    return true;
  }

  public static void loadAsync(DataObject data) {
    TaskDispatcher.async(() -> load(data));
  }
}
