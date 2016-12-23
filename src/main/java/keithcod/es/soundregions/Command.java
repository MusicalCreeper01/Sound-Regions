package keithcod.es.soundregions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by ldd20 on 12/22/2016.
 */
public class Command implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender var1, org.bukkit.command.Command var2, String var3, String[] var4) {

        if(!(var1 instanceof Player)) {
            var1.sendMessage("Command can only be ran as player");
            return false;
        }

        if (var4.length >= 2){
            String regionid = var4[0];
            if(ProtectedRegion.isValidId(regionid)){
                World world = ((Player) var1).getWorld();
                SoundRegions.INSTANCE.reloadConfig();
                Configuration config = SoundRegions.INSTANCE.getConfig();

                ConfigurationSection configEnter = config.getConfigurationSection("enter");
                ConfigurationSection configLeave = config.getConfigurationSection("exit");

                if(SoundRegions.INSTANCE.worldGuardPlugins().getRegionManager(world).hasRegion(regionid)){
                    if(var4[1].equals("remove")){
                        if(configEnter.contains(regionid)) {
                            var1.sendMessage(ChatColor.GREEN + " Region \"" + regionid + "\" removed from enter sound list!");
                            configEnter.set(regionid, null);
                            SoundRegions.INSTANCE.saveConfig();
                        }
                        if(configLeave.contains(regionid)) {
                            var1.sendMessage(ChatColor.GREEN + " Region \"" + regionid + "\" removed from exit sound list!");
                            configLeave.set(regionid, null);
                            SoundRegions.INSTANCE.saveConfig();
                        }
                        var1.sendMessage(ChatColor.GREEN + " Removed region \"" + regionid + "\" from all lists!");

                        return true;
                    }else{
                        if (var4.length == 3){
                            String sound = var4[2];
                            var1.sendMessage(ChatColor.GREEN + " Using sound \""+sound+"\"...");

                            if(var4[1].equals("enter")){
                                configEnter.set(regionid, sound);
                                var1.sendMessage(ChatColor.GREEN + " Added to enter list for region \""+regionid+"\"!");
                                SoundRegions.INSTANCE.saveConfig();
                                populateMeta(sound);
                                return true;
                            } else if(var4[1].equals("exit")){
                                configLeave.set(regionid, sound);
                                var1.sendMessage(ChatColor.GREEN + " Added to exit list for region \""+regionid+"\"!");
                                SoundRegions.INSTANCE.saveConfig();
                                populateMeta(sound);
                                return true;
                            }else{
                                var1.sendMessage(ChatColor.RED + " Invalid operation \""+var4[1]+"\"!");

                                return false;
                            }
                        }else{
                            var1.sendMessage(ChatColor.RED + " Please specify a sound!");
                            return false;
                        }
                    }
                }else {
                    var1.sendMessage(ChatColor.RED + " Region \"" + regionid + "\" does not exists in the world \""+world.getName()+"\"!");
                    return true;
                }

            }else {
                var1.sendMessage(ChatColor.RED + " Invalid region id format \"" + regionid + "\"!");
                return true;
            }
        }
        var1.sendMessage(ChatColor.RED + " Error!");
        return false;
    }


    public void populateMeta(String sound){
        Configuration config = SoundRegions.INSTANCE.getConfig();

        ConfigurationSection configMeta = config.getConfigurationSection("meta");

        if(!configMeta.contains(sound)){
            ConfigurationSection meta = configMeta.createSection(sound);
            meta.set("name", sound);
            meta.set("loop", false);
            meta.set("length", 0);
            meta.set("random.enabled", false);
            meta.set("random.min", 2);
            meta.set("random.max", 5);
        }

        SoundRegions.INSTANCE.saveConfig();
    }
}

