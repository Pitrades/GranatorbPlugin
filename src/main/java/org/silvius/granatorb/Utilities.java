package org.silvius.granatorb;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;

public class Utilities {
    public static void granatOrbCharge(Player player, int xpDrain, double newXp,  ItemStack item, boolean sendtitle){

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
        ArrayList<Component> lore = new ArrayList<>(Objects.requireNonNull(meta.lore()));
        player.giveExp(-xpDrain);
        lore.set(3, Component.text(ChatColor.RED + "VZ-Erfahrung: " + ChatColor.GRAY+((int) Math.round(newXp))+"/1400"));
        data.set(namespacedKey, PersistentDataType.DOUBLE, newXp);
        if(sendtitle) {
            player.sendTitle(ChatColor.RED + "Granatorb!", ChatColor.RED + "VZ-Erfahrung: " + ChatColor.GRAY+((int) Math.round(newXp))+"/1400", 0, 20, 6);
        }
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    public static void emptyGranatOrb(Player player, ItemStack item, double xpAmount){
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");

        //item.removeEnchantment(XpSave.customEnchantment);
        //meta.removeEnchant(Granatorb.customEnchantment);
        ArrayList<Component> lore = new ArrayList<>(Objects.requireNonNull(meta.lore()));
        ExperienceCalc.changeExp(player, (int) Math.round(0.9*xpAmount));
        lore.set(3, Component.text(ChatColor.RED + "VZ-Erfahrung: " + ChatColor.GRAY+"0/1400"));
        data.set(namespacedKey, PersistentDataType.DOUBLE, 0d);
        meta.lore(lore);
        item.setItemMeta(meta);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 0.95f);
        item.setAmount(0);
    }
}
