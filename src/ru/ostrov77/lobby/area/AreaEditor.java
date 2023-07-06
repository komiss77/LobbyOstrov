package ru.ostrov77.lobby.area;

import java.util.Iterator;
import org.bukkit.Location;
import ru.komiss77.builder.SetupMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;










public class AreaEditor implements InventoryProvider{

    public static final ItemStack fillOk = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).build();;
    public static final ItemStack fillErr = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).build();;
    
        
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
       // contents.fillRect(SlotPos.of(0), SlotPos.of(53), ClickableItem.empty(fill));
        //contents.fillRow(4, ClickableItem.empty(fill));

        final SetupMode sm = PM.getOplayer(p).setup;
        
        //SetupManager.setPosition(p, style.getPos1(p.getWorld().getName()), style.getPos2(p.getWorld().getName()));

        if (sm.getCuboid()==null) {
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PAPER)
                .name("§7локация §f"+sm.schemName)
                .addLore("")
                .addLore("§7Создайте кубоид точками диагоналей.")

                .addLore("")
                .build()));
        } else {
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PAPER)
                .name("§7локация §f"+sm.schemName)
                .addLore("ID: §3"+sm.param)
                .addLore("displayName: §e"+sm.extra1)
                .addLore("§7Размер: §b"+sm.getCuboid().sizeX()+"§7x§b"+sm.getCuboid().sizeY()+"§7x§b"+sm.getCuboid().sizeZ())
                .addLore("§7Объём: §e"+sm.getCuboid().volume())
                .build()));
        }
        


        final boolean selected = sm.min!=null && sm.max!=null
                    && sm.min.getWorld().getName().equals(sm.max.getWorld().getName())
                    && p.getWorld().getName().equals(sm.min.getWorld().getName());


        
        
        LCuboid overlap = null;
        
        if (selected) {
            
            Location loc;
            Iterator<Location> it = sm.getCuboid().borderIterator(p.getWorld());
            while (it.hasNext()) {
                loc = it.next();
                if ( AreaManager.getCuboid(loc)!=null && !AreaManager.getCuboid(loc).getName().equals(sm.schemName)) {
                    overlap = AreaManager.getCuboid(loc);
                    break;
                }
            }
            
            if (overlap==null) {
                contents.fillRect(1,1, 4,4, ClickableItem.empty(fillOk));
            } else {
                contents.fillRect(1,1, 4,4, ClickableItem.empty(fillErr));
            }
            
        }
        
        
        
        

       

        if (sm.max==null) {
             contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                .name("§7верхняя точка кубоида.")
                .addLore("§cне установлена")
                .addLore("§7Клик - установить.")
                .build(), e -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.max=p.getLocation();
                    sm.checkPosition(p);
                    //p.sendBlockChange(p.getLocation(), Material.EMERALD_BLOCK.createBlockData());
                    reopen(p, contents);
                }));
        } else {
            contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                .name("§7верхняя точка кубоида.")
                .addLore("§7")
                .addLore("§7ЛКМ-тп")
                .addLore("§7ПКМ-установить")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        p.teleport(sm.max);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                    } else if (e.isRightClick()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        sm.max=p.getLocation();
                        sm.checkPosition(p);
                        reopen(p, contents);
                    }
                }));
        }




        
        

        

        
        
        if (sm.min==null) {
             contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                .name("§7нижняя точка кубоида.")
                .addLore("§cне установлена")
                .addLore("§7Клик - установить.")
                .build(), e -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.min=p.getLocation();
                    sm.spawnPoint=p.getLocation();
                    sm.spawnPoint.setY(p.getLocation().getYaw());
                    sm.spawnPoint.setPitch(p.getLocation().getPitch());
                    sm.checkPosition(p);
                    reopen(p, contents);
                }));
        } else {
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                .name("§7нижняя точка кубоида.")
                .addLore("§7")
                .addLore("§7ЛКМ-тп")
                .addLore("§7ПКМ-установить")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        p.teleport(sm.spawnPoint);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                    } else if (e.isRightClick()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        sm.min=p.getLocation();
                        sm.spawnPoint=p.getLocation();
                        sm.spawnPoint.setY(p.getLocation().getYaw());
                        sm.spawnPoint.setPitch(p.getLocation().getPitch());
                        sm.checkPosition(p);
                        reopen(p, contents);
                    }
                }));
        }

        
    
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(sm.spawnPoint==null ? Material.BARRIER : Material.ENDER_EYE)
            .name("§7точка спавна кубоида.")
            .addLore("§7")
            .addLore(sm.spawnPoint==null ? "§cне установлена": "§7ЛКМ-тп")
            .addLore("§7ПКМ-установить")
            .build(), e -> {
                if (e.isLeftClick() && sm.spawnPoint!=null) {
                    p.teleport(sm.spawnPoint);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                } else if (e.isRightClick()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.spawnPoint=p.getLocation();
                    reopen(p, contents);
                } else {
                    PM.soundDeny(p);
                }
            }));        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(sm.spawnPoint==null ? Material.BARRIER : Material.ENDER_EYE)
            .name("§7точка спавна кубоида.")
            .addLore("§7")
            .addLore(sm.spawnPoint==null ? "§cне установлена": "§7ЛКМ-тп")
            .addLore("§7ПКМ-установить")
            .build(), e -> {
                if (e.isLeftClick() && sm.spawnPoint!=null) {
                    p.teleport(sm.spawnPoint);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                } else if (e.isRightClick()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.spawnPoint=p.getLocation();
                    reopen(p, contents);
                } else {
                    PM.soundDeny(p);
                }
            }));        
        
        
        
        
        
        
        
        
    int id = ApiOstrov.getInteger(sm.param);
    final boolean wrongID = id<1 || id>32 || (AreaManager.getCuboid(id)!=null && !AreaManager.getCuboid(id).getName().equals(sm.schemName));
    
    if (wrongID) {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.BARRIER)
            .name("§fУстановить ИД")
            .addLore( (id<1 || id>32) ? "Уникальное число от 1 до 32" : "")
            .addLore( (AreaManager.getCuboid(id)!=null) ? "Локация с ИД "+id+" уже есть!" : "")
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    } else {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
            .name("§fРедактировать ИД")
            .addLore("§7Сейчас: §a"+id)
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    }

       

        
        
        contents.set(3, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
            .name("§fРедактировать DisplayName")
            .addLore("§7Сейчас:")
            .addLore(sm.extra1)
            .build(), sm.extra1.replaceAll("§", "&"), displayName -> {
                sm.extra1 = displayName;
                if(displayName.length()>64 ) {
                    p.sendMessage("§cлимит 64 символа!");
                    return;
                }
                sm.extra1 = displayName.replaceAll("&", "§");
                //AreaManager.save(lc);
                reopen(p, contents);
        }));
        
        
        
        
        
     
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (selected && !wrongID && overlap==null && sm.spawnPoint!=null) {
            
            
            
            contents.set(5, 6, ClickableItem.of( new ItemBuilder(Material.JUKEBOX)
                .name("§2Сохранить")
                .build(), e -> {
                    
                    
                    
                    AreaManager.deleteCuboid(id); //вычистить старые ChunkContent, если были
                    final LCuboid lc = new LCuboid(id, sm.schemName, sm.extra1, sm.spawnPoint, sm.min, sm.max);
                    AreaManager.addCuboid(lc, true);
                    
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    p.closeInventory();
                    sm.lastEdit = SetupMode.LastEdit.Main.name();
                   // p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                   /* ConfirmationGUI.open( p, "§4Стереть ?", result -> {
                        if (result) {
                            sm.reset();
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        } else {
                            reopen(p, contents);
                            p.playSound(p.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                        }
                    });*/
                    //reopen(p, contents);
                }));
            
            
            
            
        } else {
            
            contents.set(5, 6, ClickableItem.empty(new ItemBuilder(Material.JUKEBOX)
                .name("§6Для сохранения:")
                .addLore(selected ? "" : "§cнет выделения")
                .addLore( wrongID ? "§cневерный ИД" : "")
                .addLore((overlap!=null) ? "§cвыделение пересекается с "+overlap.getName()+" (ID="+overlap.id+")" : "")
                .build()
            ));
            
        }
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> AreaCmd.openAreaMainMenu(p)
        ));


        
       

        


    
    
    
    }
    

        


    


    
    
    
    
    
    
    
    
}