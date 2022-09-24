package com.sliceclient.furnacejs.script;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

/**
 * This command is used to register a new command to the server.
 *
 * @author Nick
 */
public class ScriptCommand extends BukkitCommand {

    protected ScriptCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String commandLabel, String[] args) {
        return false;
    }
}
