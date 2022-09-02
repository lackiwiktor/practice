package me.ponktacology.practice.ladder;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.ladder.command.LadderCommands;

import java.util.*;
import java.util.stream.Collectors;

public class LadderService extends Service {

  private final Map<String, Ladder> ladders = Maps.newHashMap();
  private final LadderRepository ladderRepository = new LadderRepository();

  @Override
  public void configure() {
    addCommand(new LadderCommands(this));

    ladderRepository
        .loadAll()
        .forEach(it -> this.ladders.put(it.getName().toUpperCase(Locale.ROOT), it));
  }

  @Override
  public void stop() {
    ladders.values().forEach(ladderRepository::save);
    ladders.clear();
  }

  public void createLadder(Ladder ladder) {
    this.ladders.put(ladder.getName().toUpperCase(Locale.ROOT), ladder);
    ladderRepository.saveAsync(ladder);
  }

  public void deleteLadder(Ladder ladder) {
    ladders.remove(ladder.getName().toUpperCase(Locale.ROOT));
    ladderRepository.deleteAsync(ladder);
  }

  public Ladder getLadderByName(String name) {
    return ladders.get(name.toUpperCase(Locale.ROOT));
  }

  public List<Ladder> getAllLadders() {
    return ladders.values().stream()
        .filter(Ladder::isSetup)
        .sorted(Comparator.comparingInt(Ladder::getIndex))
        .collect(Collectors.toList());
  }
}
