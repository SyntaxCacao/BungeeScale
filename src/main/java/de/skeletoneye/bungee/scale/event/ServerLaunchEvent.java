package de.skeletoneye.bungee.scale.event;

import java.util.ArrayList;
import java.util.List;

import de.skeletoneye.bungee.scale.Image;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;

@Getter
public class ServerLaunchEvent extends ServerEvent
{
    private List<String> arguments = new ArrayList<>();
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
