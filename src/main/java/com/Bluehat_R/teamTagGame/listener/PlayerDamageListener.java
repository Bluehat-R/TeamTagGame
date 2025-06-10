package com.Bluehat_R.teamTagGame.listener;

import com.Bluehat_R.teamTagGame.TeamTagGame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (!TeamTagGame.isStart){
            return;
        }

        final Entity entity = event.getEntity();

        if (entity instanceof Player player){
            if (TeamTagGame.escape.hasEntity(player) || TeamTagGame.hunter.hasEntity(player)){
                event.setCancelled(true);
            }
        }
    }
}
