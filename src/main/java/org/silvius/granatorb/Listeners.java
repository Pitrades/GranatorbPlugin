package org.silvius.granatorb;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Listeners implements Listener {
    private final HashMap<UUID, Long> cooldown;
    private final HashMap<UUID, Long[]> playerRightClickTime;

    public Listeners() {
        this.cooldown=new HashMap<>();
        this.playerRightClickTime=new HashMap<>();
    }

    @EventHandler
    public void onGrindstonePrepare(PrepareResultEvent event){
        if(event.getInventory().getType()!= InventoryType.GRINDSTONE && event.getInventory().getType()!= InventoryType.ANVIL){return;}
        ItemStack result = event.getResult();
        if(result==null){return;}
        ItemMeta meta = result.getItemMeta();
        if(meta==null){return;}
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
        if (data.has(namespacedKey)){
            event.setResult(null);
        }
    }



    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        final ItemStack item = event.getItem();
        final ItemMeta meta = item.getItemMeta();
        if(meta==null){return;}
        final PersistentDataContainer data = meta.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
        if (data.has(namespacedKey)) {
            final ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text(" "));
            final double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE);
            final double newXp = xpAmount + 1395;
            lore.add(Component.text(granatOrbCommand.getLoreColor() + Integer.toString((int) Math.floor(xpAmount)) + "/1000 VZ-Erfahrung"));
            data.set(namespacedKey, PersistentDataType.DOUBLE, newXp);
            meta.lore(lore);
            item.setItemMeta(meta);
            event.getEnchanter().setLevel(event.getEnchanter().getLevel() - 30);
        }
    }


    public void fillAnimation(Player player, Block block){
        final Location playerLocation = player.getLocation().add(0, 0.8, 0); // get player location
        final Location tableLocation = block.getLocation().add(0.5, 0.5, 0.5); // get enchantment table location

        double distance = playerLocation.distance(tableLocation); // get distance between player and table
        final Vector direction = tableLocation.toVector().subtract(playerLocation.toVector()).normalize(); // get normalized vector pointing from player to table
        final Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize(); // get perpendicular vector to the direction vector
        final int lingeringTime = 40;
        final double radius = 1;
        final double speed = distance / lingeringTime; // set speed of the particles
        if(!this.cooldown.containsKey(player.getUniqueId())){
            this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME,10, 1);
        }
        else{
            final long timeElapsed = System.currentTimeMillis() - cooldown.get(player.getUniqueId());
            if(timeElapsed>200){
                player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME,10, 1);
                this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }


// create a Runnable to update the particle positions every tick
        final Runnable particleRunnable = new Runnable() {
            double t = 0; // initialize t to 0

            public void run() {
                Vector perpendicularNew = perpendicular.rotateAroundAxis(direction, t / distance);
                // calculate the position of the particle along the curve using this perpendicular vector

                final Vector position1 = tableLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.clone().multiply(Math.sin(0.5*t * Math.PI / distance) * radius));
                final Vector position2 = tableLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.multiply(-1).clone().multiply(Math.sin(0.5*t * Math.PI / distance) * radius));
                player.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, position1.getX(), position1.getY(), position1.getZ(), 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.SCULK_SOUL, position2.getX(), position2.getY(), position2.getZ(), 0, 0, 0, 0);

                t += speed;

                if (t > distance) {
                    t = 0;
                }


            }
        };

// run the particleRunnable every tick
        final int taskId = Bukkit.getScheduler().runTaskTimer(Granatorb.getPlugin(), particleRunnable, 0, 1).getTaskId();
        Runnable cancelParticlesRunnable = () -> Bukkit.getScheduler().cancelTask(taskId);


        Bukkit.getScheduler().runTaskLater(Granatorb.getPlugin(), cancelParticlesRunnable, lingeringTime);
    }


    public void emptyAnimation(Player player){
        final Location playerBottomLocation = player.getLocation().add(0, 0, 0); // get player location
        final Location playerTopLocation = player.getLocation().add(0, -2, 0); // get enchantment table location

        final double distance = playerBottomLocation.distance(playerTopLocation); // get distance between player and table
        final Vector direction = playerTopLocation.toVector().subtract(playerBottomLocation.toVector()).normalize(); // get normalized vector pointing from player to table
        System.out.println(direction);
        final Vector perpendicular = new Vector(1, 0, 0).normalize(); // get perpendicular vector to the direction vector
        final int lingeringTime = 40;
        final double radius = 0.8;
        final double speed = distance / lingeringTime; // set speed of the particles


        if(!this.cooldown.containsKey(player.getUniqueId())){
            this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE,10, 1);
        }
        else{
            final long timeElapsed = System.currentTimeMillis() - cooldown.get(player.getUniqueId());
            if(timeElapsed>200){
                player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE,10, 1);
                this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
        Long rightClickTime = this.playerRightClickTime.get(player.getUniqueId())[0];

// create a Runnable to update the particle positions every tick
        final Runnable particleRunnable = new Runnable() {
            double t = 0; // initialize t to 0

            public void run() {
                final Vector perpendicularNew = perpendicular.rotateAroundAxis(direction, 4*t/(2*Math.PI) );
                // calculate the position of the particle along the curve using this perpendicular vector

                final Vector position1 = playerBottomLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.clone().multiply(radius));
                final Vector position2 = playerBottomLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.clone().multiply( -radius));
                player.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, position1.getX(), position1.getY(), position1.getZ(), 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.SCULK_SOUL, position2.getX(), position2.getY(), position2.getZ(), 0, 0, 0, 0);


                t += speed;

                if (t > distance) {
                    t = 0;
                }


            }
        };

// run the particleRunnable every tick
        final int taskId = Bukkit.getScheduler().runTaskTimer(Granatorb.getPlugin(), particleRunnable, 0, 1).getTaskId();
        final Runnable cancelParticlesRunnable = () -> Bukkit.getScheduler().cancelTask(taskId);

        Bukkit.getScheduler().runTaskLater(Granatorb.getPlugin(), cancelParticlesRunnable, rightClickTime);
    }

    public void emptyGranatOrbCharge(Player player, ItemStack item, double xpAmount) {
        if(!this.playerRightClickTime.containsKey(player.getUniqueId())){
            playerRightClickTime.put(player.getUniqueId(), new Long[]{0L, System.currentTimeMillis()});
        }

        Long rightClickTime = this.playerRightClickTime.get(player.getUniqueId())[0];
        rightClickTime=rightClickTime+5;
        if(rightClickTime>=40){
            playerRightClickTime.put(player.getUniqueId(), new Long[]{rightClickTime, System.currentTimeMillis()});
            Utilities.emptyGranatOrb(player, item, xpAmount);
            return;
        }

        emptyAnimation(player);

        final Runnable cancelParticlesRunnable = () -> {
            Long updatedTime = playerRightClickTime.get(player.getUniqueId())[1];

            if((System.currentTimeMillis()-updatedTime)>=200){

                playerRightClickTime.put(player.getUniqueId(), new Long[]{0L, System.currentTimeMillis()});}
        };



        Bukkit.getScheduler().runTaskLater(Granatorb.getPlugin(), cancelParticlesRunnable, 20);
        playerRightClickTime.put(player.getUniqueId(), new Long[]{rightClickTime, System.currentTimeMillis()});




    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if(item==null){return;}
        int xpDrain = 50;
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer data = meta.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
        if (event.getAction().isRightClick() && data.has(namespacedKey)) {
            final Player player = event.getPlayer();
            if (!player.isSneaking()) {
                return;
            }
            final int stackSize = item.getAmount();
            final double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE) * stackSize;
            if(event.getClickedBlock()==null){
                if (xpAmount > 0) {
                //Utilities.emptyGranatOrb(player, item, xpAmount);
                    emptyGranatOrbCharge(player, item, xpAmount);
            }return;}
            if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                if (ExperienceCalc.getExp(player) > 0) {
                    if (xpAmount / stackSize == 1400) {
                        return;
                    }
                    if (ExperienceCalc.getExp(player) < xpDrain) {
                        xpDrain = ExperienceCalc.getExp(player);
                    }
                    final double newXp = xpAmount / stackSize + (double) xpDrain / stackSize;
                    if (newXp >= 1400) {
                        item.addUnsafeEnchantment(Granatorb.customEnchantment, 1);
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                        Utilities.granatOrbCharge(player, (int) Math.round(1400 * stackSize - xpAmount), 1400, item, true);
                        return;
                    }
                    Utilities.granatOrbCharge(player, xpDrain, newXp, item, true);
                    fillAnimation(player, event.getClickedBlock());
                    event.setCancelled(true);
                }
                {

                }
            } else {
                if (xpAmount > 0) {
                    emptyGranatOrbCharge(player, item, xpAmount);
                    //Utilities.emptyGranatOrb(player, item, xpAmount);
                }
                }
            }
    }


    @EventHandler
    public void onEXPCollection(PlayerPickupExperienceEvent event) {
        final Player player = event.getPlayer();
        final Inventory inventory = player.getInventory();
        for (int i = 0; i <= 8; i++) {
            final ItemStack item = inventory.getItem(i);
            if (item != null) {
                final ItemMeta meta = item.getItemMeta();
                final PersistentDataContainer data = meta.getPersistentDataContainer();
                final NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
                if (data.has(namespacedKey)) {
                    final int stackSize = item.getAmount();
                    final double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE);
                    final double newXp = xpAmount + (double) event.getExperienceOrb().getExperience() / stackSize;
                    if (xpAmount == 1400) {
                        continue;
                    }
                    if (newXp >= 1400) {
                        item.addUnsafeEnchantment(Granatorb.customEnchantment, 1);
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                        Utilities.granatOrbCharge(player, (int) Math.ceil((newXp-1400)*stackSize), 1400, item, false);
                        return;
                    }

                    Utilities.granatOrbCharge(player, 0, newXp, item, false);
                    event.getExperienceOrb().setExperience(0);
                    break;
                }
            }

        }
    }


}
