package com.highest.adventcalendar.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.highest.adventcalendar.App;

import net.md_5.bungee.api.ChatColor;

public class Messages {
    private static FileConfiguration messageConfig = null;
    private static File messageFile = null;

    public static void reloadMessages() {
        if (messageFile == null) {
            messageFile = new File(App.getApp().getDataFolder(), "messages.yml");
        }
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);

        try {
            Reader defConfigStream = new InputStreamReader(App.getApp().getResource("messages.yml"), "UTF8");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                messageConfig.setDefaults(defConfig);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getMessages() {
        if (messageConfig == null) {
            reloadMessages();
        }
        return messageConfig;
    }

    public static void saveMessages() {
        if (messageConfig == null || messageFile == null) {
            return;
        }
        try {
            getMessages().save(messageFile);
        } catch (IOException ex) {
            App.getApp().getLogger().log(Level.SEVERE, "Could not save config to " + messageFile, ex);
        }
    }

    public static void saveDefaultMessages() {
        if (messageFile == null) {
            messageFile = new File(App.getApp().getDataFolder(), "messages.yml");
        }
        if (!messageFile.exists()) {
            App.getApp().saveResource("messages.yml", false);
        }
    }

    public static String get(String string, Player player) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);

        return ChatColor.translateAlternateColorCodes('&', Messages.getMessages().getString(string).replace("%prefix%", App.prefix).replace("%year%", String.valueOf(year)).replace("%player%", player.getName()));
    }

    public static String getDay(String string, Player player, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);

        return ChatColor.translateAlternateColorCodes('&', Messages.getMessages().getString(string).replace("%prefix%", App.prefix).replace("%year%", String.valueOf(year)).replace("%player%", player.getName()).replace("%day%", String.valueOf(i)));
    }
}
