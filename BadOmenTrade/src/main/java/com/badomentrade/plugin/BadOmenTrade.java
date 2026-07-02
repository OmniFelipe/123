package com.badomentrade.plugin;

import com.badomentrade.plugin.commands.BadOmenCommand;
import com.badomentrade.plugin.listeners.TradeListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * BadOmenTrade
 * ------------
 * Añade una probabilidad configurable de recibir el efecto "Mal Presagio"
 * (Bad Omen) cada vez que un jugador completa un trade con un aldeano.
 */
public class BadOmenTrade extends JavaPlugin {

    private static BadOmenTrade instance;

    @Override
    public void onEnable() {
        instance = this;

        // Genera config.yml si no existe todavía
        saveDefaultConfig();

        // Registra el listener que escucha los trades
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);

        // Registra el comando /badomentrade
        BadOmenCommand commandExecutor = new BadOmenCommand(this);
        getCommand("badomentrade").setExecutor(commandExecutor);
        getCommand("badomentrade").setTabCompleter(commandExecutor);

        printBanner();
    }

    @Override
    public void onDisable() {
        getLogger().info("BadOmenTrade se ha desactivado correctamente.");
    }

    /**
     * Recarga config.yml desde el disco.
     */
    public void reload() {
        reloadConfig();
    }

    public static BadOmenTrade getInstance() {
        return instance;
    }

    private void printBanner() {
        double chance = getConfig().getDouble("chance", 5.0);
        getLogger().info("========================================");
        getLogger().info(" BadOmenTrade v" + getDescription().getVersion() + " habilitado");
        getLogger().info(" Probabilidad actual de Mal Presagio: " + chance + "%");
        getLogger().info(" Usa /badomentrade para ver los comandos.");
        getLogger().info("========================================");
    }
}
