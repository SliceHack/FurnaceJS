package com.sliceclient.furnacejs.javascript;

import com.sliceclient.furnacejs.FurnaceJS;
import com.sliceclient.furnacejs.script.ScriptCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.mozilla.javascript.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class JavaScript {


    /** script engine */
    private volatile Context context = Context.enter();
    private Scriptable scope = context.initStandardObjects(), scriptable = context.newObject(scope);
    private int lines = 0;

    private File file;

    private List<ScriptCommand> commands = new ArrayList<>();

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
            e.printStackTrace();
        }
    }

    public void stop() {
        for(int i = 0; i <= lines; i++) context.evaluateString(scope, "", "script", i, null);
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
    public void registerCommand(String name, Function function) {
        FurnaceJS js = FurnaceJS.instance;

        if(getCommand(name) != null) {
            ScriptCommand command = getCommand(name);
            command.getExecute().setScript(this);
            command.getExecute().setFunction(function);
            return;
        }

        ScriptCommand scriptCommand = new ScriptCommand(name, this, function);
        Command command = js.getCommandByName(name);

        js.unregisterCommand(command);
        js.registerCommand(scriptCommand);
        commands.add(scriptCommand);
    }

    public void unregisterCommand(String name) {
        FurnaceJS js = FurnaceJS.instance;
        Command command = js.getCommandByName(name);
        js.unregisterCommand(command);
        commands.remove(getCommand(name));
    }

    public ScriptCommand getCommand(String name) {
        return commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
