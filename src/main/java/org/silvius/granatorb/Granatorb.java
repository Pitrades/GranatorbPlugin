package org.silvius.granatorb;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class Granatorb extends JavaPlugin implements Listener {
    private static Granatorb plugin;
    public static CustomEnchantment customEnchantment;

    public static Granatorb getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getCommand("granatorb").setExecutor(new granatOrbCommand());
        this.getServer().getPluginManager().registerEvents(this, this);
        plugin = this;
        customEnchantment = new CustomEnchantment("granatorb");
        registerEnchantment(customEnchantment);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Listeners(), this);


    }

    @Override
    public @NotNull ComponentLogger getComponentLogger() {
        return super.getComponentLogger();
    }

    @Override
    public void onDisable() {
        try {
            final Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);


            byKey.remove(customEnchantment.getKey());
            final Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            byName.remove(customEnchantment.getName());
        } catch (Exception ignored) { }
    }

    public static void registerEnchantment(Enchantment enchantment) {
        try {
            final Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }














