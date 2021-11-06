package top.iseason.bukkit.mmoextension;

import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public final class MMOExtension extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private HashMap<String, LinkedHashMap<Integer, Double>> attributes;

    @Override
    public void onEnable() {
        Plugin mmoPlugin = Bukkit.getServer().getPluginManager().getPlugin("MMOCore");
        if (!(mmoPlugin instanceof MMOCore)) {
            Bukkit.getServer().getLogger().warning(ChatColor.RED + "未检查到MMOCore!");
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveDefaultConfig();
        }
        reloadConfigs();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    private void onPlayerLevelUpEvent(PlayerLevelUpEvent event) {
        PlayerData playerData = PlayerData.get(event.getPlayer());
        int newLevel = event.getNewLevel();
        StatInstance max_health = playerData.getStats().getInstance("MAX_HEALTH");

        max_health.addModifier("test", new StatModifier(10 + newLevel));
        //总数
        System.out.println(max_health.getTotal());
    }

    private void reloadConfigs() {
        config = getConfig();
        attributes = new HashMap<>();
        config.getKeys(false).forEach(key -> {
            LinkedHashMap<Integer, Double> pairs = new LinkedHashMap<>();
            Objects.requireNonNull(config.getConfigurationSection(key))
                    .getKeys(false)
                    .stream()
                    .sorted()
                    .forEach(level -> {
                        double value = config.getDouble(key + "." + level);
                        pairs.put(Integer.valueOf(level), value);
                    });
            attributes.put(key, pairs);
        });
    }

    //todo:根据等级调节
    private void applyAttributeByLevel(Player player, int level) {
        PlayerData playerData = PlayerData.get(player);
        PlayerStats stats = playerData.getStats();

    }
}
