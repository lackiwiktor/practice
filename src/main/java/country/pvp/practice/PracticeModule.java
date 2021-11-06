package country.pvp.practice;

import com.google.inject.AbstractModule;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.board.PracticeBoard;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderRepository;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.QueueMenuProvider;
import country.pvp.practice.visibility.VisibilityProvider;
import country.pvp.practice.visibility.VisibilityUpdater;

public class PracticeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MongoDatabase.class).toInstance(
                MongoClients.create("mongodb+srv://ponktacology:yHzd9Qcg7u1f3Q3H@cluster0.zch1g.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
                        .getDatabase("practice"));
        bind(PlayerManager.class).asEagerSingleton();
        bind(LadderManager.class).asEagerSingleton();
        bind(PlayerService.class).asEagerSingleton();
        bind(LadderRepository.class).asEagerSingleton();
        bind(VisibilityProvider.class).asEagerSingleton();
        bind(VisibilityUpdater.class).asEagerSingleton();
        bind(PracticeBoard.class).asEagerSingleton();
        bind(LadderManager.class).asEagerSingleton();
        bind(QueueManager.class).asEagerSingleton();
        bind(QueueMenuProvider.class).asEagerSingleton();
    }
}
