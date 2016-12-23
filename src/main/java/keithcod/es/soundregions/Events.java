package keithcod.es.soundregions;


import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events implements Listener {

    public Map<String, Material> records = new HashMap<>();

//    public Map<Player, String> playerrecords = new HashMap<>();
    public Map<Player, List<Location>> playerrecordloc = new HashMap<>();

    public Events (){
        records.put("13", Material.GOLD_RECORD);
        records.put("cat", Material.GREEN_RECORD);

        records.put("blocks", Material.RECORD_3);
        records.put("chirp", Material.RECORD_4);
        records.put("far", Material.RECORD_5);
        records.put("mall", Material.RECORD_6);
        records.put("mellohi", Material.RECORD_7);
        records.put("stal", Material.RECORD_8);
        records.put("strad", Material.RECORD_9);

        records.put("ward", Material.RECORD_10);
        records.put("11", Material.RECORD_11);
        records.put("wait", Material.RECORD_12);
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {
        StopPlaying(e.getPlayer());

        Configuration config = SoundRegions.INSTANCE.getConfig();

        ConfigurationSection configEnter = config.getConfigurationSection("enter");

        if(configEnter.contains(e.getRegion().getId())){
            String sound = configEnter.getString(e.getRegion().getId());
            PlaySound(e.getRegion(), e.getPlayer(), sound);

        }

        //e.getPlayer().sendMessage("You just entered " + e.getRegion().getId());
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent e) {
        StopPlaying(e.getPlayer());

        Configuration config = SoundRegions.INSTANCE.getConfig();

        ConfigurationSection configLeave = config.getConfigurationSection("exit");

        if(configLeave.contains(e.getRegion().getId())){
            String sound = configLeave.getString(e.getRegion().getId());
            PlaySound(e.getRegion(), e.getPlayer(), sound);

        }

        //e.getPlayer().sendMessage("You just left " + e.getRegion().getId());
        /*if (e.getRegion().getId().equals("jail") && e.isCancellable()) // you cannot cancel the event if the player left the region because he died
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage("You cannot leave the jail!");
        }*/
    }

    public void StopPlaying(Player player){
        //player.playEffect(player.getLocation(), Effect.RECORD_PLAY, Material.AIR.getId());
        if(playerrecordloc.containsKey(player)){
//            stopRecord(player, playerrecordloc.get(player) );
            for(Location l : playerrecordloc.get(player)){
                player.playEffect(l, Effect.RECORD_PLAY, 0);
            }

            playerrecordloc.remove(player);
//            playerrecords.remove(player);
            //player.sendMessage("Stopping sounds...");
        }

    }

    public void PlaySound(ProtectedRegion region, final Player player, String sound){
        SoundRegions.INSTANCE.reloadConfig();
        Configuration config = SoundRegions.INSTANCE.getConfig();

        ConfigurationSection configMeta = config.getConfigurationSection("meta");

        if(sound.startsWith("record:")){
            String record = sound.split(":")[1].toLowerCase();
            if(records.containsKey(record)){
                //player.sendMessage("Playing record \""+record+"\"");
                Material mat = records.get(record);
                //player.playEffect(player.getLocation(), Effect.RECORD_PLAY, mat.getId());
                Location loc = player.getLocation();
//                playRecord(player, loc, mat);

                int minx = region.getMinimumPoint().getBlockX();
                int miny = region.getMinimumPoint().getBlockY();
                int minz = region.getMinimumPoint().getBlockZ();

                int maxx = region.getMaximumPoint().getBlockX();
                int maxy = region.getMaximumPoint().getBlockY();
                int maxz = region.getMaximumPoint().getBlockZ();

                int steps = 40;

                playerrecordloc.put(player, new ArrayList<>());
                for(int y = miny; y < maxy; y += steps){
                    for(int x = minx; x < maxx; x += steps){
                        for(int z = minz; z < maxz; z += steps){
                            loc = new Location(player.getWorld(), x, y, z);
                            player.playEffect(loc, Effect.RECORD_PLAY, mat.getId());
                            playerrecordloc.get(player).add(loc);
                        }
                    }
                }


            }else{
                System.err.println("Record \""+record+"\" does not exist!");
            }
        }else{
            //player.sendMessage("Playing sound \""+sound+"\"");
            player.playSound(player.getLocation(), sound, 20f, 20f);
        }

        if(configMeta.contains(sound)) {
            ConfigurationSection meta = configMeta.getConfigurationSection(sound);
            if(meta.getBoolean("loop", false) == true){
                int length = meta.getInt("length", 20);

                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(SoundRegions.INSTANCE, () -> {
                    Location loc = player.getLocation();
                    StopPlaying(player);

                    if(region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                        PlaySound(region, player, sound);
                      //  player.sendMessage("Looping...");
                    }
                }, length);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        /*if(playerrecords.containsKey(e.getPlayer())){
            PlaySound(e.getPlayer(), playerrecords.get(e.getPlayer()));
        }*/
    }

    /*@SuppressWarnings("deprecation")
    public static void playRecord(Player p, Location loc, Material record)
    {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldEvent(1005, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), record.getId(), false));
    }

    public static void stopRecord(Player p, Location loc)
    {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldEvent(1005, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), 0, false));
    }*/

}
