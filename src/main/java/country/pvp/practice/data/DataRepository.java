package country.pvp.practice.data;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
}
