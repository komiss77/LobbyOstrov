package ru.ostrov77.lobby.bots.spots;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import ru.komiss77.modules.world.BVec;
import ru.komiss77.version.Nms;

public class EndSpot implements Spot {
	
	private final BVec loc;
	private final SpotType st;
	private final BVec prtl;
	private final World w;
	
	private static final int prtlDst = 3;
	
	public EndSpot(final BVec loc) {
		this.loc = loc;
		this.st = SpotType.END;
		final World tw = loc.w();
		this.w = tw == null ? Bukkit.getWorlds().getFirst() : tw;
		BVec portal = null;
		int dst = Integer.MAX_VALUE;
		for (int x = -prtlDst; x < prtlDst + 1; x++) {
			for (int z = -prtlDst; z < prtlDst + 1; z++) {
				for (int y = -prtlDst; y < prtlDst + 1; y++) {
					final BVec tst = BVec.of(w, loc.x + x, loc.y + y, loc.z + z);
					if (BlockType.NETHER_PORTAL.equals(Nms.fastType(w, tst.x, tst.y, tst.z))) {
						final int d = Math.abs(x) + Math.abs(y) + Math.abs(z);
						if (d < dst) {
							portal = tst;
							dst = d;
						}
					}
				}
			}
		}
		this.prtl = portal;
	}
	
	public BVec getPortal() {
		return prtl;
	}

	@Override
	public BVec getLoc() {
		return loc;
	}

	@Override
	public SpotType getType() {
		return st;
	}

	@Override
	public World getWorld() {
		return w;
	}
}
