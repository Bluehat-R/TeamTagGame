package com.Bluehat_R.teamTagGame;

import com.Bluehat_R.teamTagGame.common.Timer;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.ChatColor;

public final class TeamTagGame extends JavaPlugin {

    public static TeamTagGame plugin; // プラグイン本体インスタンス
    public static boolean isStart = false;

    public static Team coolTime;
    public static Team endPoint;
    public static Team hunter;
    public static Team escape;
    public static BossBar bossBar;
    public static Timer timer;

    @Override
    public void onEnable() {
        plugin = this;

        // --- Configの標準的な扱い ---
        saveDefaultConfig(); // 初回起動時に config.yml を生成
        reloadConfig();      // 設定をメモリに読み込む

        // --- チーム初期化 ---
        Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();

        // クールタイムチーム
        coolTime = getOrRegisterTeam(board, "coolTime", ChatColor.GREEN, "クールタイム");

        // ハンターチーム
        hunter = getOrRegisterTeam(board, "hunter", ChatColor.RED, "ハンター");

        // 逃走者チーム
        escape = getOrRegisterTeam(board, "escape", ChatColor.BLUE, "エスケープ");

        // 待機チーム
        endPoint = getOrRegisterTeam(board, "endPoint", ChatColor.YELLOW, "待機中");

        // --- イベント・コマンド登録 ---
        getServer().getPluginManager().registerEvents(new com.Bluehat_R.teamTagGame.listener.PlayerTouchListener(), this);
        getCommand("tag").setExecutor(new Commands());
        getCommand("tag").setTabCompleter(new TagGameTabCompleter());

        getLogger().info("TeamTagGame プラグインが有効になりました！");
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamTagGame プラグインが無効になりました！");

        // --- チームのクリーンアップ ---
        if (coolTime != null) coolTime.getEntries().forEach(coolTime::removeEntry);
        if (hunter != null) hunter.getEntries().forEach(hunter::removeEntry);
        if (escape != null) escape.getEntries().forEach(escape::removeEntry);
        if (endPoint != null) endPoint.getEntries().forEach(endPoint::removeEntry);

        // --- タイマーとボスバーを解放 ---
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    /**
     * 共通のTeam初期化処理
     */
    private Team getOrRegisterTeam(Scoreboard board, String name, ChatColor color, String displayName) {
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
            team.setDisplayName(displayName);
            team.setColor(color);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        } else {
            team.setColor(color);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
        return team;
    }
}
