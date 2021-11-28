package country.pvp.practice.match.snapshot;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SnapshotExpiryTask implements Runnable {

    private final InventorySnapshotManager snapshotManager;

    @Override
    public void run() {
        snapshotManager.cleanup();
    }
}
