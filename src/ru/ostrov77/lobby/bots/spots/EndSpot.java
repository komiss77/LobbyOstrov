package ru.ostrov77.lobby.bots.spots;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.version.Nms;
import ru.ostrov77.lobby.area.AreaManager;

public class EndSpot implements Spot {
	
	private final XYZ loc;
	private final SpotType st;
	private final XYZ prtl;
	private final World w;
	
	private static final int prtlDst = 3;
	
	public EndSpot(final XYZ loc) {
		this.loc = loc;
		this.st = SpotType.END;
		this.w = Bukkit.getWorld(loc.worldName);
		XYZ portal = null;
		int dst = Integer.MAX_VALUE;
		for (int x = -prtlDst; x < prtlDst + 1; x++) {
			for (int z = -prtlDst; z < prtlDst + 1; z++) {
				for (int y = -prtlDst; y < prtlDst + 1; y++) {
					final XYZ tst = new XYZ(loc.worldName, loc.x + x, loc.y + y, loc.z + z);
					if (Nms.getFastMat(w, tst.x, tst.y, tst.z) == Material.NETHER_PORTAL) {
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
	
	public XYZ getPortal() {
		return prtl;
	}

	@Override
	public XYZ getLoc() {
		return loc;
	}

	@Override
	public SpotType getType() {
		return st;
	}
	
	@Override
	public boolean equals(final Object o) {
		return o instanceof Spot && loc.equals(((Spot)o).getLoc());
	}

	@Override
	public int hashCode() {
		return AreaManager.getcLoc(loc);
	}
	
	@Override
	public String toString() {
		return loc.toString() + ", t-" + st.toString();
	}

	@Override
	public World getWorld() {
		return w;
	}
}
