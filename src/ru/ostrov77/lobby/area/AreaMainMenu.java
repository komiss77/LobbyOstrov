package ru.ostrov77.lobby.area;

import ru.komiss77.builder.SetupMode;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.komiss77.Ostrov;
import ru.komiss77.commands.BuilderCmd;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.version.AnvilGUI;



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
                    .name(lc.name)
                    .lore("ID: "+lc.id)
                    .lore("displayName: "+lc.displayName)
                    .lore("§7Размер: "+lc.getSize()+" блоков")
                    .lore("")
                    .lore("ЛКМ - тп в центр кубоида")
                    .lore("ПКМ - изменить displayName")
                    .lore("клав.Q - §cудалить")
                    .lore("")
                    .lore("§8редактирование может")
                    .lore("§8когда-нибудь допилю,")
                    .lore("§8пока упор на скорость,")
                    .lore("§8так что можно только")
                    .lore("§8удалить и сделать новый.")
                    .build(), e -> {
                        
                
                    switch (e.getClick()) {
                        
                        case LEFT:
                            p.closeInventory();
                            p.teleport(lc.getCenter(p.getLocation()));
                            //System.out.println("Schematic size="+schem.sizeX+" "+schem.sizeY+" "+schem.sizeZ);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .5f, 2);
                            return;
                            
                        case RIGHT:
                            AnvilGUI agui = new AnvilGUI(Ostrov.instance, p, lc.displayName.replaceAll("§", "&"), (player2, displayName) -> {
                        
                                if(displayName.length()>64 ) {
                                    p.sendMessage("§cлимит 64 символа!");
                                    return null;
                                }
                                lc.displayName = displayName.replaceAll("&", "§");
                                AreaManager.save(lc);
                                reopen(p, contents);
                                return null;
                            
                            //return null;
                            });//schemFile.delete();
                            //sm.setCuboid(p, lc);
                            //sm.openSchemEditMenu(p, schem.getName());
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
        
        
        
        

       /* contents.set(5, 2 , new InputButton( InputButton.InputType.ANVILL, new ItemBuilder(Material.BOOK)
            .name("§fCоздать схематик")
            .build(), "название", newName -> {

                if(newName.isEmpty() || newName.length()>16 || !ApiOstrov.checkString(newName,true,true) ) {
                    p.sendMessage("§cНедопустимое название!");
                    PM.soundDeny(p);
                    return;
                } 

                sm.resetCuboid();
                sm.openSchemEditMenu(p, newName);
//Bukkit.broadcastMessage("создание "+schemFile.getAbsolutePath());
                //p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                //reopen(p, contents);
                    
        }));*/


        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(36);
        

        
        
        contents.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("гл.меню").build(), e 
                -> sm.openMainSetupMenu(p)
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
