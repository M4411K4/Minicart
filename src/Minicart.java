
/*
 * Minecraft Minicart Mod
 * Copyright (C) 2012  M4411K4
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.logging.Logger;

public class Minicart extends Plugin
{
    public static final Logger log = Logger.getLogger("Minecraft");
    
    public final String NAME = "Minicart";
    public final String VERSION = "1.0.0";
    
    private MinicartListener listener;
    private PropertiesFile properties;
    
    public void enable()
    {
        if(!loadConfig())
        {
            log.info(NAME + " failed to load Properties File! Using default settings.");
            log.info("Disabling this plug-in (" + NAME + ") is recommended.");
        }
        
        listener = new MinicartListener(this, properties);
        
        //log.info(NAME + " " + VERSION + " enabled.");
    }
    
    public void disable()
    {
        listener.disable();
        
        log.info(NAME + " " + VERSION + " disabled.");
    }
    
    public void initialize()
    {
        etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.VEHICLE_POSITIONCHANGE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.VEHICLE_ENTERED, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.VEHICLE_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.MEDIUM);
        
        log.info(NAME + " " + VERSION + " initialized");
    }
    
    private boolean loadConfig()
    {
        properties = FileUtil.loadSetting();
        
        return !(properties == null);
    }
}
