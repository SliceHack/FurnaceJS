package com.sliceclient.furnacejs;

import com.sliceclient.furnacejs.commands.ScriptCommand;
import com.sliceclient.furnacejs.subcommand.manager.SubCommandManager;
import lombok.Getter;
import com.sliceclient.furnacejs.javascript.JavaScript;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

@Getter
public final class FurnaceJS extends JavaPlugin implements Listener {

    public static FurnaceJS instance;

    private SubCommandManager subCommandManager;

    public ArrayList<JavaScript> scripts = new ArrayList<>();

    public RegisteredListener listener;
    public EventExecutor executor;

    @Override
    public void onEnable() {
        instance = this;
        subCommandManager = new SubCommandManager();

        registerListener(this);

        createDataFolder();
        if (!getDataFolder().exists()) return;
        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if (file.getName().endsWith(".js") && !file.getName().startsWith("-")) {
                scripts.add(new JavaScript(file));
            }
        }
        executor = (listener, event) -> scripts.forEach(script -> script.callEvent(convertToScriptEvent(event.getClass().getSimpleName()), event));
        listener = new RegisteredListener(this, executor, EventPriority.NORMAL, this, false);
        registerRegisteredListener(listener);

        registerCommand("script", new ScriptCommand());
    }

    @Override
    public void onDisable() {
        scripts.forEach(JavaScript::stop);
    }

    @SuppressWarnings("all")
    public void createDataFolder() {
        if (getDataFolder().exists()) return;

        getDataFolder().mkdir();
        Bukkit.getServer().reload();
    }

    public void registerCommand(String name, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(name)).setExecutor(commandExecutor);
    }

    public String convertToScriptEvent(String name) {
        name = name.replace("Event", "");
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerRegisteredListener(RegisteredListener listener) {
        for (HandlerList handler : HandlerList.getHandlerLists()) handler.register(listener);
    }

    public JavaScript getScript(String name) {
        return scripts.stream().filter(script -> script.getFile().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void registerCommand(Command command) {
        getCommandMap().register(getPlugin(FurnaceJS.class).getName().toLowerCase(), command);
        reloadAllCommands();
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
        reloadAllCommands();
    }

    public void setPluginManager(PluginManager pluginManager) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("pluginManager");
            field.setAccessible(true);
            field.set(Bukkit.getServer(), pluginManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void reloadAllCommands() {
        try {
            Class<?> craftServer = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftServer");

            Method syncCommandsMethod = craftServer.getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
