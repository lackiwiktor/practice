package country.pvp.practice.player.data;

import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.ladder.Ladder;
import org.bson.Document;

import java.util.Map;

public class PlayerStatistics implements SerializableObject {

    private final Map<String, Integer> ratings = Maps.newHashMap();

    public int getElo(Ladder ladder) {
        return ratings.getOrDefault(ladder.getName(), 1000);
    }


    void setElo(String ladder, int rank) {
        ratings.put(ladder, rank);
    }

    public void setElo(Ladder ladder, int rank) {
        setElo(ladder.getName(), rank);
    }

    @Override
    public Document getDocument() {
        Document document = new Document();
        document.putAll(ratings);
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            setElo(entry.getKey(), (Integer) entry.getValue());
        }
    }
}
