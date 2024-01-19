
package ru.ostrov77.lobby.area;
import org.bukkit.Material;
import ru.komiss77.enums.Game;
import ru.komiss77.modules.quests.Quest;
import ru.ostrov77.lobby.quest.Quests;


public enum CuboidInfo {
    
    //                                     		        slot    	canTp       quest			hidePl  	unequpCosmetic
    NEWBIE         (Material.OAK_BOAT,   		    0,  	false,  Quests.newbie,	null,   true,   false,      0f, 0f, 0f),
    DEFAULT        (Material.GRAY_DYE,   		    0,  	false,  Quests.newbie,	null,   false,  false,      0f, 0f, 0f),
    
    SPAWN          (Material.HEART_OF_THE_SEA,      22,  	true,  	Quests.ostrov,	Game.LOBBY,   false,  false,      0f, 0f, 0f),
    CHEST          (Material.ENDER_CHEST,  		    31,  	true,  	Quests.ostrov,	null,   false,  false,      0f, 0f, -1.6f),
    MISSION        (Material.GLOBE_BANNER_PATTERN,  23,  	true,  	Quests.ostrov,	null,   false,  false,      -0.6f, -0.6f, 1.6f),
    PANDORA        (Material.SPONGE, 		        21,  	true,  	Quests.ostrov,	null,   false,  false,      1.2f, -0.4f, 1.2f),
    ARCAIM         (Material.BEDROCK, 		        4,  	true,  	Quests.arcaim,	    Game.AR,   false,  false,      1.4f, 0f, 3.2f),
    MIDGARD        (Material.LECTERN,		        6,  	true,  	Quests.midgard,	    Game.FA,   false,  false,      -1.4f, 0.4f, 3.2f),
    DAARIA         (Material.OAK_LOG,  		        25,  	true,  	Quests.daaria,	    Game.DA,   false,  false,      -3.2f, 0f, 1.4f),
    SKYWORLD       (Material.FLOWERING_AZALEA,      43,  	true,  	Quests.skyworld,    Game.SK,   false,  false,      -3.2f, 0.4f, -1.4f),
    SEDNA          (Material.CRIMSON_NYLIUM,        47,  	true,  	Quests.sedna,	    Game.SE,   false,  false,      1.4f, 0.4f, -3.2f),
    PARKUR         (Material.WHITE_WOOL,    	    28,  	true,  	Quests.parkur,	    Game.PA,   false,  false,      3.2f, 0.8f, -1.4f),
    NOPVP          (Material.HONEY_BLOCK,   	    50,  	true,  	Quests.nopvp,   null,   false,  false,      -1.4f, -0.4f, -3.2f),
    PVP            (Material.NETHERITE_AXE,         10,  	true,  	Quests.pvp,		null,   false,  false,      3.2f, -0.6f, 1.4f),
    SUMO           (Material.REDSTONE,  		    2,  	true,  	Quests.ostrov,	null,   false,  true,       2.4f, -0.2f, 2.0f),
    ;
    
    public final Material icon;
    public final int slot;
    public final Quest quest;
    public final Game game;
    public final boolean canTp;
    public final boolean hidePlayers;
    public final boolean unequpCosmetic;
    public final float relX;
    public final float relY;
    public final float relZ;
    
    CuboidInfo(final Material icon, final int slot, final boolean canTp, final Quest quest, final Game game,
        final boolean hidePlayers, final boolean unequpCosmetic, final float relX, final float relY, final float relZ) {
        this.icon = icon;
        this.slot = slot;
        this.quest = quest;
        this.game = game;
        this.canTp = canTp;
        this.hidePlayers = hidePlayers;
        this.unequpCosmetic = unequpCosmetic;
        this.relX = relX;
        this.relY = relY;
        this.relZ = relZ;
    }
    
    public static CuboidInfo find(final String cuboidName) {
    	try {return CuboidInfo.valueOf(cuboidName.toUpperCase());} 
    	catch (IllegalArgumentException e) {return DEFAULT;}
    }
    
    
}
