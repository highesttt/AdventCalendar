package com.highest.adventcalendar.Utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSmith {

    public static ItemStack makeItem(Material material, int datavalue, int amount, String name, String[] itemlore, boolean enchanted) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (itemlore != null) {
            meta.setLore(Arrays.asList(itemlore));
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        if (enchanted) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        return item;
    }
}