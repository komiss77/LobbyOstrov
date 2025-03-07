package ru.ostrov77.lobby.area;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.items.ItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.NumUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;










public class AreaEditor implements InventoryProvider{

    public static final ItemStack fillOk = new ItemBuilder(ItemType.GREEN_STAINED_GLASS_PANE).build();
    public static final ItemStack fillErr = new ItemBuilder(ItemType.RED_STAINED_GLASS_PANE).build();


    @Override
    public void init(final Player p, final InventoryContent contents) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
       // contents.fillRect(SlotPos.of(0), SlotPos.of(53), ClickableItem.empty(fill));
        //contents.fillRow(4, ClickableItem.empty(fill));

        final SetupMode sm = PM.getOplayer(p).setup;
        
        //SetupManager.setPosition(p, style.getPos1(p.getWorld().getName()), style.getPos2(p.getWorld().getName()));

        if (sm.getCuboid()==null) {
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(ItemType.PAPER)
                .name("§7локация §f"+sm.schemName)
                .lore("")
                .lore("§7Создайте кубоид точками диагоналей.")

                .lore("")
                .build()));
        } else {
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(ItemType.PAPER)
                .name("§7локация §f"+sm.schemName)
                .lore("ID: §3"+sm.param)
                .lore("displayName: §e"+sm.extra1)
                .lore("§7Размер: §b"+sm.getCuboid().sizeX()+"§7x§b"+sm.getCuboid().sizeY()+"§7x§b"+sm.getCuboid().sizeZ())
                .lore("§7Объём: §e"+sm.getCuboid().volume())
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
             contents.set(1, 4, ClickableItem.of( new ItemBuilder(ItemType.BARRIER)
                .name("§7верхняя точка кубоида.")
                .lore("§cне установлена")
                .lore("§7Клик - установить.")
                .build(), e -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.max=p.getLocation();
                    sm.checkPosition(p);
                    //p.sendBlockChange(p.getLocation(), ItemType.EMERALD_BLOCK.createBlockData());
                    reopen(p, contents);
                }));
        } else {
            contents.set(1, 4, ClickableItem.of( new ItemBuilder(ItemType.OAK_FENCE)
                .name("§7верхняя точка кубоида.")
                .lore("§7")
                .lore("§7ЛКМ-тп")
                .lore("§7ПКМ-установить")
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
             contents.set(4, 1, ClickableItem.of( new ItemBuilder(ItemType.BARRIER)
                .name("§7нижняя точка кубоида.")
                .lore("§cне установлена")
                .lore("§7Клик - установить.")
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
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(ItemType.OAK_FENCE)
                .name("§7нижняя точка кубоида.")
                .lore("§7")
                .lore("§7ЛКМ-тп")
                .lore("§7ПКМ-установить")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        p.teleport(sm.min);
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

        
    
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(sm.spawnPoint==null ? ItemType.BARRIER : ItemType.ENDER_EYE)
            .name("§7точка спавна кубоида.")
            .lore("§7")
            .lore(sm.spawnPoint==null ? "§cне установлена": "§7ЛКМ-тп")
            .lore("§7ПКМ-установить")
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
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    
        contents.set(1, 6, ClickableItem.of( new ItemBuilder(sm.spawnPoint==null ? ItemType.BARRIER : ItemType.ENDER_EYE)
            .name("§7точка спавна кубоида.")
            .lore("§7")
            .lore(sm.spawnPoint==null ? "§cне установлена": "§7ЛКМ-тп")
            .lore("§7ПКМ-установить")
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
        
        
        
        
        
        
        
        
    int id = NumUtil.intOf(sm.param, 0);
    final boolean wrongID = id<1 || id>32 || (AreaManager.getCuboid(id)!=null && !AreaManager.getCuboid(id).getName().equals(sm.schemName));
    
    if (wrongID) {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(ItemType.BARRIER)
            .name("§fУстановить ИД")
            .lore( (id<1 || id>32) ? "Уникальное число от 1 до 32" : "")
            .lore( (AreaManager.getCuboid(id)!=null) ? "Локация с ИД "+id+" уже есть!" : "")
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    } else {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(ItemType.NAME_TAG)
            .name("§fРедактировать ИД")
            .lore("§7Сейчас: §a"+id)
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    }

       

        
        
        contents.set(3, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(ItemType.NAME_TAG)
            .name("§fРедактировать DisplayName")
            .lore("§7Сейчас:")
            .lore(sm.extra1)
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
            
            
            
            contents.set(5, 6, ClickableItem.of( new ItemBuilder(ItemType.JUKEBOX)
                .name("§2Сохранить")
                .build(), e -> {
                    
                    AreaManager.deleteCuboid(id); //вычистить старые ChunkContent, если были
                    final LCuboid lc = new LCuboid(id, sm.schemName, sm.extra1, sm.spawnPoint, new XYZ(sm.min), new XYZ(sm.max));
                    AreaManager.addCuboid(lc, true);
                    
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    p.closeInventory();
                    sm.lastEdit = "Main";
                }));
            
            
            
            
        } else {
            
            contents.set(5, 6, ClickableItem.empty(new ItemBuilder(ItemType.JUKEBOX)
                .name("§6Для сохранения:")
                .lore(selected ? "" : "§cнет выделения")
                .lore( wrongID ? "§cневерный ИД" : "")
                .lore((overlap!=null) ? "§cвыделение пересекается с "+overlap.getName()+" (ID="+overlap.id+")" : "")
                .build()
            ));
            
        }
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(ItemType.OAK_DOOR).name("назад").build(), e 
                -> AreaCmd.openAreaMainMenu(p)
        ));


        
       

        


    
    
    
    }
    

        


    


    
    
    
    
    
    
    
    
}