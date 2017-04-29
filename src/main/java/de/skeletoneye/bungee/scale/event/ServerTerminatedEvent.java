package de.skeletoneye.bungee.scale.event;

import net.md_5.bungee.api.config.ServerInfo;

/**
 * Called when an instance has terminated. Not called for instances getting destroyed when BungeeCord shuts down.
 * 
 * @since 0.1.0
 */
public class ServerTerminatedEvent extends ServerEvent
{
    public ServerTerminatedEvent(ServerInfo server)
    {
        super(server);
    }
}
