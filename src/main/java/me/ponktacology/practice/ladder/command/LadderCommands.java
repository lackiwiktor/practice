package me.ponktacology.practice.ladder.command;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.queue.QueueService;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LadderCommands {

  private final LadderService ladderService;

  @Command("ladder create")
  @Permission("practice.admin")
  public void create(@Sender Player sender, @Name("name") String name) {
    if (ladderService.getLadderByName(name) != null) {
      Messenger.messageError(sender, "Ladder with this name already exists.");
      return;
    }

    Ladder ladder = new Ladder(name);
    ladderService.createLadder(ladder);
    Practice.getService(QueueService.class).createQueue(ladder);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully created new ladder.");
  }

  @Command("ladder remove")
  @Permission("practice.admin")
  public void remove(@Sender Player sender, @Name("ladder") Ladder ladder) {
    ladderService.deleteLadder(ladder);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully removed ladder.");
  }

  @Command("ladder displayName")
  @Permission("practice.admin")
  public void displayName(
      @Sender Player sender, @Name("ladder") Ladder ladder, @Name("displayName") String name) {
    ladder.setDisplayName(name);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's display name.");
  }

  @Command("ladder icon")
  @Permission("practice.admin")
  public void icon(@Sender Player sender, @Name("ladder") Ladder ladder) {
    ItemStack itemInHand = sender.getItemInHand();

    if (itemInHand == null) {
      Messenger.messageError(sender, "You must hold an item in your hand.");
      return;
    }

    ladder.setIcon(itemInHand.clone());
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's icon.");
  }

  @Command("ladder ranked")
  @Permission("practice.admin")
  public void ranked(
      @Sender Player sender, @Name("ladder") Ladder ladder, @Name("ranked") boolean ranked) {
    ladder.setRanked(ranked);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's ranked status.");
  }

  @Command("ladder inventory")
  @Permission("practice.admin")
  public void inventory(@Sender Player sender, @Name("ladder") Ladder ladder) {
    ladder.setInventory(sender.getInventory().getContents());
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's inventory.");
  }

  @Command("ladder armor")
  @Permission("practice.admin")
  public void armor(@Sender Player sender, @Name("ladder") Ladder ladder) {
    ladder.setArmor(sender.getInventory().getArmorContents());
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's armor.");
  }

  @Command("ladder editoritems")
  @Permission("practice.admin")
  public void editorItems(@Sender Player sender, @Name("ladder") Ladder ladder) {
    ladder.setEditorItems(
        Arrays.stream(sender.getInventory().getContents())
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's editor items.");
  }

  @Command("ladder allowBuild")
  @Permission("practice.admin")
  public void allowbuild(
      @Sender Player sender, @Name("ladder") Ladder ladder, @Name("allowbuild") boolean allowBuild) {
    ladder.setAllowBuild(allowBuild);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's allowBuild.");
  }

  @Command("ladder allowHealthRegeneration")
  @Permission("practice.admin")
  public void allowHealthRegeneration(
          @Sender Player sender, @Name("ladder") Ladder ladder, @Name("allowHealthRegeneration") boolean allowHealthRegeneration) {
    ladder.setAllowHealthRegeneration(allowHealthRegeneration);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's allowHealthRegeneration.");
  }

  @Command("ladder hungerDecay")
  @Permission("practice.admin")
  public void hungerDecay(
          @Sender Player sender, @Name("ladder") Ladder ladder, @Name("hungerDecay") boolean hungerDecay) {
    ladder.setHungerDecay(hungerDecay);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's hungerDecay.");
  }

  @Command("ladder showHP")
  @Permission("practice.admin")
  public void showHP(
          @Sender Player sender, @Name("ladder") Ladder ladder, @Name("showHP") boolean showHP) {
    ladder.setShowHP(showHP);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's showHP.");
  }


  @Command("ladder index")
  @Permission("practice.admin")
  public void index(
      @Sender Player sender, @Name("ladder") Ladder ladder, @Name("index") int index) {
    ladder.setIndex(index);
    Messenger.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's index.");
  }
}
