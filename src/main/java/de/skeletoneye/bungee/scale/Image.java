package de.skeletoneye.bungee.scale;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

@AllArgsConstructor
@Getter
public class Image
{
    private static Map<String, Image> registry = new HashMap<>();

    public static Image get(String name)
    {
        return Image.registry.get(name);
    }

    public static Map<String, Image> getAll()
    {
        return Image.registry;
    }

    public static void load(Path sourceDir) throws IOException
    {
        BungeeScale.getInstance().getLogger().info("Loading image " + sourceDir.getFileName().toString());
        Image image = new Image(BungeeScale.getInstance().checkConfig(sourceDir.resolve("image.yml")), sourceDir);

        if (!image.getConfig().getBoolean("enabled")) {
            BungeeScale.getInstance().getLogger().info("Image " + image.getSourceDir().getFileName().toString() + " is disabled.");
            return;
        }

        Image.registry.put(sourceDir.getFileName().toString(), image);
    }

    private Configuration config;
    private Path sourceDir;

    /**
     * Counts all instances of this image registered to the proxy.
     * 
     * @return int
     */
    public int countRegisteredInstances()
    {
        int instances = 0;

        for (String server : ProxyServer.getInstance().getServers().keySet()) {
            if (server.startsWith(this.getConfig().getString("name") + "-")) {
                instances++;
            }
        }

        return instances;
    }

    /**
     * Checks whether the maximum number of running instances (according to the image's configuration) is reached.
     * 
     * @return boolean
     */
    public boolean isMaximumReached()
    {
        int max = this.getConfig().getInt("instances.max", -1);
        return (max >= 0 && max <= this.countRegisteredInstances());
    }

    public boolean launchInstance() throws IOException
    {
        return this.launchInstance(false, false);
    }

    public boolean launchInstance(boolean delayed) throws IOException
    {
        return this.launchInstance(delayed, false);
    }

    public boolean launchInstance(boolean delayed, boolean force) throws IOException
    {
        if (this.isMaximumReached()) {
            if (force) {
                BungeeScale.getInstance().getLogger().severe("Forcefully launching a new instance of " + this.getConfig().getString("name") + ", even though the maximum number of running instances is reached.");
            } else {
                BungeeScale.getInstance().getLogger().severe("Unable to launch another instance of " + this.getConfig().getString("name") + ", since the maximum number of running instances is reached.");
                return false;
            }
        }

        // Generate identifier
        String identifier;

        do {
            identifier = this.getConfig().getString("name") + "-" + UUID.randomUUID().toString().substring(0, 7);
        } while (Files.exists(BungeeScale.getInstance().getRuntimeDir().resolve(identifier)));

        // Search empty port
        int port = 0;

        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        // Create and register ServerInfo object
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(identifier, new InetSocketAddress("0.0.0.0", port), identifier, false);
        ProxyServer.getInstance().getServers().put(identifier, info);

        if (delayed) {
            ProxyServer.getInstance().getScheduler().schedule(BungeeScale.getInstance(), new Launcher(this, info), 1 + new Random().nextInt(5), TimeUnit.SECONDS);
        } else {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeScale.getInstance(), new Launcher(this, info));
        }

        return true;
    }
}
