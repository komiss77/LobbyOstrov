
package ru.ostrov77.lobby.area;

import org.bukkit.Material;


public enum CuboidInfo {
    
    //                                      slot  canTp hidden  unequpCosmetic
    NEWBIE          (Material.ACACIA_BOAT,   1,  true,   true,   true),
    ;


    
    public final Material icon;
    public final int slot;
    public final boolean canTp;
    public final boolean hidden;
    public final boolean unequpCosmetic;
    
    private CuboidInfo (final Material icon, final int slot, final boolean canTp, final boolean hidden, final boolean unequpCosmetic) {
        this.icon = icon;
        this.slot = slot;
        this.canTp = canTp;
        this.hidden = hidden;
        this.unequpCosmetic = unequpCosmetic;
    }
    
    public static CuboidInfo find(final String cuboidName) {
        for (CuboidInfo ci : values()) {
            if (ci.name().equalsIgnoreCase(cuboidName)) {
                return ci;
            }
        }
        return null;
    }    
}
