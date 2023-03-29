package org.silvius.granatorb;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.*;
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


public class BlockListeners implements Listener {
    private final HashMap<UUID, Long> cooldown;

    public BlockListeners() {
        this.cooldown=new HashMap<>();
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


//    @EventHandler
//    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
//        Player player = event.getEnchanter();
//return;
//        ItemStack enchantItem = event.getInventory().getItem(0);
//        ItemStack lapisItem = event.getInventory().getItem(1);
//        if (lapisItem == null || lapisItem.getItemMeta()==null || enchantItem==null){return;}
//        ItemMeta meta = lapisItem.getItemMeta();
//        PersistentDataContainer data = meta.getPersistentDataContainer();
//        NamespacedKey namespacedKey = new NamespacedKey(XpSave.getPlugin(), "xpStored");
//        if(data.has(namespacedKey)){
//            double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE);
//            for(EnchantmentOffer offer : event.getOffers()){
//                assert offer != null;
//                if(offer.getEnchantmentLevel()<ExperienceCalc.getLevelFromExp((int) Math.floor(xpAmount))){
//                }
//            }
//        }
//
//
//
//
//    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {

        ItemStack item = event.getItem();
        if (item.lore().get(1).children().toString().contains("Level")) {
            ArrayList<String> lore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
            lore.add(" ");
            double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE);
            double newXp = xpAmount + 1395;
            lore.add(granatOrbCommand.getLoreColor() + Integer.toString((int) Math.round(xpAmount)) + "/1000 VZ-Erfahrung");
            data.set(namespacedKey, PersistentDataType.DOUBLE, newXp);
            meta.setLore(lore);
            item.setItemMeta(meta);

            event.getEnchanter().setLevel(event.getEnchanter().getLevel() - 30);
        }
    }


    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getItem() != null) {
            int xpDrain = 50;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
            if (event.getAction().isRightClick() && data.has(namespacedKey)) {
                Player player = event.getPlayer();
                if (!player.isSneaking()) {
                    return;
                }
                int stackSize = item.getAmount();
                double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE) * stackSize;

                if (event.getClickedBlock() != null) {
                    if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                        if (ExperienceCalc.getExp(player) > 0) {
                            if (xpAmount / stackSize == 1400) {
                                return;
                            }
                            if (ExperienceCalc.getExp(player) < xpDrain) {
                                xpDrain = ExperienceCalc.getExp(player);
                            }
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(" ");
                            double newXp = xpAmount / stackSize + (double) xpDrain / stackSize;
                            if (newXp >= 1400) {
                                item.addUnsafeEnchantment(Granatorb.customEnchantment, 1);
                                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                                Utilities.granatOrbCharge(player, (int) Math.floor(1400 * stackSize - xpAmount), 1400, item, true);
                                return;
                            }
                            Utilities.granatOrbCharge(player, xpDrain, newXp, item, true);
                            Location playerLocation = player.getLocation().add(0, 0.8, 0); // get player location
                            Location tableLocation = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5); // get enchantment table location

                            double distance = playerLocation.distance(tableLocation); // get distance between player and table
                            Vector direction = tableLocation.toVector().subtract(playerLocation.toVector()).normalize(); // get normalized vector pointing from player to table
                            Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize(); // get perpendicular vector to the direction vector
                            int lingeringTime = 40;
                            double radius = 1;
                            double speed = distance / lingeringTime; // set speed of the particles
                            int numTrails = 8; // set number of particle trails
                            double angleBetweenTrails = 2 * Math.PI / numTrails;
                            if(!this.cooldown.containsKey(player.getUniqueId())){
                                this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                                player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME,10, 1);
                            }
                            else{
                                long timeElapsed = System.currentTimeMillis() - cooldown.get(player.getUniqueId());
                                if(timeElapsed>200){
                                    player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME,10, 1);
                                    this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                                }
                            }


// create a Runnable to update the particle positions every tick
                            Runnable particleRunnable = new Runnable() {
                                double t = 0; // initialize t to 0

                                public void run() {
                                    // calculate the position of the particle along the curve
                                    for (int i = 0; i < 1; i++) {
                                        // calculate perpendicular vector for this trail by rotating the original perpendicular vector around the distance vector
                                        double angle = i * angleBetweenTrails;
                                        //Vector perpendicularNew = new Vector(Math.cos(angle) * direction.getX() + Math.sin(angle) * direction.getZ(), 0, -Math.sin(angle) * direction.getX() + Math.cos(angle) * direction.getZ()).normalize();
                                        Vector perpendicularNew = perpendicular.rotateAroundAxis(direction, t / distance);
                                        // calculate the position of the particle along the curve using this perpendicular vector

                                        Vector position1 = tableLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.clone().multiply(Math.sin(0.5*t * Math.PI / distance) * radius));
                                        Vector position2 = tableLocation.toVector().subtract(direction.clone().multiply(t)).add(perpendicularNew.multiply(-1).clone().multiply(Math.sin(0.5*t * Math.PI / distance) * radius));


                                        // spawn the particle at the calculated position

                                        player.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, position1.getX(), position1.getY(), position1.getZ(), 0, 0, 0, 0);
                                        player.getWorld().spawnParticle(Particle.SCULK_SOUL, position2.getX(), position2.getY(), position2.getZ(), 0, 0, 0, 0);
                                        //, new Particle.DustOptions(Color.PURPLE, 1f));
                                    }





                                    // increment t by the speed of the particles
                                    t += speed;

                                    if (t > distance) {
                                        t = 0;
                                    }


                                }
                            };

// run the particleRunnable every tick
                            int taskId = Bukkit.getScheduler().runTaskTimer(Granatorb.getPlugin(), particleRunnable, 0, 1).getTaskId();
                            Runnable cancelParticlesRunnable = new Runnable() {
                                public void run() {
                                    Bukkit.getScheduler().cancelTask(taskId);
                                }
                            };


                            Bukkit.getScheduler().runTaskLater(Granatorb.getPlugin(), cancelParticlesRunnable, lingeringTime);

                            event.setCancelled(true);
                        }
                        {

                        }
                    } else {
                        if (xpAmount > 0) {
                            Utilities.emptyGranatOrb(player, item, xpAmount);
                        }
                    }
                } else {
                    if (xpAmount > 0) {
                        Utilities.emptyGranatOrb(player, item, xpAmount);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onEXPCollection(PlayerPickupExperienceEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();
        for (int i = 0; i <= 8; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey namespacedKey = new NamespacedKey(Granatorb.getPlugin(), "xpStored");
                if (data.has(namespacedKey)) {
                    int stackSize = item.getAmount();
                    double xpAmount = data.get(namespacedKey, PersistentDataType.DOUBLE);
                    double newXp = xpAmount + (double) event.getExperienceOrb().getExperience() / stackSize;
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
