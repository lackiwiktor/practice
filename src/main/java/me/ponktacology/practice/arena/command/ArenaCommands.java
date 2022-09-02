package me.ponktacology.practice.arena.command;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaService;
import me.ponktacology.practice.arena.ArenaType;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.arena.match.MatchArenaGenerator;
import me.ponktacology.practice.arena.match.StateSelectionData;
import me.ponktacology.practice.arena.thimble.ThimbleArena;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.Region;
import me.ponktacology.practice.util.WorldEditUtils;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Set;

@RequiredArgsConstructor
public class ArenaCommands extends PlayerCommands {

  private static final File WORLD_EDIT_SCHEMATICS_FOLDER =
      new File(Practice.getPractice().getDataFolder(), "schematics");

  private final ArenaService arenaService;

  @Command("arena create")
  @Permission("practice.admin")
  public void create(
      @Sender Player sender, @Name("name") String name, @Name("type") ArenaType type) {
    if (arenaService.getArenaByName(name) != null) {
      Messenger.messageError(sender, "Arena with this name already exists.");
      return;
    }

    Arena arena;
    switch (type) {
      case MATCH:
        arena = new MatchArena(name);
        break;
      case THIMBLE:
        arena = new ThimbleArena(name);
        break;
      default:
        return;
    }

    arenaService.createArena(arena);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully created new arena.");
  }

  @Command("arena delete")
  @Permission("practice.admin")
  public void remove(@Sender Player sender, @Name("arena") Arena arena) {
    arenaService.deleteArena(arena);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully removed arena.");
  }

  @Command("arena displayName")
  @Permission("practice.admin")
  public void displayName(
      @Sender Player sender, @Name("arena") Arena arena, @Name("displayName") String name) {
    arena.setDisplayName(name);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's display name.");
  }

  @Command("arena icon")
  @Permission("practice.admin")
  public void icon(@Sender Player sender, @Name("arena") Arena arena) {
    ItemStack itemInHand = sender.getItemInHand();
    if (itemInHand == null) {
      Messenger.messageError(sender, "You must hold an item in your hand.");
      return;
    }
    arena.setIcon(itemInHand.clone());
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's icon.");
  }

  @Command("arena gridindex")
  @Permission("practice.admin")
  public void gridIndex(
      @Sender Player sender, @Name("arena") MatchArena arena, @Name("index") int index) {
    arena.setGridIndex(index);
    Messenger.messageSuccess(sender, "Successfully set grid index.");
  }

  @Command("arena schematic")
  @Permission("practice.admin")
  public void region(@Sender Player sender, @Name("arena") MatchArena arena) {
    PracticePlayer session = get(sender);
    if (!session.isSelecting()) {
      Messenger.messageError(sender, "You must be in editing mode to set region.");
      return;
    }
    StateSelectionData selectionData = session.getStateData();
    if (!selectionData.isReady()) {
      Messenger.messageSuccess(session, "You haven't selected region yet!");
      return;
    }
    Region region = Region.from(selectionData.getSelection());
    File file = new File(WORLD_EDIT_SCHEMATICS_FOLDER, arena.getName() + ".schematic");

    try {
      WorldEditUtils.save(file, sender.getWorld(), region);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Messenger.messageSuccess(sender, "Finished.");
  }

  @Command("arena generate")
  @Permission("practice.admin")
  public void generate(
      @Sender Player sender, @Name("arena") MatchArena arena, @Name("amount") int amount) {
    if (arena.getSchematic() == null) {
      Messenger.messageError(sender, "Schematic is not set. Use /arena selector.");
      return;
    }

    try {
      Set<MatchArenaCopy> copies = MatchArenaGenerator.generate(arena, amount);
      arena.addCopies(copies);
    } catch (Exception e) {
      e.printStackTrace();
    }

    sender.sendMessage("Finished generating copies for arena .");
  }

  @Command("arena selector")
  @Permission("practice.admin")
  public void selector(@Sender Player sender) {
    PracticePlayer session = get(sender);
    session.setState(PlayerState.SELECTING, new StateSelectionData());
    sender.getInventory().addItem(new ItemStack(Material.GOLD_PICKAXE));
  }

  @Command("arena teleport")
  @Permission("practice.admin")
  public void teleport(@Sender Player sender) {
    sender.teleport(new Location(Bukkit.getWorld("arenas"), 1_000, 60, 1_000));
  }
}
