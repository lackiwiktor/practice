package me.ponktacology.practice.arena.match;

import com.google.common.collect.Sets;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaType;
import me.ponktacology.practice.util.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchArena extends Arena {

  @Getter private final Set<MatchArenaCopy> copies = Sets.newHashSet();
  @Getter @Setter private int gridIndex;

  public MatchArena(String name) {
    super(name, ArenaType.MATCH);
  }

  @Override
  public Document getDocument() {
    List<Document> copyDocuments =
        copies.stream().map(MatchArenaCopy::getDocument).collect(Collectors.toList());

    Document document = super.getDocument();
    document.put("copies", copyDocuments);

    return document;
  }

  @Override
  public void applyDocument(Document document) {
    super.applyDocument(document);

    document
        .getList("copies", Document.class)
        .forEach(
            it -> {
              MatchArenaCopy arenaCopy =
                  new MatchArenaCopy(UUID.fromString(it.getString("_id")), this);
              arenaCopy.applyDocument(it);
              copies.add(arenaCopy);
            });

    Logger.log("Loaded %d copies of %s arena.", copies.size(), getName());
  }

  public void addCopies(Set<MatchArenaCopy> copies) {
    this.copies.addAll(copies);
  }

  public @Nullable MatchArenaCopy getAvailableCopy() {
    return copies.stream().filter(it -> !it.isOccupied()).findFirst().orElse(null);
  }

  public File getSchematic() {
    return new File(
        Practice.getPractice().getDataFolder(),
        "schematics" + File.separator + getName() + ".schematic");
  }
}
