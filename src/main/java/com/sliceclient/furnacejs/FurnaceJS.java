package com.sliceclient.furnacejs;

import lombok.Getter;
import com.sliceclient.furnacejs.javascript.JavaScript;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

@Getter
public final class FurnaceJS extends JavaPlugin implements Listener {

    private JavaScript script;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        createDataFolder();
        script = new JavaScript(new File(getDataFolder(), "test.js"));
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if(!event.getMessage().equalsIgnoreCase("reload")) return;

        script.reload();
    }

    @SuppressWarnings("all")
    public void createDataFolder() {
        if(getDataFolder().exists()) return;

        getDataFolder().mkdir();
    }
}
