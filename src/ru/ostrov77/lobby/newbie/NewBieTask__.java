package ru.ostrov77.lobby.newbie;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.lobby.Main;


public class NewBieTask__ implements Runnable {
    
    private final BukkitTask task;
    protected int tick;
    private final String name;
    protected String migrationInfo="";
    //protected boolean equip;
    private final Oplayer op;
    
    //private static final List<String>scoreAmin = Arrays.asList("§bПолучено задание", "§eПолучено задание");
    
    public NewBieTask__  (final Player p) {
        name = p.getName();
        op = PM.getOplayer(p);
        //op.hideScore();
        task = Bukkit.getScheduler().runTaskTimer(Main.instance, NewBieTask__.this, 1, 1);
        //NewBie.tasks.put(name, NewBieTask.this);
        p.teleport(Main.newBieSpawnLocation);// тп на 30 160 50
        //p.setGameMode(GameMode.SPECTATOR);  //только спектатор! или может выкл полёт и падать
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 5));
        //ApiOstrov.sendTitle(p, "", "§3Вам доводилось летать во сне?", 80, 20, 40);
        //ApiOstrov.sendBossbar(p, "@1 Страшный сон.", 5, BarColor.WHITE, BarStyle.SOLID, false);
        //p.playSound( p.getLocation(), Sound.AMBIENT_BASALT_DELTAS_LOOP, 1f, 0.5f);
        p.setCollidable(false);
        p.getInventory().clear();
    }
    
    
    
    
    //добавить везде звуки
    
    
    @Override
    public void run() {
        
        final Player p = Bukkit.getPlayerExact(name);
        if (p==null || !p.isOnline()) {
            this.cancel();
            //NewBie.log("cancel : "+name);
            return;
        }
        
        

        
       // if (tick<500) { //не давать выйти из поля звёзд
       // }

       // if (tick>=700) { //на корабле
            //if ((tick-700)%1600==0) p.playSound( p.getLocation(), Sound.AMBIENT_BASALT_DELTAS_LOOP, 10f, 0.3f);
            //if (tick%250==0) p.playSound( p.getLocation(), Sound.AMBIENT_WARPED_FOREST_MOOD, 0.3f, 2); //- другие не играть, отменяют основной
       // }
        
        switch (tick) {
            
            //case 30:
                //ApiOstrov.sendTitle(p, "", "§3Вам доводилось летать во сне?", 50, 1, 20);
                //break;
                
            case 20:
                ApiOstrov.sendBossbar(p, "#3 Остров.", 5, BarColor.PINK, BarStyle.SOLID, false);
                break;
                
            //case 100:
            //    op.score.getSideBar().setTitle("§5Новые задачи");
            //    op.score.getSideBar().updateLine(3, "");
            //    break;

            //case 140:
            //    op.score.getSideBar().updateLine(2, "§7Разговорить лоцмана");
           //     break;
                
            //case 180:
            //    op.score.getSideBar().updateLine(1, "§7Добраться до спавна");
             //   Main.oscom.give(p);//ApiOstrov.getMenuItemManager().giveItem(p, "newbie");
            //    break;
                
            case 200:
                ApiOstrov.getMenuItemManager().giveItem(p, "newbie");
                break;
                
                
                
                
                
                
                
                
            //case 500:
             //   op.score.getSideBar().reset();
             //   break;

                
            //case 690://460:
                //p.stopSound(Sound.ITEM_ELYTRA_FLYING);
                //p.setFallDistance(0);
              //  break;
                
                
                
                
            //case 700://460:
                //ApiOstrov.sendBossbar(p, "#2 Прибытие.", 5, BarColor.PINK, BarStyle.SOLID, false);
            //    //p.playSound( p.getLocation(), Sound.AMBIENT_BASALT_DELTAS_LOOP, 10f, 0.3f);
             //   break;
                
          //  case 780://460:
                //p.sendMessage("§5[§dСтарпом§d] §fКэп? Вы что, уснули?");
           //     //p.playSound( p.getLocation(), Sound.ENTITY_ILLUSIONER_AMBIENT, 1, 1);
           //     break;
                
          ///  case 840:
                //p.sendMessage("§5[§dСтарпом§d] §fК нам пристыковался посадочный модуль, ждут Вас. Осталось только заполнить миграционныу карту.");
                //score.getSideBar().setTitle("§5Новые задачи");
                //score.getSideBar().updateLine(3, "");
                //p.playSound( p.getLocation(), Sound.ENTITY_EVOKER_CELEBRATE, 1, 1);
              //  break;

           // case 880:
                //p.sendMessage("§5[§dСтарпом§d] §fДержите Ваш ОсКом.");
                //p.getInventory().setItem(0, NewBie.clock.clone());
                
                //score.getSideBar().updateLine(3, "§7Заполнить миграционную.");
                //score.getSideBar().updateLine(2, "§7карту.");
                //score.getSideBar().updateLine(1, "§7Пройти на посадку.");
               // p.playSound( p.getLocation(), Sound.ENTITY_VINDICATOR_AMBIENT, 1, 1);
            //    break;
                


        }
        
        
        tick++;
        
        
    }
    
    public void cancel() {
        task.cancel();
        //NewBie.tasks.remove(name);
    }    

    



}
