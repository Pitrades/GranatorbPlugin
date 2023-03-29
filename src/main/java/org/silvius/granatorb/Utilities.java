package org.silvius.granatorb;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Utilities {
    public static void granatOrbCharge(Player player, int xpDrain, double newXp,  ItemStack item, boolean sendtitle){

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");

        ArrayList< String > lore = new ArrayList < > ();
        lore.add(" ");
        ExperienceCalc.changeExp(player, -xpDrain);
        lore.add(granatOrbCommand.getLoreColor() + Integer.toString((int) Math.round(newXp))+"/1400 VZ-Erfahrung");
        data.set(namespacedKey, PersistentDataType.DOUBLE, newXp);
        if(sendtitle) {
            player.sendTitle(ChatColor.RED + "Granatorb!", granatOrbCommand.getLoreColor() + Integer.toString((int) Math.round(newXp)) + "/1400 VZ-Erfahrung", 0, 20, 6);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static void emptyGranatOrb(Player player, ItemStack item, double xpAmount){
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");

        //item.removeEnchantment(XpSave.customEnchantment);
        meta.removeEnchant(Granatorb.customEnchantment);
        ArrayList < String > lore = new ArrayList < > ();
        lore.add(" ");
        ExperienceCalc.changeExp(player, (int) Math.round(0.9*xpAmount));
        lore.add(granatOrbCommand.getLoreColor() + "0/1400 VZ-Erfahrung");
        data.set(namespacedKey, PersistentDataType.DOUBLE, 0d);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
