package org.loopz.seaterrenos.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Terrain {

    private int id;
    private String owner;
    private boolean clan;
    private String name;
    private String icon;
    private List<String> desc;
    private World world;
    private double minX;
    private double maxX;
    private double minZ;
    private double maxZ;
    private double y;

    public final Permissions permissions;

    private Map<String, Friend> friends;

    public Terrain(int id, String owner, boolean clan, String name, String icon, List<String> desc, World world, double minX, double maxX, double minZ, double maxZ, double y) {
        this.id = id;
        this.owner = owner;
        this.clan = clan;
        this.name = name;
        this.icon = icon;
        this.desc = desc;
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.y = y;
        this.friends = new HashMap<>();
        this.permissions = new Permissions();
    }

    public Map<String, Friend> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Friend> friends) {
        this.friends = friends;
    }

    public Permissions getPermissions(String name) {
        return friends.get(name).getPermissions();
    }

    public void mergeTerrain(Terrain terrain) {
        this.maxX = Math.max(this.maxX, terrain.maxX);
        this.minX = Math.min(this.minX, terrain.minX);
        this.maxZ = Math.max(this.maxZ, terrain.maxZ);
        this.minZ = Math.min(this.minZ, terrain.minZ);
        this.friends = combineFriends(this.friends, terrain.friends);
    }

    private Map<String, Friend> combineFriends(Map<String, Friend> friends1, Map<String, Friend> friends2) {
        Map<String, Friend> combinedFriends = new HashMap<>(friends1);
        for (Map.Entry<String, Friend> entry : friends2.entrySet()) {
            if (!combinedFriends.containsKey(entry.getKey())) {
                combinedFriends.put(entry.getKey(), entry.getValue());
            }
        }
        return combinedFriends;
    }

}

