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
        if (strings[0].isEmpty()) {
            commandSender.sendMessage("サブコマンドが必要です");
            return false;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            TeamTagGame.plugin.noSaveReloadConfig();
            commandSender.sendMessage(miniMessage.deserialize("<yellow>config.ymlを再読込しました. "));
            return true;
        }

        final boolean[] isconsoler = {false};

        ArrayList<String> consolecommands = Lists.newArrayList("start", "stop");

        consolecommands.forEach(consolecommand -> {
            if (strings[0].equalsIgnoreCase(consolecommand)) {
                if (!(commandSender instanceof Player)) {
                    isconsoler[0] = true;
                }
            }
        });

        if (isconsoler[0]) {
            commandSender.sendMessage(miniMessage.deserialize("このコマンドはプレイヤーのみ実行できます！"));
            return false;
        }

        switch (strings[0]) {
            case "start" -> {
                if (checkStart(commandSender)) return true;
                if (TeamTagGame.endPoint.getSize() < 1 + TeamTagGame.config.getInt("hunter", 2)) {
                    commandSender.sendMessage(miniMessage.deserialize("<red>必要な人数に満たないため開始できません！ "));
                    return true;
                }

                TeamTagGame.bossBar = Bukkit.createBossBar("残り時間", BarColor.GREEN, BarStyle.SOLID);
                TeamTagGame.bossBar.setProgress(1.0);
                TeamTagGame.bossBar.setVisible(false);

                final BossBar bossBar = Bukkit.createBossBar("まもなく開始…", BarColor.BLUE, BarStyle.SOLID);
                bossBar.setProgress(1.0);
                bossBar.setVisible(true);
                TeamTagGame.timer = new Timer(TeamTagGame.config.getInt("time", 10) * 60, TeamTagGame.bossBar);

                final Random random = new Random();
                Scoreboard scoreboard = TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard();
                Team entryTeam = scoreboard.getTeam("endPoint");
                Team escapeTeam = scoreboard.getTeam("escape");
                Team hunterTeam = scoreboard.getTeam("hunter");

                Collection<String> entryPlayers = entryTeam.getEntries();

                for (int i = 0; i < TeamTagGame.config.getInt("hunter", 2); i++) {
                    if (entryPlayers.isEmpty()) {
                        break;
                    }

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
                    Collection<String> endPointPlayers = escapeTeam.getEntries();

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
                            final int x = TeamTagGame.config.getInt("escape-start.x", 0);
                            final int y = TeamTagGame.config.getInt("escape-start.y", 0);
                            final int z = TeamTagGame.config.getInt("escape-start.z", 0);
                            final float yaw = (float) TeamTagGame.config.getDouble("escape-start.yaw", 0.0);
                            final float pitch = (float) TeamTagGame.config.getDouble("escape-start.pitch", 0.0);
                            target.teleport(new Location(target.getWorld(), x, y, z, yaw, pitch));
                            TeamTagGame.bossBar.addPlayer(target);
                            bossBar.addPlayer(target);
                            target.setGameMode(GameMode.ADVENTURE);
                            target.sendMessage(miniMessage.deserialize("<green>あなたは逃走者チームになりました！"));
                            target.getInventory().clear();
                            final ItemStack itemStack = new ItemStack(Material.COOKED_BEEF, 64);
                            target.getInventory().addItem(itemStack);
                        }
                    }.runTask(TeamTagGame.plugin);
                }

                for (String playername : hunterTeam.getEntries()) {
                    Player target = TeamTagGame.plugin.getServer().getPlayer(playername);
                    target.sendMessage(miniMessage.deserialize("<green>まもなく鬼ごっこを開始します・・・"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            final int x = TeamTagGame.config.getInt("hunter-start.x", 0);
                            final int y = TeamTagGame.config.getInt("hunter-start.y", 0);
                            final int z = TeamTagGame.config.getInt("hunter-start.z", 0);
                            final float yaw = (float) TeamTagGame.config.getDouble("hunter-start.yaw", 0.0);
                            final float pitch = (float) TeamTagGame.config.getDouble("hunter-start.pitch", 0.0);
                            target.teleport(new Location(target.getWorld(), x, y, z, yaw, pitch));
                            TeamTagGame.bossBar.addPlayer(target);
                            bossBar.addPlayer(target);
                            target.setGameMode(GameMode.ADVENTURE);
                            target.sendMessage(miniMessage.deserialize("<green>あなたは鬼チームになりました！"));
                            target.getInventory().clear();
                            final ItemStack itemStack = new ItemStack(Material.COOKED_BEEF, 64);
                            target.getInventory().addItem(itemStack);
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
                if (TeamTagGame.isStart) {
                    commandSender.sendMessage("まだ開始されていないため実行できません. ");
                    return true;
                }

                new EndGame().stop();
                commandSender.sendMessage(miniMessage.deserialize("<green>鬼ごっこをコマンドで強制終了させました. "));
            }

            case "join" -> {

                if (strings.length == 1) {
                    if (checkStart(commandSender)) return true;
                    if (commandSender instanceof Player player) {
                        if (TeamTagGame.endPoint.getEntries().contains(player)) {
                            commandSender.sendMessage(miniMessage.deserialize("<red>既に参加しているため実行できません！ "));
                            return true;
                        } else {
                            TeamTagGame.endPoint.addEntity(player);
                            commandSender.sendMessage(miniMessage.deserialize("<aqua>鬼ごっこの待機グループに参加しました！ "));
                        }
                    }

                } else {
                    if (strings.length == 2) {
                        if (checkStart(commandSender)) return true;

                        final Player target = TeamTagGame.plugin.getServer().getPlayer(strings[1]);
                        if (target == null) {
                            commandSender.sendMessage(miniMessage.deserialize("<red>" + strings[1] + "が見つかりませんでした. "));
                            return true;
                        }

                        if (TeamTagGame.endPoint.getEntries().contains(target)) {
                            commandSender.sendMessage(miniMessage.deserialize("<red>既に" + strings[1] + "は参加しています！ "));
                            return true;
                        }

                        addPlayerToTeam(target, "endPoint");
                        commandSender.sendMessage(miniMessage.deserialize("<aqua>鬼ごっこの待機グループに" + strings[1] + "を参加させました！ "));
                        target.sendMessage(miniMessage.deserialize("<aqua>あなたは鬼ごっこの待機グループに参加しました！ "));
                    }
                }
            }

            case "joinall" -> {
                if (checkStart(commandSender)) return true;

                int i = 0;
                for (final Player target : TeamTagGame.plugin.getServer().getOnlinePlayers()) {
                    if (TeamTagGame.endPoint.getEntries().contains(target)) {
                        continue;
                    }

                    addPlayerToTeam(target, "endPoint");
                    target.sendMessage(miniMessage.deserialize("<aqua>あなたは鬼ごっこの待機グループに参加しました！ "));
                    i++;
                }

                commandSender.sendMessage(miniMessage.deserialize("<aqua>" + i + "名のプレイヤーを参加させました！"));
            }

            case "leave" -> {
                if (strings.length == 1) {
                    if (checkStart(commandSender)) return true;
                    if (commandSender instanceof Player player) {
                        if (!TeamTagGame.endPoint.getEntries().contains(player)) {
                            commandSender.sendMessage(miniMessage.deserialize("<red>元々参加していません！ "));
                        } else {
                            removePlayerToTeam(player, "endPoint");
                            commandSender.sendMessage(miniMessage.deserialize("<green>鬼ごっこの待機グループから退出しました！ "));
                        }
                    }
                } else {
                    if (strings.length == 2) {
                        if (checkStart(commandSender)) return true;
                    }

                    final Player target = TeamTagGame.plugin.getServer().getPlayer(strings[1]);
                    if (target == null) {
                        commandSender.sendMessage(miniMessage.deserialize("<red>" + strings[1] + "は元々参加していません！ "));
                        return true;
                    }

                    removePlayerToTeam(target, "endPoint");
                    commandSender.sendMessage(miniMessage.deserialize("<green>鬼ごっこの待機グループから" + strings[1] + "を退出させました！ "));
                    target.sendMessage(miniMessage.deserialize("<green>鬼ごっこの待機グループから退出しました！ "));
                }
            }

            case "set" -> {
                if (checkStart(commandSender)) return true;
                if (strings.length == 2) {
                    if (!strings[1].isEmpty()) {
                        switch (strings[1]) {
                            case "escape" -> setLocation(commandSender, "escape");
                            case "hunter" -> setLocation(commandSender, "hunter");
                            case "end" -> setLocation(commandSender, "end");
                        }
                    }
                } else if (strings.length == 3) {
                    if (!strings[1].isEmpty()) {
                        switch (strings[1]) {
                            case "size" -> {
                                if (!strings[2].isEmpty()) {
                                    if (strings[2].chars().allMatch(Character::isDigit)) {
                                        final int size = Integer.parseInt(strings[2]);
                                        TeamTagGame.config.set("hunter", size);
                                        TeamTagGame.plugin.saveReloadConfig();
                                        commandSender.sendMessage(miniMessage.deserialize("<aqua>ハンターの人数を" + size + "人に設定しました."));
                                    }
                                }
                            }
                            case "time" -> {
                                if (!strings[2].isEmpty()) {
                                    if (strings[2].chars().allMatch(Character::isDigit)) {
                                        final int time = Integer.parseInt(strings[2]);
                                        TeamTagGame.config.set("time", time);
                                        TeamTagGame.plugin.saveReloadConfig();
                                        commandSender.sendMessage(miniMessage.deserialize("<aqua>制限時間を" + time + "分に設定しました."));
                                    }
                                }
                            }
                        }
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
                    for (final String playerName : TeamTagGame.plugin.getServer().getScoreboardManager().getMainScoreboard().getEntries()) {
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final List<String> complete = new ArrayList<>();

        if (strings.length == 1) {
            List<String> options = Arrays.asList("reload", "join", "joinall", "leave", "start", "set", "list", "stop");

            for (String option : options) {
                if (option.startsWith(strings[0].toLowerCase())) {
                    complete.add(option);
                }
            }
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("join")) {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (strings[1].isEmpty() || target.getName().toLowerCase().startsWith(strings[1].toLowerCase())) {
                        complete.add(target.getName());
                    }
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                List<String> setOptions = Arrays.asList("escape", "hunter", "end", "size", "time");

                for (String option : setOptions) {
                    if (option.startsWith(strings[1].toLowerCase())) {
                        complete.add(option);
                    }
                }
            }
        }

        return complete;
    }


    public boolean checkStart(CommandSender commandSender) {
        if (TeamTagGame.isStart) {
            commandSender.sendMessage(miniMessage.deserialize("既に開始されているため実行できません. "));
            return true;
        }
        return false;
    }

    public void addPlayerToTeam(Player player, String teamName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        team.addEntry(player.getName());
    }

    public void removePlayerToTeam(Player player, String teamName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            player.sendMessage(miniMessage.deserialize("<red>チームがありません！ "));
            return;
        }

        team.removeEntry(player.getName());
    }

    public void setLocation(CommandSender sender, String type) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行できます。");
            return;
        }

        final Location location = player.getLocation();
        String pathPrefix = "";

        switch (type) {
            case "escape":
                pathPrefix = "escape-start";
                break;
            case "hunter":
                pathPrefix = "hunter-start";
                break;
            case "end":
                pathPrefix = "end-game";
                break;
            default:
                sender.sendMessage("無効なタイプです。");
                return;
        }

        TeamTagGame.config.set(pathPrefix + ".x", location.getBlockX());
        TeamTagGame.config.set(pathPrefix + ".y", location.getBlockY());
        TeamTagGame.config.set(pathPrefix + ".z", location.getBlockZ());
        TeamTagGame.config.set(pathPrefix + ".yaw", location.getYaw());
        TeamTagGame.config.set(pathPrefix + ".pitch", location.getPitch());

        TeamTagGame.plugin.saveReloadConfig();
        player.sendMessage(miniMessage.deserialize(type + "の開始地点を登録しました．"));
    }

    private boolean isAuthor(String playerName) {
        List<String> authors = TeamTagGame.plugin.getDescription().getAuthors();
        return authors.contains(playerName);
    }

}
