package me.ponktacology.practice.arena.thimble;

import com.google.common.collect.Sets;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaType;
import me.ponktacology.practice.arena.match.Restorable;
import me.ponktacology.practice.util.Region;
import me.ponktacology.practice.util.RegionAdapter;
import me.ponktacology.practice.util.serialization.LocationAdapter;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

@Getter
@Setter
public class ThimbleArena extends Arena implements Restorable {

  private final Set<Block> modifiedBlocks = Sets.newHashSet();
  private Location jumpingLocation;
  private Location spectatingLocation;
  private Region waterRegion;

  public ThimbleArena(String name) {
    super(name, ArenaType.THIMBLE);
  }

  @Override
  public Document getDocument() {
    Document document = super.getDocument();
    document.put("jumpingLocation", LocationAdapter.toJson(jumpingLocation));
    document.put("spectatingLocation", LocationAdapter.toJson(spectatingLocation));
    document.put("waterRegion", RegionAdapter.toJson(waterRegion));
    return document;
  }

  @Override
  public void applyDocument(Document document) {
    super.applyDocument(document);
    jumpingLocation = LocationAdapter.fromJson(document.getString("jumpingLocation"));
    spectatingLocation = LocationAdapter.fromJson(document.getString("spectatingLocation"));
    waterRegion = RegionAdapter.fromJson(document.getString("waterRegion"));
  }

  @Override
  public void restore() {
    waterRegion.forEachBlock(
        jumpingLocation.getWorld(), block -> block.setType(Material.STATIONARY_WATER));
  }


  public boolean isWaterRegionFull() {
    return false;
  }
}
