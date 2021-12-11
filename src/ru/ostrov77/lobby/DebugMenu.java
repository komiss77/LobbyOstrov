package ru.ostrov77.lobby;


import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;





public class DebugMenu implements InventoryProvider {
    

    

    
    public DebugMenu() {

    }

    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = contents.pagination();
        
        final ArrayList<ClickableItem> menuEntry = new ArrayList<>();
        
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
        
        
        
        
        
        
        
        
        
        
        
        for (final LobbyFlag flag : LobbyFlag.values()) {
            
            final boolean isSet = lp.hasFlag(flag);

            menuEntry.add( ClickableItem.of(new ItemBuilder(isSet ? Material.LIME_DYE : Material.GRAY_DYE)
                .name( "§f"+flag.displayName)
                .lore("§7")
                .lore( isSet ? "§7ПКМ - §2сброс" : "§7ЛКМ - §4поставить")
                .lore("§7")
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
            
            
        
        
        
        contents.set( 2, 4,  ClickableItem.of(new ItemBuilder( Material.REDSTONE)
            .name("§fсброс аккаунта до новичка")
            .lore("§7")
            .lore("§7Шифт+ПКМ - §cресетнуть")
            .lore("§7")
            .lore("§7Всё сотрётся, кинет на аркаим.")
            .lore("§7После возврата в лобби")
            .lore("§7можно начать тестить всё с нуля.")
            .build(), e -> {
//System.out.println("ClaimFlags "+e.getClick()+" isSet="+isSet);
            if (e.getClick()==ClickType.SHIFT_RIGHT) {
                p.closeInventory();
                ApiOstrov.sendToServer(p, "arcaim", "");
                Ostrov.async(()-> LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `lobbyData` WHERE `name` = '"+p.getName()+"';"), 20);
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
