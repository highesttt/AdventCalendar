package com.highest.adventcalendar.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.highest.adventcalendar.App;

public class PlayerData {

    App main;
    public File file;
    public FileConfiguration yml;
    Player player;
    UUID uuid;

    HashMap<Integer, Boolean> calendar = new HashMap<>();

    public PlayerData(UUID uuid) {
        main = App.getApp();
        this.uuid = uuid;
        load();
    }

    void load() {
        try {
            // if App.getApp.getDataFolder users/ doesn't exist, create it
            file = new File(App.getApp().getDataFolder(), "users/" + uuid.toString() + ".yml");
            // if file doesnt exist
            if (!file.exists()) {
                // create file
                file.createNewFile();
            }

            yml = YamlConfiguration.loadConfiguration(file);

            for (int i = 1; i <= 24; i++) {
                if (yml.contains("calendar." + i)) {
                    calendar.put(i, yml.getBoolean("calendar." + i));
                } else {
                    calendar.put(i, false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void forceSave() {

        try {
            for (int i = 1; i <= 24; i++) {
                if (calendar.get(i)) {
                    yml.set("calendar." + i, calendar.get(i));
                }
            }
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        main.save(this);
    }

    public boolean isClaimed(int Day) {
        return calendar.get(Day);
    }

    public void setClaimed(int Day, boolean claimed) {
        calendar.replace(Day, claimed);
        save();
    }
}
