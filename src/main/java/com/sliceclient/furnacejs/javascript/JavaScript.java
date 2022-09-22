package com.sliceclient.furnacejs.javascript;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class JavaScript {


    /** script engine */
    private volatile Context context = Context.enter();
    private Scriptable scope = context.initStandardObjects(), scriptable = context.newObject(scope);
    private int lines = 0;

    private File file;

    public JavaScript(File file) {
        this.context.setLanguageVersion(Context.VERSION_ES6);
        this.file = file;
        this.setup();
    }

    public void setup() {
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

    public List<Object> reload() {
        stop();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            for(String s : builder.toString().split("\n")) eval(s);

            return Arrays.asList(new Date().getTime(), true);
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.asList(new Date().getTime(), false);
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

}
