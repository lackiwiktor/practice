package country.pvp.practice.arena.duplicated;

import country.pvp.practice.arena.Arena;
import org.bson.Document;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class DuplicatedArena extends Arena {

    private final UUID id;
    private final Arena parent;
    private int offset;

    public DuplicatedArena(UUID id, Arena parent) {
        super(parent.getName());
        this.parent = parent;
        this.id = id;
    }

    public DuplicatedArena(UUID id, Arena parent, int offset) {
        super(parent.getName());
        this.id = id;
        this.parent = parent;
        this.offset = offset;
    }

    public static DuplicatedArena from(UUID id, Arena arena) {
        return new DuplicatedArena(id, arena);
    }

    public static DuplicatedArena from( Arena arena, int offset) {
        return new DuplicatedArena(UUID.randomUUID(), arena, offset);
    }

    @Override
    public String getCollection() {
        return "duplicated_arenas";
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());
        document.put("parent", parent.getId());
        document.put("offset", offset);

        return document;
    }

    @Override
    public void applyDocument( Document document) {
        offset = document.getInteger("offset");
    }

    @Override
    public @Nullable Location getSpawnLocation1() {
        return applyOffset(super.getSpawnLocation1());
    }

    @Override
    public @Nullable Location getSpawnLocation2() {
        return applyOffset(super.getSpawnLocation2());
    }

    @Override
    public @Nullable Location getSpectatorLocation() {
        return applyOffset(super.getSpectatorLocation());
    }

    public @Nullable Location applyOffset(@Nullable Location location) {
        if (location == null) return null;

        return location.clone().add(offset, 0, offset);
    }
}
