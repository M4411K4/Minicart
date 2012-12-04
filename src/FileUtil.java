
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public class FileUtil
{
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String SETTINGS_PATH = "plugins/config/Minicart/minicart.properties";
    
    private static String getDefault()
    {
        String output = ""+
        
        "#[NOTE]: Features come from CraftBook. For more information on CraftBook: http://wiki.sk89q.com/wiki/CraftBook \r\n"+
        "\r\n"+
        "\r\n"+
        "###################\r\n"+
        "\r\n"+
        "# Have minecarts get removed after a certain number of seconds of being \r\n"+
        "# unoccupied by any player. The check is performed every 3 seconds, so \r\n"+
        "# it may take up to the following number of seconds plus 3 before a minecart \r\n"+
        "# is removed. \r\n"+
        "decay-time=0\r\n"+
        "\r\n"+
        "#true - (default) allows [Print] signs which display messages to minecart passengers. \r\n"+
        "#false - disables minecart [Print] messages. \r\n"+
        "track-message-enabled=true\r\n"+
        
        "";
        
        return output;
    }
    
    public static PropertiesFile loadSetting()
    {
        File file = new File(SETTINGS_PATH);
        
        if(!file.exists())
        {
            if(!saveSettings(SETTINGS_PATH, getDefault()))
            {
                return null;
            }
        }
        
        PropertiesFile properties = new PropertiesFile(SETTINGS_PATH);
        
        try
        {
            properties.load();
        }
        catch(IOException e)
        {
            return null;
        }
        
        return properties;
    }
    
    private static boolean saveSettings(String path, String output)
    {
        File file = new File(path);
        
        if(!file.exists())
        {
            File folder = file.getParentFile();
            if(!folder.exists()) 
            {
                if(!folder.mkdirs())
                    return false;
            }
            
            
            try
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                return false;
            }
        }
        
        BufferedWriter buffWriter = null;
        
        try
        {
            buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"));
            
            buffWriter.write(output);
        }
        catch (UnsupportedEncodingException e)
        {
            log.warning("Minicart mod saveSettings Unsupported Encoding Error: "+e);
            return false;
        }
        catch (FileNotFoundException e)
        {
            log.warning("Minicart mod saveSettings: "+path+" could not be found. "+e);
            return false;
        }
        catch (IOException e)
        {
            log.warning("Minicart mod saveSettings Error: "+e);
            return false;
        }
        finally
        {
            if(buffWriter != null)
            {
                try
                {
                    buffWriter.flush();
                    buffWriter.close();
                }
                catch(IOException e)
                {
                    log.warning("Minicart mod could not close BufferedWriter.");
                }
            }
        }
        
        return true;
    }
}
