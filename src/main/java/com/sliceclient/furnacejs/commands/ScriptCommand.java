package com.sliceclient.furnacejs.commands;

import com.sliceclient.furnacejs.FurnaceJS;
import com.sliceclient.furnacejs.subcommand.SubCommand;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ScriptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if(args.length < 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("§cUsage: /script <");
            int index = 0;

            for(SubCommand sub : FurnaceJS.instance.getSubCommandManager().getSubCommands()) {
                sb.append(sub.getName());
                if(index != FurnaceJS.instance.getSubCommandManager().getSubCommands().size() - 1) sb.append("|");
                index++;
            }
            sb.append(">");
            sender.sendMessage(sb.toString());
            return true;
        }

        String type = args[0];
        SubCommand subCommand = FurnaceJS.instance.getSubCommandManager().get(type);

        if(subCommand == null) {
            sender.sendMessage("§cUnknown sub command \"" + type + "\".");
            return true;
        }

        subCommand.execute(sender, removeFirstValue(args));
        return true;
    }

    public String[] removeFirstValue(String[] args) {
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }
}
