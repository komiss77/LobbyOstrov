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
    
   // private final String schemName;// private final String schemName;
    
    
    public AreaEditor() {
        //this.schemName = schemName;
    }
        
    
    
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
                .lore("")
                .lore("§7Создайте кубоид точками диагоналей.")

                .lore("")
                .build()));
        } else {
            contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PAPER)
                .name("§7локация §f"+sm.schemName)
                .lore("ID: §3"+sm.param)
                .lore("displayName: §e"+sm.extra1)
                .lore("§7Размер: §b"+sm.getCuboid().getSizeX()+"§7x§b"+sm.getCuboid().getSizeY()+"§7x§b"+sm.getCuboid().getSizeZ())
                .lore("§7Объём: §e"+sm.getCuboid().getSize())
                .build()));
        }
        


        final boolean selected = sm.pos1!=null && sm.pos2!=null
                    && sm.pos1.getWorld().getName().equals(sm.pos2.getWorld().getName())
                    && p.getWorld().getName().equals(sm.pos1.getWorld().getName());


        
        
        LCuboid overlap = null;
        
        if (selected) {
            
            Location loc;
            Iterator<Location> it = sm.getCuboid().borderIterator(p.getWorld());
            while (it.hasNext()) {
                loc = it.next();
                if ( AreaManager.getCuboid(loc)!=null && !AreaManager.getCuboid(loc).name.equals(sm.schemName)) {
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
        
        
        
        

       

        if (sm.pos2==null) {
             contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                .name("§7верхняя точка кубоида.")
                .lore("§7")
                .lore("§7Клик - установить.")
                .build(), e -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.pos2=p.getLocation();
                    sm.checkPosition(p);
                    //p.sendBlockChange(p.getLocation(), Material.EMERALD_BLOCK.createBlockData());
                    reopen(p, contents);
                }));
        } else {
            //p.sendBlockChange(sm.pos2, Material.EMERALD_BLOCK.createBlockData());
            contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                .name("§7верхняя точка кубоида.")
                .lore("§7")
                .lore("§7ЛКМ-тп")
                .lore("§7ПКМ-установить")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        p.teleport(sm.pos2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                    } else if (e.isRightClick()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        //p.sendBlockChange(sm.pos2, Material.AIR.createBlockData());
                        sm.pos2=p.getLocation();
                        sm.checkPosition(p);
                        //p.sendBlockChange(sm.pos2, Material.EMERALD_BLOCK.createBlockData());
                        reopen(p, contents);
                    }
                }));
        }




        
        

        

        
        
        if (sm.pos1==null) {
             contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                .name("§7нижняя точка кубоида.")
                .lore("§7")
                .lore("§7Клик - установить.")
                .build(), e -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    sm.pos1=p.getLocation();
                    sm.checkPosition(p);
                    //p.sendBlockChange(p.getLocation(), Material.EMERALD_BLOCK.createBlockData());
                    reopen(p, contents);
                }));
        } else {
            //p.sendBlockChange(style.getPos1(p.getWorld().getName()), Material.EMERALD_BLOCK.createBlockData());
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                .name("§7нижняя точка кубоида.")
                .lore("§7")
                .lore("§7ЛКМ-тп")
                .lore("§7ПКМ-установить")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        p.teleport(sm.pos1);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                    } else if (e.isRightClick()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        //p.sendBlockChange(sm.pos1, Material.AIR.createBlockData());
                        sm.pos1=p.getLocation();
                        sm.checkPosition(p);
                        //p.sendBlockChange(sm.pos1, Material.EMERALD_BLOCK.createBlockData());
                        reopen(p, contents);
                    }
                }));
        }

        
        
        
    int id = ApiOstrov.getInteger(sm.param);
    final boolean wrongID = id<1 || id>32 || (AreaManager.getCuboid(id)!=null && !AreaManager.getCuboid(id).name.equals(sm.schemName));
    
    if (wrongID) {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.BARRIER)
            .name("§fУстановить ИД")
            .lore( (id<1 || id>32) ? "Уникальное число от 1 до 32" : "")
            .lore( (AreaManager.getCuboid(id)!=null) ? "Локация с ИД "+id+" уже есть!" : "")
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    } else {
        
        contents.set(2, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
            .name("§fРедактировать ИД")
            .lore("§7Сейчас: §a"+id)
            .build(), "1-32", param -> {
                sm.param = param;
                reopen(p, contents);
        }));
        
    }

       

        
        
        contents.set(3, 6 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
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
        
        
        
        
        
     
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (selected && !wrongID && overlap==null) {
            
            
            
            contents.set(5, 6, ClickableItem.of( new ItemBuilder(Material.JUKEBOX)
                .name("§2Сохранить")
                .build(), e -> {
                    
                    
                    
                    AreaManager.deleteCuboid(id); //вычистить старые ChunkContent, если были
                    final LCuboid lc = new LCuboid(id, sm.schemName, sm.extra1, sm.pos1, sm.pos2);
                    AreaManager.addCuboid(lc, true);
                    
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    p.closeInventory();
                    sm.lastEdit = SetupMode.LastEdit.Main;
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
                .lore(selected ? "" : "§cнет выделения")
                .lore( wrongID ? "§cневерный ИД" : "")
                .lore((overlap!=null) ? "§cвыделение пересекается с "+overlap.name+" (ID="+overlap.id+")" : "")
                .build()
            ));
            
        }
        
        
        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("назад").build(), e 
                -> AreaCmd.openAreaMainMenu(p)
        ));


        
       

        


    
    
    
    }
    

        


    


    
    
    
    
    
    
    
    
}