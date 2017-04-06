package de.skeletoneye.bungee.scale.event;

import net.md_5.bungee.api.config.ServerInfo;

public class ServerTerminatedEvent extends ServerEvent
{
    public ServerTerminatedEvent(ServerInfo server)
    {
        super(server);
    }
}
