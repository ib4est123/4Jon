package me.prestige.kit;

import lombok.Getter;
import me.prestige.kit.commands.AcceptCommand;
import me.prestige.kit.commands.ArenaCommand;
import me.prestige.kit.commands.DuelCommand;
import me.prestige.kit.duel.DuelManager;
import me.prestige.kit.duel.listener.DeathListener;
import me.prestige.kit.listener.HidingListener;
import me.prestige.kit.listener.SimplyLobbyListener;
import me.prestige.kit.listener.SoupListener;
import me.prestige.kit.util.PersistableLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

/**
 * Created by TREHOME on 04/14/2017.
 */
@Getter
public class Kits extends JavaPlugin {

    private DuelManager duelManager;


    @Override
    public void onEnable(){
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        getConfig().options().copyDefaults(true);

        duelManager = new DuelManager(this);
        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable(){
        duelManager.save();
    }

    private void registerListener() {
        PluginManager pluginManager  = Bukkit.getPluginManager();
        pluginManager.registerEvents(new HidingListener(this), this);
        pluginManager.registerEvents(new SoupListener(),this);
        pluginManager.registerEvents(new SimplyLobbyListener(this), this);
        pluginManager.registerEvents(new DeathListener(this),this);
    }

    private void registerCommands() {
        getCommand("setup").setExecutor(new ArenaCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("accept").setExecutor(new AcceptCommand(this));
        // Handling permissions so its "command.commandName"
        final Map<String, Map<String, Object>> map =  getDescription().getCommands();
        for(final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = getCommand(entry.getKey());
            command.setPermission("command." + entry.getKey());
            command.setPermissionMessage(ChatColor.RED + "No permission.");
        }
    }

}
