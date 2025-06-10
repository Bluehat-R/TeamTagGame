package com.Bluehat_R.teamTagGame;

import com.Bluehat_R.teamTagGame.common.Timer; // Timer クラスをインポート
import org.bukkit.boss.BossBar; // BossBar クラスをインポート
import org.bukkit.configuration.file.FileConfiguration; // FileConfiguration をインポート
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class TeamTagGame extends JavaPlugin {

    public static boolean isStart = false;
    public static Team coolTime;
    public static TeamTagGame plugin;

    // ↓↓↓ ここから追加・修正する部分 ↓↓↓

    public static Team endPoint; // Commands.java で使われているので static で宣言
    public static FileConfiguration config; // Commands.java で使われているので static で宣言
    public static BossBar bossBar; // Commands.java で使われているので static で宣言
    public static Timer timer; // Commands.java で使われているので static で宣言
    public static Team hunter; // Commands.java で使われているので static で宣言
    public static Team escape; // Commands.java で使われているので static で宣言

    @Override
    public void onEnable() {
        plugin = this;
        config = getConfig(); // config を初期化

        // config.yml を保存・ロードする
        saveDefaultConfig(); // config.yml がなければ作成する
        reloadConfig(); // config.yml を再読み込みする

        Scoreboard board = getServer().getScoreboardManager().getMainScoreboard();

        // 各チームの初期化（前回の coolTime と同じように、既存か新規作成かチェックする）
        coolTime = board.getTeam("coolTime");
        if (coolTime == null) {
            coolTime = board.registerNewTeam("coolTime");
            coolTime.setDisplayName("クールタイム");
            coolTime.setAllowFriendlyFire(false);
            coolTime.setCanSeeFriendlyInvisibles(false);
        }

        hunter = board.getTeam("hunter"); // hunter チームを初期化
        if (hunter == null) {
            hunter = board.registerNewTeam("hunter");
            hunter.setDisplayName("ハンター");
            // 必要に応じてその他の設定
        }

        escape = board.getTeam("escape"); // escape チームを初期化
        if (escape == null) {
            escape = board.registerNewTeam("escape");
            escape.setDisplayName("エスケープ");
            // 必要に応じてその他の設定
        }

        endPoint = board.getTeam("endPoint"); // endPoint チームを初期化
        if (endPoint == null) {
            endPoint = board.registerNewTeam("endPoint");
            endPoint.setDisplayName("待機中");
            // 必要に応じてその他の設定
        }

        // イベントリスナーとコマンドエグゼキュータを登録
        getServer().getPluginManager().registerEvents(new com.Bluehat_R.teamTagGame.listener.PlayerTouchListener(), this);
        getCommand("tag").setExecutor(new Commands()); // コマンドの登録

        getLogger().info("TeamTagGame プラグインが有効になりました！");
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamTagGame プラグインが無効になりました！");

        // クリーンアップ処理
        if (coolTime != null) {
            coolTime.getEntries().forEach(coolTime::removeEntry);
            coolTime.unregister();
        }
        if (hunter != null) {
            hunter.getEntries().forEach(hunter::removeEntry);
            hunter.unregister();
        }
        if (escape != null) {
            escape.getEntries().forEach(escape::removeEntry);
            escape.unregister();
        }
        if (endPoint != null) {
            endPoint.getEntries().forEach(endPoint::removeEntry);
            endPoint.unregister();
        }

        // TimerとBossBarのクリーンアップ
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    // saveReloadConfig メソッドを追加 (config.yml の再読み込み)
    public void saveReloadConfig() {
        saveConfig(); // 現在のconfigを保存
        reloadConfig(); // configを再読み込み
        // 必要に応じて、再読み込み後にconfigの値を参照する他の初期化処理を追加
    }

    // noSaveReloadConfig メソッドを追加 (これは保存しない再読み込みかな？)
    public void noSaveReloadConfig() {
        reloadConfig(); // configを再読み込みするだけで保存はしない
        // 必要に応じて、再読み込み後にconfigの値を参照する他の初期化処理を追加
    }
}