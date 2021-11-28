package country.pvp.practice.player.data;

import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.ladder.Ladder;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerStatistics implements SerializableObject {

    private final Map<String, Integer> ratings = Maps.newHashMap();

    public int getElo(@NotNull Ladder ladder) {
        return ratings.getOrDefault(ladder.getName(), 1000);
    }


    void setElo(String ladder, int rank) {
        ratings.put(ladder, rank);
    }

    public void setElo(@NotNull Ladder ladder, int rank) {
        setElo(ladder.getName(), rank);
    }

    @Override
    public @NotNull Document getDocument() {
        Document document = new Document();
        document.putAll(ratings);
        return document;
    }

    @Override
    public void applyDocument(@NotNull Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            setElo(entry.getKey(), (Integer) entry.getValue());
        }
    }
}
