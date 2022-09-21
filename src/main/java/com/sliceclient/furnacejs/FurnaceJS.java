package com.sliceclient.furnacejs;

import com.sliceclient.furnacejs.commands.ReloadCommand;
import lombok.Getter;
import com.sliceclient.furnacejs.javascript.JavaScript;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Getter
public final class FurnaceJS extends JavaPlugin implements Listener {

    public static ArrayList<JavaScript> scripts = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        createDataFolder();
        if (!getDataFolder().exists()) return;
        for (File file : Objects.requireNonNull(getDataFolder().listFiles())) {
            if (file.getName().endsWith(".js")) {
                scripts.add(new JavaScript(file));
            }
        }

        registerCommand("reload", new ReloadCommand(getDataFolder()));
    }

    @SuppressWarnings("all")
    public void createDataFolder() {
        if (getDataFolder().exists()) return;

        getDataFolder().mkdir();
    }

    public void registerCommand(String name, CommandExecutor commandExecutor) {
        Objects.requireNonNull(getCommand(name)).setExecutor(commandExecutor);
    }
}
