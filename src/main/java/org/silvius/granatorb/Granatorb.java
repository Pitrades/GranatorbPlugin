package org.silvius.granatorb;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Granatorb extends JavaPlugin implements Listener {
    private static Granatorb plugin;

    public static Granatorb getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getCommand("granatorb").setExecutor(new granatOrbCommand());
        this.getServer().getPluginManager().registerEvents(this, this);
        plugin = this;
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Listeners(), this);


    }

    @Override
    public @NotNull ComponentLogger getComponentLogger() {
        return super.getComponentLogger();
    }

    @Override
    public void onDisable() {
    }



    }














