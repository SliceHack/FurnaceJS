package com.sliceclient.furnacejs.subcommand;

import com.sliceclient.furnacejs.subcommand.data.CommandInfo;
import lombok.Getter;
import org.bukkit.command.CommandSender;

/**
 * Sub command base for ScriptCommand
 * example: /script <name> <args>
 *
 * @author Nick
 * @since 9/22/2022 3:40 PM
 * */
@Getter
public abstract class SubCommand {

    /** CommandInfo */
    private final CommandInfo info = getClass().getAnnotation(CommandInfo.class);

    /** The date for the command */
    private final String name, permission;
    private final String[] aliases;

    public SubCommand() {
        name = info.name();
        permission = info.permission();
        aliases = info.aliases();
    }

    public abstract void execute(CommandSender sender, String[] args);
}
