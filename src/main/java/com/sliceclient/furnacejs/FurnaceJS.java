package com.sliceclient.furnacejs;

import com.sliceclient.furnacejs.commands.ScriptCommand;
import com.sliceclient.furnacejs.subcommand.manager.SubCommandManager;
import lombok.Getter;
import com.sliceclient.furnacejs.javascript.JavaScript;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

@Getter
public final class FurnaceJS extends JavaPlugin implements Listener {

    public static FurnaceJS instance;

    private SubCommandManager subCommandManager;

    public ArrayList<JavaScript> scripts = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        subCommandManager = new SubCommandManager();

        registerListener(this);

        createDataFolder();
        if (!getDataFolder().exists()) return;
        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if (file.getName().endsWith(".js")) {
                scripts.add(new JavaScript(file));
            }
        }

        registerCommand("script", new ScriptCommand());
    }

    @SuppressWarnings("all")
    public void createDataFolder() {
        if (getDataFolder().exists()) return;

        getDataFolder().mkdir();
    }

    public void registerCommand(String name, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(name)).setExecutor(commandExecutor);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public JavaScript getScript(String name) {
        return scripts.stream().filter(script -> script.getFile().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void registerCommand(BukkitCommand command) {
        getCommandMap().register(getPlugin(FurnaceJS.class).getName().toLowerCase(), command);
    }

    public SimpleCommandMap getCommandMap() {
        SimpleCommandMap commandMap = null;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (SimpleCommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    public Command getCommandByName(String name) {
        return getCommandMap().getCommand(name);
    }

    public void unregisterCommand(String name) {
        Command command = getCommandByName(name);
        if (command == null) return;
        command.unregister(getCommandMap());
    }

    public void unregisterCommand(Command command) {
        if (command == null) return;
        command.unregister(getCommandMap());
    }
}
