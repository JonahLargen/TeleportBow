package me.jonah.TeleportBow;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportBow extends JavaPlugin implements Listener{

	@Override
	public void onEnable(){
		getConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.getServer().addRecipe(TeleportBowRecipe());
		Bukkit.getLogger().info("TeleportBow by BossesCraftCake is being Enabled!");
		if (!getConfig().contains("velocity")){
			getConfig().createSection("velocity");
			getConfig().set("velocity", 1.1);
			saveConfig();
		}
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("TeleportBow by BossesCraftCake is being Disabled.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player){
			Player p = (Player) sender;
			if (p.isOp()){
				if (label.equalsIgnoreCase("reloadteleportbow")){
					reloadConfig();
					saveConfig();
					p.sendMessage(ChatColor.GREEN + "Teleport Bow Config Reloaded.");
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onShootBow(EntityShootBowEvent event){
		if (event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if (event.isCancelled() == false){
				ItemStack bow = event.getBow();
				if (bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) == 3 && p.getInventory().contains(Material.ENDER_PEARL)){
					event.setCancelled(true);
					p.launchProjectile(EnderPearl.class).setVelocity(event.getProjectile().getVelocity().multiply(getConfig().getDouble("velocity")));
					p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
					bow.setDurability((short) (bow.getDurability() - 2));
					p.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL,1));
				}
				if (bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) == 3 && !p.getInventory().contains(Material.ENDER_PEARL)){
					event.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You must carry ender pearls to use this!");
				}
			}
		}
	}

	public ShapedRecipe TeleportBowRecipe(){
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
		bowMeta.setDisplayName(ChatColor.DARK_PURPLE + "Ender Bow");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Teleport I");
		bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		bow.setItemMeta(bowMeta);

		ShapedRecipe recipe = new ShapedRecipe(bow);
		recipe.shape("AXA", "ABA", "AEA");		
		recipe.setIngredient('X', Material.NETHER_STAR);
		recipe.setIngredient('B', Material.BOW);
		recipe.setIngredient('E', Material.EYE_OF_ENDER);

		return recipe;
	}
}
