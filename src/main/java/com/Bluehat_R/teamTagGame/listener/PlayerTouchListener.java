package com.Bluehat_R.teamTagGame.listener;

import com.Bluehat_R.teamTagGame.TeamTagGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class PlayerTouchListener implements Listener {
    MiniMessage miniMessage = miniMessage();
    @EventHandler
    public void onPlayerTouch(EntityDamageByEntityEvent event) {
        if (!TeamTagGame.isStart) {
            return;
        }
        final Entity entity = event.getEntity();
        final Entity attack = event.getDamager();
        if (entity instanceof Player target && attack instanceof Player attacker) {
            if (TeamTagGame.coolTime.hasEntry(attack.getName())){
                return;
            }

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team hunterTeam = scoreboard.getTeam("hunter");
            Team escapeTeam = scoreboard.getTeam("escape");

            if (escapeTeam.hasEntry(target.getName()) && hunterTeam.hasEntry(attacker.getName())) {
                hunterTeam.removeEntry(attacker.getName());
                escapeTeam.addEntry(attacker.getName());
                escapeTeam.removeEntry(target.getName());
                hunterTeam.addEntry(target.getName());

                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 255, false, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 255, false, false));

                final BossBar bossBar = Bukkit.createBossBar("鬼が復活まで", BarColor.RED, BarStyle.SOLID);
                this.start(bossBar, target);

                for (final String playerName : hunterTeam.getEntries()) {
                    Player player = Bukkit.getPlayer(playerName);
                    player.sendMessage(miniMessage.deserialize("<green>鬼が" + attacker.getName() + "から" + target.getName() + "に変わりました．"));
                }

                for (final String playerName : escapeTeam.getEntries()) {
                    Player player = Bukkit.getPlayer(playerName);
                    player.sendMessage(miniMessage.deserialize("<green>鬼が" + attacker.getName() + "から" + target.getName() + "に変わりました．"));
                }

                TeamTagGame.coolTime.addEntities(target);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeamTagGame.coolTime.removeEntities(target);
                    }
                }.runTaskLaterAsynchronously(TeamTagGame.plugin, 200L);
            }
        }
    }

    private void start(BossBar bossBar, Player player){
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);
        new BukkitRunnable() {
            int i = 10;
            @Override
            public void run() {
                if (i <= -1){
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    this.cancel();
                    return;
                }
                bossBar.setProgress((double) i / 10);
                bossBar.setTitle("追跡可能まであと" + i + "秒・・・");
                i--;
            }
        }.runTaskTimerAsynchronously(TeamTagGame.plugin, 0L, 20L);
    }
}
