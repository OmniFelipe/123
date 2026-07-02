package com.badomentrade.plugin.listeners;

import com.badomentrade.plugin.BadOmenTrade;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

/**
 * Escucha cada trade completado con un aldeano (o mercader errante, si está
 * habilitado en config.yml) y, con la probabilidad configurada, aplica el
 * efecto Mal Presagio al jugador junto con mensajes, sonido y partículas.
 */
public class TradeListener implements Listener {

    private final BadOmenTrade plugin;
    private final Random random = new Random();

    public TradeListener(BadOmenTrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVillagerTrade(PlayerTradeEvent event) {
        Player player = event.getPlayer();
        AbstractVillager merchant = event.getVillager();
        FileConfiguration config = plugin.getConfig();

        // Los jugadores con permiso de bypass nunca reciben el efecto
        if (player.hasPermission("badomentrade.bypass")) {
            return;
        }

        // Por defecto solo cuenta un Villager real; el Wandering Trader es opcional
        boolean isVillager = merchant instanceof Villager;
        boolean isWanderingTrader = merchant instanceof WanderingTrader;
        boolean includeWandering = config.getBoolean("include-wandering-trader", false);

        if (!isVillager && !(isWanderingTrader && includeWandering)) {
            return;
        }

        // Respeta la lista de mundos deshabilitados
        List<String> disabledWorlds = config.getStringList("disabled-worlds");
        World world = player.getWorld();
        if (disabledWorlds.contains(world.getName())) {
            return;
        }

        double chance = config.getDouble("chance", 5.0);
        double roll = random.nextDouble() * 100.0;

        if (roll <= chance) {
            applyBadOmen(player);
        }
    }

    private void applyBadOmen(Player player) {
        FileConfiguration config = plugin.getConfig();

        int amplifier = Math.max(0, config.getInt("amplifier", 0));
        int durationSeconds = config.getInt("duration-seconds", 300);
        int durationTicks = durationSeconds < 0 ? Integer.MAX_VALUE : durationSeconds * 20;

        PotionEffectType badOmen = PotionEffectType.BAD_OMEN;
        if (badOmen == null) {
            plugin.getLogger().warning("No se encontró el PotionEffectType BAD_OMEN en esta versión del servidor.");
            return;
        }

        player.addPotionEffect(new PotionEffect(badOmen, durationTicks, amplifier, false, true, true));

        sendChatMessage(player, config);
        sendTitle(player, config);
        sendActionBar(player, config);
        playSound(player, config);
        spawnParticles(player, config);
        broadcastToOthers(player, config);
    }

    private void sendChatMessage(Player player, FileConfiguration config) {
        String message = config.getString("messages.effect-applied", "");
        if (message != null && !message.isEmpty()) {
            player.sendMessage(colorize(message));
        }
    }

    private void sendTitle(Player player, FileConfiguration config) {
        String title = config.getString("messages.title", "");
        String subtitle = config.getString("messages.subtitle", "");
        if (title != null && !title.isEmpty()) {
            player.sendTitle(colorize(title), colorize(subtitle), 10, 60, 20);
        }
    }

    private void sendActionBar(Player player, FileConfiguration config) {
        if (!config.getBoolean("messages.actionbar-enabled", true)) {
            return;
        }
        String actionbar = config.getString("messages.actionbar", "");
        if (actionbar != null && !actionbar.isEmpty()) {
            player.sendActionBar(colorize(actionbar));
        }
    }

    private void playSound(Player player, FileConfiguration config) {
        if (!config.getBoolean("sound.enabled", true)) {
            return;
        }
        String soundName = config.getString("sound.sound", "ENTITY_ILLUSIONER_CAST_SPELL");
        try {
            Sound sound = Sound.valueOf(soundName);
            float volume = (float) config.getDouble("sound.volume", 1.0);
            float pitch = (float) config.getDouble("sound.pitch", 1.0);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Sonido inválido en config.yml (sound.sound): " + soundName);
        }
    }

    private void spawnParticles(Player player, FileConfiguration config) {
        if (!config.getBoolean("particles.enabled", true)) {
            return;
        }
        String particleName = config.getString("particles.type", "SMOKE");
        try {
            Particle particle = Particle.valueOf(particleName);
            int amount = config.getInt("particles.amount", 30);
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(particle, loc, amount, 0.5, 1, 0.5, 0.02);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Partícula inválida en config.yml (particles.type): " + particleName);
        }
    }

    private void broadcastToOthers(Player player, FileConfiguration config) {
        if (!config.getBoolean("messages.broadcast-others", false)) {
            return;
        }
        String broadcastMsg = config.getString("messages.broadcast-message", "");
        if (broadcastMsg == null || broadcastMsg.isEmpty()) {
            return;
        }
        String formatted = colorize(broadcastMsg.replace("%player%", player.getName()));
        for (Player online : player.getServer().getOnlinePlayers()) {
            if (!online.equals(player) && online.hasPermission("badomentrade.notify")) {
                online.sendMessage(formatted);
            }
        }
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
