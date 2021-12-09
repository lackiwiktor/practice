package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.message.Messager;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.snapshot.InventorySnapshotMenuProvider;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SnapshotCommands {

    private final InventorySnapshotManager snapshotManager;
    private final InventorySnapshotMenuProvider inventorySnapshotMenuProvider;

    @Command("viewsnapshot")
    public void viewInv(@Sender Player sender, @Name("id") UUID id) {
        Optional<InventorySnapshot> snapshotOptional = snapshotManager.get(id);

        if (!snapshotOptional.isPresent()) {
            Messager.messageError(sender, "This inventory snapshot has expired.");
            return;
        }

        InventorySnapshot snapshot = snapshotOptional.get();
        inventorySnapshotMenuProvider.provide(snapshot).openMenu(sender);
    }
}
