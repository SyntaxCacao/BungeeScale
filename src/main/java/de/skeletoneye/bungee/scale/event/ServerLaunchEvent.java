package de.skeletoneye.bungee.scale.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.skeletoneye.bungee.scale.Image;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Cancellable;

@Getter
public class ServerLaunchEvent extends ServerEvent implements Cancellable
{
    private List<String> includes = new ArrayList<>();
    private Map<String, String> replacements = new HashMap<>();
    private @Setter boolean cancelled = false;
    private Image image;

    public ServerLaunchEvent(ServerInfo server, Image image)
    {
        super(server);
        this.image = image;
    }

    /**
     * Adds an include to be applied to only this instance.
     * 
     * @param name
     */
    public void addInclude(String name)
    {
        this.includes.add(name);
    }

    /**
     * Performs the given replacement on the launch command.
     * <code>varName = "test"</code> matches <code>{test}</code>
     * 
     * @param varName the string to search for
     * @param value its replacement
     */
    public void replace(String varName, String value)
    {
        this.replacements.put(varName, value);
    }
}
