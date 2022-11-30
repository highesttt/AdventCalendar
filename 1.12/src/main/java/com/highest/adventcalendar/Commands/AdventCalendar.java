package com.highest.adventcalendar.Commands;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.highest.adventcalendar.App;
import com.highest.adventcalendar.Utils.ItemSmith;
import com.highest.adventcalendar.Utils.Messages;
import com.highest.adventcalendar.Utils.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class AdventCalendar implements CommandExecutor, Listener {

    private static HashMap<Integer, Boolean> claimed = new HashMap<Integer, Boolean>();
    private static HashMap<Integer, Integer> slots = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> slotsrev = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Material> item = new HashMap<Integer, Material>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("adventcalendar.use")) {
                player.sendMessage(Messages.get("no-permission", player));
                return true;
            }
            Inventory inv = Bukkit.createInventory(null, 54, Messages.get("inventory-name", player));
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("adventcalendar.reload")) {
                    App.getApp().reloadConfig();
                    App.config = App.getApp().getConfig();
                    Messages.reloadMessages();
                    App.prefix = Messages.getMessages().getString("prefix");
                    player.sendMessage(Messages.get("message-reload-success", player));
                    return true;
                } else {
                    player.sendMessage(Messages.get("no-permission-reload", player));
                    return true;
                }
            }

            if (player.hasPermission("adventcalendar.anyday")) {
                day = (args.length < 1) ? day : Integer.parseInt(args[0]);
                month = (args.length < 1) ? month : 11;
            }

            if (month == 11 && day < 25) {
                for (int i = 0; i < 54; i++) {
                    inv.setItem(i, getFillerItem());
                }
                init(player);
                FileConfiguration file = App.config;
                boolean enchantCurrent = file.getBoolean("items.enchant-current-day");
                boolean enchantClaimed = file.getBoolean("items.enchant-claimed");
                boolean enchantPast = file.getBoolean("items.enchant-past");

                String nameUnclaimedPast = ChatColor.translateAlternateColorCodes('&', file.getString("items.name.unclaimed-past"));
                String nameUnclaimedPresent = ChatColor.translateAlternateColorCodes('&', file.getString("items.name.unclaimed-present"));
                String nameUnclaimedFuture = ChatColor.translateAlternateColorCodes('&', file.getString("items.name.unclaimed-future"));
                String nameClaimedPast = ChatColor.translateAlternateColorCodes('&', file.getString("items.name.claimed-past"));
                String nameClaimedPresent = ChatColor.translateAlternateColorCodes('&', file.getString("items.name.claimed-present"));

                String loreUnclaimedPast = ChatColor.translateAlternateColorCodes('&', file.getString("items.lore.unclaimed-past"));
                String loreUnclaimedPresent = ChatColor.translateAlternateColorCodes('&', file.getString("items.lore.unclaimed-present"));
                String loreUnclaimedFuture = ChatColor.translateAlternateColorCodes('&', file.getString("items.lore.unclaimed-future"));
                String loreClaimedPast = ChatColor.translateAlternateColorCodes('&', file.getString("items.lore.claimed-past"));
                String loreClaimedPresent = ChatColor.translateAlternateColorCodes('&', file.getString("items.lore.claimed-present"));
                for (int i = 1; i < day; i++) {
                    if (claimed.get(i)) {
                        inv.setItem(slotsrev.get(i), ItemSmith.makeItem(item.get(2), 0, 1, nameClaimedPast.replace("%day%", Integer.toString(i)), loreClaimedPast.replace("%day%", Integer.toString(i)).split("%nl%"), enchantClaimed));
                    } else {
                        inv.setItem(slotsrev.get(i), ItemSmith.makeItem(item.get(-1), 0, 1, nameUnclaimedPast.replace("%day%", Integer.toString(i)), loreUnclaimedPast.replace("%day%", Integer.toString(i)).split("%nl%"), enchantPast));
                    }
                }
                if (claimed.get(day)) {
                    boolean enchant = (enchantCurrent || enchantClaimed) ? true : false;
                    inv.setItem(slotsrev.get(day), ItemSmith.makeItem(item.get(3), 0, 1, nameClaimedPresent.replace("%day%", Integer.toString(day)), loreClaimedPresent.replace("%day%", Integer.toString(day)).split("%nl%"), enchant));
                } else {
                    inv.setItem(slotsrev.get(day), ItemSmith.makeItem(item.get(1), 0, 1, nameUnclaimedPresent.replace("%day%", Integer.toString(day)), loreUnclaimedPresent.replace("%day%", Integer.toString(day)).split("%nl%"), enchantCurrent));
                }
                for (int i = day + 1; i <= 24; i++) {
                    inv.setItem(slotsrev.get(i), ItemSmith.makeItem(item.get(0), 0, 1, nameUnclaimedFuture.replace("%day%", Integer.toString(i)), loreUnclaimedFuture.replace("%day%", Integer.toString(i)).split("%nl%"), false));
                }
                inv.setItem(38, ItemSmith.makeItem(Material.BARRIER, 0, 1, ChatColor.RED + "Close", null, false));
                inv.setItem(42, ItemSmith.makeItem(Material.BARRIER, 0, 1, ChatColor.RED + "Close", null, false));
                player.openInventory(inv);
            } else {
                player.sendMessage(Messages.get("not-december", player));
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        // get inventory name
        if (event.getInventory().getName().equals(Messages.get("inventory-name", (Player) (event.getWhoClicked()))) == true) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getType() == Material.BARRIER) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                if (event.getAction() == null) {
                    return;
                }
                int day = slots.get(event.getSlot());
                boolean allowExpired = App.config.getBoolean("items.allow-expired");
                if (item.get(2).equals(event.getCurrentItem().getType()) || item.get(3).equals(event.getCurrentItem().getType())) {
                    player.sendMessage(Messages.getDay("reward-already-claimed", player, day));
                } else if (item.get(0).equals(event.getCurrentItem().getType())) {
                    player.sendMessage(Messages.getDay("reward-not-available", player, day));
                } else if (item.get(-1).equals(event.getCurrentItem().getType()) && !allowExpired) {
                    player.sendMessage(Messages.getDay("reward-expired", player, day));
                } else if (item.get(1).equals(event.getCurrentItem().getType()) || (item.get(-1).equals(event.getCurrentItem().getType()) && allowExpired)) {
                    claimed.put(day, true);
                    giveRewards(player, day);
                    player.closeInventory();
                }
            }
        }
    }
    private ItemStack getFillerItem() {
        int r = (int) (Math.random() * 3);
        // 0 = white
        // 1 = green
        // 2 = red
        if (r == 0) {
            return ItemSmith.makeItem(Material.STAINED_GLASS_PANE, 0, 1, " ", null, false);
        } else if (r == 1) {
            return ItemSmith.makeItem(Material.STAINED_GLASS_PANE, 5, 1, " ", null, false);
        } else {
            return ItemSmith.makeItem(Material.STAINED_GLASS_PANE, 14, 1, " ", null, false);
        }
    }

    private void giveRewards(Player player, int day) {
        PlayerData data = App.getPlayerData(player.getUniqueId());
        data.setClaimed(day, true);

        FileConfiguration file = App.config;
        player.sendMessage(Messages.getDay("reward-successfully-claimed", player, day));
        file.getStringList("rewards." + day).forEach(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        });
    }

    private void init(Player player) {
        claimed.clear();
        for (int i = 1; i <= 24; i++) {
            boolean claimed = App.getPlayerData(player.getUniqueId()).isClaimed(i);
            AdventCalendar.claimed.put(i, claimed);
        }
        FileConfiguration file = App.config;
        // -1 unclaimed, items.unclaimed-past in config
        item.put(-1, Material.getMaterial(file.getString("items.unclaimed-past")));
        // 0 unclaimed, items.unclaimed-future in config
        item.put(0, Material.getMaterial(file.getString("items.unclaimed-future")));
        // 1 unclaimed, items.unclaimed-present in config
        item.put(1, Material.getMaterial(file.getString("items.unclaimed-present")));
        // 2 claimed, items.claimed-past in config
        item.put(2, Material.getMaterial(file.getString("items.claimed-past")));
        // 3 claimed, items.claimed-present in config
        item.put(3, Material.getMaterial(file.getString("items.claimed-present")));
        slots.put(10, 1);
        slots.put(11, 2);
        slots.put(12, 3);
        slots.put(13, 4);
        slots.put(14, 5);
        slots.put(15, 6);
        slots.put(16, 7);
        slots.put(19, 8);
        slots.put(20, 9);
        slots.put(21, 10);
        slots.put(22, 11);
        slots.put(23, 12);
        slots.put(24, 13);
        slots.put(25, 14);
        slots.put(28, 15);
        slots.put(29, 16);
        slots.put(30, 17);
        slots.put(31, 18);
        slots.put(32, 19);
        slots.put(33, 20);
        slots.put(34, 21);
        slots.put(39, 22);
        slots.put(40, 23);
        slots.put(41, 24);

        slotsrev.put(1, 10);
        slotsrev.put(2, 11);
        slotsrev.put(3, 12);
        slotsrev.put(4, 13);
        slotsrev.put(5, 14);
        slotsrev.put(6, 15);
        slotsrev.put(7, 16);
        slotsrev.put(8, 19);
        slotsrev.put(9, 20);
        slotsrev.put(10, 21);
        slotsrev.put(11, 22);
        slotsrev.put(12, 23);
        slotsrev.put(13, 24);
        slotsrev.put(14, 25);
        slotsrev.put(15, 28);
        slotsrev.put(16, 29);
        slotsrev.put(17, 30);
        slotsrev.put(18, 31);
        slotsrev.put(19, 32);
        slotsrev.put(20, 33);
        slotsrev.put(21, 34);
        slotsrev.put(22, 39);
        slotsrev.put(23, 40);
        slotsrev.put(24, 41);
    }
}
