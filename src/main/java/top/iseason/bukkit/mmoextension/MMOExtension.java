package top.iseason.bukkit.mmoextension;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.Objects;

public final class MMOExtension extends JavaPlugin implements CommandExecutor {
    private final ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    private YamlConfiguration config;

    @Override
    public void onEnable() {
        Plugin mmoPlugin = Bukkit.getServer().getPluginManager().getPlugin("MMOCore");
        if (!(mmoPlugin instanceof MMOCore)) {
            Bukkit.getServer().getLogger().warning(ChatColor.RED + "未检查到MMOCore!");
            return;
        }
        Objects.requireNonNull(Bukkit.getPluginCommand("mmoExtension")).setExecutor(this);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // mme updateAttribute  player
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (args.length != 2) {
            return true;
        }
        if (!args[0].equals("updateAttribute")) {
            return true;
        }
        Player player = Bukkit.getServer().getPlayer(args[1]);
        if (player == null) {
            return true;
        }
        PlayerData playerData = PlayerData.get(player);
        System.out.println(playerData.getAttributes().toJsonString());
        return true;
    }
}
