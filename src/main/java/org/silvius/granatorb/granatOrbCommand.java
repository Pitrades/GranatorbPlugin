package org.silvius.granatorb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    final static ChatColor loreColor = ChatColor.LIGHT_PURPLE;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            final Player player = ((Player) commandSender).getPlayer();

            assert player != null;
            if(!player.hasPermission("xpSave.granatorb")){
                commandSender.sendMessage(ChatColor.RED+"Keine Berechtigung");
                return true;
            }
            commandSender.sendMessage("Du hast einen Granatorb erhalten");
            final ItemStack stack = new ItemStack(Material.LAPIS_LAZULI);
            final ItemMeta meta = stack.getItemMeta();
            meta.displayName(Component.text(ChatColor.RED + "Granat-Orb"));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Dieser rötlich schimmernde Granat sammelt"));
            lore.add(Component.text( "magische Essenzen aus Eurer Umgebung"));
            lore.add(Component.text("(CIT) Granatorb").color(NamedTextColor.BLACK));
            lore.add(Component.text(ChatColor.RED + "VZ-Erfahrung: " + ChatColor.GRAY+"0/1400"));
            final PersistentDataContainer data = meta.getPersistentDataContainer();
            final NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
            data.set(namespacedKey, PersistentDataType.DOUBLE, 0d);
            meta.lore(lore);
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
