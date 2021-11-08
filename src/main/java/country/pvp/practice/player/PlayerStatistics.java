package country.pvp.practice.player;

import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.ladder.Ladder;
import org.bson.Document;

import java.util.Map;

public class PlayerStatistics implements SerializableObject {

    private final Map<String, Integer> ranks = Maps.newHashMap();

    public int getRank(Ladder ladder) {
        return ranks.getOrDefault(ladder.getName(), 1000);
    }

    public void setRank(String ladder, int rank) {
        ranks.put(ladder, rank);
    }

    @Override
    public Document getDocument() {
        Document document = new Document();

        for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
            document.put(entry.getKey(), entry.getValue());
        }

        return document;
    }

    @Override
    public void applyDocument(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            setRank(entry.getKey(), (Integer) entry.getValue());
        }
    }
}
