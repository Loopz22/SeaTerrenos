package org.loopz.seaterrenos.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;

public class TerrainDAO {

    public final Map<Integer, Terrain> areas = new HashMap<>();


    public Terrain getTerrainById(int id) {
        return this.areas.get(id);
    }

    public void addTerrain(int id, String owner, boolean clan, String name, String icon, List<String> desc, World world, double minX, double maxX, double minZ, double maxZ, double y) {
        val terrain = new Terrain(id, owner.toUpperCase(), clan, name, icon, desc, world, minX, maxX, minZ, maxZ, y);
        this.areas.put(id, terrain);
    }

    public void addFriend(Terrain terrain, String name) {
        Friend friend = new Friend(name, new Permissions());
        Map<String, Friend> friends = terrain.getFriends();
        if (!friends.containsKey(name)) {
            friends.put(name, friend);
        }
    }

    public void setFriend(Terrain terrain, String name, Permissions permissions) {
        Friend friend = new Friend(name, permissions);
        terrain.getFriends().put(name, friend);
    }

    public String getTerrainFriends(Map<String, Friend> friends) {
        Gson gson = new Gson();
        List<Friend> friendsList = new ArrayList<>(friends.values());
        return gson.toJson(friendsList);
    }
    public void setTerrainFriends(Terrain terrain, String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Friend>>(){}.getType();
        List<Friend> friendsList = gson.fromJson(json, type);
        for (Friend friend : friendsList) {
            setFriend(terrain, friend.getName(), friend.getPermissions());
        }
    }

    public Collection<String> getAllFriends(Map<String, Friend> friends) {
        return friends.keySet();
    }

    public boolean isInTerrain(Location loc) {
        int playerX = loc.getBlockX();
        int playerZ = loc.getBlockZ();

        for (Terrain terrain : areas.values()) {
            if (playerX >= terrain.getMinX() && playerX <= terrain.getMaxX() && playerZ >= terrain.getMinZ() && playerZ <= terrain.getMaxZ()) {
                return true;
            }
        }

        return false;
    }

    public List<Terrain> getAllTerrains() {
        return new ArrayList<>(areas.values());
    }

    public Terrain getTerrainByLocation(Location location) {
        double locationX = location.getX();
        double locationZ = location.getZ();

        for (Terrain terrain : areas.values()) {
            double areaX1 = terrain.getMinX();
            double areaX2 = terrain.getMaxX();
            double areaZ1 = terrain.getMinZ();
            double areaZ2 = terrain.getMaxZ();

            if (locationX >= areaX1 && locationX <= areaX2 && locationZ >= areaZ1 && locationZ <= areaZ2) {
                return terrain;
            }
        }

        return null;
    }


    public Terrain getTerrainByOwner(String ownerName) {
        for (Terrain terrain : areas.values()) {
            if (terrain.getOwner().equals(ownerName)) {
                return terrain;
            }
        }
        return null;
    }
    public int getTerrainArea(Terrain terrain) {

        return (int) ((terrain.getMaxX() - terrain.getMinX()) - 1);
    }

    public int getTerrainPrice(Terrain terrain, int area) {
        Map<Biome, Integer> biomeCount = new HashMap<>();
        int totalBiomes = 0;
        double minx = terrain.getMinX();
        double maxx = terrain.getMaxX();
        double minz = terrain.getMinZ();
        double maxz = terrain.getMaxZ();
        for (double x = minx; x <= maxx; x++) {
            for (double z = minz; z <= maxz; z++) {
                Biome biome = terrain.getWorld().getBiome((int) x, (int) z);
                biomeCount.put(biome, biomeCount.getOrDefault(biome, 0) + 1);
                totalBiomes++;
            }
        }

        Biome mostCommonBiome = null;
        int mostCommonCount = 0;
        for (Map.Entry<Biome, Integer> entry : biomeCount.entrySet()) {
            if (entry.getValue() > mostCommonCount) {
                mostCommonBiome = entry.getKey();
                mostCommonCount = entry.getValue();
            }
        }

        int averagePrice = 0;
        for (Map.Entry<Biome, Integer> entry : biomeCount.entrySet()) {
            averagePrice += entry.getValue() * getBiomePrice(entry.getKey()) / totalBiomes;
        }

        return mostCommonBiome != null ? getBiomePrice(mostCommonBiome) * area : averagePrice * area;
    }

    private int getBiomePrice(Biome biome) {
        switch (biome) {
            case FOREST:
                return 3000;
            case OCEAN:
                return 2500;
            case PLAINS:
                return 2700;
            case JUNGLE:
                return 2200;
            case DESERT:
                return 1000;
            default:
                return 2000;
        }
    }

    public int calculatePrice(Player p, int area) {

        Biome biome = p.getLocation().getWorld().getBiome(p.getLocation().getBlockX(), p.getLocation().getBlockZ());

        switch (biome) {
            case FOREST:
                return area * 3000;
            case OCEAN:
                return area * 2500;
            case PLAINS:
                return area * 2700;
            case JUNGLE:
                return area * 2200;
            case DESERT:
                return area * 1000;
            default:
                return area * 2000;
        }
    }

    public boolean hasTerrain(Player p, double xMin, double xMax, double zMin, double zMax) {
        for (Terrain terrain : areas.values()) {
            if ((xMin < terrain.getMaxX()) && (xMax > terrain.getMinX()) && (zMin < terrain.getMaxZ()) && (zMax > terrain.getMinZ())) {
                return true;
            }
            if (!Objects.equals(terrain.getOwner().toUpperCase(), p.getName().toUpperCase())) {
                if (!(xMin - 1 > terrain.getMaxX()) && !(xMax + 1 < terrain.getMinX()) && !(zMin - 1 > terrain.getMaxZ()) && !(zMax + 1 < terrain.getMinZ())) {
                    return true;
                }
            } else {
                /*if (!(xMin + 1 > terrain.getMaxX()) && !(xMax - 1 < terrain.getMinX()) && !(zMin + 1 > terrain.getMaxZ()) && !(zMax - 1 < terrain.getMinZ())) {
                    return true;
                }*/
                if (!(xMin > terrain.getMaxX()) && !(xMax < terrain.getMinX()) && !(zMin > terrain.getMaxZ()) && !(zMax < terrain.getMinZ())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean verifyMerge(Terrain terrain) {
        double x1 = (terrain.getMinX() + terrain.getMaxX()) / 2;
        double z1 = (terrain.getMinZ() + terrain.getMaxZ()) / 2;
        for (Terrain terrains : areas.values()) {
            double x2 = (terrains.getMinX() + terrains.getMaxX()) / 2;
            double z2 = (terrains.getMinZ() + terrains.getMaxZ()) / 2;
            double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
            if (distance > 1) {
                return false;
            }
        }
        return true;
    }
}
