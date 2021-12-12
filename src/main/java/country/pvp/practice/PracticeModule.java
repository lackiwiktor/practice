package country.pvp.practice;

import com.google.inject.AbstractModule;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.arena.ArenaService;
import country.pvp.practice.board.PracticeBoard;
import country.pvp.practice.invitation.InvitationManager;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderService;
import country.pvp.practice.leaderboards.LeaderBoardsService;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.snapshot.InventorySnapshotMenuProvider;
import country.pvp.practice.party.PartyManager;
import country.pvp.practice.party.PartyService;
import country.pvp.practice.party.menu.PartyEventMenuProvider;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.duel.PlayerDuelService;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.menu.QueueMenuProvider;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.settings.PracticeSettingsService;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PracticeModule extends AbstractModule {

    private final Configuration configuration;

    @Override
    protected void configure() {
        bind(MongoDatabase.class).toInstance(
                MongoClients.create(configuration.getMongoString())
                        .getDatabase("practice"));
        bind(InvitationManager.class).asEagerSingleton();
        bind(InvitationService.class).asEagerSingleton();
        bind(ItemBarService.class).asEagerSingleton();
        bind(PlayerManager.class).asEagerSingleton();
        bind(PlayerService.class).asEagerSingleton();
        bind(VisibilityUpdater.class).asEagerSingleton();
        bind(LadderManager.class).asEagerSingleton();
        bind(LadderService.class).asEagerSingleton();
        bind(ArenaManager.class).asEagerSingleton();
        bind(ArenaService.class).asEagerSingleton();
        bind(QueueManager.class).asEagerSingleton();
        bind(PracticeSettings.class).asEagerSingleton();
        bind(PracticeSettingsService.class).asEagerSingleton();
        bind(MatchProvider.class).asEagerSingleton();
        bind(PracticeBoard.class).asEagerSingleton();
        bind(MatchManager.class).asEagerSingleton();
        bind(QueueMenuProvider.class).asEagerSingleton();
        bind(InventorySnapshotManager.class).asEagerSingleton();
        bind(InventorySnapshotMenuProvider.class).asEagerSingleton();
        bind(PartyManager.class).asEagerSingleton();
        bind(PartyService.class).asEagerSingleton();
        bind(PlayerDuelService.class).asEagerSingleton();
        bind(LeaderBoardsService.class).asEagerSingleton();
        bind(PartyEventMenuProvider.class).asEagerSingleton();
    }
}
