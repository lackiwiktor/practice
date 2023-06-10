package me.ponktacology.practice.player.data;

import com.google.common.collect.Maps;
import de.gesundkrank.jskills.Rating;
import me.ponktacology.practice.match.ranking.trueskill.TrueSkillUtil;
import me.ponktacology.practice.util.data.SerializableObject;
import me.ponktacology.practice.ladder.Ladder;
import org.bson.Document;

import java.util.Map;

public class PlayerStatistics implements SerializableObject {

    private final Map<String, Rating> ratings = Maps.newHashMap();

    public Rating getElo(Ladder ladder) {
        return ratings.getOrDefault(ladder.getName(), new Rating(TrueSkillUtil.INITIAL_MEAN, TrueSkillUtil.STD));
    }

    void setElo(String ladder, Rating rating) {
        ratings.put(ladder, rating);
    }

    public void setElo(Ladder ladder, Rating rating) {
        setElo(ladder.getName(), rating);
    }

    @Override
    public Document getDocument() {
        Document document = new Document();
        ratings.forEach((key, value) -> document.put(key, new Document("mean", value.getMean())
                .append("std", value.getStandardDeviation())));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            Document ratingDocument = (Document) entry.getValue();
            Rating rating = new Rating(ratingDocument.getInteger("mean"),
                    ratingDocument.getInteger("std"));
            setElo(entry.getKey(), rating);
        }
    }
}
