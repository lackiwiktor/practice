package me.ponktacology.practice.database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Configuration;
import me.ponktacology.practice.Service;
import lombok.Getter;

@RequiredArgsConstructor
public class DatabaseService extends Service {

  private final Configuration configuration;
  @Getter private MongoDatabase mongoDatabase;

  @Override
  public void configure() {
    mongoDatabase = MongoClients.create(configuration.getMongoString()).getDatabase("practice");
  }
}
