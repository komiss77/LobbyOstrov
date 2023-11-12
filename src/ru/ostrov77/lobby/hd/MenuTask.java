package ru.ostrov77.lobby.hd;

class MenuTask {//implements Runnable {
	/*

    protected BukkitTask task;
    protected int tick;
    protected final Player p;
    //protected final String name;
    protected final Position center;
    //protected final String worldName;
    //protected final double x,y,z;
    protected final EnumMap<CuboidInfo,Hologram> holo;
    private final LobbyPlayer lp;
    
    public MenuTask (final Player p, final Position center, final EnumMap<CuboidInfo,Hologram> holo) {
        this.p = p;
        this.holo = holo;
        this.center = center;
        //name = p.getName();
        lp = Main.getLobbyPlayer(p);
        //worldName = p.getWorld().getName();
       // x = center.getX();
        //y = center.getY();
        //z = center.getZ();
        
        //task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, this, 1, 10); HolographicDisplays ConcurrentModificationException
        task = Bukkit.getScheduler().runTaskTimer(Main.instance, this, 4, 8);
        p.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, .5f, 2);
    }
    

    
    
    
    
    
    @Override
    public void run() {
        
        //final Player p = Bukkit.getPlayerExact(name);
        if (p==null || !p.isOnline()) {
            cancel();
            return;
        }
        
        if (p.isDead() || p.isSneaking() || isAway()) {
            cancel();
            p.playSound(center.toLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, .5f, 2);
            return;
        }
        
        
        final Location eye = p.getEyeLocation();
        
        
        Hologram h;
        LCuboid lc;
        Vector v;
        
        if (tick > 1) { //смотрим куда навёлся
            
            v = new Vector(-Math.sin(Math.toRadians(eye.getYaw())), -Math.sin(Math.toRadians(eye.getPitch())), Math.cos(Math.toRadians(eye.getYaw())));
            eye.setY(eye.getY() + 0.35d);
            
            double dx, dy, dz;
            
            for (CuboidInfo ci : holo.keySet()) {
                
                h = holo.get(ci);
                //final Location dir = h.getPosition().toLocation().subtract(eye);
                dx = h.getPosition().getX() - eye.getX();
                dy = h.getPosition().getY() - eye.getY();
                dz = h.getPosition().getZ() - eye.getZ();
                
                //if (Math.abs(dir.getX() / v.getX() - dir.getZ() / v.getZ()) < 0.6d && Math.abs(Math.tan(Math.toRadians(-eye.getPitch())) * Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()) - dir.getY()) < 0.6d) {
                //определить смотрит ли на голограмму 
                if ( Math.abs(dx / v.getX() - dz / v.getZ()) < 0.6d  &&  Math.abs(Math.tan(Math.toRadians(-eye.getPitch())) * Math.sqrt(dx * dx + dz * dz) - dy) < 0.6d ) {
                    
                    if (h.getLines().size() == 1) {
                        lc = AreaManager.getCuboid(ci);
                        if (lp.isAreaDiscovered(lc.id)) {
                            h.getLines().appendText(lc.displayName);
                            ApiOstrov.sendActionBarDirect(p, "§a[Клик]§f - ТП");
                        } else {
                            h.getLines().appendText("§7*???*");
                            if(lp.compasstarget==ci) {
                                ApiOstrov.sendActionBarDirect(p, "§5[Клик]§f - Сброс Компаса");
                            } else {
                                ApiOstrov.sendActionBarDirect(p, "§7(не изучено) §d[Клик]§f - Навести Компас");
                            }
                        }
                    
                    }
                } else if (h.getLines().size() == 2) {
                    
                    h.getLines().remove(1);
                    
                }
            }
            
            
            
        } else   if (tick==1) { //расстановка по местам через пол секунды
            
            final Position eyePos = Position.of(eye);
            
            for (CuboidInfo ci : holo.keySet()) {
                h = holo.get(ci);
                if (ci.canTp) {
                    //final Location pos = loc.clone();
                    eye.setPitch(ci.relPitch);
                    eye.setYaw(p.getLocation().getYaw() + ci.relYaw);
                    v = eye.getDirection().multiply(2.8f);
                    //h.setPosition(pos.add(loc.getDirection().multiply(2.8f)));
                    //eye.add(v);
                    //h.setPosition( Position.of(eyePos.getWorldName(), eyePos.getX()+v.getX(), eyePos.getY()+v.getY(), eyePos.getZ()+v.getZ()) );//h.setPosition(center.add(v.getX(), v.getY(), v.getZ()));
                    h.setPosition( eyePos.getWorldName(), eyePos.getX()+v.getX(), eyePos.getY()+v.getY(), eyePos.getZ()+v.getZ() );//h.setPosition(center.add(v.getX(), v.getY(), v.getZ()));
                    //eye.subtract(v);
            	}

            }
            
        } 
        
        
        
        



        
        
        tick++;

    }
    
    
    
    
    public void cancel() {
        task.cancel();
        
        for (Hologram h : holo.values()) {
            h.delete();
        }
        HD.TASKS.remove(lp.name);
        
    }
    
    

    
  

    
    private boolean isAway() {
        return !p.getWorld().getName().equals(center.getWorldName()) || 
                Math.abs(p.getLocation().getBlockX()-center.getBlockX())>3 ||
                Math.abs(p.getLocation().getBlockY()-center.getBlockY())>3 ||
                Math.abs(p.getLocation().getBlockZ()-center.getBlockZ())>3
                ;
    }

    protected boolean isCanceled() {
        return task==null || task.isCancelled();
    }
    
    
    
    
    
    
    
    
    
    
*/}
