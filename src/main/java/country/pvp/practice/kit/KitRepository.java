package country.pvp.practice.kit;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.data.MongoRepository;

import java.util.Set;

public class KitRepository extends MongoRepository<Kit> {

    @Inject
    public KitRepository(MongoDatabase database) {
        super(database);
    }

    public Set<Kit> loadAll() {
        Set<Kit> kits = Sets.newHashSet();

        database.getCollection("kit").find().forEach(it -> {
            Kit kit = new Kit(it.getString("name"));
            kit.apply(it);
            kits.add(kit);
        });

        return kits;
    }
}
