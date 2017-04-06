package de.skeletoneye.bungee.scale.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public abstract class ServerEvent extends Event
{
    private ServerInfo server;
}
