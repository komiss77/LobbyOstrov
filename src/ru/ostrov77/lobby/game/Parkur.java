package ru.ostrov77.lobby.game;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.NumUtil;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.quest.Quests;

public class Parkur {

	public final Player p;
	public XYZ bLast;
	public XYZ bNext;
	public int jumps;
	
	public static BlockFace[] sds = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH ,BlockFace.SOUTH};
	
	public Parkur(final Player p) {
		this.p = p;
		jumps = 0;
	}
	
	public void nextBlock() {
		p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z).setBlockData(BlockType.AIR.createBlockData(), false);
		bLast = bNext;
		jumps++;
		final Block b = p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z);
		final BlockFace sd = Parkur.sds[Ostrov.random.nextInt(4)];
		final Block n;
		switch (jumps >> 3) {
		case 0:
			n = NumUtil.rndBool() ?
				b.getRelative(sd, 3).getRelative(sd.getModZ() == 0 ? 
					(NumUtil.rndBool() ? BlockFace.NORTH : BlockFace.SOUTH) 
					: 
					(NumUtil.rndBool() ? BlockFace.WEST : BlockFace.EAST)) 
				: 
				b.getRelative(sd, 2).getRelative(BlockFace.UP);
			n.setBlockData(BlockType.LIME_CONCRETE.createBlockData(), false);
			break;
		case 1:
			n = NumUtil.rndBool() ? 
					b.getRelative(sd, 4).getRelative(sd.getModZ() == 0 ? 
						(NumUtil.rndBool() ? BlockFace.NORTH : BlockFace.SOUTH) 
						: 
						(NumUtil.rndBool() ? BlockFace.WEST : BlockFace.EAST)) 
					: 
					b.getRelative(sd, 3).getRelative(BlockFace.UP);
				n.setBlockData(BlockType.YELLOW_CONCRETE.createBlockData(), false);
			break;
		case 2:
		default:
			n = NumUtil.rndBool() ? 
					b.getRelative(sd, 5).getRelative(sd.getModZ() == 0 ? 
						(NumUtil.rndBool() ? BlockFace.NORTH : BlockFace.SOUTH) 
						: 
						(NumUtil.rndBool() ? BlockFace.WEST : BlockFace.EAST)) 
					: 
					b.getRelative(sd, 4).getRelative(BlockFace.UP);
				n.setBlockData(BlockType.ORANGE_CONCRETE.createBlockData(), false);
			break;
		}
		bNext = new XYZ(n.getLocation());
		
        if (bNext.y > 250) {
            p.sendMessage("§7[§bМини-Паркур§7] >> Вы... прошли до конца??! Пропрыгано блоков: §b" + jumps);
            final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
			Quests.parkur.complete(p, lp, false);
            p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z).setBlockData(BlockType.AIR.createBlockData(), false);
            p.getWorld().getBlockAt(bNext.x, bNext.y, bNext.z).setBlockData(BlockType.AIR.createBlockData(), false);
            p.teleport(AreaManager.getCuboid("parkur").spawnPoint);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1.2f);
            //Main.miniParks.remove(this);
            lp.pkrist = null;
        }
	}

	/*public static PKrist getPK(final String name) {
		for (final PKrist pr : Main.miniParks) {
			if (pr.p.getName().equals(name)) {
				return pr;
			}
		}
		return null;
	}*/
}
