package de.skeletoneye.bungee.scale;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeScale extends Plugin
{
    private static @Getter BungeeScale instance;

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
            Files.createDirectory(serversDir);

            // Check whether configuration file exists and read it
            final Configuration networkCfg = this.checkConfig(serversDir.resolve("network.yml"));

            // Check whether other required directories exist
            final Path staticDir = serversDir.resolve("static");
            Files.createDirectory(staticDir);

            final Path imagesDir = serversDir.resolve("images");
            Files.createDirectory(imagesDir);

            final Path includesDir = serversDir.resolve("includes");
            Files.createDirectory(includesDir);

            // Make sure runtime/ is existing and empty
            final Path runtimeDir = serversDir.resolve("runtime");

            if (Files.exists(runtimeDir)) {
                this.getLogger().info("Clearing runtime directory");
                FileUtils.deleteDirectory(runtimeDir.toFile());
            }

            Files.createDirectory(runtimeDir);

            // Singleton
            BungeeScale.instance = this;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
