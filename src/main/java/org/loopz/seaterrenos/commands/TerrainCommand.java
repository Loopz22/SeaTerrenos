package org.loopz.seaterrenos.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.loopz.seaterrenos.Main;
import org.loopz.seaterrenos.api.NumberFormatter;
import org.loopz.seaterrenos.fastinv.FastInv;
import org.loopz.seaterrenos.fastinv.ItemBuilder;
import org.loopz.seaterrenos.manager.Terrain;

import java.util.ArrayList;
import java.util.List;

public class TerrainCommand implements CommandExecutor, Listener {

    private final Main plugin;
    public ArrayList<String> terraincreator = new ArrayList<String>();
    public ArrayList<String> terraincreator2 = new ArrayList<String>();
    public ArrayList<String> terrainaddmember = new ArrayList<String>();
    private final ArrayList<String> terrainsetname = new ArrayList<String>();
    private final ArrayList<String> terrainseticon = new ArrayList<String>();
    private final ArrayList<String> terrainsetdesc = new ArrayList<String>();

    public TerrainCommand(Main plugin) {
        this.plugin = plugin;
    }

//PLAYER TERRAIN
    public void inv(Player player) {
        NumberFormatter numberFormatter = new NumberFormatter();
        FastInv inv = new FastInv(27, "Terrenos - Menu");
        int terrains = 0;
        int patrimonio = 0;
        for (Terrain terrain : plugin.terrainDAO.getAllTerrains()) {
            if (terrain.getOwner().equals(player.getName().toUpperCase())) {
                terrains++;
                patrimonio += plugin.terrainDAO.getTerrainPrice(terrain, plugin.terrainDAO.getTerrainArea(terrain));
            }
        }
        inv.setItem(11, new ItemBuilder(Material.BOOK).name("§aPerfil").addLore("§7", "§7Terrenos: §e" + terrains, "§7Seu patrimõnio: §2$§f" + numberFormatter.formatNumber(patrimonio), "§7").build());
        inv.setItem(14, new ItemBuilder(Material.FENCE).name("§aCriar terreno §7(Clique direito)").addLore("§7", "§7Clique para §e§ncriar§7 um novo terreno.").build());
        inv.setItem(15, new ItemBuilder(Material.DIRT).name("§aMeus terrenos §7(Clique direito)").addLore("§7", "§7Clique para §e§nvisualizar§7 os terrenos que você faz parte.").build());
        inv.addClickHandler(e -> {
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aCriar terreno §7(Clique direito)")) {
                        this.terraincreator.add(player.getName());
                        player.sendMessage("§eDigite a area do terreno a ser comprado, para cancelar a operação digite §7§ncancelar§7.");
                        player.closeInventory();
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aMeus terrenos §7(Clique direito)")) {
                        player.closeInventory();
                    }
                }
        );
        inv.open(player);
    }
    public void invOwner(Player player) {
        NumberFormatter numberFormatter = new NumberFormatter();
        Terrain terrain = plugin.terrainDAO.getTerrainByLocation(player.getLocation().getBlock().getLocation());
        FastInv inv = new FastInv(27, terrain.getName() + " - Menu");
        inv.setItem(11, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("§aSeu Terreno")
                .addLore("§7")
                .addLore("§7Área: §e" + plugin.terrainDAO.getTerrainArea(terrain) + "m²")
                .addLore("§7Preço avaliado: §2" + numberFormatter.formatNumber(plugin.terrainDAO.getTerrainPrice(terrain, plugin.terrainDAO.getTerrainArea(terrain))))
                .addLore("§7")
                .addLore("§7Clique com botão direito para uman §e§n§reavaliação§7 do preço.")
                .build());
        inv.setItem(14, new ItemBuilder(Material.DOUBLE_PLANT)
                .name("§aAdicionar membro §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nadicionar§7 um membro ao seu terreno.")
                .build());
        inv.setItem(15, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("§aOpções §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nalterar§7 as opções do seu terreno.")
                .build());
        inv.addClickHandler(e -> {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aOpções §7(Clique direito)")) {
                        invOption(player);
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aAdicionar membro §7(Clique direito)")) {
                        this.terrainaddmember.add(player.getName());
                        player.sendMessage("§eDigite o nome do membro a ser convidado, para cancelar a operação digite §7§ncancelar§7.");
                        player.closeInventory();
                    }
                }
        );
        inv.open(player);
    }
    public void invOption(Player player) {
        FastInv inv = new FastInv(27, plugin.terrainDAO.getTerrainByOwner(player.getName().toUpperCase()).getName() + " - Opções");
        inv.setItem(12, new ItemBuilder(Material.SKULL_ITEM)
                .name("§aMembros §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Gerencia os §e§nmembros§7 do seu terreno.")
                .build());
        inv.setItem(13, new ItemBuilder(Material.SUGAR_CANE)
                .name("§aPlantações §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Gerencia as §e§nplantações§7 dos seus terrenos.")
                .build());
        inv.setItem(14, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("§aDesign §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Gerencia o §e§ndesign§7 do seu terreno.")
                .build());

        inv.addClickHandler(e -> {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aPlantações §7(Clique direito)")) {

                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aMembros §7(Clique direito)")) {
                        invMembers(player);
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aDesign §7(Clique direito)")) {
                        invDesign(player);

                    }
                }
        );
        inv.open(player);
    }
    public void invMembers(Player player) {
        FastInv inv = new FastInv(54, plugin.terrainDAO.getTerrainByLocation(player.getLocation().getBlock().getLocation()).getName() + " - Membros");
        int friends = 11;
        inv.setItem(45, new ItemBuilder(Material.ARROW)
                .name("§aVoltar §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nvoltar§7 ao menu de opções.")
                .build()
        );
        inv.addClickHandler(e -> {
            if (e.getCurrentItem().getItemMeta() == null) {
                return;
            }
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aVoltar §7(Clique direito)")) {
                invOption(player);
            }
        });
        Terrain terrain = plugin.terrainDAO.getTerrainByLocation(player.getLocation().getBlock().getLocation());
        if (terrain == null) {
            player.sendMessage("§cVocê precisa estar dentro do terreno para gerenciar os membros.");
            return;
        }
            for (String friend : plugin.terrainDAO.getAllFriends(terrain.getFriends())) {
                if (friends == 17) {
                    friends = 20;
                }
                if (friends == 26) {
                    friends = 29;
                }
                if (friends == 35) {
                    friends = 38;
                }
                if (friends == 44) {
                    return;
                }

                inv.setItem(friends, new ItemBuilder(Material.SKULL_ITEM)
                        .name("§a" + friend)
                        .addLore("§7")
                        .addLore("§7Para alterar as permissões deste jogador do terreno clique botão direito.")
                        .addLore("§7e para remover o jogador do terreno clique esquerdo.")
                        .addLore("§7 ")
                        .build());
                friends++;

                inv.addClickHandler(e -> {
                            if (e.getCurrentItem().getItemMeta() == null) {
                                return;
                            }
                            e.setCancelled(true);
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a" + friend)) {
                                if (e.getClick().isRightClick()) {
                                    invPermissions(player, friend);
                                    return;
                                }
                                terrain.getFriends().remove(friend);
                                invMembers(player);
                            }
                        }
                );
            }
        inv.open(player);
    }
    public void invPermissions(Player player, String member) {
        Terrain terrain = plugin.terrainDAO.getTerrainByLocation(player.getLocation().getBlock().getLocation());
        FastInv inv = new FastInv(36, terrain.getName() + " - " + member);
        inv.setItem(27, new ItemBuilder(Material.ARROW)
                .name("§aVoltar §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nvoltar§7 ao menu de membros.")
                .build());
        inv.setItem(11, new ItemBuilder(!terrain.getPermissions(member).isPlaceBlocks() ? Material.BARRIER : Material.GRASS)
                .name(!terrain.getPermissions(member).isPlaceBlocks() ? "§cColocar blocos §7(Desativado)" : "§aColocar blocos §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isPlaceBlocks() ? "§7Clique para ativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para desativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(12, new ItemBuilder(!terrain.getPermissions(member).isBreakBlocks() ? Material.BARRIER : Material.DIAMOND_PICKAXE)
                .name(!terrain.getPermissions(member).isBreakBlocks() ? "§cQuebrar blocos §7(Desativado)" : "§aQuebrar blocos §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isBreakBlocks() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(13, new ItemBuilder(!terrain.getPermissions(member).isBreakSpawners() ? Material.BARRIER : Material.MOB_SPAWNER)
                .name(!terrain.getPermissions(member).isBreakSpawners() ? "§cQuebrar spawners §7(desativado)" : "§aQuebrar spawners §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isBreakSpawners() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(14, new ItemBuilder(!terrain.getPermissions(member).isInteract() ? Material.BARRIER : Material.TRIPWIRE_HOOK)
                .name(!terrain.getPermissions(member).isInteract() ? "§cInteragir §7(Desativado)" : "§aInteragir §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isInteract() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(15, new ItemBuilder(!terrain.getPermissions(member).isOpenChest() ? Material.BARRIER : Material.CHEST)
                .name(!terrain.getPermissions(member).isOpenChest() ? "§cAbrir baús §7(Desativado)" : "§aAbrir baús §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isInteract() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(21, new ItemBuilder(!terrain.getPermissions(member).isKillMobs() ? Material.BARRIER : Material.DIAMOND_SWORD)
                .name(!terrain.getPermissions(member).isKillMobs() ? "§cMatar mobs §7(Desativado)" : "§aMatar mobs §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isKillMobs() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(22, new ItemBuilder(!terrain.getPermissions(member).isInteractDoors()? Material.BARRIER : Material.WOOD_DOOR)
                .name(!terrain.getPermissions(member).isInteractDoors() ? "§cAbrir/fechar portas §7(Desativado)" : "§aAbrir/fechar portas §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isInteractDoors() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
        inv.setItem(23, new ItemBuilder(!terrain.getPermissions(member).isAdministrator() ? Material.BARRIER : Material.BEACON)
                .name(!terrain.getPermissions(member).isAdministrator() ? "§cAdministrador §7(Desativado)" : "§aAdministrador §7(Ativado)")
                .addLore("§7")
                .addLore(!terrain.getPermissions(member).isAdministrator() ? "§7Clique para desativar essa §e§npermissão§7 para §n" + member + "." : "§7Clique para ativar essa §e§npermissão§7 para §n" + member + ".")
                .build());
       inv.addClickHandler(e -> {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }

           switch(e.getCurrentItem().getItemMeta().getDisplayName().toUpperCase()) {
               case "§ACOLOCAR BLOCOS §7(ATIVADO)":
                   terrain.getPermissions(member).setPlaceBlocks(false);
                   invPermissions(player, member);
                   break;
               case "§CCOLOCAR BLOCOS §7(DESATIVADO)":
                   terrain.getPermissions(member).setPlaceBlocks(true);
                   invPermissions(player, member);
                   break;
               case "§AQUEBRAR BLOCOS §7(ATIVADO)":
                   terrain.getPermissions(member).setBreakBlocks(false);
                   invPermissions(player, member);
                   break;
               case "§CQUEBRAR BLOCOS §7(DESATIVADO)":
                   terrain.getPermissions(member).setBreakBlocks(true);
                   invPermissions(player, member);
                   break;
               case "§AQUEBRAR SPAWNERS §7(ATIVADO)":
                   terrain.getPermissions(member).setBreakSpawners(false);
                   invPermissions(player, member);
                   break;
               case "§CQUEBRAR SPAWNERS §7(DESATIVADO)":
                   terrain.getPermissions(member).setBreakSpawners(true);
                   invPermissions(player, member);
                   break;
               case "§AINTERAGIR §7(ATIVADO)":
                   terrain.getPermissions(member).setInteract(false);
                   invPermissions(player, member);
                   break;
               case "§CINTERAGIR §7(DESATIVADO)":
                   terrain.getPermissions(member).setInteract(true);
                   invPermissions(player, member);
                   break;
               case "§AABRIR BAÚS §7(ATIVADO)":
                   terrain.getPermissions(member).setOpenChest(false);
                   invPermissions(player, member);
                   break;
               case "§CABRIR BAÚS §7(DESATIVADO)":
                   terrain.getPermissions(member).setOpenChest(true);
                   invPermissions(player, member);
                   break;
               case "§AMATAR MOBS §7(ATIVADO)":
                   terrain.getPermissions(member).setKillMobs(false);
                   invPermissions(player, member);
                   break;
               case "§CMATAR MOBS §7(DESATIVADO)":
                   terrain.getPermissions(member).setKillMobs(true);
                   invPermissions(player, member);
                   break;
               case "§AABRIR/FECHAR PORTAS §7(ATIVADO)":
                   terrain.getPermissions(member).setInteractDoors(false);
                   invPermissions(player, member);
                   break;
               case "§CABRIR/FECHAR PORTAS §7(DESATIVADO)":
                   terrain.getPermissions(member).setInteractDoors(true);
                   invPermissions(player, member);
                   break;
               case "§AADMINISTRADOR §7(ATIVADO)":
                   terrain.getPermissions(member).setAdministrator(false);
                   invPermissions(player, member);
                   break;
               case "§CADMINISTRADOR §7(DESATIVADO)":
                   terrain.getPermissions(member).setAdministrator(true);
                   invPermissions(player, member);
                   break;
               case "§AVOLTAR §7(CLIQUE DIREITO)":
                   invMembers(player);
                   break;
           }
                }
        );
        inv.open(player);
    }
    public void invDesign(Player player) {
        FastInv inv = new FastInv(27, plugin.terrainDAO.getTerrainByOwner(player.getName().toUpperCase()).getName() + " - Design");
        inv.setItem(12, new ItemBuilder(Material.NAME_TAG)
                .name("§aNome §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Altera o §e§nnome§7 do terreno.")
                .build());
        inv.setItem(13, new ItemBuilder(Material.getMaterial(plugin.terrainDAO.getTerrainByOwner(player.getName().toUpperCase()).getIcon()))
                .name("§aIcone §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Altera o §e§nicone§7 do terreno.")
                .build());
        inv.setItem(14, new ItemBuilder(Material.SIGN)
                .name("§aDescrição §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Altera a §e§ndescrição§7 do terreno.")
                .build());
        inv.addClickHandler(e -> {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aNome §7(Clique direito)")) {
                        this.terrainsetname.add(player.getName());
                        player.sendMessage("§eDigite o novo nome do terreno, para cancelar a operação digite §7§ncancelar§7.");
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aIcone §7(Clique direito)")) {
                        this.terrainseticon.add(player.getName());
                        player.sendMessage("§eDigite o nome do novo icone do terreno, para cancelar a operação digite §7§ncancelar§7.");
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aDescrição §7(Clique direito)")) {
                        this.terrainsetdesc.add(player.getName());
                        player.sendMessage("§eDigite a nova descrição do terreno, para cancelar a operação digite §7§ncancelar§7.");
                    }
                }
        );
        inv.open(player);
    }
//CLAN TERRAIN
    public void invClan(Player player) {
    NumberFormatter numberFormatter = new NumberFormatter();
    FastInv inv = new FastInv(27, "Terrenos - Menu");
    String clan = null;
    int terrains = 0;
    int patrimonio = 0;
    clan = "SHALALA";
    for (Terrain terrain : plugin.terrainDAO.getAllTerrains()) {
        if (terrain.getOwner().equals(clan.toUpperCase())) {
            terrains++;
            patrimonio += plugin.terrainDAO.getTerrainPrice(terrain, plugin.terrainDAO.getTerrainArea(terrain));
        }
    }
    inv.setItem(11, new ItemBuilder(Material.BOOK).name("§aPerfil").addLore("§7", "§7Terrenos: §e" + terrains, "§7Patrimõnio: §2$§f" + numberFormatter.formatNumber(patrimonio), "§7").build());
    inv.setItem(14, new ItemBuilder(Material.FENCE).name("§aCriar terreno §7(Clique direito)").addLore("§7", "§7Clique para §e§ncriar§7 um novo terreno.").build());
    inv.setItem(15, new ItemBuilder(Material.DIRT).name("§aTerrenos do clan §7(Clique direito)").addLore("§7", "§7Clique para §e§nvisualizar§7 os terrenos do clan.").build());
    inv.addClickHandler(e -> {
                if (e.getCurrentItem().getItemMeta() == null) {
                    return;
                }
                e.setCancelled(true);
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aCriar terreno §7(Clique direito)")) {
                    this.terraincreator.add(player.getName());
                    player.sendMessage("§eDigite a area do terreno a ser comprado, para cancelar a operação digite §7§ncancelar§7.");
                    player.closeInventory();
                    return;
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aTerrenos do clan §7(Clique direito)")) {
                    player.closeInventory();
                }
            }
    );
    inv.open(player);
}
    public void invLeader(Player player) {
        NumberFormatter numberFormatter = new NumberFormatter();
        Terrain terrain = plugin.terrainDAO.getTerrainByLocation(player.getLocation().getBlock().getLocation());
        FastInv inv = new FastInv(27, terrain.getName() + " - Menu");
        inv.setItem(11, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("§aTerreno do clan")
                .addLore("§7")
                .addLore("§7Área: §e" + plugin.terrainDAO.getTerrainArea(terrain) + "m²")
                .addLore("§7Preço avaliado: §2" + numberFormatter.formatNumber(plugin.terrainDAO.getTerrainPrice(terrain, plugin.terrainDAO.getTerrainArea(terrain))))
                .addLore("§7")
                .addLore("§7Clique com botão direito para uman §e§nreavaliação§7 do preço.")
                .build());
        inv.setItem(14, new ItemBuilder(Material.DOUBLE_PLANT)
                .name("§aAdicionar membro §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nadicionar§7 um membro ao seu terreno.")
                .build());
        inv.setItem(15, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("§aOpções §7(Clique direito)")
                .addLore("§7")
                .addLore("§7Clique para §e§nalterar§7 as opções do seu terreno.")
                .build());
        inv.addClickHandler(e -> {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getItemMeta() == null) {
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aOpções §7(Clique direito)")) {
                        invOption(player);
                        return;
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aAdicionar membro §7(Clique direito)")) {
                        this.terrainaddmember.add(player.getName());
                        player.sendMessage("§eDigite o nome do membro a ser convidado, para cancelar a operação digite §7§ncancelar§7.");
                        player.closeInventory();
                    }
                }
        );
        inv.open(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cHey! Você não pode fazer isto.");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("terreno")) {
            Player p = (Player) sender;
            if (plugin.terrainDAO.isInTerrain(p.getLocation())) {
                if (plugin.terrainDAO.getTerrainByLocation(p.getLocation().getBlock().getLocation()).getOwner().equals(p.getName().toUpperCase())) {
                    invOwner(p);
                    return false;
                }
                if (plugin.terrainDAO.getTerrainByLocation(p.getLocation().getBlock().getLocation()).getFriends().containsKey(p.getName())) {
                    //inv friend
                    return false;
                }
                //info terrain
            }
            inv(p);
            return false;
        }
        return false;
    }

    //PLAYER TERRAIN
    public void buyTerrain(@NotNull Player p, int area) {
        double x = p.getLocation().getBlockX();
        double z = p.getLocation().getBlockZ();
        double xMin = x - (double) (area + 1) / 2;
        double xMax = x + (double) (area + 1) / 2;
        double zMin = z - (double) (area + 1) / 2;
        double zMax = z + (double) (area + 1) / 2;

        if (plugin.terrainDAO.hasTerrain(p, xMin, xMax, zMin, zMax)) {
            p.sendMessage("§cSeu terreno está invadindo outro terreno.");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5f, 1f);
            return;
        }
        int price = plugin.terrainDAO.calculatePrice(p, area);
        if (plugin.economy.getBalance(p.getName()) < price) {
            p.sendMessage("§cVocê não possui dinheiro suficiente para comprar este terreno.");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5f, 1f);
            return;
        }

        int id = plugin.terrainDAO.getAllTerrains().size() + 1;
        List<String> desc = new ArrayList<>();
        desc.add(" ");
        desc.add(ChatColor.translateAlternateColorCodes('&', "&7Descrição do seu terreno"));
        plugin.terrainDAO.addTerrain(id, p.getName().toUpperCase(), false, "§aTerreno " + id, "GRASS", desc, p.getLocation().getWorld(), xMin, xMax, zMin, zMax, p.getLocation().getY());

        World world = p.getWorld();
        for (double x2 = xMin + 1; x2 <= xMax - 1; x2++) {
            world.getBlockAt((int) x2, (int) p.getLocation().getY(), (int) zMin).setType(Material.FENCE);
            world.getBlockAt((int) x2, (int) p.getLocation().getY(), (int) zMax).setType(Material.FENCE);
        }

        for (double z2 = zMin + 1; z2 <= zMax - 1; z2++) {
            world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) z2).setType(Material.FENCE);
            world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) z2).setType(Material.FENCE);
        }

        world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) zMin).setType(Material.FENCE);
        world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) zMin).setType(Material.FENCE);
        world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) zMax).setType(Material.FENCE);
        world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) zMax).setType(Material.FENCE);

        p.sendMessage("§eTerreno comprado com sucesso.");
        this.terraincreator2.remove(p.getName());
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
    }
    //CLAN TERRAIN
    public void buyTerrainClan(@NotNull Player p, int area, String clan) {
        double x = p.getLocation().getBlockX();
        double z = p.getLocation().getBlockZ();
        double xMin = x - (double) (area + 1) / 2;
        double xMax = x + (double) (area + 1) / 2;
        double zMin = z - (double) (area + 1) / 2;
        double zMax = z + (double) (area + 1) / 2;

        if (plugin.terrainDAO.hasTerrain(p, xMin, xMax, zMin, zMax)) {
            p.sendMessage("§cO terreno do clan está invadindo outro terreno.");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5f, 1f);
            return;
        }
        int price = plugin.terrainDAO.calculatePrice(p, area);
        if (plugin.economy.getBalance(p.getName()) < price) {
            p.sendMessage("§cO clan não possui fundos suficiente para comprar este terreno.");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5f, 1f);
            return;
        }

        int id = plugin.terrainDAO.getAllTerrains().size() + 1;
        List<String> desc = new ArrayList<>();
        desc.add(" ");
        desc.add(ChatColor.translateAlternateColorCodes('&', "&7Descrição do seu terreno"));
        plugin.terrainDAO.addTerrain(id, clan.toUpperCase(), true, "§aTerreno " + id, "GRASS", desc, p.getLocation().getWorld(), xMin, xMax, zMin, zMax, p.getLocation().getY() + 1);

        World world = p.getWorld();
        for (double x2 = xMin + 1; x2 <= xMax - 1; x2++) {
            world.getBlockAt((int) x2, (int) p.getLocation().getY(), (int) zMin).setType(Material.NETHER_FENCE);
            world.getBlockAt((int) x2, (int) p.getLocation().getY(), (int) zMax).setType(Material.NETHER_FENCE);
        }

        for (double z2 = zMin + 1; z2 <= zMax - 1; z2++) {
            world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) z2).setType(Material.NETHER_FENCE);
            world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) z2).setType(Material.NETHER_FENCE);
        }

        world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) zMin).setType(Material.NETHER_FENCE);
        world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) zMin).setType(Material.NETHER_FENCE);
        world.getBlockAt((int) xMin, (int) p.getLocation().getY(), (int) zMax).setType(Material.NETHER_FENCE);
        world.getBlockAt((int) xMax, (int) p.getLocation().getY(), (int) zMax).setType(Material.NETHER_FENCE);
        Location blockLocation = new Location(world, xMin, p.getLocation().getY() + 1, zMin);
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(blockLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setCustomName("§b" + clan);

        p.sendMessage("§eTerreno comprado com sucesso.");
        this.terraincreator2.remove(p.getName());
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
    }
    public int area = 0;
    @EventHandler
    public void chat23(AsyncPlayerChatEvent e) {
        NumberFormatter numberFormatter = new NumberFormatter();
        Player p = e.getPlayer();
        int price = 0;
        if (this.terraincreator.contains(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancelar")) {
                this.terraincreator.remove(p.getName());
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
                p.sendMessage("§cOperação cancelada com sucesso.");
                return;
            }
            try {
                area = Integer.parseInt(e.getMessage());
            } catch (NumberFormatException exception) {
                p.sendMessage("§cUtilize somente numeros.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (area <= 1) {
                p.sendMessage("§cUtilize somente numeros positivos.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (area % 2 == 0) {
                p.sendMessage("§cUtilize apenas numeros impares.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (area > 39) {
                p.sendMessage("§cEste terreno é grande de mais, no momento estamos na versão beta e para evitar bugs, não é suportado terrenos maiores que 39 blocos quadrados.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            price = plugin.terrainDAO.calculatePrice(p, area);
            p.sendMessage("§eVocê irá gastar §7$§7§n" + numberFormatter.formatNumber(price));
            p.sendMessage("§eDigite §7§nSIM§e para confirmar e §7§nNÃO§e para cancelar.");
            this.terraincreator2.add(p.getName());
            this.terraincreator.remove(p.getName());
            return;
        }
        if(this.terraincreator2.contains(p.getName())) {
            e.setCancelled(true);
            if(e.getMessage().equalsIgnoreCase("sim")) {
                Bukkit.getScheduler().runTask(plugin, () -> buyTerrain(p, area));
                return;
            }
            if(e.getMessage().equalsIgnoreCase("não")) {
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
                p.sendMessage("§cOperação cancelada com sucesso.");

                this.terraincreator2.remove(p.getName());
            }
            p.sendMessage("§eVocê irá gastar §7$§7§n" + numberFormatter.formatNumber(price));
            p.sendMessage("§eDigite §7§nSIM§e para confirmar e §7§nNÃO§e para cancelar.");
        }
        if (this.terrainaddmember.contains(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancelar")) {
                this.terrainaddmember.remove(p.getName());
                p.sendMessage("§cOperação cancelada com sucesso.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            Player target = Bukkit.getPlayerExact(e.getMessage());
            if (target == null) {
                p.sendMessage("§cO jogador não existe ou não está online.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (target == p) {
                p.sendMessage("§cVocê não pode adicionar você mesmo.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(p.getLocation().getBlock().getLocation());
            if (terrain == null) {
                p.sendMessage("§cVocê precisa estar dentro de um terreno seu para adicionar um amigo.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            plugin.terrainDAO.addFriend(terrain, target.getName());
            p.sendMessage("§eVocê adicionou o jogador §7§n" + target.getName() + "§e ao seu terreno.");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
            this.terrainaddmember.remove(p.getName());
            return;
        }
        if (this.terrainsetname.contains(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancelar")) {
                this.terrainsetname.remove(p.getName());
                p.sendMessage("§cOperação cancelada com sucesso.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            boolean isValid = e.getMessage().matches("^[a-zA-Z0-9& ]+$");
            if (!isValid) {
                p.sendMessage("§cNome inválido, utilize somente letras e numeros.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (e.getMessage().length() > 14) {
                p.sendMessage("§cO nome no seu terreno não deve ser maior do que 14 caracteres.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(p.getLocation());
            if (terrain == null) {
                p.sendMessage("§cVocê precisa estar dentro de um terreno seu para alterar o nome.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            terrain.setName(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            p.sendMessage("§eO nome do seu terreno foi alterado para §7''" + terrain.getName() + "''§7 §e com sucesso.");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
            this.terrainsetname.remove(p.getName());
            return;
        }
        if (this.terrainseticon.contains(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancelar")) {
                this.terrainseticon.remove(p.getName());
                p.sendMessage("§cOperação cancelada com sucesso.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            if (Material.getMaterial(e.getMessage().toUpperCase()) == null) {
                p.sendMessage("§cIcone inválido, utilize o nome de algum item, por exemplo: §7§nDIAMOND_SWORD");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(p.getLocation());
            if (terrain == null) {
                p.sendMessage("§cVocê precisa estar dentro de um terreno seu para alterar o icone.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            terrain.setIcon(e.getMessage().toUpperCase());
            p.sendMessage("§eO icone do seu terreno foi alterado para §7§n" + terrain.getIcon() + "§e com sucesso.");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
            this.terrainseticon.remove(p.getName());
            return;
        }
        if (this.terrainsetdesc.contains(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancelar")) {
                this.terrainsetdesc.remove(p.getName());
                p.sendMessage("§cOperação cancelada com sucesso.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            boolean isValid = e.getMessage().matches("^[a-zA-Z0-9& ]+$");
            if (!isValid) {
                p.sendMessage("§cDescrição inválida, utilize somente letras e numeros.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            Terrain terrain = plugin.terrainDAO.getTerrainByLocation(p.getLocation());
            if (terrain == null) {
                p.sendMessage("§cVocê precisa estar dentro de um terreno seu para alterar a sua descrição.");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 5F, 1F);
                return;
            }
            List<String> desc = new ArrayList<>();
            desc.add(" ");
            desc.add(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            terrain.setDesc(desc);
            p.sendMessage("§eA descrição do seu terreno foi alterado para §7§n" + ChatColor.translateAlternateColorCodes('&', e.getMessage()) + "§e com sucesso.");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 5f, 1f);
            this.terrainsetdesc.remove(p.getName());
        }
    }
}
