package me.ponktacology.practice.player.data;

import com.google.common.collect.Maps;
import com.mongodb.lang.Nullable;
import me.ponktacology.practice.util.data.SerializableObject;
import me.ponktacology.practice.kit.NamedKit;
import me.ponktacology.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerKits implements SerializableObject {

    private final Map<String, NamedKit[]> kits = Maps.newHashMap();

    public void setKit(Ladder ladder, NamedKit kit, int index) {
        getKits(ladder)[index] = kit;
    }

    public NamedKit getKit(Ladder ladder, int index) {
        return getKits(ladder)[index];
    }

    public @Nullable NamedKit[] getKits(Ladder ladder) {
        return kits.getOrDefault(ladder.getName(), null);
    }

    @Override
    public Document getDocument() {
        Document document = new Document();

        for (Map.Entry<String, NamedKit[]> entry : kits.entrySet()) {
            document.put(entry.getKey(), Arrays.stream(entry.getValue()).map(it -> it == null ? null : it.getDocument()).collect(Collectors.toList()));
        }

        return document;
    }

    @Override
    public void applyDocument(Document document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            List<Document> documents = (List<Document>) entry.getValue();
            NamedKit[] kits = documents.stream().map(it -> {
                if (it == null) return null;
                NamedKit kit = new NamedKit(it.getString("name"));
                kit.applyDocument(it);
                return kit;
            }).toArray(s -> new NamedKit[7]);

            this.kits.put(entry.getKey(), kits);
        }
    }

    public void removeKit(Ladder ladder, int index) {
        getKits(ladder)[index] = null;
    }

    public boolean hasKits(Ladder ladder) {
        return getKits(ladder) != null;
    }
}
