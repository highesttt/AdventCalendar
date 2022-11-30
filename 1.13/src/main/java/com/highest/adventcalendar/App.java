package com.highest.adventcalendar;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.highest.adventcalendar.Commands.AdventCalendar;
import com.highest.adventcalendar.Utils.LoadData;
import com.highest.adventcalendar.Utils.Messages;
import com.highest.adventcalendar.Utils.PlayerData;

public class App extends JavaPlugin {

    public static String prefix = "\u00a7f";
    static App app;

    public static String version = "1.0";

    public static File file;
    public static FileConfiguration yml;

    public static List<PlayerData> saveQueue = Lists.newArrayList();
    static HashMap<UUID, PlayerData> players = new HashMap<>();
    BukkitTask saveTask;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        app = this;

        // Load plugin message
        getLogger().info("[" + getDescription().getName() + "] Plugin Loaded! Running version " + getDescription().getVersion());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AdventCalendar(), this);
        pm.registerEvents(new LoadData(), this);

        // Load commands
        getCommand("calendar").setExecutor(new AdventCalendar());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File folder = new File(getDataFolder(), "users");
        if (!folder.exists()) {
            folder.mkdir();
        }
        processSaveQueue();
        loadOnlinePlayers();
        loadConfig();
        config = this.getConfig();
        Messages.saveDefaultMessages();
        prefix = Messages.getMessages().getString("prefix");
    }
    @Override
    public void onDisable() {
        getLogger().info("[" + getDescription().getName() + "] Plugin Unloaded!");
        saveTask.cancel();
        if (!saveQueue.isEmpty()) {
            for (PlayerData data : saveQueue) {
                data.forceSave();
            }
        }
    }

    void loadConfig() {
        file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveDefaultConfig();
        }
        yml = YamlConfiguration.loadConfiguration(file);
    }

    void loadOnlinePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            loadPlayerData(p.getUniqueId());
        }
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return players.get(uuid);
    }

    public static void loadPlayerData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        players.put(uuid, data);
    }

    public static void unloadPlayerData(UUID uuid) {
        PlayerData data = players.get(uuid);
        data.save();
        players.remove(uuid);
    }

    void processSaveQueue() {
        saveTask = Bukkit.getScheduler().runTaskLaterAsynchronously(app, new Runnable() {
            public void run() {
                if (!saveQueue.isEmpty()) {
                    PlayerData data = saveQueue.get(0);
                    saveQueue.remove(0);
                    data.forceSave();
                }
                processSaveQueue();
            }
        }, 20);
    }

    public void save(PlayerData data) {
        if (!saveQueue.contains(data)) {
            saveQueue.add(data);
        }
    }

    public static App getApp() {
        return app;
    }

}
