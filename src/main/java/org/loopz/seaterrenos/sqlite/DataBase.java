package org.loopz.seaterrenos.sqlite;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.loopz.seaterrenos.Main;
import org.loopz.seaterrenos.manager.Terrain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBase {

    private final Main plugin;

    public DataBase(Main plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement stm = null;

        try {
            stm = this.plugin.connection.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS SeaTerrenos (id INTEGER PRIMARY KEY, owner varChar(16) NOT NULL, clan boolean, name varChar(16) NOT NULL, icon varChar(20) NOT NULL, desc TEXT NOT NULL, world varChar(20) NOT NULL, x_min INTEGER NOT NULL, x_max INTEGER NOT NULL, z_min INTEGER NOT NULL, z_max INTEGER NOT NULL, y INTEGER NOT NULL, friends TEXT);");
            stm.executeUpdate();
        } catch (Exception var11) {
            var11.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var10) {
                var10.printStackTrace();
            }

        }
    }


    public void saveAreas() {
        PreparedStatement stmt = null;
        try {
            stmt = this.plugin.connection.getConnection().prepareStatement("INSERT OR REPLACE INTO SeaTerrenos (id, owner, clan, name, icon, desc, world, x_min, z_min, x_max, z_max, y, friends) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Terrain terrain : plugin.terrainDAO.getAllTerrains()) {
                String desc = String.join( ",", terrain.getDesc());
                stmt.setInt(1, terrain.getId());
                stmt.setString(2, terrain.getOwner().toUpperCase());
                stmt.setBoolean(3, terrain.isClan());
                stmt.setString(4, terrain.getName());
                stmt.setString(5, terrain.getIcon());
                stmt.setString(7, desc);
                stmt.setString(8, terrain.getWorld().getName());
                stmt.setDouble(9, terrain.getMinX());
                stmt.setDouble(10, terrain.getMinZ());
                stmt.setDouble(11, terrain.getMaxX());
                stmt.setDouble(12, terrain.getMaxZ());
                stmt.setDouble(13, terrain.getY());
                stmt.setString(14, plugin.terrainDAO.getTerrainFriends(terrain.getFriends()));
                stmt.executeUpdate();
            }
        } catch (Exception var) {
            var.printStackTrace();
        } finally {
            try {
                assert stmt != null;
                stmt.close();
            } catch (SQLException var) {
                var.printStackTrace();
            }
        }
    }

    public void loadAreas() {
        PreparedStatement stm = null;
        try {
            stm = this.plugin.connection.getConnection().prepareStatement("SELECT * FROM SeaTerrenos");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String owner = rs.getString("owner");
                boolean clan = rs.getBoolean("clan");
                String name = rs.getString("name");
                String icon = rs.getString("icon");
                String desc = rs.getString("desc");
                World world = Bukkit.getWorld(rs.getString("world"));
                double minX = rs.getDouble("x_min");
                double minZ = rs.getDouble("z_min");
                double maxX = rs.getDouble("x_max");
                double maxZ = rs.getDouble("z_max");
                double y = rs.getDouble("y");
                String[] elementos = desc.split(",");
                List<String> desclist = new ArrayList<>(Arrays.asList(elementos));
                plugin.terrainDAO.addTerrain(id, owner.toUpperCase(), clan, name, icon, desclist, world, minX, maxX, minZ, maxZ, y);
                if(clan) {
                    Location blockLocation = new Location(world, maxX, y, minX);
                    ArmorStand armorStand = (ArmorStand) world.spawnEntity(blockLocation, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setCustomNameVisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCustomName("§b" + clan);
                    return;
                }
                String[] friends = rs.getString("friends").split(";");
                for (String friend : friends) {
                    plugin.terrainDAO.setTerrainFriends(plugin.terrainDAO.getTerrainById(id), friend);
                }
            }
        } catch (Exception var) {
            var.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var) {
                var.printStackTrace();
            }
        }
    }




    /*
    public boolean isInTerrain(Location loc) {
        PreparedStatement stm = null;
        try {
            stm = this.plugin.connection.getConnection().prepareStatement("SELECT * FROM SeaTerrenos WHERE x_min <= ? AND ? <= x_max AND z_min <= ? AND ? <= z_max");
            stm.setInt(1, loc.getBlockX());
            stm.setInt(2, loc.getBlockX());
            stm.setInt(3, loc.getBlockZ());
            stm.setInt(4, loc.getBlockZ());
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (Exception var) {
            var.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var) {
                var.printStackTrace();
            }
        }
        return false;
    }
*/
    /*
    public boolean isOwner(Player p) {
        PreparedStatement stm = null;
        try {
            stm = this.plugin.connection.getConnection().prepareStatement("SELECT * FROM SeaTerrenos WHERE owner = ? AND x_min <= ? AND x_max >= ? AND z_min <= ? AND z_max >= ?");
            stm.setString(1, p.getName().toUpperCase());
            stm.setInt(2, p.getLocation().getBlockX());
            stm.setInt(3, p.getLocation().getBlockX());
            stm.setInt(4, p.getLocation().getBlockZ());
            stm.setInt(5, p.getLocation().getBlockZ());
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (Exception var) {
            var.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var) {
                var.printStackTrace();
            }
        }
        return false;
    }
 */
    /*
    public void createTerrain(String Owner, World world, double xmin, double xmax, double zmin, double zmax) {
        PreparedStatement stm = null;

        try {
            stm = this.plugin.connection.getConnection().prepareStatement("INSERT INTO SeaTerrenos (owner, x_min, x_max, z_min, z_max) VALUES (?,?,?,?,?)");
            stm.setString(1, Owner.toUpperCase());
            stm.setDouble(2, xmin);
            stm.setDouble(3, xmax);
            stm.setDouble(4, zmin);
            stm.setDouble(5, zmax);
            stm.executeUpdate();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var13) {
                var13.printStackTrace();
            }
        }
    }

    */
    /*
    public boolean hasTerrain(double xMax, double xMin, double zMax, double zMin) {
        PreparedStatement stm = null;
        try {
            stm = this.plugin.connection.getConnection().prepareStatement("SELECT * FROM SeaTerrenos WHERE x_min <= " + xMax + " AND x_max >= " + xMin + " AND z_min <= " + zMax + " AND z_max >= " + zMin);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (Exception var) {
            var.printStackTrace();
        } finally {
            try {
                assert stm != null;
                stm.close();
            } catch (SQLException var) {
                var.printStackTrace();
            }
        }
        return false;
    }
    */
}
