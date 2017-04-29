package de.skeletoneye.bungee.scale.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

/**
 * Represents an instance-related event.
 * 
 * @since 0.1.0
 */
@AllArgsConstructor
@Getter
public abstract class ServerEvent extends Event
{
    /**
     * This instance's BungeeCord registration
     */
    private ServerInfo server;
}
