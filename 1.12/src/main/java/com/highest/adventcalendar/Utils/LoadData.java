package com.highest.adventcalendar.Utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.highest.adventcalendar.App;

public class LoadData implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(final PlayerLoginEvent event) {
        if (event.getResult().equals(Result.ALLOWED)) {
            Bukkit.getScheduler().runTaskAsynchronously(App.getApp(), new Runnable() {
                public void run() {
                    App.loadPlayerData(event.getPlayer().getUniqueId());
                }
            });
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        PlayerData data = App.getPlayerData(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(App.getApp(), new Runnable() {
            public void run() {
                Bukkit.getScheduler().runTaskLater(App.getApp(), () -> {
                    data.forceSave();
                }, 10L);
                Bukkit.getScheduler().runTaskLater(App.getApp(), () -> {
                    App.unloadPlayerData(event.getPlayer().getUniqueId());
                }, 20L);
            }
        });
    }
}
