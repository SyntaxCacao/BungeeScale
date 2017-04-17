package de.skeletoneye.bungee.scale.event;

import java.util.ArrayList;
import java.util.List;

import de.skeletoneye.bungee.scale.Image;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Cancellable;

@Getter
public class ServerLaunchEvent extends ServerEvent implements Cancellable
{
    private List<String> arguments = new ArrayList<>();
    private @Setter boolean cancelled = false;
    private Image image;

    public ServerLaunchEvent(ServerInfo server, Image image)
    {
        super(server);
        this.image = image;
    }

    public void addArgument(String argument)
    {
        this.arguments.add(argument);
    }
}
