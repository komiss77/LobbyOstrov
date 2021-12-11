package ru.ostrov77.lobby;

import org.bukkit.Location;
import ru.komiss77.Ostrov;


public class XYZ {

    public static XYZ fromString(final String asString) {
        final String[] split = asString.split(",");
        try {
            final XYZ xyz= new XYZ (split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            return xyz;
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            Ostrov.log_err("XYZ fromString  ="+asString+" "+ex.getMessage());
            return null;
        }    
    }
    
    public final String worldName;
    public final int x;
    public final int y;
    public final int z;
    
    
    public XYZ(final Location loc) {
        worldName = loc.getWorld().getName();
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
    }

    XYZ(final String worldName, final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public boolean nearly(final Location loc) {
        return worldName.equals(loc.getWorld().getName()) && square(loc.getBlockX()-x) + square(loc.getBlockY()-y) + square(loc.getBlockZ()-z) < 16; //число подобрать точнее!
    }
    
    private static int square(final int num) {
        return num * num;
    }
    
    @Override
    public String toString() {
        return worldName+","+x+","+y+","+z;
    }
    
}
