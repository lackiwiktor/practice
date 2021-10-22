package country.pvp.practice;

import country.pvp.practice.data.DataRepository;
import country.pvp.practice.itembar.ItemBarListener;
import country.pvp.practice.kit.Kit;
import country.pvp.practice.player.PreparePlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

  @Override
  public void onEnable() {
    DataRepository.connect(
        "mongodb+srv://ponktacology:yHzd9Qcg7u1f3Q3H@cluster0.zch1g.mongodb.net/myFirstDatabase?retryWrites=true&w=majority",
        "practice");
    Kit.load();
    Bukkit.getPluginManager().registerEvents(new ItemBarListener(), this);
    Bukkit.getPluginManager().registerEvents(new PreparePlayerListener(), this);
  }
}
