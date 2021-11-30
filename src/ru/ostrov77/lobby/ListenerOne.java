package ru.ostrov77.lobby;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.StatFlag;
import ru.komiss77.events.BungeeDataRecieved;
import ru.komiss77.events.MysqlDataLoaded;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;





public class ListenerOne implements Listener {
    
    
    final ItemStack fw = mkFwrk (new ItemBuilder(Material.FIREWORK_ROCKET)
                .setName("§7Топливо для §bКрыльев")
                .lore("§7Осторожно,")
                .lore("§7иногда взрывается!")
                .build()
    );
    
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent e) {
        //final Player p = e.getPlayer();
        
    }
    
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onBungeeData(final BungeeDataRecieved e) {
        final Player p = e.getPlayer();
        final Oplayer op = PM.getOplayer(p);
        if (!op.hasFlag(StatFlag.NewBieDone)) {
            op.setFlag(StatFlag.NewBieDone, true);
            p.teleport(Main.newBieSpawnLocation);// тп на 30 160 50
        } else {
            //очистить инв - не надо, файлы не сохр!
            //выдать положенные предметы
            if (op.hasFlag(StatFlag.Elytra)) {
                p.getInventory().setItem(2, fw);
            }
            //тп на точку выхода
        }
        
        
        
    }
    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onLocalDataLoad(final MysqlDataLoaded e) {
        e.setCancelled(true); //чтобы не ставило режим пвп, скорость ходьбы,полёта, погоду и прочее ненужное для лобби
        final Player p = e.getPlayer();
        final Oplayer op = PM.getOplayer(p);
        if (op!=null && op.world_positions.containsKey("world")) {
            final Location logoutLoc = op.world_positions.get("world");
            if (logoutLoc!=null && ApiOstrov.isLocationSave(p, logoutLoc)) {
                p.teleport(logoutLoc);
p.sendMessage("log: тп на точку выхода");
            }
        }
    }    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(final ProjectileLaunchEvent e) { //PlayerElytraBoostEvent !!!
            final Projectile prj = e.getEntity();
            if (prj.getShooter() instanceof Player && prj.getType() == EntityType.FIREWORK) {
                Ostrov.sync(()-> ((HumanEntity) prj.getShooter()).getInventory().setItem(2, fw), 8);
            }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInter(final PlayerInteractEvent e) {
        if (e.hasItem() && e.getClickedBlock() != null  && e.getItem().getType()==Material.FIREWORK_ROCKET) {
            e.setUseItemInHand(Event.Result.DENY);
        }
    }
    
    private static ItemStack mkFwrk(final ItemStack fw) {
        final FireworkMeta fm = (FireworkMeta) fw.getItemMeta();
        final FireworkEffect fc = FireworkEffect.builder().withColor(Color.TEAL).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).build();
        final FireworkEffect fl = FireworkEffect.builder().withColor(Color.LIME).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        fm.addEffects(fc,fc,fc,fc,fl,fl,fl);
        fw.setItemMeta(fm);
        return fw;
    }


    
}
