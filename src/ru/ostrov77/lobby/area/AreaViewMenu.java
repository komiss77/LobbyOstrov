package ru.ostrov77.lobby.area;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;





public class AreaViewMenu implements InventoryProvider {
    
    
    
    private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("§8.").build());
    

    
    public AreaViewMenu() {
    }
    
    //@Override
   // public void onClose(final Player p, final InventoryContent content) {
   // }


    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        content.fillRect(0,0, 4,8, fill);
        
        final LobbyPlayer lp = Main.getLobbyPlayer(p);

        
        final Pagination pagination = content.pagination();

        
        final List<ClickableItem> areas = new ArrayList<>();
        final List<ClickableItem> unknow = new ArrayList<>();
        
        
        for (LCuboid lc : AreaManager.getCuboids()) {
            
            if (lc.getName().equals("newbie")) continue; //спавн новичка не показываем
            
            if (lp.isAreaDiscovered(lc.id)) {
                
                final ItemStack is = new ItemBuilder(Material.MAP)
                        .name(lc.displayName)
                        .lore("§7Открыта")
                        .lore("§7ЛКМ - тп")
                        .build();

                    areas.add(ClickableItem.of(is, e-> {
                        ApiOstrov.teleportSave(p, lc.spawnPoint, false);
                    }));
                    
            } else {
                
                final ItemStack is = new ItemBuilder(Material.GRAY_DYE)
                        .name(lc.displayName)
                        .lore("§7Не изучена")
                        .lore("§7ЛКМ - навести компас")
                        .build();

                    unknow.add(ClickableItem.of(is, e-> {
                        p.setCompassTarget(lc.spawnPoint);
                    }));
                
            }
            
        }

        
        
        
        
        
        
        areas.addAll(unknow);
        
        
        pagination.setItems(areas.toArray(new ClickableItem[areas.size()]));
        pagination.setItemsPerPage(21);    
        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));


        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (!pagination.isLast()) {
            content.set(4, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }

        if (!pagination.isFirst()) {
            content.set(4, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }


        

    }


    
    
    
    
    
    
    
    
    
}
