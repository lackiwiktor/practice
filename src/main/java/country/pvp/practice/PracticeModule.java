package country.pvp.practice;

import com.google.inject.AbstractModule;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.kit.KitManager;
import country.pvp.practice.kit.KitRepository;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerRepository;
import country.pvp.practice.visibility.VisibilityProvider;
import country.pvp.practice.visibility.VisibilityUpdater;

public class PracticeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MongoDatabase.class).toInstance(
                MongoClients.create("mongodb+srv://ponktacology:yHzd9Qcg7u1f3Q3H@cluster0.zch1g.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
                        .getDatabase("practice"));
        bind(PlayerManager.class).asEagerSingleton();
        bind(KitManager.class).asEagerSingleton();
        bind(PlayerRepository.class).asEagerSingleton();
        bind(KitRepository.class).asEagerSingleton();
        bind(VisibilityProvider.class).asEagerSingleton();
        bind(VisibilityUpdater.class).asEagerSingleton();
    }
}
