package com.sliceclient.furnacejs.script;

import com.sliceclient.furnacejs.javascript.JavaScript;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.mozilla.javascript.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This command is used to register a new command to the server.
 *
 * @author Nick
 */
@Getter @Setter
@SuppressWarnings("all")
public class ScriptCommand extends Command {

    private ScriptCommandExecuter execute;

    public ScriptCommand(String command, JavaScript script, Function function) {
        super(command);
        this.execute = new ScriptCommandExecuter(script, function);

        // default values
        aliases().usage("/" + command).permission(null).description("No description provided");
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String label, String[] args) {
        execute.execute(sender, args);
        return true;
    }

    /**
     * Called by the script to set the description of the command
     *
     * @param description the description of the command
     * */
    public ScriptCommand description(String description) {
        this.setDescription(description);
        return this;
    }

    /**
     * Called by the script to set the usage of the command
     *
     * @param usage the usage of the command
     * */
    public ScriptCommand usage(String usage) {
        this.setUsage(usage);
        return this;
    }

    /**
     * Called by the script to set the permission of the command
     *
     * @param permission the permission of the command
     * */
    public ScriptCommand permission(String permission) {
        this.setPermission(permission);
        return this;
    }

    /**
     * Called by the script to set the permission message of the command
     *
     * @param permissionMessage the permission message of the command
     * */
    public ScriptCommand permissionMessage(String permissionMessage) {
        this.setPermissionMessage(permissionMessage);
        return this;
    }

    /**
     * Called by the script to set the aliases of the command
     *
     * @param aliases the aliases of the command
     * */
    public ScriptCommand aliases(String... aliases) {
        this.setAliases(Arrays.asList(aliases));
        return this;
    }


}
