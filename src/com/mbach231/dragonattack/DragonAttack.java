package com.mbach231.dragonattack;

import com.mbach231.dragonattack.CustomDragons.DragonTypeEn;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class DragonAttack extends JavaPlugin implements Listener {

    public final static boolean TEST_MODE = true;


    World world_;

    Map<EnderDragon, Dragon> dragonMap_;
    NamedLocations namedLocations_;
    File configFile_;
    FileConfiguration config_;

    Map<UUID, DragonTypeEn> dragonTypeMap_;
    Set<EnderDragon> dragonSet_;
    public static Map<Fireball, EnderDragon> fireballMap_;
    public static Map<EnderDragon, Location> homeLocMap_;
    ProjectileManager projectileManager_;
    CustomLoot customLoot_;
    public static String debugString_ = "";
    boolean constantAttack_ = false;
    int attackNum_ = 0;
    String worldName_;
    int clusterSearchRadius_;
    long timeLastSpawn_;
    long spawnWaitTime_;
    int spawnClusterSize_;
    int spawnHeight_;
    int spawnRange_;
    int followRange_;
    double dropEggChance_;
    double dropLootChance_;
    boolean enableCompassTracking_;
    Material trackingCostMaterial_;
    int trackingCostCount_;
    ItemStack locateSacrificeItem_;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        dragonTypeMap_ = new HashMap();
        dragonSet_ = new HashSet();
        fireballMap_ = new HashMap();
        homeLocMap_ = new HashMap();

        projectileManager_ = new ProjectileManager();
        customLoot_ = new CustomLoot(this.getConfig());
        config_ = this.getConfig();
        dragonMap_ = new ConcurrentHashMap();

        getConfigVars();

        world_ = Bukkit.getWorld(worldName_);
        for (Entity entity : world_.getEntities()) {
            if (entity instanceof EnderDragon) {
                entity.remove();
            }
        }
        namedLocations_ = new NamedLocations(world_, config_);

        getServer().getPluginManager().registerEvents(this, this);

        handleAutoSpawn();
    }

    @Override
    public void onDisable() {
        for (Map.Entry<EnderDragon, Dragon> entry : dragonMap_.entrySet()) {
            Entity entity = entry.getKey();

            net.minecraft.server.v1_8_R3.Entity e = ((CraftEntity) entity).getHandle();
            e.getWorld().removeEntity(e);

        }
        for (Entity entity : world_.getEntities()) {
            if (entity instanceof EnderDragon) {
                net.minecraft.server.v1_8_R3.Entity e = ((CraftEntity) entity).getHandle();
                e.getWorld().removeEntity(e);
            }
        }
        dragonMap_.clear();
        getConfig().set("time-last-spawn", timeLastSpawn_);
        this.saveConfig();
    }

    private void getConfigVars() {
        worldName_ = getConfig().getString("spawn-rules.world-name");
        clusterSearchRadius_ = getConfig().getInt("spawn-rules.cluster-search-radius");
        spawnHeight_ = getConfig().getInt("spawn-rules.spawn-height");
        spawnRange_ = getConfig().getInt("spawn-rules.spawn-range");
        followRange_ = getConfig().getInt("spawn-rules.follow-range");
        timeLastSpawn_ = getConfig().getLong("time-last-spawn");
        spawnWaitTime_ = getConfig().getLong("spawn-rules.wait-time");
        spawnClusterSize_ = getConfig().getInt("spawn-rules.cluster-size");
        dropEggChance_ = getConfig().getDouble("drop-egg-chance");
        dropLootChance_ = getConfig().getDouble("drop-loot-chance");

        enableCompassTracking_ = getConfig().getBoolean("tracking.enable-compass-tracking");
        trackingCostMaterial_ = Material.getMaterial(getConfig().getString("tracking.cost-material"));
        trackingCostCount_ = getConfig().getInt("tracking.cost-count");
        locateSacrificeItem_ = new ItemStack(trackingCostMaterial_, trackingCostCount_);

    }

    private void handleAutoSpawn() {
        int minutesPerCheck = 1;

        final long spawnWaitTimeInHours = spawnWaitTime_ / 60;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {

                if (dragonMap_.isEmpty()) {
                    long currentTime = System.currentTimeMillis();
                    long timeSinceLastSpawn = currentTime - timeLastSpawn_; // ms
                    long hoursSinceLastSpawn = timeSinceLastSpawn / 1000 / 60 / 60;

                    // If enough time has passed
                    if (hoursSinceLastSpawn >= spawnWaitTimeInHours) {
                        // Get largest cluster
                        Set<Player> largestCluster = namedLocations_.getLargestCluster(clusterSearchRadius_);
                        if (largestCluster != null) {

                            // Extra check in case cluster is empty, cannot spawn to no players
                            if (!largestCluster.isEmpty() && largestCluster.size() >= spawnClusterSize_) {

                                // Get random player from set
                                Player player = largestCluster.iterator().next();

                                
                                
                                // Get random dragon type
                                //DragonTypeEn randomType = DragonTypeEn.values()[(int) (Math.random() * DragonTypeEn.values().length)];
                                DragonTypeEn randomType = CustomDragons.getRandomType();
                                
                                
                                summonDragonNearPlayer(player, randomType);

                                timeLastSpawn_ = currentTime;
                            }
                        }
                    }
                }
            }
        }, 20L, minutesPerCheck * 60 * 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (sender instanceof Player) {
            Player caller = (Player) sender;

            DragonTypeEn dragonType;
            if ((commandLabel.equalsIgnoreCase("dasummonrandom"))) {
                if (args.length != 1) {
                    return false;
                }

                dragonType = getType(args[0]);
                if (dragonType == null) {
                    return false;
                }

                List<Player> playerList = world_.getPlayers();
                Player chosenPlayer = playerList.get((int) Math.floor(Math.random() * playerList.size()));
                summonDragonToPlayer(chosenPlayer, dragonType);

            } else if ((commandLabel.equalsIgnoreCase("dasummonplayer"))) {
                if (args.length != 2) {
                    return false;
                }

                dragonType = getType(args[0]);
                if (dragonType == null) {
                    return false;
                }

                Player target = caller.getServer().getPlayer(args[1]);
                if (target == null) {
                    caller.sendMessage(ChatColor.GRAY + "Cannot find player " + args[0]);
                    return true;
                }
                summonDragonNearPlayer(target, dragonType);

            } else if ((commandLabel.equalsIgnoreCase("dasummonloc"))) {
                if (args.length != 4) {
                    return false;
                }

                dragonType = getType(args[0]);
                if (dragonType == null) {
                    return false;
                }

                int x, y, z;
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                    summonDragonToCoordinate(world_, x, y, z, dragonType);
                } catch (Exception e) {
                    return false;
                }
            } else if ((commandLabel.equalsIgnoreCase("daloot"))) {
                for (ItemStack item : customLoot_.getLootList()) {
                    world_.dropItem(caller.getLocation(), item);
                }
                world_.dropItem(caller.getLocation(), new ItemStack(Material.ARROW, 128));
            } else if ((commandLabel.equalsIgnoreCase("dakill"))) {

                for (Map.Entry<EnderDragon, Dragon> entry : dragonMap_.entrySet()) {

                    Entity entity = entry.getKey();

                    net.minecraft.server.v1_8_R3.Entity e = ((CraftEntity) entity).getHandle();
                    e.world.removeEntity(e);
                }

                caller.sendMessage(ChatColor.GRAY + "Removed " + dragonMap_.size() + " dragons!");

                for (Entity entity : world_.getEntities()) {
                    if (entity instanceof EnderDragon) {
                        entity.getLocation().getChunk().load();
                        entity.remove();
                        entity.getLocation().getChunk().unload();
                    }
                }
                dragonMap_.clear();


                if (constantAttack_) {
                    constantAttack_ = false;
                    caller.sendMessage(ChatColor.GRAY + "Stopping dragon attack!");
                }

            } else if ((commandLabel.equalsIgnoreCase("dacluster"))) {

                String clusterString = namedLocations_.getPlayerClusterOutput(100);
                caller.sendMessage(clusterString);

            } else if ((commandLabel.equalsIgnoreCase("dareload"))) {

                this.reloadConfig();
                config_ = this.getConfig();
                namedLocations_.loadConfigLocations(config_);
                caller.sendMessage("Config reloaded!");
            } else if ((commandLabel.equalsIgnoreCase("dahome"))) {
                for (Map.Entry<EnderDragon, Dragon> entry : dragonMap_.entrySet()) {
                    entry.getValue().gotoHome();
                }
                caller.sendMessage("Sent " + dragonMap_.size() + " dragons home!");
            }
        }
        return true;
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon)event.getEntity();
            dragonMap_.remove(dragon);
        }
    }
    
    /*
     Used for cleaning up dragons after reset.
     If a dragon is loaded into a chunk that does not exist in the current
     dragonMap_ (such as a dragon existing post-reset), it will be removed.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof EnderDragon) {
                EnderDragon ed = (EnderDragon) entity;

                boolean removeDragon = true;

                UUID uuid = ed.getUniqueId();

                for (Map.Entry<EnderDragon, Dragon> entry : dragonMap_.entrySet()) {
                    Entity dragonEntity = entry.getKey();
                    if (uuid.equals(dragonEntity.getUniqueId())) {
                        removeDragon = false;
                        break;
                    }
                }
                if (removeDragon) {
                    ed.remove();
                }
            }
        }
    }

    /*
     Non-functional for some reason since switching to 1.8.
     Currently, cancels damage on the dragon, but does not
     reapply damage, even if directly calling damage function
     on the entity. Needs more investigating, commented out the
     function until solution is found.
    
    
     // Cancel their flight pattern change when hit by arrow
     @EventHandler
     public void onDragonArrowHit(EntityDamageEvent event) {

     if (!(event.getEntity() instanceof EnderDragon)) {
     return;
     }
     EnderDragon dragon = (EnderDragon) event.getEntity();

     if (event instanceof EntityDamageByEntityEvent) {
     EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) event;
     Entity attacker = mobevent.getDamager();
     if (attacker instanceof Arrow) {

     // Cancel event
     //event.setCancelled(true);
                
     // Damage the dragon, will make sound and turn dragon red, will not cause
     // them to change flight direction
     //dragon.damage(event.getDamage());
                
     }
     }

     }
     */
    // Cancel portal creation. Add custom drops
    @EventHandler()
    public void onPortalCreate(EntityCreatePortalEvent event) {
        if (event.getPortalType() == PortalType.ENDER) {
            event.setCancelled(true);
        }

        if (Math.random() < dropEggChance_) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.DRAGON_EGG, 1));
        }

        if (Math.random() < dropLootChance_) {

            ItemStack randomItem = CustomLoot.getRandomItem();

            if (randomItem != null) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), randomItem);
            }
        }

    }

    
    // Prevents dragons from destroying environment and flying through blocks
    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity == null) {
            return;
        }

        Location entityLoc = entity.getLocation();

        if (entity instanceof EnderDragon) {
            if (entity.getLocation().getBlockY() < event.getLocation().getBlockY()) {
                entityLoc.setY(entityLoc.getY() - 1);
                entity.teleport(entityLoc);
            } else {
                entityLoc.setY(entityLoc.getY() + 1);
                entity.teleport(entityLoc);
            }
            event.blockList().clear();
        }
    }

    //: Detect fireball event
    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {

        if (event.getEntityType() == EntityType.ENDER_DRAGON) {

            EnderDragon enderDragon = (EnderDragon) event.getEntity();
            
            if (dragonMap_.containsKey(enderDragon)) {
                dragonMap_.get(enderDragon).handleTargetEvent(event);
            }

        }
    }

    // Apply effect to player if damaged by dragon of certain types
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getDamager();
            if (fireball.getShooter() instanceof EnderDragon) {

                event.setCancelled(false);

                if (event.getEntity() instanceof LivingEntity) {
                    EnderDragon dragonEntity = (EnderDragon) fireball.getShooter();
                    Dragon dragon = dragonMap_.get(dragonEntity);
                    if (dragon != null) {
                        dragon.handleFireballHit(event);
                    }
                }

            }
        }
    }

    
    // Handles player attempting to track dragons via right-clicking with a compass
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {

        if (enableCompassTracking_) {
            Player player = event.getPlayer();

            if (player.getItemInHand().getType().equals(Material.COMPASS)) {
                EnderDragon enderDragon = getClosestDragon(player.getLocation());

                if (enderDragon == null) {
                    player.sendMessage("There are no dragons to locate!");
                    return;
                }
                Dragon dragon = dragonMap_.get(enderDragon);

                if (dragon == null) {
                    player.sendMessage(ChatColor.RED + "Unexpected error occurred, could not locate dragon!");
                    return;
                }

                // Check for locate cost in player inventory
                if (player.getInventory().containsAtLeast(locateSacrificeItem_, locateSacrificeItem_.getAmount())) {
                    player.getInventory().removeItem(locateSacrificeItem_);
                    player.updateInventory();
                    String name = dragon.getDragonType().getName();
                    Location loc = enderDragon.getLocation();
                    int dist = (int) loc.distance(player.getLocation());
                    player.sendMessage(name + " located " + Integer.toString(dist) + "m away from you.");
                    //player.sendMessage(name + " located at: " + loc.getBlockX() + ", " + loc.getBlockZ());
                } else {
                    player.sendMessage("You lack the materials required to locate dragons!");
                }
            }
        }
    }

    public void summonDragonNearPlayer(Player player, DragonTypeEn type) {
        Location spawnLoc = adjustY(player.getLocation(), true);
        spawnLoc = adjustLocation(spawnLoc, 100);
        summonDragonToLocation(spawnLoc, type);
    }

    public void summonDragonToPlayer(Player player, DragonTypeEn type) {
        Location spawnLoc = adjustY(player.getLocation(), true);
        summonDragonToLocation(spawnLoc, type);
    }

    public void summonDragonToCoordinate(World world, int x, int y, int z, DragonTypeEn type) {
        summonDragonToLocation(new Location(world, x, y, z), type);
    }

    private void summonDragonToLocation(Location spawnLoc, DragonTypeEn type) {
        spawnLoc.getChunk().load();

        ConfigLocation closestNamedLocation = namedLocations_.getClosestNamedLocation(spawnLoc);

        Location homeLoc;
        String broadcastMsg;
        if (closestNamedLocation != null) {
            homeLoc = closestNamedLocation.getLocation();
            broadcastMsg = getAttackString(closestNamedLocation.getName(), spawnLoc);
        } else {
            homeLoc = spawnLoc;
            broadcastMsg = "Dragon sighted!";
        }

        Dragon dragon = new Dragon(type, spawnLoc, homeLoc, followRange_);
        dragonMap_.put(dragon.getDragon(), dragon);
        getLogger().log(Level.INFO, "Adding ID, type: {0}, {1}", new Object[]{dragon.getDragon().getUniqueId(), type.toString()});

        Bukkit.broadcastMessage(ChatColor.GRAY + broadcastMsg);
        world_.playSound(spawnLoc, Sound.ENDERDRAGON_GROWL, (float) 75, (float) 0.6);

    }

    private String getAttackString(String attackLoc, Location dragonLocation) {
        Location loc = namedLocations_.getLocation(attackLoc);

        if (loc != null) {
            
            // 2d distance, do not calculate with y-coord
            int distance = (int)Math.sqrt(Math.pow(loc.getX() - dragonLocation.getX(), 2) + Math.pow(loc.getZ() - dragonLocation.getZ(), 2));
            //double distance = loc.distance(dragonLocation);
            String string = "Dragon sighted " + Integer.toString(distance) + "m away from " + attackLoc + "!";

            return string;
        }

        return "A dragon has spawned!";
    }

    private DragonTypeEn getType(String string) {

        for (DragonTypeEn type : DragonTypeEn.values()) {
            if (type.toString().toLowerCase().equals(string.toLowerCase())) {
                return type;
            }
        }

        return null;
    }

    private Location adjustY(Location location, boolean defaultHeight) {
        if (defaultHeight == true) {
            location.setY(Math.max(spawnHeight_, location.getWorld().getHighestBlockYAt(location) + 50));
        } else {
            location.setY(Math.max(location.getBlockY(), location.getWorld().getHighestBlockYAt(location) + 50));
        }
        return location;
    }

    // Moves the dragon at most 'range' meters away from provided location at random
    private Location adjustLocation(Location location, int range) {

        double xDiff = Math.random() * range;
        double zDiff = Math.random() * range;

        if (Math.random() > 0.5) {
            xDiff = -xDiff;
        }
        if (Math.random() > 0.5) {
            zDiff = -zDiff;
        }

        location.setX(location.getX() + xDiff);
        location.setZ(location.getZ() + zDiff);

        return location;
    }

    private EnderDragon getClosestDragon(Location location) {
        EnderDragon dragon = null;
        double minDist = 0;
        double currentDist = 0;
        EnderDragon currentDragon;
        for (Map.Entry<EnderDragon, Dragon> entry : dragonMap_.entrySet()) {
            currentDragon = entry.getKey();

            currentDist = currentDragon.getLocation().distance(location);

            if (dragon == null) {
                dragon = currentDragon;
                minDist = currentDist;
                continue;
            }

            if (currentDist < minDist) {
                dragon = currentDragon;
                minDist = currentDist;
            }

        }
        return dragon;
    }

    public Map<String, Location> loadConfigLocations() {

        Map<String, Location> locMap = new HashMap();

        List<String> locList = (List<String>) this.getConfig().getStringList("locations");

        for (String locString : locList) {
            String[] locSplit = locString.split(", ");

            // If incorrect length for line, skip
            if (locSplit.length != 3) {
                continue;
            }

            String name = locSplit[0];
            Location loc = new Location(world_, Integer.getInteger(locSplit[1]), 62, Integer.getInteger(locSplit[2]));

            locMap.put(name, loc);

        }

        return locMap;
    }

}
