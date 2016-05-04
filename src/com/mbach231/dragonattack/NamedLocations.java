
package com.mbach231.dragonattack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 *
 */
public class NamedLocations {

    Set<ConfigLocation> namedLocationSet_;
    World world_;

    NamedLocations(World world, FileConfiguration config) {
        this.world_ = world;
        this.namedLocationSet_ = loadConfigLocations(config);
    }

    public Set<ConfigLocation> loadConfigLocations(FileConfiguration config) {

        Set<ConfigLocation> locSet = new HashSet();

        List<String> locList = config.getStringList("locations");

        for (String locString : locList) {
            String[] locSplit = locString.split(", ");

            // If incorrect length for line, skip
            if (locSplit.length == 3) {
                String name = locSplit[0];
                int x = Integer.valueOf(locSplit[1].trim());
                int z = Integer.valueOf(locSplit[2].trim());
                Location loc = new Location(world_, x, 62, z);

                locSet.add(new ConfigLocation(name, loc));
            }

        }

        return locSet;
    }

    public ConfigLocation getClosestNamedLocation(Location location) {

        ConfigLocation closest = null;
        double minDistance = 0;
        for (ConfigLocation namedLocation : namedLocationSet_) {

            String locString = namedLocation.getName();
            Location loc = namedLocation.getLocation();

            if (closest == null) {
                minDistance = loc.distance(location);
                closest = namedLocation;
                continue;
            }

            if (loc.distance(location) < minDistance) {
                minDistance = loc.distance(location);
                closest = namedLocation;
            }

        }

        return closest;
    }

    public Location getLocation(String location) {

        for (ConfigLocation namedLocation : namedLocationSet_) {
            if (location.equals(namedLocation.getName())) {
                return namedLocation.getLocation();
            }
        }
        return null;
    }

    public int get2dDistance(Location loc1, Location loc2) {
        int x1 = loc1.getBlockX();
        int z1 = loc1.getBlockZ();

        int x2 = loc2.getBlockX();
        int z2 = loc2.getBlockZ();

        int distance = (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));

        return distance;
    }

    
    public Set<Player> getLargestCluster(int clusterSearchRange) {
        if (!getPlayerClusters(clusterSearchRange).entrySet().isEmpty()) {
            return getPlayerClusters(clusterSearchRange).entrySet().iterator().next().getValue();
        }
        return null;
    }

    private Map<Player, Set<Player>> getPlayerClusters(int clusterSearchRange) {
        Map<Player, Set<Player>> clusterMap = new HashMap();
        ClusterComp comparator = new ClusterComp(clusterMap);
        Map<Player, Set<Player>> sortedClusterMap = new TreeMap(comparator);

        Player clustersPlayer = null;
        Set<Player> currentCluster = null;

        // Iterate through all players to put them into clusters
        for (Player currentPlayer : world_.getPlayers()) {

            boolean existsInCluster = false;

            // If this player is already in a cluster
            for (Map.Entry<Player, Set<Player>> clusterEntry : clusterMap.entrySet()) {

                // if the current player has already been put into a cluster, retrieve that
                // cluster information, allow the cluster to continue to grow
                if (clusterEntry.getValue().contains(currentPlayer)) {
                    clustersPlayer = clusterEntry.getKey();
                    currentCluster = clusterEntry.getValue();
                    existsInCluster = true;
                    break;
                }
            }

            // Otherwise this is a new cluster
            if (!existsInCluster) {
                clustersPlayer = currentPlayer;
                currentCluster = new HashSet();
            }

            // Iterate through all players to test for nearby players
            for (Player player : world_.getPlayers()) {
                if (currentPlayer.getLocation().distance(player.getLocation()) < clusterSearchRange) {
                    currentCluster.add(player);
                }
            }
            clusterMap.put(clustersPlayer, currentCluster);
        }

        sortedClusterMap.putAll(clusterMap);

        return sortedClusterMap;
    }

    public String getPlayerClusterOutput(int clusterSearchRange) {
        String string = "";

        Map<Player, Set<Player>> clusterMap = getPlayerClusters(clusterSearchRange);
        ConfigLocation closestLoc;

        for (Map.Entry<Player, Set<Player>> clusterEntry : clusterMap.entrySet()) {
            Player player = clusterEntry.getKey();
            
            string += player.getName();
            string += "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ")";

            closestLoc = getClosestNamedLocation(player.getLocation());
            if (closestLoc != null) {
                string += "[" + getClosestNamedLocation(player.getLocation()).getName() + ", ";
                string += get2dDistance(player.getLocation(), getClosestNamedLocation(player.getLocation()).getLocation()) + "]";
            } else {
                string += "[null,null]";
            }

            string += " : " + clusterEntry.getValue().size() + "\n";
        }
        return string;
    }
}

class ClusterComp implements Comparator<Player> {

    Map<Player, Set<Player>> base;

    public ClusterComp(Map<Player, Set<Player>> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(Player a, Player b) {
        if (base.get(a).size() >= base.get(b).size()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
