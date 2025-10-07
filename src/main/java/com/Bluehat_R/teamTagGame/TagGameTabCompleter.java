package com.Bluehat_R.teamTagGame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class TagGameTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // /taggame <subcommand>
        if (args.length == 1) {
            return Arrays.asList("join", "joinall", "leave", "list", "reload", "set", "start", "stop");
        }

        // /taggame set <subcommand>
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return Arrays.asList("escape", "hunter", "end", "size", "time");
        }

        return null; // それ以外は候補なし
    }
}
