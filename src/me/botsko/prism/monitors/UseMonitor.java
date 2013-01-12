package me.botsko.prism.monitors;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.botsko.prism.Prism;

public class UseMonitor {
	
	/**
	 * 
	 */
	private Prism plugin;
	
	/**
	 * 
	 */
	protected final ArrayList<String> blocksToAlert;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<String,Integer> countedEvents = new ConcurrentHashMap<String,Integer>();
	
	
	/**
	 * 
	 * @param plugin
	 */
	@SuppressWarnings("unchecked")
	public UseMonitor( Prism plugin ){
		this.plugin = plugin;
		blocksToAlert = (ArrayList<String>) plugin.getConfig().getList("prism.alerts.uses.item-placement");
		resetEventsQueue();
	}
	
	
	/**
	 * 
	 * @param playername
	 * @return
	 */
	protected void incrementCount(String playername, String msg){
		
		int count = 0;
		if(countedEvents.containsKey(playername)){
			count = countedEvents.get(playername);
		}
		count++;
		countedEvents.put(playername, count );
		
		if(count == 5){
			plugin.alertPlayers(null, playername + " continues - pausing warnings.");
		} else {
			if(count < 5){
				plugin.alertPlayers(null, ChatColor.GRAY + playername + " " + msg);
			}
		}
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	protected boolean checkFeatureShouldProceed( Player player ){
		// Ensure enabled
		if(!plugin.getConfig().getBoolean("prism.alerts.uses.enabled")){
			return false;
		}
		
		// Ignore players who would see the alerts
		if ( plugin.getConfig().getBoolean("prism.alerts.uses.ignore-staff") && player.hasPermission("prism.alerts") ){
			return false;
		}
		
		// Ignore certain ranks
		if( player.hasPermission("prism.bypass-use-alerts")){
			return false;
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param player
	 * @param block
	 */
	public void alertOnBlockPlacement( Player player, Block block ){
		
		// Ensure enabled
		if( !checkFeatureShouldProceed( player ) ){
			return;
		}
		
		String playername = player.getName();
		String blockType = ""+block.getTypeId();
		
		// Ensure we're tracking this block
		if(blocksToAlert.contains( blockType )){
			String alias = plugin.getItems().getItemStackAliasById(block.getTypeId(), block.getData());
			incrementCount(playername, "placed "+alias);
		}
	}
	
	
	/**
	 * 
	 * @param player
	 * @param use_msg
	 */
	public void alertOnItemUse( Player player, String use_msg ){
		
		// Ensure enabled
		if( !checkFeatureShouldProceed( player ) ){
			return;
		}
		
		String playername = player.getName();
		incrementCount(playername,use_msg);
		
	}
	
	
	/**
	 * Reset the queue every now and then
	 * Technically this can reset someone's counts too early but
	 * that just means staff will see extra warnings.
	 */
	public void resetEventsQueue(){
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		    	countedEvents = new ConcurrentHashMap<String,Integer>();
		    }
		}, 7000L, 7000L);
	}
}