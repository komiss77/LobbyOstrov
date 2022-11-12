
package ru.ostrov77.lobby.area;

import org.bukkit.Material;



public enum CuboidInfo {
    
    //                                     			slot  	canTp  hidePl  unequpCosmetic
    NEWBIE         (Material.OAK_BOAT,   			0,  	false,  true,       false,      0f, 0f),
    DEFAULT        (Material.GRAY_DYE,   			0,  	false,  false,      false,      0f, 0f),
    
    SPAWN          (Material.HEART_OF_THE_SEA,                  22,  	true,  	false,      false,      0f, -12f),
    CHEST          (Material.ENDER_CHEST,  			31,  	true,  	false,      false,      0f, 12f),
    MISSION        (Material.GLOBE_BANNER_PATTERN,              23,  	true,  	false,      false,      24f, 0f),
    PANDORA        (Material.SPONGE,  				21,  	true,  	false,      false,      -24f, -4f),
    ARCAIM         (Material.BEDROCK,  				4,  	true,  	false,      false,      16f, -36f),
    MIDGARD        (Material.CAMPFIRE,  			6,  	true,  	false,      false,      36f, -24f),
    DAARIA         (Material.OAK_LOG,  				25,  	true,  	false,      false,      48f, -4f),
    SKYWORLD       (Material.FLOWERING_AZALEA,                  43,  	true,  	false,      false,      42f, 16f),
    NOPVP          (Material.HONEYCOMB,  			50,  	true,  	false,      false,      12f, 24f),
    SEDNA          (Material.CRIMSON_NYLIUM,                  47,  	true,  	false,      false,      -28f, -16f),
    PARKUR         (Material.FEATHER,  				28,  	true,  	false,      false,      -48f, 0f),
    PVP            (Material.NETHERITE_AXE,                     10,  	true,  	false,      false,      -32f, -24f),
    SUMO           (Material.REDSTONE,  			2,  	true,  	false,      true,       -12f, -32f),
    ;


    
    public final Material icon;
    public final int slot;
    public final boolean canTp;
    public final boolean hidePlayers;
    public final boolean unequpCosmetic;
    public final float relYaw;
    public final float relPitch;
    
    private CuboidInfo (final Material icon, final int slot, final boolean canTp, final boolean hidePlayers, final boolean unequpCosmetic, final float relYaw, final float relPitch) {
        this.icon = icon;
        this.slot = slot;
        this.canTp = canTp;
        this.hidePlayers = hidePlayers;
        this.unequpCosmetic = unequpCosmetic;
        this.relYaw = relYaw;
        this.relPitch = relPitch;
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
/*public enum CuboidInfo {
    
    //                                     			slot  	canTp  hidePl  unequpCosmetic
    NEWBIE         (Material.OAK_BOAT,   			0,  	false,  true,       false,      0, 0),
    DEFAULT        (Material.GRAY_DYE,   			0,  	false,  false,      false,      0, 0),
    
    SPAWN          (Material.HEART_OF_THE_SEA,                  22,  	true,  	false,      false,      0, 0),
    CHEST          (Material.ENDER_CHEST,  			31,  	true,  	false,      false,      -1, 0),
    MISSION        (Material.GLOBE_BANNER_PATTERN,              23,  	true,  	false,      false,      0, -1),
    PANDORA        (Material.SPONGE,  				21,  	true,  	false,      false,      0, 1),
    MIDGARD        (Material.CAMPFIRE,  			6,  	true,  	false,      false,      1, 0),
    DAARIA         (Material.OAK_LOG,  				25,  	true,  	false,      false,      -1, -1),
    ARCAIM         (Material.BEDROCK,  				4,  	true,  	false,      false,      1, 1),
    SUMO           (Material.REDSTONE,  			2,  	true,  	false,      true,       0, 2),
    PVP            (Material.NETHERITE_AXE,                     10,  	true,  	false,      false,      0, -2),
    PARKUR         (Material.FEATHER,  				28,  	true,  	false,      false,      1, 2),
    SEDNA          (Material.CRIMSON_NYLIUM,                    47,  	true,  	false,      false,      1, -2),
    NOPVP          (Material.HONEYCOMB,  			50,  	true,  	false,      false,      -1, 2),
    SKYWORLD       (Material.FLOWERING_AZALEA,                  43,  	true,  	false,      false,      -1, -2),
    ;


    
    public final Material icon;
    public final int slot;
    public final boolean canTp;
    public final boolean hidePlayers;
    public final boolean unequpCosmetic;
    public final int v;
    public final int h;
    
    private CuboidInfo (final Material icon, final int slot, final boolean canTp, final boolean hidePlayers, final boolean unequpCosmetic, final int v, final int h) {
        this.icon = icon;
        this.slot = slot;
        this.canTp = canTp;
        this.hidePlayers = hidePlayers;
        this.unequpCosmetic = unequpCosmetic;
        this.v = v;
        this.h = h;
    }
    
    public static CuboidInfo find(final String cuboidName) {
        for (CuboidInfo ci : values()) {
            if (ci.name().equalsIgnoreCase(cuboidName)) {
                return ci;
            }
        }
        return DEFAULT;
    }
    
    
}*/
