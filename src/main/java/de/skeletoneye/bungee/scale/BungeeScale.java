package de.skeletoneye.bungee.scale;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import de.skeletoneye.bungee.scale.event.ServerLaunchListener;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

@Getter
public class BungeeScale extends Plugin
{
    private static @Getter BungeeScale instance;

    private Path includesDir;
    private Configuration networkConfig;
    private Path runtimeDir;

    // TODO To be moved somewhere else
    public Configuration checkConfig(Path path) throws IOException
    {
        if (!Files.exists(path)) {
            this.getLogger().info("Unable to find " + path.getFileName().toString() + ", creating it.");

            try (InputStream stream = this.getResourceAsStream("templates/" + path.getFileName().toString())) {
                Files.copy(stream, path);
            }
        }

        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(path.toFile());
    }

    @Override
    public void onEnable()
    {
        try {
            // Check whether servers directory exists
            final Path serversDir = Paths.get("servers");
            Files.createDirectories(serversDir);

            // Check whether configuration file exists and read it
            this.networkConfig = this.checkConfig(serversDir.resolve("network.yml"));

            // Check whether other required directories exist
            final Path staticDir = serversDir.resolve("static");
            Files.createDirectories(staticDir);

            final Path imagesDir = serversDir.resolve("images");
            Files.createDirectories(imagesDir);

            this.includesDir = serversDir.resolve("includes");
            Files.createDirectories(this.includesDir);

            // Check whether driver.jar exists
            final Path driverJar = imagesDir.resolve("driver.jar");

            if (!Files.exists(driverJar)) {
                this.getLogger().severe("Unable to find " + driverJar.toString() + ", not going to start.");
                return;
            }

            // Make sure runtime/ is existing and empty
            this.runtimeDir = serversDir.resolve("runtime");

            if (Files.exists(this.runtimeDir)) {
                this.getLogger().info("Clearing runtime directory");
                FileUtils.deleteDirectory(this.runtimeDir.toFile());
            }

            Files.createDirectory(this.runtimeDir);

            // Singleton
            BungeeScale.instance = this;

            // Load all images from their directory
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDir)) {
                for (Path item : stream) {
                    if (!Files.isDirectory(item)) {
                        continue;
                    }

                    Image.load(item);
                }
            }

            // Launch instances according to configuration
            Map<String, Image> images = Image.getAll();

            for (String key : images.keySet()) {
                int instances = images.get(key).getConfig().getInt("instances.startup");

                for (int i = 0; i < instances; i++) {
                    images.get(key).launchInstance(true);
                }
            }
            
            this.getProxy().getPluginManager().registerListener(this, new ServerLaunchListener());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
