package country.pvp.practice.player.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.kit.NamedKit;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerKits implements SerializableObject {

    private final Map<String, List<NamedKit>> kits = Maps.newHashMap();

    public List<NamedKit> getKits(Ladder ladder) {
        return kits.getOrDefault(ladder.getName(), Collections.emptyList());
    }

    public boolean hasKits(Ladder ladder) {
        return kits.containsKey(ladder.getName());
    }

    public void addKit(Ladder ladder, NamedKit kit) {
        kits.computeIfAbsent(ladder.getName(), (k) -> Lists.newLinkedList()).add(kit);
    }

    public void removeKit(Ladder ladder, NamedKit kit) {
        kits.getOrDefault(ladder.getName(), Collections.emptyList()).remove(kit);
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void applyDocument(Document document) {

    }
}
