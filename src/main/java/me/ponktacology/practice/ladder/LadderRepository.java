package me.ponktacology.practice.ladder;

import com.google.common.collect.Sets;
import me.ponktacology.practice.util.data.mongo.MongoRepositoryImpl;

import java.util.Set;

public class LadderRepository extends MongoRepositoryImpl<Ladder> {

    @Override
    protected String getCollectionName() {
        return "ladders";
    }

    public Set<Ladder> loadAll() {
        Set<Ladder> ladders = Sets.newHashSet();

        getCollection().find().forEach(it -> {
            Ladder ladder = new Ladder(it.getString("_id"));
            ladder.applyDocument(it);
            ladders.add(ladder);
        });

        return ladders;
    }
}
