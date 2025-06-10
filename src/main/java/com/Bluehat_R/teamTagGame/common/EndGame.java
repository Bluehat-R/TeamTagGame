package com.Bluehat_R.teamTagGame.common;

import com.Bluehat_R.teamTagGame.TeamTagGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class EndGame {
    MiniMessage miniMessage = miniMessage();
    public void end(BossBar bossBar){
        bossBar.setVisible(false);
        bossBar.removeAll();
        for (final String playerName : TeamTagGame.escape.getEntries()){
            Player player = Bukkit.getPlayer(playerName);
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int x = TeamTagGame.config.getInt("end-game.x", 0);
                    final int y = TeamTagGame.config.getInt("end-game.y", 0);
                    final int z = TeamTagGame.config.getInt("end-game.z", 0);
                    final float yaw = (float) TeamTagGame.config.getDouble("end-game.yaw", 0.0);
                    final float pitch = (float) TeamTagGame.config.getDouble("end-game.pitch", 0.0);
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<aqua>鬼ごっこが終了しました！\nあなたは逃走成功です！"));
                    for (final String playerName : TeamTagGame.hunter.getEntries()){
                        Player target = Bukkit.getPlayer(playerName);
                        player.sendMessage(miniMessage.deserialize("鬼は" + target.getName() + "でした．"));
                    }
                }
            }.runTask(TeamTagGame.plugin);
        }

        for (final String playerName : TeamTagGame.hunter.getEntries()){
            Player player = Bukkit.getPlayer(playerName);
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int x = TeamTagGame.config.getInt("end-game.x", 0);
                    final int y = TeamTagGame.config.getInt("end-game.y", 0);
                    final int z = TeamTagGame.config.getInt("end-game.z", 0);
                    final float yaw = (float) TeamTagGame.config.getDouble("end-game.yaw", 0.0);
                    final float pitch = (float) TeamTagGame.config.getDouble("end-game.pitch", 0.0);
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("鬼ごっこが終了しました！\nあなたは鬼でした！"));
                }
            }.runTask(TeamTagGame.plugin);
        }

        TeamTagGame.hunter.unregister();
        TeamTagGame.escape.unregister();
        TeamTagGame.hunter = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("hunter");
        TeamTagGame.escape = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("escape");
        TeamTagGame.hunter.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        TeamTagGame.escape.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for (Player player : TeamTagGame.plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("escape")) {
                TeamTagGame.endPoint.addEntry(player.getName());
                TeamTagGame.escape.removeEntry(player.getName());
            } else if (player.hasPermission("hunter")) {
                TeamTagGame.endPoint.addEntry(player.getName());
                TeamTagGame.hunter.removeEntry(player.getName());
            }
        }

        TeamTagGame.isStart = false;
        TeamTagGame.timer = null;
    }

    public void stop(){
        TeamTagGame.timer.getBossBar().setVisible(false);
        TeamTagGame.timer.getBossBar().removeAll();
        TeamTagGame.timer.cancel();
        for (final String playerName : TeamTagGame.escape.getEntries()){
            Player player = Bukkit.getPlayer(playerName);
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int x = TeamTagGame.config.getInt("end-game.x", 0);
                    final int y = TeamTagGame.config.getInt("end-game.y", 0);
                    final int z = TeamTagGame.config.getInt("end-game.z", 0);
                    final float yaw = (float) TeamTagGame.config.getDouble("end-game.yaw", 0.0);
                    final float pitch = (float) TeamTagGame.config.getDouble("end-game.pitch", 0.0);
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<red>鬼ごっこが強制終了しました．"));
                }
            }.runTask(TeamTagGame.plugin);
        }

        for (final String playerName : TeamTagGame.hunter.getEntries()){
            Player player = Bukkit.getPlayer(playerName);
            new BukkitRunnable() {
                @Override
                public void run() {
                    final int x = TeamTagGame.config.getInt("end-game.x", 0);
                    final int y = TeamTagGame.config.getInt("end-game.y", 0);
                    final int z = TeamTagGame.config.getInt("end-game.z", 0);
                    final float yaw = (float) TeamTagGame.config.getDouble("end-game.yaw", 0.0);
                    final float pitch = (float) TeamTagGame.config.getDouble("end-game.pitch", 0.0);
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<red>鬼ごっこが強制終了しました．"));
                }
            }.runTask(TeamTagGame.plugin);
        }

        TeamTagGame.hunter.unregister();
        TeamTagGame.escape.unregister();
        TeamTagGame.hunter = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("hunter");
        TeamTagGame.escape = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("escape");
        TeamTagGame.hunter.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        TeamTagGame.escape.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for (Player player : TeamTagGame.plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("escape")) {
                TeamTagGame.endPoint.addEntry(player.getName());
                TeamTagGame.escape.removeEntry(player.getName());
            } else if (player.hasPermission("hunter")) {
                TeamTagGame.endPoint.addEntry(player.getName());
                TeamTagGame.hunter.removeEntry(player.getName());
            }
        }

        TeamTagGame.isStart = false;
        TeamTagGame.timer = null;
    }
}
