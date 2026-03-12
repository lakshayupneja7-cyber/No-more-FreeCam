package me.lakshay.freecam;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class MovementListener implements Listener {

    private final Plugin plugin;

    public MovementListener(Plugin plugin) {
        this.plugin = plugin;
    }

    private final HashMap<UUID, Location> lastLocation = new HashMap<>();
    private final HashMap<UUID, Float> lastYaw = new HashMap<>();
    private final HashMap<UUID, Float> lastPitch = new HashMap<>();
    private final HashMap<UUID, Integer> suspicion = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!player.isOnline()) return;

        // ignore elytra
        if (player.isGliding()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        double distance = from.distance(to);

        float yawChange = Math.abs(from.getYaw() - to.getYaw());
        float pitchChange = Math.abs(from.getPitch() - to.getPitch());

        UUID uuid = player.getUniqueId();

        // store initial values
        lastLocation.putIfAbsent(uuid, from);
        lastYaw.putIfAbsent(uuid, from.getYaw());
        lastPitch.putIfAbsent(uuid, from.getPitch());
        suspicion.putIfAbsent(uuid, 0);

        // check if player body did not move
        boolean stationary = distance < 0.02;

        // big camera rotation
        boolean rapidRotation = yawChange > 70 || pitchChange > 50;

        if (stationary && rapidRotation) {

            int count = suspicion.get(uuid) + 1;
            suspicion.put(uuid, count);

            if (count > 6) {

                player.kickPlayer("§cFreeCam Detected!\n§7Disable FreeCam and rejoin.");

                plugin.getLogger().info(
                        player.getName() + " kicked for FreeCam rotation behaviour."
                );

                suspicion.remove(uuid);
            }

        } else {

            // reset suspicion when normal movement happens
            suspicion.put(uuid, 0);

        }

        lastLocation.put(uuid, to);
        lastYaw.put(uuid, to.getYaw());
        lastPitch.put(uuid, to.getPitch());
    }
}
