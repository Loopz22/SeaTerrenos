package org.loopz.seaterrenos;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.loopz.seaterrenos.commands.TerrainCommand;
import org.loopz.seaterrenos.events.Events;
import org.loopz.seaterrenos.fastinv.FastInvManager;
import org.loopz.seaterrenos.manager.TerrainDAO;
import org.loopz.seaterrenos.sqlite.Connection;
import org.loopz.seaterrenos.sqlite.DataBase;

public class Main extends JavaPlugin {

    public TerrainDAO terrainDAO;
    public Connection connection;
    public DataBase db;
    private TerrainCommand terrainCommand;
    private Events events;
    public Economy economy = null;
    @Override
    public void onLoad() {
        this.connection = new Connection(this);
        this.db = new DataBase(this);
        this.terrainDAO = new TerrainDAO();
        this.events = new Events(this);
        this.terrainCommand = new TerrainCommand(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FastInvManager.register(this);
        this.connection.openConnectionSQLITE();
        this.db.loadAreas();
        getCommand("terreno").setExecutor(terrainCommand);
        Bukkit.getPluginManager().registerEvents(terrainCommand, this);
        Bukkit.getPluginManager().registerEvents(events, this);
        if(!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§c[SeaTerrenos] - Não foi possível carregar o Vault...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        db.saveAreas();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

}
