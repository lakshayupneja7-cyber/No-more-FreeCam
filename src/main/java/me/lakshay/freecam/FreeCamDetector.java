package me.lakshay.freecam;

import org.bukkit.plugin.java.JavaPlugin;

public class FreeCamDetector extends JavaPlugin {

    @Override
    public void onEnable() {

        getLogger().info("Lakshay FreeCam Detector Enabled");

        getServer().getPluginManager().registerEvents(
                new MovementListener(this),
                this
        );
    }

    @Override
    public void onDisable() {

        getLogger().info("Lakshay FreeCam Detector Disabled");

    }
}
