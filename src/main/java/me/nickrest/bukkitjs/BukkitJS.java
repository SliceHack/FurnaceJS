package me.nickrest.bukkitjs;

import lombok.Getter;
import me.nickrest.bukkitjs.javascript.JavaScript;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Getter
public final class BukkitJS extends JavaPlugin implements Listener {

    private final HashMap<Player, JavaScript> scripts = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        try {
            String message = event.getMessage();

            int index = message.indexOf("js");
            String js = message.substring(index + 3);

            if (index == -1) return;
            if (js.replace(" ", "").isEmpty()) return;

            if (scripts.get(event.getPlayer()) == null) {
                scripts.put(event.getPlayer(), new JavaScript());
                scripts.get(event.getPlayer()).eval("const player = Bukkit.getPlayer('" + event.getPlayer().getName() + "');");
            }

            JavaScript script = scripts.get(event.getPlayer());

            script.eval(js);
            event.setCancelled(true);
        } catch (Exception ignored) {}
    }
}
