package com.sliceclient.furnacejs.script;

import com.sliceclient.furnacejs.javascript.JavaScript;
import lombok.Getter;
import lombok.Setter;
import org.mozilla.javascript.Function;

/**
 * This command is used to execute command in the script.
 *
 * @author Nick
 */
@Getter @Setter
public class ScriptCommandExecuter {

    private JavaScript script;
    private Function function;

    public ScriptCommandExecuter(JavaScript script, Function function) {
        this.script = script;
        this.function = function;
    }

    public void execute(Object... args) {
        try {
            function.call(script.getContext(), script.getScope(), script.getScope(), args);
        } catch (Exception e) {
            script.printError(e);
        }
    }

}
