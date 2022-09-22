package com.sliceclient.furnacejs.subcommand.manager;

import com.sliceclient.furnacejs.subcommand.SubCommand;
import com.sliceclient.furnacejs.subcommand.subcommands.ReloadCommand;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The sub command manager is used to manage sub commands.
 * for the script command.
 *
 * @author Nick
 * @since 9/22/2022 3:47 PM
 */
@Getter
public class SubCommandManager {

    /** The list of sub commands. */
    private final List<SubCommand> subCommands = new ArrayList<>();

    public SubCommandManager() {
        register(new ReloadCommand());
    }

    /**
     * Registers a sub command.
     *
     * @param subCommand The sub command.
     */
    public void register(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    /**
     * Gets a sub command by name.
     *
     * @param name The name of the sub command.
     * */
    public SubCommand get(String name) {
        return subCommands.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
