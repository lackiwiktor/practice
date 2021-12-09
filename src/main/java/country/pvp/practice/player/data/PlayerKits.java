package country.pvp.practice.player.data;

import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public NamedKit[] getKits(Ladder ladder) {
        return kits.computeIfAbsent(ladder.getName(), (k) -> new NamedKit[7]);
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
        return Arrays.stream(getKits(ladder)).anyMatch(Objects::nonNull);
    }
}
