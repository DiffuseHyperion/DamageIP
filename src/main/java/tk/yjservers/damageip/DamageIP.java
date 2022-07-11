package tk.yjservers.damageip;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Objects;

public final class DamageIP extends JavaPlugin implements Listener {

    private static FileConfiguration config;
    private HashMap<String, Integer> playerDigits;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.RED + "Let the trolling commence...");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        config = getConfig();
        playerDigits = new HashMap<>();
        for (String s : config.getKeys(false)) {
            playerDigits.put(s, config.getInt(s + ".digits"));
            getLogger().info(s + " is at digit " + config.getInt(s + ".digits"));
        }
    }

    @Override
    public void onDisable() {
    }


    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            String pname = p.getDisplayName();
            String IP = getIP(p);

            if (!config.contains(pname) || !playerDigits.containsKey(pname)) {
                getLogger().info("Registering " + pname);
                config.createSection(pname);
                config.createSection(pname + ".digits");
                config.set(pname + ".digits", 0);

                playerDigits.put(pname, 0);

                saveConfig();
            }

            int currentDigit = playerDigits.get(pname);

            if (currentDigit >= IP.length() - 1) {
                Bukkit.broadcastMessage(pname + "'s entire IP: " + IP);
            } else {
                int plusDigit = playerDigits.get(pname) + 1;
                playerDigits.put(pname, plusDigit);
                Bukkit.broadcastMessage("Digit " + plusDigit + " of " + pname + "'s IP: " + IP.substring(0, plusDigit));
                config.set(pname + ".digits", plusDigit);
                saveConfig();
            }
        }
    }

    public String getIP(Player p) {
        return Objects.requireNonNull(p.getAddress()).getAddress().toString().split("/")[1];
    }
}
