package ru.ostrov77.lobby.quest;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;





public class QuestViewMenu implements InventoryProvider {
    
    
    
    private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build());
    

    
    public QuestViewMenu() {
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

        
        final List<ClickableItem> buttons = new ArrayList<>();
        
        
        for (Quest q : lp.questAccept) {
            
            final ItemStack is = new ItemBuilder(Material.SCUTE)
                    .name(q.displayName)
                    .lore(Quest.getLore(q))
                    .lore("§aАктивно")
                    .build();
            
                buttons.add(ClickableItem.empty(is));
                    
        }
        
        LCuboid lc;
        for (Quest q : Quest.values()) {
           if (lp.questAccept.contains(q) || lp.questDone.contains(q)) continue;
            
            final ItemStack is;
            
            if (q.attachedArea.isEmpty()) {
                
                 is = new ItemBuilder(Material.FIRE_CHARGE)
                    .name(q.displayName)
                    .lore(Quest.getLore(q))
                    .lore("§6Предстоит")
                    .build();
                 
            } else {
                lc = AreaManager.getCuboid(q.attachedArea);
                is = new ItemBuilder(Material.FIRE_CHARGE)
                    .name(q.displayName)
                    .lore(Quest.getLore(q))
                    .lore("§6Предстоит")
                    .lore("§7Откроется при изучении локации")
                    .lore(lc==null ? "" : lc.displayName)
                    .build();
                 
            }
            
            
            
                buttons.add(ClickableItem.empty(is));
        }
        
       for (Quest q : lp.questDone) {
            
            final ItemStack is = new ItemBuilder(Material.FIREWORK_STAR)
                    .name(q.displayName)
                    .lore("§8Завершено")
                    .build();
            
                buttons.add(ClickableItem.empty(is));
        }
        
        
        
        
        
        
        
        
        
        
        
        
        pagination.setItems(buttons.toArray(new ClickableItem[buttons.size()]));
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
