package country.pvp.practice.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.kit.PlayerKit;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerKits implements SerializableObject {

    private final Map<String, List<PlayerKit>> kits = Maps.newHashMap();

    public List<PlayerKit> getKits(Ladder ladder) {
        return kits.getOrDefault(ladder.getName(), Collections.emptyList());
    }

    public boolean hasKits(Ladder ladder) {
        return kits.containsKey(ladder.getName());
    }

    public void addKit(Ladder ladder, PlayerKit kit) {
        kits.computeIfAbsent(ladder.getName(), (k) -> Lists.newLinkedList()).add(kit);
    }

    public void removeKit(Ladder ladder, PlayerKit kit) {
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
