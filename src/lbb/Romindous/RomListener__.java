package lbb.Romindous;
/*
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;

import org.bukkit.Axis;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import net.kyori.adventure.text.TextComponent;
import net.minecraft.core.BaseBlockPosition;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Timer;

public class RomListener implements Listener {
	
	private static final BlockFace[] nr = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPrtl(final EntityPortalEnterEvent e) {
		if (e.getEntityType() == EntityType.PLAYER && !Rom.prts.isEmpty()) {
			final Location loc = e.getLocation();
			int d = Integer.MAX_VALUE;
			String n = "";
			for (final Entry<BaseBlockPosition, String> en : Rom.prts.entrySet()) {
				final int dd = (int) en.getKey().distanceSquared(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
				if (dd < d) {
					d = dd;
					n = en.getValue();
				}
			}
			final Player p = (Player) e.getEntity();
			if (Timer.has(p, "portal")) {
				return;
		    }
		    Timer.add(p, "portal", 5);
			p.performCommand("server " + n);
		}
	}

	@EventHandler
	public void onBrk(final BlockBreakEvent e) {
		final FileConfiguration cfg = Rom.instance.getConfig();
		final Location loc;
		if (e.getBlock().getType() == Material.NETHER_PORTAL && cfg.isConfigurationSection("prtls")) {
			loc = e.getBlock().getLocation();
			int d = Integer.MAX_VALUE;
			BaseBlockPosition rb = new BaseBlockPosition(0, 0, 0);
			for (final Entry<BaseBlockPosition, String> en : Rom.prts.entrySet()) {
				final int dd = (int) en.getKey().distanceSquared(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
				if (dd < d) {
					d = dd;
					rb = en.getKey();
				}
			}
			
			//убираем из HashMap
			final String pnm = Rom.prts.remove(rb);
		
			//убираем из файла
			if (Rom.prts.isEmpty()) {
				cfg.set("prtls", null);
			} else {
				final StringBuffer nx = new StringBuffer("");
				final StringBuffer ny = new StringBuffer("");
				final StringBuffer nz = new StringBuffer("");
				final StringBuffer ns = new StringBuffer("");
				d = Rom.prts.size();
				for (final Entry<BaseBlockPosition, String> en : Rom.prts.entrySet()) {
					d--;
					nx.append(en.getKey().getX() + (d == 0 ? "" : ":"));
					ny.append(en.getKey().getY() + (d == 0 ? "" : ":"));
					nz.append(en.getKey().getZ() + (d == 0 ? "" : ":"));
					ns.append(en.getValue() + (d == 0 ? "" : ":"));
				}
				cfg.set("prtls.x", nx.toString());
				cfg.set("prtls.y", ny.toString());
				cfg.set("prtls.z", nz.toString());
				cfg.set("prtls.s", ns.toString());
			}
			
			try {
				cfg.save(Rom.instance.getDataFolder() + File.separator + "config.yml");
				e.getPlayer().sendMessage("§6Портал " + pnm + " убран!");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if (e.getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			loc = e.getBlock().getLocation();
			//убираем из HashMap
			final BaseBlockPosition rb = new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if (Rom.plts.remove(rb) != null) {
				//убираем из файла
				if (Rom.plts.isEmpty()) {
					cfg.set("Rom.plts", null);
				} else {
					final StringBuffer nbx = new StringBuffer("");
					final StringBuffer nby = new StringBuffer("");
					final StringBuffer nbz = new StringBuffer("");
					final StringBuffer nex = new StringBuffer("");
					final StringBuffer ney = new StringBuffer("");
					final StringBuffer nez = new StringBuffer("");
		
					int d = Rom.plts.size();
					for (final Entry<BaseBlockPosition, BaseBlockPosition> en : Rom.plts.entrySet()) {
						d--;
						nbx.append(en.getKey().getX() + (d == 0 ? "" : ":"));
						nby.append(en.getKey().getY() + (d == 0 ? "" : ":"));
						nbz.append(en.getKey().getZ() + (d == 0 ? "" : ":"));
						nex.append(en.getValue().getX() + (d == 0 ? "" : ":"));
						ney.append(en.getValue().getY() + (d == 0 ? "" : ":"));
						nez.append(en.getValue().getZ() + (d == 0 ? "" : ":"));
					}
					cfg.set("Rom.plts.bx", nbx.toString());
					cfg.set("Rom.plts.by", nby.toString());
					cfg.set("Rom.plts.bz", nbz.toString());
					cfg.set("Rom.plts.ex", nex.toString());
					cfg.set("Rom.plts.ey", ney.toString());
					cfg.set("Rom.plts.ez", nez.toString());
				}
				
				try {
					cfg.save(Rom.instance.getDataFolder() + File.separator + "config.yml");
					e.getPlayer().sendMessage("§6Плита на коорд. (§7" + rb.getX() + "§6, §7" + rb.getY() + "§6, §7" + rb.getZ() + "§6) убрана!");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@EventHandler
	public void onPlt(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (e.getAction() == Action.PHYSICAL) {
			final Location loc = e.getClickedBlock().getLocation();
			final BaseBlockPosition lp = Rom.plts.get(new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			if (lp != null) {
				e.setCancelled(true);
				loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
				loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
				loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
				p.setGameMode(GameMode.SPECTATOR);
				p.setFlying(true);
				Rom.tps.put(p, lp);
			}
		} else {
			final HashSet<Material> ms = Rom.mts.get(p.getName());
			if (ms != null && e.getClickedBlock() != null) {
				final Material m = e.getClickedBlock().getType();
				if (ms.size() < 50 && ms.add(m)) {
					ApiOstrov.sendTitle(p, "", "§7Найден блок §6" + m.toString().replace('_', ' ').toLowerCase() + "§7, осталось: §6" + (50 - ms.size()));
					//bossbar???
				}
			}
		}
	}

	@EventHandler
	public void onPlc(final BlockPlaceEvent e) {
		final Block b = e.getBlockPlaced();
		if (b.getType() == Material.FIRE) {
			if (plcAtmpt(b, BlockFace.EAST) || plcAtmpt(b, BlockFace.SOUTH)) {
				final ItemStack it = e.getItemInHand();
				if (it != null && it.hasItemMeta()) {
					final String nm = ((TextComponent) it.getItemMeta().displayName()).content();
					final Location loc = b.getLocation();
					Rom.prts.put(new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), nm);
					final FileConfiguration cfg = Rom.instance.getConfig();
					final ConfigurationSection cs = cfg.getConfigurationSection("prtls");
					if (cs == null) {
						cfg.set("prtls.x", loc.getBlockX());
						cfg.set("prtls.y", loc.getBlockY());
						cfg.set("prtls.z", loc.getBlockZ());
						cfg.set("prtls.s", nm);
					} else {
						cs.set("x", cs.getString("x") + ":" + loc.getBlockX());
						cs.set("y", cs.getString("y") + ":" + loc.getBlockY());
						cs.set("z", cs.getString("z") + ":" + loc.getBlockZ());
						cs.set("s", cs.getString("s") + ":" + nm);
					}
					
					try {
						cfg.save(Rom.instance.getDataFolder() + File.separator + "config.yml");
						e.getPlayer().sendMessage("§eПортал " + nm + " создан!");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		} else if (b.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			final Player p = e.getPlayer();
			final Location loc = b.getLocation();
			if (p.hasMetadata("tp")) {
				final FileConfiguration cfg = Rom.instance.getConfig();
				final ConfigurationSection cs = cfg.getConfigurationSection("Rom.plts");
				final BaseBlockPosition fst = (BaseBlockPosition) p.getMetadata("tp").get(0).value();
				if (cs == null) {
					cfg.set("Rom.plts.bx", fst.getX());
					cfg.set("Rom.plts.by", fst.getY());
					cfg.set("Rom.plts.bz", fst.getZ());
					cfg.set("Rom.plts.ex", loc.getBlockX());
					cfg.set("Rom.plts.ey", loc.getBlockY());
					cfg.set("Rom.plts.ez", loc.getBlockZ());
				} else {
					cs.set("bx", cs.getString("bx") + ":" + fst.getX());
					cs.set("by", cs.getString("by") + ":" + fst.getY());
					cs.set("bz", cs.getString("bz") + ":" + fst.getZ());
					cs.set("ex", cs.getString("ex") + ":" + loc.getBlockX());
					cs.set("ey", cs.getString("ey") + ":" + loc.getBlockY());
					cs.set("ez", cs.getString("ez") + ":" + loc.getBlockZ());
				}
				Rom.plts.put(fst, new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				
				try {
					cfg.save(Rom.instance.getDataFolder() + File.separator + "config.yml");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				p.sendMessage("§2Вторая плита поставлена на координатах (§7" + loc.getBlockX() + "§2, §7" + loc.getBlockY() + "§2, §7" + loc.getBlockZ() + "§2)!");
				p.removeMetadata("tp", Rom.instance);
				e.setCancelled(true);
				p.sendMessage("§2Плита создана!");
			} else {
				p.setMetadata("tp", new FixedMetadataValue(Rom.instance, new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
				p.sendMessage("§aПервая плита поставлена на координатах (§7" + loc.getBlockX() + "§a, §7" + loc.getBlockY() + "§a, §7" + loc.getBlockZ() + "§a)!");
			}
		} else if (b.getType() == Material.BEDROCK) {
			Rom.loadCfgs();
			e.getPlayer().sendMessage("§eПерезагружено!");
		}
	}

	@EventHandler
	public void onLeave(final PlayerQuitEvent e) {
		e.getPlayer().removeMetadata("tp", Rom.instance);
	}

	public boolean plcAtmpt(Block b, final BlockFace bf) {
		byte i = 0;
		while (b.getRelative(bf.getOppositeFace()).getType().isAir() && i < 10) {
			i++;
			b = b.getRelative(bf.getOppositeFace());
		}

		byte h = 1;
		byte w = 1;

		hh : while (h < 20) {
			switch (b.getRelative(BlockFace.UP, h).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				h++;
				break;
			default:
				break hh;
			}
		}

		ww : while (w < 20) {
			switch (b.getRelative(bf, w).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				w++;
				break;
			default:
				break ww;
			}
		}
		
		if (bf.getModX() == 0) {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, 0, w - 1), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, w - 1), (byte) 2)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, y, w - 1), (byte) 1)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(0, 0, xz), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, h - 1, xz), (byte) 1)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(0, y, xz);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
				final Orientable or = (Orientable) pb.getBlockData();
				or.setAxis(Axis.Z);
				pb.setBlockData(or);
			}
		} else {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, h - 1, 0), (byte) 3)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(w - 1, y, 0), (byte) 2)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(xz, 0, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(xz, h - 1, 0), (byte) 2)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(xz, y, 0);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
			}
		}
		return true;
	}

	public boolean ntAirMinBlck(final Block b, byte amt) {
		for (final BlockFace bf : RomListener.nr) {
			if (!b.getRelative(bf).getType().isAir()) {
				amt--;
			}
		}
		return amt <= 0;
	}
}
*/