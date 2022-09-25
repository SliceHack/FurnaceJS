package com.sliceclient.furnacejs.javascript;

import com.sliceclient.furnacejs.FurnaceJS;
import com.sliceclient.furnacejs.script.ScriptCommand;
import com.sliceclient.furnacejs.script.ScriptCommandExecuter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.mozilla.javascript.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Getter @Setter
@SuppressWarnings("unused")
public class JavaScript {


    /** script engine */
    private volatile Context context = Context.enter();
    private Scriptable scope = context.initStandardObjects(), scriptable = context.newObject(scope);
    private int lines = 0;

    private File file;

    private HashMap<String, Function> events = new HashMap<>();
    private ArrayList<ScriptCommand> commands = new ArrayList<>();

    public JavaScript(File file) {
        this.context.setLanguageVersion(Context.VERSION_ES6);
        this.file = file;
        this.setup();
    }

    public void setup() {
        reload();
    }


    /**
     * Puts a class in the engine
     *
     * @param clazz the class to put in the engine
     * */
    public void putClass(Class<?> clazz) {
        Scriptable scriptable = context.getWrapFactory().wrapJavaClass(context, scope, clazz);
        scope.put(clazz.getSimpleName(), scope, scriptable);
    }

    /**
     * Puts a class in the engine
     *
     * @param clazz the class to put in the engine
     * */
    public void putClass(String value, Class<?> clazz) {
        Scriptable scriptable = context.getWrapFactory().wrapJavaClass(context, scope, clazz);
        scope.put(value, scope, scriptable);
    }

    /**
     * Prints a message to the chat
     *
     * @param message the message to print
     * */
    public void addChatMessage(String message) {
        Bukkit.broadcastMessage(message);
    }


    public void put(String name, Object object) {
        Scriptable scriptable = context.getWrapFactory().wrapAsJavaObject(context, scope, object, null);
        scope.put(name, scope, scriptable);
    }

    /**
     * Executes javascript
     *
     * @param js the javascript to execute
     * */
    @SuppressWarnings("all") // return void warning
    public Object eval(String js) {
        try {
            Object obj = context.evaluateString(scope, js, "script", lines, null);
            lines++;
            return obj;
        } catch (Exception e) {
            printError(e);
        }
        return null;
    }

    public void reload() {
        lines = 0;
        context = Context.enter();
        context.getWrapFactory().setJavaPrimitiveWrap(false);
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        scope = context.initStandardObjects();
        stop();

        put("script", this);
        put("server", Bukkit.getServer());
        for(ChatColor color : ChatColor.values()) put(color.name(), color);
        putClass(Bukkit.class);
        putClass("console", Console.class);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            for(String s : builder.toString().split("\n")) eval(s);
        } catch (Exception e) {
            printError(e);
        }
        callEvent("load");
    }

    public void stop() {
        commands.forEach(command -> command.unregister(FurnaceJS.instance.getCommandMap()));
        events.clear();
        FurnaceJS.instance.reloadAllCommands();
    }

    /***
     * Prints an error to the console
     *
     * @param e the error to print
     * */
    public void printError(Exception e) {
        addChatMessage(ChatColor.RED + e.getMessage());
    }

    /**
     * This method is called by the script
     *
     * @param name the name of the command
     * @param function the function to call
     * */
    public ScriptCommand registerCommand(String name, Function function) {
        FurnaceJS js = FurnaceJS.instance;

        if(getCommand(name) != null) {
            Command command = getCommand(name);
            if(command instanceof ScriptCommand) {
                ScriptCommand scriptCommand = (ScriptCommand) command;
                scriptCommand.setExecute(new ScriptCommandExecuter(this, function));
                return scriptCommand;
            }
        }

        ScriptCommand scriptCommand = new ScriptCommand(name, this, function);
        Command command = js.getCommandByName(name);

        js.unregisterCommand(command);
        js.registerCommand(scriptCommand);
        commands.add(scriptCommand);
        return scriptCommand;
    }

    /**
     * This method is called by the script
     *
     * @param name the name of the event
     * @param function the function to call
     * */
    public void on(String name, Function function) {
        events.put(name, function);
    }

    public void unregisterCommand(String name) {
        FurnaceJS js = FurnaceJS.instance;
        Command command = js.getCommandByName(name);
        js.unregisterCommand(command);

        if(command instanceof ScriptCommand) { commands.remove(command); }
        FurnaceJS.instance.reloadAllCommands();
    }

    public Command getCommand(String name) {
        return FurnaceJS.instance.getCommandMap().getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void callEvent(String name, Object... args) {
        Function function = events.get(name);
        if(function == null) return;
        function.call(context, scope, scope, args);
    }

}
