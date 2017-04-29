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

/**
 * Called when an instance is going to be launched.
 * 
 * @since 0.1.0
 */
@Getter
public class ServerLaunchEvent extends ServerEvent implements Cancellable
{
    /**
     * List of includes added using {@link #addInclude(String)}
     * 
     * @since 0.2.0
     */
    private List<String> includes = new ArrayList<>();

    /**
     * Map of replacements added using {@link #replace(String, String)}
     * 
     * @since 0.2.0
     */
    private Map<String, String> replacements = new HashMap<>();
    
    /**
     * If set to <i>true</i>, the instance won't be launched.
     */
    private @Setter boolean cancelled = false;
    
    /**
     * This instance's image
     */
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
     * Performs the given replacement on the launch command. <code>search = "test"</code> matches <code>{test}</code>
     * 
     * @param search the string to search for
     * @param replace its replacement
     */
    public void replace(String search, String replace)
    {
        this.replacements.put(search, replace);
    }
}
