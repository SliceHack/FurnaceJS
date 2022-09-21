package me.nickrest.bukkitjs.javascript;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

@Getter @Setter
public class JavaScript {


    /** script engine */
    private volatile Context context = Context.enter();
    private Scriptable scope = context.initStandardObjects(), scriptable = context.newObject(scope);
    private int lines = 0;

    public JavaScript() {
        context.setLanguageVersion(Context.VERSION_ES6);
        putClass(Bukkit.class);
        putClass("console", Console.class);
    }

    /**
     * Puts a class in the engine
     *
     * @param clazz the class to put in the engine
     * */
    public void putClass(Class<?> clazz) {
        eval("const " + clazz.getSimpleName() + " = Packages." + clazz.getName() + ";");
    }

    /**
     * Puts a class in the engine
     *
     * @param clazz the class to put in the engine
     * */
    public void putClass(String value, Class<?> clazz) {
        eval("const " + value + " = Packages." + clazz.getName() + ";");
    }

    /**
     * Prints a message to the chat
     *
     * @param message the message to print
     * */
    public void addChatMessage(String message) {
        Bukkit.broadcastMessage(message);
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

    /**
     * Gets a variable from the script engine.
     *
     * @param name The name of the variable.
     * */
    public Object getVariable(String name) {
        return "return " + name + ";";
    }

    /***
     * Prints an error to the console
     *
     * @param e the error to print
     * */
    public void printError(Exception e) {
        addChatMessage(ChatColor.RED + e.getMessage());
    }

}
