package me.ponktacology.practice.player;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import me.ponktacology.practice.util.data.mongo.MongoRepositoryImpl;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerRepository extends MongoRepositoryImpl<PracticePlayer> {

  @Override
  protected String getCollectionName() {
    return "players";
  }

  @Override
  public void load(PracticePlayer entity) {
    org.bson.Document document = getCollection()
            .find(Filters.eq("_id", entity.getId()))
            .first();

    if (document == null) {
      save(entity);
      return;
    }

    entity.applyDocument(document);
  }

  public List<PracticePlayer> get(Bson filter, Bson sort, int count) {
    List<PracticePlayer> sessions = Lists.newArrayList();

    List<Document> documents =
            getCollection()
            .find(filter)
            .sort(sort)
            .limit(count)
            .into(new ArrayList<>());

    for (Document document : documents) {
      UUID uuid = UUID.fromString(document.getString("_id"));
      PracticePlayer session = new PracticePlayer(uuid);
      session.applyDocument(document);
      sessions.add(session);
    }

    return sessions;
  }

}
