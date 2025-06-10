package com.Bluehat_R.teamTagGame;

import com.Bluehat_R.teamTagGame.common.Timer;
import com.Bluehat_R.teamTagGame.listener.PlayerDamageByBlockListener;
import com.Bluehat_R.teamTagGame.listener.PlayerDamageByEntityListener;
import com.Bluehat_R.teamTagGame.listener.PlayerDamageListener;
import com.Bluehat_R.teamTagGame.listener.PlayerTouchListener;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class TeamTagGame extends JavaPlugin {
    public static TeamTagGame plugin;
    public static FileConfiguration config;
    public static boolean isStart = false;
    public static BossBar bossBar;
    public static Timer timer;
    public static Team endPoint;
    public static Team hunter;
    public static Team escape;
    public static Team coolTime;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            plugin = this;

            final File configFile = new File(this.getDataFolder(), "config.yml");
            if (!configFile.exists()){
                this.saveDefaultConfig();
            }

            config = this.getConfig();

            final PluginCommand command = this.getCommand("tag");
            if (command != null) {
                command.setExecutor(new Commands());
                command.setTabCompleter(new Commands());
            }

            this.getServer().getPluginManager().registerEvents(new PlayerTouchListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerDamageByBlockListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerDamageByEntityListener(), this);

            final Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
            if (endPoint != null) {
                endPoint.unregister();
            }
            if (hunter != null) {
                hunter.unregister();
            }
            if (escape != null) {
                escape.unregister();
            }
            endPoint = scoreboard.registerNewTeam("endPoint");
            endPoint.color(NamedTextColor.AQUA);
            endPoint.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            hunter = scoreboard.registerNewTeam("hunter");
            hunter.color(NamedTextColor.RED);
            hunter.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            escape = scoreboard.registerNewTeam("escape");
            escape.color(NamedTextColor.BLUE);
            escape.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        } catch (Exception exception) {
            exception.printStackTrace(new PrintWriter(new StringWriter()));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            HandlerList.unregisterAll(this);
            if (timer != null) {
                timer.getBossBar().setVisible(false);
                timer.getBossBar().removeAll();
                timer.cancel();
            }
        } catch (Exception exception) {
            exception.printStackTrace(new PrintWriter(new StringWriter()));
        }
    }

    public void saveReloadConfig() {
        this.saveConfig();
        this.reloadConfig();
        config = this.getConfig();
    }

    public void noSaveReloadConfig() {
        this.reloadConfig();
        config = this.getConfig();
    }
}