package country.pvp.practice.match.snapshot;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class InventorySnapshotMenuProvider {

    private final InventorySnapshotManager snapshotManager;

    public InventorySnapshotMenu provide(InventorySnapshot snapshot) {
        return new InventorySnapshotMenu(snapshotManager, snapshot);
    }
}
