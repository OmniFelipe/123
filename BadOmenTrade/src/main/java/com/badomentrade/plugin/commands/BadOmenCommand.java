package com.badomentrade.plugin.commands;

import com.badomentrade.plugin.BadOmenTrade;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comando /badomentrade — permite recargar la configuración, consultar o
 * cambiar la probabilidad en caliente, y ver información del plugin.
 */
public class BadOmenCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("reload", "chance", "info");

    private final BadOmenTrade plugin;

    public BadOmenCommand(BadOmenTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("badomentrade.admin")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "chance" -> handleChance(sender, args);
            case "info" -> handleInfo(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(prefix() + ChatColor.GREEN + "Configuración recargada correctamente.");
    }

    private void handleChance(CommandSender sender, String[] args) {
        if (args.length < 2) {
            double current = plugin.getConfig().getDouble("chance");
            sender.sendMessage(prefix() + "Probabilidad actual: " + ChatColor.YELLOW + current + "%");
            return;
        }

        try {
            double value = Double.parseDouble(args[1]);
            if (value < 0 || value > 100) {
                sender.sendMessage(ChatColor.RED + "El valor debe estar entre 0 y 100.");
                return;
            }
            plugin.getConfig().set("chance", value);
            plugin.saveConfig();
            sender.sendMessage(prefix() + ChatColor.GREEN + "Probabilidad actualizada a " + value + "%.");
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Debes indicar un número válido. Ejemplo: /badomentrade chance 10");
        }
    }

    private void handleInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "===== BadOmenTrade =====");
        sender.sendMessage(ChatColor.WHITE + "Versión: " + ChatColor.GRAY + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.WHITE + "Probabilidad: " + ChatColor.GRAY + plugin.getConfig().getDouble("chance") + "%");
        sender.sendMessage(ChatColor.WHITE + "Duración: " + ChatColor.GRAY + plugin.getConfig().getInt("duration-seconds") + "s");
        sender.sendMessage(ChatColor.WHITE + "Nivel: " + ChatColor.GRAY + (plugin.getConfig().getInt("amplifier") + 1));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "===== BadOmenTrade - Ayuda =====");
        sender.sendMessage(ChatColor.YELLOW + "/badomentrade reload " + ChatColor.GRAY + "- Recarga la configuración.");
        sender.sendMessage(ChatColor.YELLOW + "/badomentrade chance [valor] " + ChatColor.GRAY + "- Ve o cambia la probabilidad (%).");
        sender.sendMessage(ChatColor.YELLOW + "/badomentrade info " + ChatColor.GRAY + "- Muestra información del plugin.");
    }

    private String prefix() {
        return ChatColor.LIGHT_PURPLE + "[BadOmenTrade] " + ChatColor.RESET;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> results = new ArrayList<>();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    results.add(sub);
                }
            }
            return results;
        }
        return Collections.emptyList();
    }
}
