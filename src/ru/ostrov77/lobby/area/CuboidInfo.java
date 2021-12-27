
package ru.ostrov77.lobby.area;

import org.bukkit.Material;


public enum CuboidInfo {
    
    //                                     			slot  	canTp  hidePl  unequpCosmetic
    NEWBIE         (Material.OAK_BOAT,   			0,  	false,  true,   false),
    DEFAULT        (Material.GRAY_DYE,   			0,  	false,  false,  false),
    
    SPAWN          (Material.HEART_OF_THE_SEA,                  22,  	true,  	false,  false),
    CHEST          (Material.ENDER_CHEST,  			31,  	true,  	false,  false),
    MISSION        (Material.GLOBE_BANNER_PATTERN,              23,  	true,  	false,  false),
    PANDORA        (Material.SPONGE,  				21,  	true,  	false,  false),
    MIDGARD        (Material.CAMPFIRE,  			6,  	true,  	false,  false),
    DAARIA         (Material.OAK_LOG,  				25,  	true,  	false,  false),
    ARCAIM         (Material.BEDROCK,  				4,  	true,  	false,  false),
    SUMO           (Material.REDSTONE,  			2,  	true,  	false,  true),
    PVP            (Material.NETHERITE_AXE,                     10,  	true,  	false,  false),
    PARKUR         (Material.FEATHER,  				28,  	true,  	false,  false),
    SEDNA          (Material.CRIMSON_NYLIUM,                    47,  	true,  	false,  false),
    NOPVP          (Material.HONEYCOMB,  			50,  	true,  	false,  false),
    SKYWORLD       (Material.FLOWERING_AZALEA,                  43,  	true,  	false,  false),
    ;


    
    public final Material icon;
    public final int slot;
    public final boolean canTp;
    public final boolean hidePlayers;
    public final boolean unequpCosmetic;
    
    private CuboidInfo (final Material icon, final int slot, final boolean canTp, final boolean hidePlayers, final boolean unequpCosmetic) {
        this.icon = icon;
        this.slot = slot;
        this.canTp = canTp;
        this.hidePlayers = hidePlayers;
        this.unequpCosmetic = unequpCosmetic;
    }
    
    public static CuboidInfo find(final String cuboidName) {
        for (CuboidInfo ci : values()) {
            if (ci.name().equalsIgnoreCase(cuboidName)) {
                return ci;
            }
        }
        return DEFAULT;
    }
}
