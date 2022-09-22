package com.sliceclient.furnacejs.subcommand.subcommands;

import com.sliceclient.furnacejs.FurnaceJS;
import com.sliceclient.furnacejs.javascript.JavaScript;
import com.sliceclient.furnacejs.subcommand.SubCommand;
import com.sliceclient.furnacejs.subcommand.data.CommandInfo;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

@CommandInfo(name = "reload", permission = "furnacejs.reload")
public class ReloadCommand extends SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§cUsage: /script reload file_name");
            return;
        }

        String fileName = args[0];
        if(!fileName.endsWith(".js")) fileName += ".js";

        File file = new File(FurnaceJS.instance.getDataFolder(), fileName);
        if(!file.exists()) {
            sender.sendMessage("§cFile \"" + fileName + "\" does not exist.");
            return;
        }

        JavaScript script = FurnaceJS.instance.getScript(fileName);
        if(script == null) {
            sender.sendMessage("§cFile \"" + fileName + "\" is not loaded.");
            return;
        }
        script.reload();
        sender.sendMessage("§aFile \"" + fileName + "\" has been reloaded.");
    }
}
