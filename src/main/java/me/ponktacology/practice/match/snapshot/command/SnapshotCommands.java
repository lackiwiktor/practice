package me.ponktacology.practice.match.snapshot.command;

import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.snapshot.InventorySnapshotMenu;
import me.ponktacology.practice.match.snapshot.InventorySnapshotService;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SnapshotCommands {

  private final InventorySnapshotService inventorySnapshotService;

  @Command("viewsnapshot")
  public void viewSnapshot(@Sender Player sender, @Name("id") UUID id) {
    Optional<InventorySnapshot> snapshotOptional = inventorySnapshotService.get(id);

    if (!snapshotOptional.isPresent()) {
      Messenger.messageError(sender, "This inventory snapshot has expired.");
      return;
    }

    InventorySnapshot snapshot = snapshotOptional.get();
    new InventorySnapshotMenu(snapshot).openMenu(sender);
  }
}
