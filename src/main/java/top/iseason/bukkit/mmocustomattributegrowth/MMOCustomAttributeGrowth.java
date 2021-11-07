package top.iseason.bukkit.mmocustomattributegrowth;

import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.AsyncPlayerDataLoadEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MMOCustomAttributeGrowth extends JavaPlugin implements Listener {
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
        attributes.clear();
    }

    //升级时更新数据
    @EventHandler
    private void onPlayerLevelUpEvent(PlayerLevelUpEvent event) {
        int newLevel = event.getNewLevel();
        applyAttributeByLevel(PlayerData.get(event.getPlayer()), newLevel);
    }

    //读取配置
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
                        if (value == 0.0) return;
                        pairs.put(Integer.valueOf(level), value);
                    });
            attributes.put(key, pairs);
        });
    }

    //根据等级计算数据
    private void applyAttributeByLevel(PlayerData playerData, int level) {
        if (level == 1) return;
        PlayerStats stats = playerData.getStats();
        attributes.forEach((k, v) -> {
            StatInstance instance = stats.getInstance(k);
            StatModifier modifier2 = instance.getModifier("mmocoreClass");
            double baseValue = instance.getBase();
            if (modifier2 != null) {
                baseValue += modifier2.getValue();
            }
            double powValue = 1;
            for (int i = 2; i <= level; i++) {
                powValue *= getValueByLevel(v, i);
            }
            instance.addModifier("MMOExtension", new StatModifier(baseValue * (powValue - 1)));
        });

    }

    //根据等级获取倍率
    private double getValueByLevel(LinkedHashMap<Integer, Double> v, int level) {
        double value = 0.0;
        for (Map.Entry<Integer, Double> next : v.entrySet()) {
            if (level > next.getKey()) {
                value = next.getValue();
            } else {
                break;
            }
        }
        return value;
    }

    //登录时更新数据
    @EventHandler
    private void onAsyncPlayerDataLoadEvent(AsyncPlayerDataLoadEvent event) {
        PlayerData data = event.getData();
        applyAttributeByLevel(data, data.getLevel());
    }
}
