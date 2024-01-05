package ru.ostrov77.lobby;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.area.AreaViewMenu;


public class DebugMenu implements InventoryProvider {
    

 
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = contents.pagination();
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();

        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
        
        
        
        
        
        
        
        
        
        
        
        
        for (final LobbyFlag flag : LobbyFlag.values()) {
            
            final boolean isSet = lp.hasFlag(flag);

            menuEntry.add( ClickableItem.of(new ItemBuilder(isSet ? Material.LIME_DYE : Material.GRAY_DYE)
                .name( "§f"+flag.displayName)
                .addLore("§7")
                .addLore( isSet ? "§7ПКМ - §2сброс" : "§7ЛКМ - §4поставить")
                .addLore("§7")
                .build(), e -> {
//System.out.println("ClaimFlags "+e.getClick()+" isSet="+isSet);
                if (e.isLeftClick() && !isSet) {
                        lp.setFlag(flag, true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                        //f.log(LogType.Информация, p.getName()+" : включение флага террикона "+flag.displayName);
                        reopen(p, contents);
                        return;
                } else if (e.isRightClick() && isSet) {
                        lp.setFlag(flag, false);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.5f, 1);
                        //f.log(LogType.Информация, p.getName()+" : выключение флага террикона "+flag.displayName);
                        reopen(p, contents);
                        return;
                } 
                PM.soundDeny(p);
            }));            
            
        }
            
            
        contents.set( 2, 1,  ClickableItem.of(new ItemBuilder( Material.COMPASS)
            .name("§fМеню локаций")
            .build(), e -> {
//System.out.println("ClaimFlags "+e.getClick()+" isSet="+isSet);
            if (e.getClick()==ClickType.SHIFT_RIGHT) {
                SmartInventory.builder()
                    .type(InventoryType.CHEST)
                    .id("area"+p.getName()) 
                    .provider(new AreaViewMenu())
                    .title("Локации")
                    .size (6,9)
                    .build()
                    .open(p);
                return;
            } 
            PM.soundDeny(p);
        }));
        
        
        contents.set( 2, 4,  ClickableItem.of(new ItemBuilder( Material.REDSTONE)
            .name("§fсброс аккаунта до новичка")
            .addLore("§7")
            .addLore("§7Шифт+ПКМ - §cресетнуть")
            .addLore("§7")
            .addLore("§7Всё сотрётся, кинет на аркаим.")
            .addLore("§7После возврата в лобби")
            .addLore("§7можно начать тестить всё с нуля.")
            .build(), e -> {
//System.out.println("ClaimFlags "+e.getClick()+" isSet="+isSet);
            if (e.getClick()==ClickType.SHIFT_RIGHT) {
                p.performCommand("oscom reset");
                //reopen(p, contents);
                return;
            } 
            PM.soundDeny(p);
        }));    
        
        
            
            
        
        
        
        
        
        
        
        
        pagination.setItems(menuEntry.toArray(new ClickableItem[menuEntry.size()]));
        pagination.setItemsPerPage(18);
        

        

 
        
        if (!pagination.isLast()) {
            contents.set(2, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> contents.getHost().open(p, pagination.next().getPage()) )
            );
        }

        if (!pagination.isFirst()) {
            contents.set(2, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> contents.getHost().open(p, pagination.previous().getPage()) )
            );
        }
        
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0, 0)).allowOverride(false));
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
