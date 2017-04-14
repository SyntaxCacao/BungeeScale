package de.skeletoneye.bungee.scale;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.skeletoneye.bungee.scale.event.ServerLaunchEvent;
import de.skeletoneye.bungee.scale.event.ServerTerminatedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class Launcher implements Runnable
{
    private Image image;
    private ServerInfo server;

    @Override
    public void run()
    {
        try {
            // Create runtime directory
            BungeeScale.getInstance().getLogger().info("Launching " + this.getServer().getName() + " on port " + this.getServer().getAddress().getPort());

            Path runtimeDir = BungeeScale.getInstance().getRuntimeDir().resolve(this.getServer().getName());
            FileUtils.copyDirectory(this.getImage().getSourceDir().toFile(), runtimeDir.toFile());

            List<String> includes = BungeeScale.getInstance().getNetworkConfig().getStringList("includes");
            includes.addAll(this.getImage().getConfig().getStringList("includes"));

            for (String include : includes) {
                Path includeDir = BungeeScale.getInstance().getIncludesDir().resolve(include);

                if (Files.exists(includeDir)) {
                    FileUtils.copyDirectory(includeDir.toFile(), runtimeDir.toFile());
                } else {
                    BungeeScale.getInstance().getLogger().severe("Unable to find include " + include + ", skipping it.");
                }
            }

            // Call ServerLaunchEvent
            ServerLaunchEvent launchEvent = new ServerLaunchEvent(this.getServer(), this.getImage());
            launchEvent = ProxyServer.getInstance().getPluginManager().callEvent(launchEvent);

            // Prepare launch command
            String command = BungeeScale.getInstance().getNetworkConfig().getString("launchCommand");
            command = command.replaceAll("\\{driver\\}", "../../images/driver.jar");
            command = command.replaceAll("\\{identifier\\}", this.getServer().getName());
            command = command.replaceAll("\\{port\\}", String.valueOf(this.getServer().getAddress().getPort()));

            // Add arguments given from ServerLaunchEvent
            List<String> input = new ArrayList<>();
            input.addAll(Arrays.asList(command.split(" ")));
            input.addAll(launchEvent.getArguments());

            // Builder and start process
            ProcessBuilder builder = new ProcessBuilder(input.toArray(new String[] {}));
            builder.directory(runtimeDir.toFile());
            Process process = builder.start();

            try {
                // Wait for process to terminate
                process.waitFor();
                BungeeScale.getInstance().getLogger().info(this.getServer().getName() + " has terminated.");

                // Call ServerTerminatedEvent
                ProxyServer.getInstance().getPluginManager().callEvent(new ServerTerminatedEvent(this.getServer()));

                // Unregister ServerInfo object
                ProxyServer.getInstance().getServers().remove(this.getServer().getName());
            } catch (InterruptedException exception) {
                // Getting a InterruptedException while running Process#waitFor means BungeeCord is going to shut down
                // and the server has not terminated yet
                BungeeScale.getInstance().getLogger().severe("Destroying " + this.getServer().getName());
                process.destroy();
            }
        } catch (IOException exception) {
            BungeeScale.getInstance().getLogger().severe("Exception while launching " + this.getServer().getName());
            exception.printStackTrace();
        }
    }
}
