package org.silvius.granatorb;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class granatOrbCommand implements CommandExecutor {
    static ChatColor loreColor = ChatColor.LIGHT_PURPLE;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();

            if(!player.hasPermission("lyriaseelenbindung.seelenbindung")){
                commandSender.sendMessage(ChatColor.RED+"Keine Berechtigung");
                return true;
            }
            commandSender.sendMessage("Du hast einen Granatorb erhalten");
            ItemStack stack = new ItemStack(Material.LAPIS_LAZULI);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Granatorb");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(ChatColor.LIGHT_PURPLE + "0/1400 VZ-Erfahrung");
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
            data.set(namespacedKey, PersistentDataType.DOUBLE, 0d);
            meta.setLore(lore);
            stack.addUnsafeEnchantment(Granatorb.customEnchantment, 1);
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);

        }
        return true;

    }

    public static ChatColor getLoreColor(){
        return loreColor;
    }
}
