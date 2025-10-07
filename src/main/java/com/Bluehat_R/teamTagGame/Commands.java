package com.Bluehat_R.teamTagGame;

import com.google.common.collect.Lists;
import com.Bluehat_R.teamTagGame.common.EndGame;
import com.Bluehat_R.teamTagGame.common.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class Commands implements CommandExecutor, TabCompleter {
    MiniMessage miniMessage = miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("サブコマンドが必要です");
            return false;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            TeamTagGame.plugin.reloadConfig();
            commandSender.sendMessage(miniMessage.deserialize("<yellow>config.ymlを再読込しました. "));
            return true;
        }

        boolean isConsole = !(commandSender instanceof Player);
        List<String> playerOnlyCommands = Lists.newArrayList("start", "stop");

        if (playerOnlyCommands.contains(strings[0].toLowerCase()) && isConsole) {
            commandSender.sendMessage(miniMessage.deserialize("このコマンドはプレイヤーのみ実行できます！"));
            return false;
        }

        switch (strings[0].toLowerCase()) {
            case "start" -> {
                if (checkStart(commandSender)) return true;
                if (TeamTagGame.endPoint.getSize() < 1 + TeamTagGame.plugin.getConfig().getInt("hunter", 2)) {
                    commandSender.sendMessage(miniMessage.deserialize("<red>必要な人数に満たないため開始できません！ "));
                    return true;
                }

                TeamTagGame.bossBar = Bukkit.createBossBar("残り時間", BarColor.GREEN, BarStyle.SOLID);
                TeamTagGame.bossBar.setProgress(1.0);
                TeamTagGame.bossBar.setVisible(false);

                final BossBar bossBar = Bukkit.createBossBar("まもなく開始…", BarColor.BLUE, BarStyle.SOLID);
                bossBar.setProgress(1.0);
                bossBar.setVisible(true);
                TeamTagGame.timer = new Timer(TeamTagGame.plugin.getConfig().getInt("time", 10) * 60, TeamTagGame.bossBar);

                final Random random = new Random();
                Scoreboard scoreboard = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard();
                Team entryTeam = scoreboard.getTeam("endPoint");
                Team escapeTeam = scoreboard.getTeam("escape");
                Team hunterTeam = scoreboard.getTeam("hunter");

                Collection<String> entryPlayers = entryTeam.getEntries();

                for (int i = 0; i < TeamTagGame.plugin.getConfig().getInt("hunter", 2); i++) {
                    if (entryPlayers.isEmpty()) break;

                    int index = random.nextInt(entryPlayers.size());
                    String playerName = (String) entryPlayers.toArray()[index];
                    Player target = TeamTagGame.plugin.getServer().getPlayer(playerName);

                    if (target != null) {
                        TeamTagGame.hunter.addEntry(playerName);
                        entryTeam.removeEntry(playerName);
                        TeamTagGame.endPoint.removeEntry(playerName);
                    }
                }

                if (escapeTeam != null) {
                    Collection<String> endPointPlayers = entryTeam.getEntries();
                    for (String playerName : endPointPlayers) {
                        Player target = TeamTagGame.plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            escapeTeam.addEntry(playerName);
                            entryTeam.removeEntry(playerName);
                            TeamTagGame.endPoint.removeEntry(playerName);
                        }
                    }
                }

                for (String playername : escapeTeam.getEntries()) {
                    Player target = TeamTagGame.plugin.getServer().getPlayer(playername);
                    target.sendMessage(miniMessage.deserialize("<green>まもなく鬼ごっこを開始します・・・"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            var cfg = TeamTagGame.plugin.getConfig();
                            final int x = cfg.getInt("escape-start.x", 0);
                            final int y = cfg.getInt("escape-start.y", 0);
                            final int z = cfg.getInt("escape-start.z", 0);
                            final float yaw = (float) cfg.getDouble("escape-start.yaw", 0.0);
                            final float pitch = (float) cfg.getDouble("escape-start.pitch", 0.0);
                            target.teleport(new Location(target.getWorld(), x, y, z, yaw, pitch));
                            TeamTagGame.bossBar.addPlayer(target);
                            bossBar.addPlayer(target);
                            target.setGameMode(GameMode.ADVENTURE);
                            target.sendMessage(miniMessage.deserialize("<green>あなたは逃走者チームになりました！"));
                            target.getInventory().clear();
                            target.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                        }
                    }.runTask(TeamTagGame.plugin);
                }

                for (String playername : hunterTeam.getEntries()) {
                    Player target = TeamTagGame.plugin.getServer().getPlayer(playername);
                    target.sendMessage(miniMessage.deserialize("<green>まもなく鬼ごっこを開始します・・・"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            var cfg = TeamTagGame.plugin.getConfig();
                            final int x = cfg.getInt("hunter-start.x", 0);
                            final int y = cfg.getInt("hunter-start.y", 0);
                            final int z = cfg.getInt("hunter-start.z", 0);
                            final float yaw = (float) cfg.getDouble("hunter-start.yaw", 0.0);
                            final float pitch = (float) cfg.getDouble("hunter-start.pitch", 0.0);
                            target.teleport(new Location(target.getWorld(), x, y, z, yaw, pitch));
                            TeamTagGame.bossBar.addPlayer(target);
                            bossBar.addPlayer(target);
                            target.setGameMode(GameMode.ADVENTURE);
                            target.sendMessage(miniMessage.deserialize("<green>あなたは鬼チームになりました！"));
                            target.getInventory().clear();
                            target.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                        }
                    }.runTask(TeamTagGame.plugin);
                }

                for (String hunterName : hunterTeam.getEntries()) {
                    Player hunter = TeamTagGame.plugin.getServer().getPlayer(hunterName);
                    for (String escapeName : escapeTeam.getEntries()) {
                        Player escapePlayer = TeamTagGame.plugin.getServer().getPlayer(escapeName);
                        escapePlayer.sendMessage(miniMessage.deserialize("鬼は" + hunter.getName() + "です．"));
                    }
                }

                new BukkitRunnable() {
                    int i = 10;
                    @Override
                    public void run() {
                        if (i <= -1) {
                            bossBar.setVisible(false);
                            bossBar.removeAll();
                            TeamTagGame.timer.runTaskTimerAsynchronously(TeamTagGame.plugin, 0L, 20L);
                            TeamTagGame.bossBar.setVisible(true);
                            this.cancel();
                            return;
                        }
                        bossBar.setProgress((double) i / 10);
                        bossBar.setTitle("鬼ごっこ開始まで　残り: " + i + "秒");
                        i--;
                    }
                }.runTaskTimerAsynchronously(TeamTagGame.plugin, 0L, 20L);
                TeamTagGame.isStart = true;
            }

            case "stop" -> {
                if (!TeamTagGame.isStart) {
                    commandSender.sendMessage("まだ開始されていないため実行できません.");
                    return true;
                }
                new EndGame().stop();
                commandSender.sendMessage(miniMessage.deserialize("<green>鬼ごっこを強制終了しました."));
            }

            case "set" -> {
                if (checkStart(commandSender)) return true;
                if (strings.length == 2) {
                    switch (strings[1]) {
                        case "escape" -> setLocation(commandSender, "escape");
                        case "hunter" -> setLocation(commandSender, "hunter");
                        case "end" -> setLocation(commandSender, "end");
                    }
                } else if (strings.length == 3) {
                    if (strings[1].equalsIgnoreCase("size") && strings[2].chars().allMatch(Character::isDigit)) {
                        int size = Integer.parseInt(strings[2]);
                        TeamTagGame.plugin.getConfig().set("hunter", size);
                        TeamTagGame.plugin.saveConfig();
                        commandSender.sendMessage(miniMessage.deserialize("<aqua>ハンター人数を" + size + "人に設定しました."));
                    } else if (strings[1].equalsIgnoreCase("time") && strings[2].chars().allMatch(Character::isDigit)) {
                        int time = Integer.parseInt(strings[2]);
                        TeamTagGame.plugin.getConfig().set("time", time);
                        TeamTagGame.plugin.saveConfig();
                        commandSender.sendMessage(miniMessage.deserialize("<aqua>制限時間を" + time + "分に設定しました."));
                    }
                }
            }

            case "list" -> {
                if (TeamTagGame.isStart) {
                    int i = 1;
                    commandSender.sendMessage(Component.text("----- 参加者一覧(鬼) -----"));
                    for (final String playerName : TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("hunter").getEntries()) {
                        Player target = TeamTagGame.plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            commandSender.sendMessage(miniMessage.deserialize(i + ". " + target.getName()));
                            i++;
                        }
                    }

                    i = 1;
                    commandSender.sendMessage(Component.text("----- 参加者一覧(逃走者) -----"));
                    for (final String playerName : TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("escape").getEntries()) {
                        Player target = TeamTagGame.plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            commandSender.sendMessage(Component.text(i + ". " + target.getName()));
                            i++;
                        }
                    }
                } else {
                    int i = 1;
                    commandSender.sendMessage(Component.text("----- 参加者一覧 -----"));
                    for (final String playerName : TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("endPoint").getEntries()) {
                        Player target = TeamTagGame.plugin.getServer().getPlayer(playerName);
                        if (target != null) {
                            commandSender.sendMessage(Component.text(i + ". " + target.getName()));
                            i++;
                        }
                    }
                }
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            List<String> options = Arrays.asList("reload", "join", "joinall", "leave", "start", "set", "list", "stop");
            for (String opt : options) if (opt.startsWith(args[0].toLowerCase())) complete.add(opt);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            List<String> setOptions = Arrays.asList("escape", "hunter", "end", "size", "time");
            for (String opt : setOptions) if (opt.startsWith(args[1].toLowerCase())) complete.add(opt);
        }
        return complete;
    }

    public boolean checkStart(CommandSender sender) {
        if (TeamTagGame.isStart) {
            sender.sendMessage(miniMessage.deserialize("既に開始されているため実行できません."));
            return true;
        }
        return false;
    }

    public void setLocation(CommandSender sender, String type) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行できます。");
            return;
        }

        String pathPrefix = switch (type) {
            case "escape" -> "escape-start";
            case "hunter" -> "hunter-start";
            case "end" -> "end-game";
            default -> null;
        };

        if (pathPrefix == null) {
            sender.sendMessage("無効なタイプです。");
            return;
        }

        Location loc = player.getLocation();
        var cfg = TeamTagGame.plugin.getConfig();
        cfg.set(pathPrefix + ".x", loc.getBlockX());
        cfg.set(pathPrefix + ".y", loc.getBlockY());
        cfg.set(pathPrefix + ".z", loc.getBlockZ());
        cfg.set(pathPrefix + ".yaw", loc.getYaw());
        cfg.set(pathPrefix + ".pitch", loc.getPitch());
        TeamTagGame.plugin.saveConfig();

        player.sendMessage(miniMessage.deserialize("<aqua>" + type + "の開始地点を登録しました."));
    }
}