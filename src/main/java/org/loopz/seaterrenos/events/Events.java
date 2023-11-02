package org.loopz.seaterrenos.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.loopz.seaterrenos.Main;
import org.loopz.seaterrenos.manager.Terrain;

public class Events implements Listener {

    private final Main plugin;
    
    public Events(Main plugin) {
     this.plugin = plugin;   
    }
    
    
    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();

        if (plugin.terrainDAO.isInTerrain(event.getBlock().getLocation())) {
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(event.getBlock().getLocation());
            if(terrain.getOwner().equals(p.getName().toUpperCase())) {
                return;
            }
            if (!terrain.getFriends().containsKey(p.getName())) {
                event.setCancelled(true);
                return;
            }
            if(event.getBlock().getType().equals(Material.MOB_SPAWNER)) {
                if(!terrain.getPermissions(p.getName()).isBreakSpawners()) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            if(!terrain.getPermissions(p.getName()).isBreakBlocks()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void placeblock(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (plugin.terrainDAO.isInTerrain(event.getBlock().getLocation())) {
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(event.getBlock().getLocation());
            if(terrain.getOwner().equals(p.getName().toUpperCase())) {
                return;
            }
            if (!terrain.getFriends().containsKey(p.getName())) {
                event.setCancelled(true);
                return;
            }
            if(!terrain.getPermissions(p.getName()).isPlaceBlocks()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void explode(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (plugin.terrainDAO.isInTerrain(event.getClickedBlock().getLocation())) {
            Player p = event.getPlayer();
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(event.getClickedBlock().getLocation());
            if(terrain.getOwner().equals(p.getName().toUpperCase())) {
                return;
            }
            if (!terrain.getFriends().containsKey(p.getName())) {
                event.setCancelled(true);
                return;
            }
            Block block = event.getClickedBlock();
            if(block != null && canPlaceItemOnBlock(event.getClickedBlock().getLocation(), p)) {
                if(!terrain.getPermissions(p.getName()).isPlaceBlocks()) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            if (block != null && block.getType() == Material.WOODEN_DOOR) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(!terrain.getPermissions(p.getName()).isInteractDoors()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
    
            if(!terrain.getPermissions(p.getName()).isInteract()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interact2(PlayerInteractAtEntityEvent event) {
        if (plugin.terrainDAO.isInTerrain(event.getRightClicked().getLocation())) {
            Player p = event.getPlayer();
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(event.getRightClicked().getLocation());
            if (!terrain.getPermissions(p.getName()).isInteract()) {
                event.setCancelled(true);
            }
        }
    }

    public boolean canPlaceItemOnBlock(Location location, LivingEntity entity) {
        Block block = location.getBlock();
        ItemStack itemStack = entity.getEquipment().getItemInHand();
        return block.getType().isSolid() && block.getRelative(BlockFace.UP).isEmpty();
    }

}
