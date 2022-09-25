package com.sliceclient.furnacejs.script;

import com.sliceclient.furnacejs.javascript.JavaScript;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.mozilla.javascript.Function;

/**
 * This command is used to register a new command to the server.
 *
 * @author Nick
 */
@Getter @Setter
public class ScriptCommand extends Command {

    private ScriptCommandExecuter execute;

    public ScriptCommand(String command, JavaScript script, Function function) {
        super(command);
        this.execute = new ScriptCommandExecuter(script, function);
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String label, String[] args) {
        execute.execute(sender, args);
        return true;
    }

}
