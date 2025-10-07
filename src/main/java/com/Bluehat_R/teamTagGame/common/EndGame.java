package com.Bluehat_R.teamTagGame.common;

import com.Bluehat_R.teamTagGame.TeamTagGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class EndGame {
    MiniMessage miniMessage = miniMessage();

    public void end(BossBar bossBar) {
        bossBar.setVisible(false);
        bossBar.removeAll();

        // --- Config読み込み ---
        var cfg = TeamTagGame.plugin.getConfig();
        final int x = cfg.getInt("end-game.x", 0);
        final int y = cfg.getInt("end-game.y", 0);
        final int z = cfg.getInt("end-game.z", 0);
        final float yaw = (float) cfg.getDouble("end-game.yaw", 0.0);
        final float pitch = (float) cfg.getDouble("end-game.pitch", 0.0);

        // --- 逃走者側 ---
        for (String playerName : new ArrayList<>(TeamTagGame.escape.getEntries())) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) continue;

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<aqua>鬼ごっこが終了しました！\nあなたは逃走成功です！"));
                    for (String hunterName : TeamTagGame.hunter.getEntries()) {
                        Player hunter = Bukkit.getPlayer(hunterName);
                        if (hunter != null)
                            player.sendMessage(miniMessage.deserialize("鬼は" + hunter.getName() + "でした．"));
                    }
                }
            }.runTask(TeamTagGame.plugin);
        }

        // --- 鬼側 ---
        for (String playerName : new ArrayList<>(TeamTagGame.hunter.getEntries())) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) continue;

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<red>鬼ごっこが終了しました！\nあなたは鬼でした！"));
                }
            }.runTask(TeamTagGame.plugin);
        }

        // --- チームのリセット ---
        clearTeam(TeamTagGame.hunter);
        clearTeam(TeamTagGame.escape);

        // --- 全員を待機チームへ戻す ---
        for (Player player : Bukkit.getOnlinePlayers()) {
            TeamTagGame.endPoint.addEntry(player.getName());
        }

        TeamTagGame.isStart = false;
        TeamTagGame.timer = null;
    }

    public void stop() {
        if (TeamTagGame.timer != null) {
            TeamTagGame.timer.getBossBar().setVisible(false);
            TeamTagGame.timer.getBossBar().removeAll();
            TeamTagGame.timer.cancel();
        }

        var cfg = TeamTagGame.plugin.getConfig();
        final int x = cfg.getInt("end-game.x", 0);
        final int y = cfg.getInt("end-game.y", 0);
        final int z = cfg.getInt("end-game.z", 0);
        final float yaw = (float) cfg.getDouble("end-game.yaw", 0.0);
        final float pitch = (float) cfg.getDouble("end-game.pitch", 0.0);

        // --- 全員強制送還 ---
        for (Player player : Bukkit.getOnlinePlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
                    player.sendMessage(miniMessage.deserialize("<red>鬼ごっこが強制終了しました．"));
                }
            }.runTask(TeamTagGame.plugin);
        }

        clearTeam(TeamTagGame.hunter);
        clearTeam(TeamTagGame.escape);

        for (Player player : Bukkit.getOnlinePlayers()) {
            TeamTagGame.endPoint.addEntry(player.getName());
        }

        TeamTagGame.isStart = false;
        TeamTagGame.timer = null;
    }

    private void clearTeam(Team team) {
        List<String> entries = new ArrayList<>(team.getEntries());
        for (String name : entries) {
            team.removeEntry(name);
        }
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }
}
