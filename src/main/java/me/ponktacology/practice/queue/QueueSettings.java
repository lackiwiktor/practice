package me.ponktacology.practice.queue;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.ponktacology.practice.settings.Setting;
import me.ponktacology.practice.settings.Settings;
import me.ponktacology.practice.util.data.SerializableObject;
import org.bson.Document;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

@Getter
public class QueueSettings implements Settings, SerializableObject {

  private final Setting<Integer> pingFactor =
          new Setting<>(
                  "Ping Range",
                  "Preferred difference in ping.",
                  Material.STICK,
                  Lists.newArrayList(
                          new Setting.Option<>("Unrestricted", -1),
                          new Setting.Option<>("300 ms", 300),
                          new Setting.Option<>("200 ms", 200),
                          new Setting.Option<>("150 ms", 150),
                          new Setting.Option<>("100 ms", 100),
                          new Setting.Option<>("50 ms", 50),
                          new Setting.Option<>("25 ms", 25)));

  @Override
  public List<Setting<?>> getSettings() {
    return Collections.singletonList(pingFactor);
  }

  @Override
  public Document getDocument() {
    Document document = new Document();

    for (Setting<?> setting : getSettings()) {
      document.append(setting.getName(), setting.getDocument());
    }

    return document;
  }

  @Override
  public void applyDocument(Document document) {
    for (Setting<?> setting : getSettings()) {
      setting.applyDocument(document.get(setting.getName(), Document.class));
    }
  }
}
