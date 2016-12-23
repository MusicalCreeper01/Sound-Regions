package keithcod.es.soundregions;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by ldd20 on 12/22/2016.
 */
public class SoundRegions extends JavaPlugin {
    public static SoundRegions INSTANCE;

    private static final Logger log = Logger.getLogger("Minecraft");

    WorldGuardPlugin plugin = WGBukkit.getPlugin();

    @Override
    public void onEnable() {
        INSTANCE = this;

        if(!getConfig().isConfigurationSection("enter"))
            getConfig().createSection("enter");
        if(!getConfig().isConfigurationSection("exit"))
            getConfig().createSection("exit");

        if(!getConfig().isConfigurationSection("meta"))
            getConfig().createSection("meta");

        saveConfig();

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("soundregion").setExecutor(new Command());
    }

    public WorldGuardPlugin worldGuardPlugins (){
        return plugin;
    }


    @Override
    public void onDisable(){

    }
}
