package ru.ostrov77.lobby.game;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;

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
		p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z).setType(Material.AIR, false);
		bLast = bNext;
		jumps++;
		final Block b = p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z);
		final BlockFace sd = Parkur.sds[Ostrov.random.nextInt(4)];
		final Block n;
		switch (jumps >> 3) {
		case 0:
			n = ApiOstrov.randBoolean() ? 
				b.getRelative(sd, 3).getRelative(sd.getModZ() == 0 ? 
					(ApiOstrov.randBoolean() ? BlockFace.NORTH : BlockFace.SOUTH) 
					: 
					(ApiOstrov.randBoolean() ? BlockFace.WEST : BlockFace.EAST)) 
				: 
				b.getRelative(sd, 2).getRelative(BlockFace.UP);
			n.setType(Material.LIME_CONCRETE, false);
			break;
		case 1:
			n = ApiOstrov.randBoolean() ? 
					b.getRelative(sd, 4).getRelative(sd.getModZ() == 0 ? 
						(ApiOstrov.randBoolean() ? BlockFace.NORTH : BlockFace.SOUTH) 
						: 
						(ApiOstrov.randBoolean() ? BlockFace.WEST : BlockFace.EAST)) 
					: 
					b.getRelative(sd, 3).getRelative(BlockFace.UP);
				n.setType(Material.YELLOW_CONCRETE, false);
			break;
		case 2:
		default:
			n = ApiOstrov.randBoolean() ? 
					b.getRelative(sd, 5).getRelative(sd.getModZ() == 0 ? 
						(ApiOstrov.randBoolean() ? BlockFace.NORTH : BlockFace.SOUTH) 
						: 
						(ApiOstrov.randBoolean() ? BlockFace.WEST : BlockFace.EAST)) 
					: 
					b.getRelative(sd, 4).getRelative(BlockFace.UP);
				n.setType(Material.ORANGE_CONCRETE, false);
			break;
		}
		bNext = new XYZ(n.getLocation());
		
            if (bNext.y > 250) {
                p.sendMessage("§7[§bМини-Паркур§7] >> Вы... прошли до конца??! Пропрыгано блоков: §b" + jumps);
                QuestManager.tryCompleteQuest(p, Main.getLobbyPlayer(p), Quest.MiniPark);
                p.getWorld().getBlockAt(bLast.x, bLast.y, bLast.z).setType(Material.AIR, false);
                p.getWorld().getBlockAt(bNext.x, bNext.y, bNext.z).setType(Material.AIR, false);
                p.teleport(AreaManager.getCuboid("parkur").spawnPoint);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1.2f);
                //Main.miniParks.remove(this);
                Main.getLobbyPlayer(p).pkrist = null;
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
