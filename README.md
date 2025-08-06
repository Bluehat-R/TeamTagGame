# TagGame Plugin

このプラグインは、Minecraftでチーム分けをして鬼ごっこを遊ぶためのプラグインです。

## インストール方法

1. [プラグインのダウンロードリンク] からプラグインのjarファイルをダウンロードします。
2. ダウンロードしたファイルを、Minecraftサーバーの `plugins` フォルダに入れます。
3. サーバーを再起動するか、`/reload` コマンドを実行します。

4. ## 使い方

1. 鬼ごっこのゲームエリアを設定します。
    - 逃走者のスタート地点を決めます: `/tag set escape`
    - 鬼のスタート地点を決めます: `/tag set hunter`
    - ゲーム終了時にワープする地点を決めます: `/tag set end`

2. チームの人数や制限時間を設定します。
    - 鬼の人数を設定します: `/tag set size <人数>`
    - 制限時間を設定します: `/tag set time <分>`

3. プレイヤーを参加させます。
    - 自分を参加させる: `/tag join`
    - 全員を参加させる: `/tag joinall`
    - 特定のプレイヤーを参加させる: `/tag join <プレイヤー名>`

4. ゲームを開始します: `/tag start`

## コマンド一覧

- `/tag join`: 自分をゲームに参加させます。
- `/tag join <player>`: 指定したプレイヤーをゲームに参加させます。
- `/tag joinall`: オンラインのプレイヤー全員をゲームに参加させます。
- `/tag leave`: ゲームから退出します。
- `/tag list`: 鬼と逃走者のリストを表示します。
- `/tag reload`: 設定をリロードします。
- `/tag set escape`: 逃走者のスタート地点を設定します。
- `/tag set hunter`: 鬼のスタート地点を設定します。
- `/tag set end`: ゲーム終了後のワープ地点を設定します。
- `/tag set size <size>`: 鬼の人数を設定します。
- `/tag set time <time>`: 制限時間を分単位で設定します。
- `/tag start`: ゲームを開始します。
- `/tag stop`: ゲームを終了させます。

## パーミッション

このプラグインの全てのコマンドは、管理者権限（OP）を持つプレイヤーのみが実行できます。

## Credits

* **Original Author (原作者)**: [hinaplugin](https://github.com/hinaplugin)
* **draft (原案)**: [TagGame](https://github.com/hinaplugin/TagGame)
* **Refactored by (改修者)**: [Bluehat-R](https://github.com/Bluehat-R)
