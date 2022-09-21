package com.sliceclient.furnacejs.commands;

import com.sliceclient.furnacejs.FurnaceJS;
import com.sliceclient.furnacejs.javascript.JavaScript;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ReloadCommand implements CommandExecutor {

    private final File dataFolder;

    public ReloadCommand(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("furnacejs.reload")) return true;
        if (strings.length == 0) {
            commandSender.sendMessage("§cUsage: /furnacejs reload <file>");
            return true;
        }

        String fileName = strings[0];
        if (!fileName.endsWith(".js")) fileName += ".js";
        File filePath = new File(dataFolder, fileName);

        JavaScript requiredScript = FurnaceJS.scripts.stream()
                .filter(script -> filePath.equals(script.getFile()))
                .findAny()
                .orElse(null);

        if (requiredScript == null) return true;

        commandSender.sendMessage("§aReloading script...");
        Date date = new Date();
        List<Object> response = requiredScript.reload();
        Long time = (Long) response.get(0);
        if (!(response.get(1) instanceof Boolean)) return true;
        if ((Boolean) response.get(1)) {
            commandSender.sendMessage("§aReloaded script in " + (time - date.getTime()) + "ms");
        } else {
            commandSender.sendMessage("§cFailed to reload script in " + (time - date.getTime()) + "ms");
        }

        return true;
    }
}
