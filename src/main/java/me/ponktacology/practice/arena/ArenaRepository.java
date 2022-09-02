package me.ponktacology.practice.arena;

import com.google.common.collect.Sets;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.arena.thimble.ThimbleArena;
import me.ponktacology.practice.util.data.mongo.MongoRepositoryImpl;

import java.util.Set;

public class ArenaRepository extends MongoRepositoryImpl<Arena> {

  @Override
  protected String getCollectionName() {
    return "arenas";
  }

  public Set<Arena> loadAll() {
    Set<Arena> arenas = Sets.newHashSet();

    getCollection()
        .find()
        .forEach(
            it -> {

              ArenaType type = ArenaType.valueOf(it.getString("type"));

              Arena arena;
              switch (type) {
                case THIMBLE:
                  arena = new ThimbleArena(it.getString("_id"));
                  break;
                case MATCH:
                  arena = new MatchArena(it.getString("_id"));
                  break;
                default:
                  return;
              }

              arena.applyDocument(it);
              arenas.add(arena);
            });

    return arenas;
  }
}
