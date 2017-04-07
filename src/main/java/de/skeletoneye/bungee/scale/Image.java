package de.skeletoneye.bungee.scale;

import java.io.IOException;
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

    public void launchInstance() throws IOException
    {
        this.launchInstance(false);
    }

    public void launchInstance(boolean delayed) throws IOException
    {
        // Generate identifier
        String identifier;

        do {
            identifier = this.getConfig().getString("name") + "-" + UUID.randomUUID().toString().substring(0, 7);
        } while (Files.exists(BungeeScale.getInstance().getRuntimeDir().resolve(identifier)));

        if (delayed) {
            ProxyServer.getInstance().getScheduler().schedule(BungeeScale.getInstance(), new Launcher(this, identifier), 1 + new Random().nextInt(5), TimeUnit.SECONDS);
        } else {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeScale.getInstance(), new Launcher(this, identifier));
        }
    }
}
