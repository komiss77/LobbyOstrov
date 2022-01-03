package ru.ostrov77.lobby.hd;

import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.Position;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.beta.hologram.line.TextHologramLine;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;


public class HD {
	
    protected static final ConcurrentHashMap<String,MenuTask> tasks = new ConcurrentHashMap<>();
    private static final ItemStack arrow = new ItemBuilder(Material.ARROW).addEnchantment(Enchantment.ARROW_INFINITE, 1).build();
    private static final ItemStack empty = new ItemStack(Material.GRAY_DYE);

    public static void openAreaMenu(Player p, LobbyPlayer lp) {
        
         
        
        //if (tasks.containsKey(p.getName())) { //если меню открыто - повторный клик закроет
        //    tasks.get(p.getName()).cancel();
            //return;
       // }
        final Location eye = p.getEyeLocation();
        eye.setPitch(0); //чтобы не зависило от вверх - вниз
        final Vector direction = eye.getDirection();
        eye.add(direction.multiply(2));
        eye.setY(eye.getY()+1);
        //final Location holoCenter = p.getEyeLocation().add(direction.multiply(2));
        final Position center = Position.of(eye);
        
        final EnumMap<CuboidInfo,Hologram> holo = new EnumMap(CuboidInfo.class);
        
        for (final LCuboid lc : AreaManager.getCuboids()) {
            final CuboidInfo ci = lc.getInfo();
            
            if (ci==CuboidInfo.DEFAULT || !ci.canTp) continue;

            final Hologram h = HolographicDisplaysAPI.get(Main.instance).createHologram(center);
            final VisibilitySettings visiblity = h.getVisibilitySettings();
            visiblity.setIndividualVisibility(p, VisibilitySettings.Visibility.VISIBLE);
            visiblity.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
            
            if (lp.isAreaDiscovered(lc.id)) {

                final ItemHologramLine i = h.getLines().appendItem(new ItemStack(ci.icon));
                i.setClickListener( (cl) -> {
//p.sendMessage("§8log: "+lc.getName()+" canTp?"+ci.canTp);
                        if (ci.canTp) {
                            tasks.get(p.getName()).cancel();
                            ApiOstrov.teleportSave(p, lc.spawnPoint, false);
                        }
                    }
                );
                
              /*  final  TextHologramLine t = h.getLines().appendText(lc.displayName);
                t.setClickListener( (cl) -> {
//p.sendMessage("§8log: "+lc.getName()+" canTp?"+ci.canTp);
                        if (ci.canTp) {
                            tasks.get(p.getName()).cancel();
                            ApiOstrov.teleportSave(p, lc.spawnPoint, false);
                        }
                    }
                );*/
                
                
            } else {

                final ItemHologramLine i = h.getLines().appendItem(lp.compasstarget == lc.getInfo() ? arrow : empty);
                i.setClickListener( (cl) -> {
                        if (lp.compasstarget != lc.getInfo()) {
//p.sendMessage(lc.getName()+" setCompassTarget");
                            setCompassTarget(p, lp, lc);
                            i.setItemStack(arrow);
                        } else {
//p.sendMessage(lc.getName()+" resetCompassTarget");
                            AreaManager.resetCompassTarget(p, lp);
                            i.setItemStack(new ItemStack(empty));
                        }                    
                    }
                );
                
               /* final  TextHologramLine t = h.getLines().appendText(lc.displayName);
                t.setClickListener( (cl) -> {
                        if (lp.compasstarget != lc.getInfo()) {
//p.sendMessage(lc.getName()+" setCompassTarget");
                            setCompassTarget(p, lp, lc);
                            i.setItemStack(new ItemStack(Material.ARROW));
                        } else {
//p.sendMessage(lc.getName()+" resetCompassTarget");
                            AreaManager.resetCompassTarget(p, lp);
                            i.setItemStack(new ItemStack(Material.GRAY_DYE));
                        }                    
                    }
                );*/
                
            }
            
            holo.put(ci, h);
        }
        
        
        
        final MenuTask a = new MenuTask(p, center, holo);
        tasks.put(p.getName(), a);
        
       /* Hologram h = HolographicDisplaysAPI.get(Main.instance).createHologram(p.getEyeLocation());
        final VisibilitySettings visiblity = h.getVisibilitySettings();
        visiblity.setIndividualVisibility(p, VisibilitySettings.Visibility.VISIBLE);
        visiblity.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        ItemHologramLine l = h.getLines().appendItem(new ItemStack(Material.NAUTILUS_SHELL));
        l.setClickListener( (cl) -> {
            p.sendMessage("click ");
            //h.delete();
                });*/
    
    
    }

    public static boolean isOpen( final Player p) {
        return tasks.containsKey(p.getName()) && tasks.get(p.getName())!=null && !tasks.get(p.getName()).isCanceled() ;
    }
   /* protected static Location getHoloCentr(final Player p) {
        //final Location l = p.getEyeLocation();
        final Vector direction = p.getLocation().getDirection();
        final Location holoLoc = p.getEyeLocation().add(direction.multiply(2));
        holoLoc.setY(holoLoc.getY() + 1 + lines*0.25);//adelante.setY(adelante.getY() + hologram.size()*0.15);
        return holoLoc;
    }*/

    private static void setCompassTarget(final Player p, final LobbyPlayer lp, final LCuboid newTarget) {
        if (isOpen(p) && tasks.get(p.getName()).holo.containsKey(lp.compasstarget)) { //в карте есть пункт с предыдущей целью компаса
            final Hologram h = tasks.get(p.getName()).holo.get(lp.compasstarget); //вытаскиваем голограмму с предыдущей целью компаса
            if (h.getLines().size()!=0 && (h.getLines().get(0) instanceof ItemHologramLine)) { //первай строка - предмет
                final ItemHologramLine i = (ItemHologramLine) h.getLines().get(0); //вытаскиваем строку-предмет
                i.setItemStack(empty); //меняем её тип (цель не могла быть ранее открыта, так что только серый шарик
            }
        }  
        AreaManager.setCompassTarget(p, lp, newTarget); //
    
    }
    
    
}
