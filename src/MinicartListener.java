import java.util.HashMap;
import java.util.Map;


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

public class MinicartListener extends PluginListener
{
    private final int SIGN_POST = 63;
    
    @SuppressWarnings("unused")
    private final Minicart minicartPlugin;
    private final PropertiesFile properties;
    
    private MinecartDecayWatcher decayWatcher;
    private Map<Player, MessageRepeatProtection> msgRepeatProtection = new HashMap<Player, MessageRepeatProtection>();
    
    private boolean trackMessageEnabled;
    
    public MinicartListener(Minicart minicartPlugin, PropertiesFile properties)
    {
        super();
        
        this.minicartPlugin = minicartPlugin;
        this.properties = properties;
        
        if(decayWatcher != null)
        {
            decayWatcher.disable();
        }
        
        int decay = 0;
        if(this.properties.containsKey("decay-time"))
            decay = this.properties.getInt("decay-time", 0);
        if(decay > 0)
        {
            decayWatcher = new MinecartDecayWatcher(decay);
        }
        
        trackMessageEnabled = true;
        if(this.properties.containsKey("track-message-enabled"))
            trackMessageEnabled = this.properties.getBoolean("track-message-enabled", true);
    }
    
    @Override
    public boolean onSignChange(Player player, Sign sign)
    {
        if(player == null || sign == null)
        {
            return false;
        }
        
        String line1 = sign.getText(0);
        
        if(line1.equalsIgnoreCase("[Print]"))
        {
            sign.setText(0, "[Print]");
            sign.update();
            
            player.sendMessage(Colors.Gold + "Message print block detected.");
        }
        
        return false;
    }
    
    @Override
    public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z)
    {
        if(vehicle == null || !(vehicle instanceof Minecart))
        {
            return;
        }
        
        World world = vehicle.getWorld();
        Minecart minecart = (Minecart)vehicle;
        
        if(trackMessageEnabled && world.getBlockIdAt(x, y-2, z) == SIGN_POST)
        {
            ComplexBlock cblock = world.getComplexBlock(x, y-2, z);
            if(!(cblock instanceof Sign) || cblock.getBlock().isPowered())
            {
                return;
            }
            
            Sign sign = (Sign)cblock;
            String line1 = sign.getText(0);
            
            if(line1.equalsIgnoreCase("[Print]"))
            {
                Player player = minecart.getPassenger();
                if(player == null)
                {
                    return;
                }
                
                if(msgRepeatProtection.containsKey(player))
                {
                    MessageRepeatProtection mrp = msgRepeatProtection.get(player);
                    long now = System.currentTimeMillis();
                    if(mrp.X == x && mrp.Y == y && mrp.Z == z && mrp.TIME + 3000 > now)
                    {
                        return;
                    }
                }
                
                msgRepeatProtection.put(player, new MessageRepeatProtection(x, y, z, System.currentTimeMillis()));
                
                String msg = sign.getText(1) + sign.getText(2) + sign.getText(3);
                player.sendMessage("> " + msg);
            }
        }
    }
    
    @Override
    public void onVehicleEnter(BaseVehicle vehicle, HumanEntity player)
    {
        if(vehicle == null || !(vehicle instanceof Minecart))
        {
            return;
        }
        
        if(decayWatcher != null)
        {
            decayWatcher.trackEnter((Minecart)vehicle);
        }
    }
    
    @Override
    public void onVehicleDestroyed(BaseVehicle vehicle)
    {
        if(vehicle == null || !(vehicle instanceof Minecart))
        {
            return;
        }
        
        if(decayWatcher != null)
        {
            decayWatcher.forgetMinecart((Minecart)vehicle);
        }
    }
    
    @Override
    public void onDisconnect(Player player)
    {
        if(player == null)
        {
            return;
        }
        
        msgRepeatProtection.remove(player);
    }
    
    public void disable()
    {
        if(decayWatcher != null)
        {
            decayWatcher.disable();
        }
    }
    
    class MessageRepeatProtection
    {
        public final int X;
        public final int Y;
        public final int Z;
        
        public final long TIME;
        
        public MessageRepeatProtection(int x, int y, int z, long time)
        {
            this.X = x;
            this.Y = y;
            this.Z = z;
            this.TIME = time;
        }
    }
}
