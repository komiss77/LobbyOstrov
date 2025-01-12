package ru.ostrov77.lobby.bots;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import ru.komiss77.modules.bots.BotManager;
import ru.komiss77.modules.bots.Botter;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.ClassUtil;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.bots.spots.*;


public class SpotManager {
	
    private static final List<Spot> spots;
    public static final String[] names;
    private static BukkitTask task;
    
    static {
        spots = new ArrayList<>();
        names = readNames();
        startTask();
    }

    public static final LobbyBot BOT_EXT = new LobbyBot();

    public static void startTask() {
        if (task!=null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            final Spot sp = getRndSpot(SpotType.SPAWN);
            if (sp != null) {
            	final int pls = Bukkit.getOnlinePlayers().size();
            	if (pls != 0 && Main.rnd.nextInt(pls + 1) == 0) {
                    final Location loc = new WXYZ(sp.getLoc()).getCenterLoc();
                    final Botter bt = BotManager.createBot(ClassUtil.rndElmt(names), loc.getWorld(), BOT_EXT);
                    bt.telespawn(null, loc);
                }
            }
        }, 200, 200);
    }

    public static void addSpot(final XYZ loc, final SpotType st) {
        final Spot sp = switch (st) {
            case END -> new EndSpot(loc);
            case SPAWN -> new SpawnSpot(loc);
            case WALK -> new WalkSpot(loc);
        };
        AreaManager.saveSpot(loc, st);
        spots.add(sp);
    }

    public static void deleteSpot(final XYZ loc) {
        spots.remove(new WalkSpot(loc));
        AreaManager.saveSpot(loc, null);
        if (spots.isEmpty()) {
            return;
        }
        Bukkit.broadcast(Component.text("-=-=-=-=-=-=-=-=-"));
        for (final Spot sp : spots) {
            final BlockType mt = switch (sp.getType()) {
                case END -> BlockType.FIRE_CORAL;
                case SPAWN -> BlockType.BRAIN_CORAL;
                case WALK -> BlockType.TUBE_CORAL;
            };
            final Waterlogged wl = (Waterlogged) mt.createBlockData();
            wl.setWaterlogged(false);
            sp.getLoc().getCenterLoc().getBlock().setBlockData(wl, false);
        }
    }
    
    public static @Nullable Spot getRndSpot(final SpotType tp) {
    	final ArrayList<Spot> spa = new ArrayList<>();
    	for (final Spot sp : spots) {
    		if (sp.getType() == tp) spa.add(sp);
    	}
		return spa.isEmpty() ? null :
            spa.get(Main.rnd.nextInt(spa.size()));
    }

    public static String[] readNames() {
        final File fl = new File(Bukkit.getPluginsFolder().getAbsolutePath() + File.separator + "LobbyOstrov" + File.separator + "names.txt");
        if (fl.exists()) {
            try {
                return Files.readAllLines(Path.of(fl.getAbsolutePath())).toArray(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return new String[]{"Bot"};
            }
        }
        return new String[]{"Bot"};
    }
 

}
   


//    public static final HashMap<Integer, Bot> npcs = new HashMap<>();
//
//    //заглушки 
//    public static void addSpot(XYZ fromString, SpotType valueOf) {
//    }
//
//    public static void removePlayer(Player pl) {
//    }
//
//    public static void clearBots() {
//    }
//    public static void injectPlayer(Player p) {
//    }
//
//    public static Spot getRndSpawnSpot() {
//        return null;
//    }
//
//    public static void deleteSpot(XYZ xyz) {
//    }

/*
    protected static final List<Spot> spots = new ArrayList<>();

    protected static final String[] names = readNames();

    public static final Method getEnt = mkGet(".entity.CraftLivingEntity");
    public static final Method getWrld = mkGet(".CraftWorld");

    private static Method mkGet(final String pth) {
        try {
            return Class.forName(Bukkit.getServer().getClass().getPackageName() + pth).getDeclaredMethod("getHandle");
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BotManager() {

        BotType.values();

        for (final Player pl : Bukkit.getOnlinePlayers()) {
            injectPlayer(pl);
        }

        Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            final Spot sp = BotManager.getRndSpawnSpot();
            if (sp != null) {
                for (int i = Bukkit.getOnlinePlayers().size() - 1; i >= 0; i--) {
                    new Bot(sp, BotType.REGULAR);
                }

                final Iterator<Bot> it = npcs.values().iterator();
                while (it.hasNext()) {
                    final Bot bt = it.next();
                    if (bt.rplc == null || !bt.rplc.isValid()) {
                        bt.remove(false, false);
                        it.remove();
                    }
                }
            }
        }, 200, 2000);

        /*new BukkitRunnable() {
			
			@Override
			public void run() {
				final Iterator<Bot> it = npcs.values().iterator();
				while (it.hasNext()) {
					final Bot bt = it.next();
					if (bt.tick()) {
						bt.remove(true);
						it.remove();
					}
				}
			}
		}.runTaskTimer(Main.instance, 20, 2);** /
    }

    public static String[] readNames() {
        final File fl = new File(Bukkit.getPluginsFolder().getAbsolutePath() + File.separator + "LobbyOstrov" + File.separator + "names.txt");
        if (fl.exists()) {
            try {
                return Files.readAllLines(Path.of(fl.getAbsolutePath())).toArray(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return new String[]{""};
            }
        }
        return new String[]{""};
    }

    public static Player getNrPl(final World w, final BlockPosition bp, final int dst) {
        Player p = null;
        int dd = Integer.MAX_VALUE;
        for (final Player pl : w.getPlayers()) {
            final Location l = pl.getLocation();
            final int d = Math.abs(l.getBlockX() - bp.u()) + Math.abs(l.getBlockY() - bp.v()) + Math.abs(l.getBlockZ() - bp.w());
            if (d < dd && d < dst) {
                p = pl;
            }
        }
        return p;
    }

    public static LivingEntity getNrLent(final World w, final BlockPosition bp, final int dst, final boolean invTgt, final EntityType[] tgts) {
        LivingEntity lent = null;
        int dd = Integer.MAX_VALUE;
        if (invTgt) {
            boolean isNot = true;
            for (final LivingEntity le : w.getLivingEntities()) {
                final Location l = le.getLocation();
                final int d = Math.abs(l.getBlockX() - bp.u()) + Math.abs(l.getBlockY() - bp.v()) + Math.abs(l.getBlockZ() - bp.w());
                if (d < dd && d < dst && !npcs.containsKey(le.getEntityId()) && le.isOnGround()) {
                    final EntityType et = le.getType();
                    for (final EntityType e : tgts) {
                        if (et == e) {
                            isNot = false;
                            break;
                        }
                    }
                    if (isNot) {
                        dd = d;
                        lent = le;
                    }
                    isNot = true;
                }
            }
        } else {
            for (final LivingEntity le : w.getLivingEntities()) {
                final Location l = le.getLocation();
                final int d = Math.abs(l.getBlockX() - bp.u()) + Math.abs(l.getBlockY() - bp.v()) + Math.abs(l.getBlockZ() - bp.w());
                if (d < dd && d < dst && !npcs.containsKey(le.getEntityId()) && le.isOnGround()) {
                    final EntityType et = le.getType();
                    for (final EntityType e : tgts) {
                        if (et == e) {
                            dd = d;
                            lent = le;
                            break;
                        }
                    }
                }
            }
        }
        return lent;
    }

    public static final Packet<?>[] sbd = pcktGet();

    private static Packet<?>[] pcktGet() {
        final Packet<?>[] ps = new Packet<?>[4];
        final ScoreboardServer ss = Main.ds.aF();
        final ScoreboardTeam st = ss.g(Bot.nm);
        st.a(EnumNameTagVisibility.b);
        ps[0] = PacketPlayOutScoreboardTeam.a(st);
        ps[1] = PacketPlayOutScoreboardTeam.a(st, true);
        ps[2] = PacketPlayOutScoreboardTeam.a(st, Bot.nm, a.a);
        ps[3] = PacketPlayOutScoreboardTeam.a(st, false);
        ss.d(st);
        return ps;
    }

    /*public static final Method getItm = itGet();
	private static Method itGet() {net.minecraft.world.item.ItemStack.
		try {
			Bukkit.broadcast(Component.text(Arrays.toString(Class.forName(Bukkit.getServer().getClass().getPackageName() + ".inventory.CraftItemStack").getMethods())));
			return Class.forName(Bukkit.getServer().getClass().getPackageName() + ".inventory.CraftItemStack").getMethod("asNMSCopy");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}* /
    public static final Field id = getIdFld();

    private static Field getIdFld() {
        final Field fld = PacketPlayInUseEntity.class.getDeclaredFields()[0];
        fld.setAccessible(true);
        return fld;
    }

    public static void sendWrldPckts(final net.minecraft.world.level.World w, final Packet<?>... ps) {
        for (final EntityHuman e : w.w()) {
            if (e instanceof EntityPlayer) {
                final NetworkManager nm = ((EntityPlayer) e).networkManager;
                for (final Packet<?> p : ps) {
                    nm.a(p);
                }
            }
        }
    }

    public static void removePlayer(final Player p) {
        final Channel channel = Main.ds.bh().a(p.getName()).b.b.m;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }

    public static void injectPlayer(final Player p) {
    	final NetworkManager nm = Main.ds.bh().a(p.getName()).networkManager;
    	try {
        	nm.m.pipeline().addBefore("packet_handler", p.getName(), new ChannelDuplexHandler() {
                @Override
                public void channelRead(final ChannelHandlerContext chc, final Object packet) throws Exception  {
                	if (packet instanceof PacketPlayInUseEntity) {
                		final PacketPlayInUseEntity uep = (PacketPlayInUseEntity) packet;
                		if (uep.getActionType() == b.b) {
                			for (final Bot bt : BotManager.npcs.values()) {
                				if (bt.ae() == uep.getEntityId()) {
                    				bt.hurt(p);
                    				id.set(uep, bt.rid);
                    				break;
                				}
                			}
                		}
                	}
                    super.channelRead(chc, packet);
                }
                
    			@Override
                public void write(final ChannelHandlerContext chc, final Object packet, final ChannelPromise channelPromise) throws Exception {
    			if (packet instanceof PacketPlayOutSpawnEntity) {
    				if (BotManager.npcs.get(((PacketPlayOutSpawnEntity) packet).b()) != null) return;
    			}
                    super.write(chc, packet, channelPromise);
                }
            });
		} catch (IllegalArgumentException e) {
			//хз смысла фиксить нет
			Ostrov.log_warn("Уже подключен игрок " + p.getName());
		}
    	for (final Bot bt : npcs.values()) {
    		bt.updateAll(nm);
    	}
    }

    public static net.minecraft.world.item.ItemStack getItem(final ItemStack it) {
        return net.minecraft.world.item.ItemStack.fromBukkitCopy(it);
    }

    public static WorldServer getNMSWrld(final org.bukkit.World w) {
        try {
            return (WorldServer) getWrld.invoke(w);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }

    public static EntityLiving getNMSLEnt(final LivingEntity tgt) {
        try {
            return (EntityLiving) getEnt.invoke(tgt);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }

    public static void addSpot(final XYZ loc, final SpotType st) {
        final Spot sp;
        switch (st) {
            case END:
                sp = new EndSpot(loc);
                break;
            case SPAWN:
                sp = new SpawnSpot(loc);
                break;
            case WALK:
            default:
                sp = new WalkSpot(loc);
                break;
        }
        AreaManager.saveSpot(loc, st);
        spots.add(sp);
    }

    public static void deleteSpot(final XYZ loc) {
        spots.remove(new WalkSpot(loc));
        AreaManager.saveSpot(loc, null);
        if (spots.isEmpty()) {
            return;
        }
        Bukkit.broadcast(Component.text("-=-=-=-=-=-=-=-=-"));
        for (final Spot sp : spots) {
            final ItemType mt;
            switch (sp.getType()) {
                case END:
                    mt = ItemType.FIRE_CORAL;
                    break;
                case SPAWN:
                    mt = ItemType.BRAIN_CORAL;
                    break;
                case WALK:
                default:
                    mt = ItemType.TUBE_CORAL;
                    break;
            }
            final Waterlogged wl = (Waterlogged) mt.createBlockData();
            wl.setWaterlogged(false);
            sp.getLoc().getCenterLoc().getBlock().setBlockData(wl, false);
            Bukkit.broadcast(Component.text(sp.toString()));
        }
        Bukkit.broadcast(Component.text("=-=-=-=-=-=-=-=-="));
    }

    public static void showSpots() {
    }

    public static Spot getRndSpawnSpot() {
        Collections.shuffle(spots, Main.rnd);
        for (final Spot sp : spots) {
            if (sp.getType() == SpotType.SPAWN) {
                return sp;
            }
        }
        return null;
    }

    public static Spot getCloseSpot(final Location loc, final List<Spot> old) {
        final List<Spot> sps = new ArrayList<>();
        sps.addAll(spots);
        sps.removeAll(old);
        Collections.shuffle(sps, Main.rnd);
        int num = sps.size() >> 1;
        int dst = Integer.MAX_VALUE;
        Spot fsp = null;
        for (final Spot sp : sps) {
            final int d = sp.getLoc().getDistance(loc);
            if (d < dst) {
                dst = d;
                fsp = sp;
            }
            if ((num--) == 0) {
                return fsp;
            }
        }
        return fsp;
    }

    public static void clearBots() {
        final HashMap<Integer, Bot> ns = new HashMap<>();
        ns.putAll(npcs);
        for (final Bot bt : ns.values()) {
            bt.remove(false, false);
        }
        npcs.clear();
    }

    public static void updateBots(final Player p) {
        final EntityPlayer pl = Main.ds.bh().a(p.getName());
        final String wn = p.getWorld().getName();
        final NetworkManager nm = pl.networkManager;
        for (final Bot bt : BotManager.npcs.values()) {
            if (bt.s.getWorld().getName().equals(wn)) {
                p.sendMessage("updating bot-" + bt.rid);
                bt.rplc.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));
                bt.updateAll(nm);
            }
        }
    }*/
