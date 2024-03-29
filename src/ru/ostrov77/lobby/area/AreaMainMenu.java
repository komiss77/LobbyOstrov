package ru.ostrov77.lobby.area;

import ru.komiss77.builder.SetupMode;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.BuilderCmd;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;



public class AreaMainMenu implements InventoryProvider {

    
    public AreaMainMenu() {
    }

    
    
    
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.fillRow(4, ClickableItem.empty(BuilderCmd.fill));
        
        final SetupMode sm = PM.getOplayer(p).setup;
        
        
        final Pagination pagination = contents.pagination();
        
        
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        
            for (final int cuboidId : AreaManager.getCuboidIds()) {
                
                final LCuboid lc = AreaManager.getCuboid(cuboidId);
                        
                menuEntry.add(ClickableItem.of(new ItemBuilder( Material.BOOKSHELF )
                    .name(lc.getName())
                    .addLore("ID: §3"+lc.id)
                    .addLore("displayName: "+lc.displayName)
                    .addLore("§7Размер: §b"+lc.sizeX()+"§7x§b"+lc.sizeY()+"§7x§b"+lc.sizeZ())
                    .addLore("§7Объём: §b"+lc.volume()+" блоков")
                    .addLore("")
                    .addLore("ЛКМ - §6тп в точку спавна кубоида")
                    .addLore("ПКМ - §6редактировать")
                    .addLore("клав.Q - §cудалить")
                    .addLore("")
                    //.addLore("§8редактирование может")
                    //.addLore("§8когда-нибудь допилю,")
                    //.addLore("§8пока упор на скорость,")
                    //.addLore("§8так что можно только")
                    //.addLore("§8удалить и сделать новый.")
                    .build(), e -> {
                        
                
                    switch (e.getClick()) {
                        
                        case LEFT:
                            p.closeInventory();
                            p.teleport(lc.spawnPoint);
                            //System.out.println("Schematic size="+schem.sizeX+" "+schem.sizeY+" "+schem.sizeZ);
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .5f, 2);
                            return;
                            
                        case RIGHT:
                           // AnvilGUI agui = new AnvilGUI(Ostrov.instance, p, lc.displayName.replaceAll("§", "&"), (player2, displayName) -> {
                        
                              //  if(displayName.length()>64 ) {
                              //      p.sendMessage("§cлимит 64 символа!");
                             //       return null;
                             //   }
                              //  lc.displayName = displayName.replaceAll("&", "§");
                              //  AreaManager.save(lc);
                               // reopen(p, contents);
                              //  return null;
                            
                            //return null;
                            //});//schemFile.delete();
                            sm.setCuboid(p, lc);
                            sm.param = String.valueOf(lc.id);
                            sm.extra1 = lc.displayName;
                            sm.spawnPoint = lc.spawnPoint;
                            AreaCmd.openAreaEditMenu(p, lc.getName());
                            //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                            //reopen(p, contents);
                            return;
                            
                        case DROP:
                            AreaManager.deleteCuboid(cuboidId);
                            p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                            reopen(p, contents);
                            return;
                            
                        default:
                            break; 
                    }
                PM.soundDeny(p);
            }));
            }
        
        
        
        

        contents.set(5, 2 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.BOOK)
            .name("§fCоздать зону")
            .addLore("При нажатии нужно будет")
            .addLore("ввести уникальное название")
            .addLore("без пробелов для")
            .addLore("идентификации в плагине.")
            .build(), "name", newName -> {

                if(newName.isEmpty() || newName.length()>16 || !ApiOstrov.checkString(newName,true,true) ) {
                    p.sendMessage("§cНедопустимое название!");
                    PM.soundDeny(p);
                    return;
                } 
                if (AreaManager.getCuboid(newName)!=null) {
                    p.sendMessage("§cлокация с названием "+newName+" уже есть!");
                    return;
                }
                sm.resetCuboid();
                AreaCmd.openAreaEditMenu(p, newName);
//Bukkit.broadcastMessage("создание "+schemFile.getAbsolutePath());
                //p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                //reopen(p, contents);
                    
        }));


        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[0]));
        pagination.setItemsPerPage(36);
        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("закончить режим редактора").build(), e 
                ->  p.performCommand("builder end")
        ));
        
        
        if (!pagination.isLast()) {
            contents.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));
        

        
        

    }
    
    
        
}
