package de.skeletoneye.bungee.scale;

import java.io.File;
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

    private void applyIncludes(File runtimeDir, List<String> includes) throws IOException
    {
        for (String include : includes) {
            Path includeDir = BungeeScale.getInstance().getIncludesDir().resolve(include);

            if (Files.exists(includeDir)) {
                FileUtils.copyDirectory(includeDir.toFile(), runtimeDir);
            } else {
                BungeeScale.getInstance().getLogger().severe("Unable to find include " + include + ", skipping it.");
            }
        }
    }

    @Override
    public void run()
    {
        try {
            // Create runtime directory
            BungeeScale.getInstance().getLogger().info("Launching " + this.getServer().getName() + " on port " + this.getServer().getAddress().getPort());

            Path runtimeDir = BungeeScale.getInstance().getRuntimeDir().resolve(this.getServer().getName());
            Files.createDirectory(runtimeDir);

            // Apply global includes
            this.applyIncludes(runtimeDir.toFile(), BungeeScale.getInstance().getNetworkConfig().getStringList("includes"));

            // Copy image files to runtime directory
            FileUtils.copyDirectory(this.getImage().getSourceDir().toFile(), runtimeDir.toFile());

            // Apply image-specific includes
            this.applyIncludes(runtimeDir.toFile(), this.getImage().getConfig().getStringList("includes"));

            // Call ServerLaunchEvent
            ServerLaunchEvent launchEvent = new ServerLaunchEvent(this.getServer(), this.getImage());
            launchEvent = ProxyServer.getInstance().getPluginManager().callEvent(launchEvent);

            if (launchEvent.isCancelled()) {
                BungeeScale.getInstance().getLogger().info("Launch process for " + this.getServer().getName() + " cancelled.");
                FileUtils.deleteDirectory(runtimeDir.toFile());
                return;
            }

            // Prepare launch command
            String command = BungeeScale.getInstance().getNetworkConfig().getString("launchCommand");
            command = command.replaceAll("\\{driver\\}", "../../images/driver.jar");
            command = command.replaceAll("\\{identifier\\}", this.getServer().getName());
            command = command.replaceAll("\\{port\\}", String.valueOf(this.getServer().getAddress().getPort()));

            // Add arguments given from ServerLaunchEvent
            List<String> input = new ArrayList<>();
            input.addAll(Arrays.asList(command.split(" ")));
            input.addAll(launchEvent.getArguments());

            // Build and start process
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

                // Launch another instance if required
                if (this.getImage().getConfig().getBoolean("restart.enabled", false)) {
                    this.getImage().launchInstance(true, this.getImage().getConfig().getBoolean("restart.forced", false));
                }
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
