package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.element.NotificationBar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		NotificationBar.remove(player);
		User user = User.get(player);
		user.setIndividualPrefix(null);
		user.setPlayerList(null);
		user.setScoreboard(null);
	}

}
