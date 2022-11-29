package ru.ostrov77.lobby.quest;

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
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;





public class QuestViewMenu implements InventoryProvider {
    
    
    
    private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build());
    

    
    public QuestViewMenu() {
    }


    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        content.fillRect(0,0, 4,8, fill);
        
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
        final Pagination pagination = content.pagination();

        
        final List<ClickableItem> buttons = new ArrayList<>();
        
        final boolean builder = ApiOstrov.isLocalBuilder(p, false);
        
        for (Quest q : lp.questAccept) {
            
            final ItemStack is = new ItemBuilder(Material.SCUTE)
                    .name(q.displayName)
                    .lore(Quest.getLore(q))
                    .lore("§aАктивно" + (q.ammount>0 ? "§7, прогресс: §f"+lp.getProgress(q)+" §7из §f"+q.ammount : "") )
                    .lore(builder ? "§b*Отладка: §eЛКМ-завершить" : "")
                    .lore(builder && q.ammount>0 ? "§b*Отладка: §eПКМ-добавить процесс" : "")
                    .build();
                    
            if (builder) {
                buttons.add(ClickableItem.of(is, e-> {
                    if (e.isLeftClick()) {
                        if (q.ammount>0) {
                            lp.setProgress(q, q.ammount);
                        }
                        if (!QuestManager.tryCompleteQuest(p, lp, q, false)) { //если не хватает условий
                            //QuestManager.completeAdv(p, lp, q); //завершить принудительно
                            //p.sendMessage("§e*Не удалось заверишить квест, проверь счётчики.");
                        }
                        reopen(p, content);
                    } else if (e.isRightClick() && q.ammount>0) {
                        int progress = lp.getProgress(q); //берём из кэша
                        progress++;
                        lp.setProgress(q, progress);
                        Main.advance.sendProgress(p, q, progress);
                        reopen(p, content);
                    }
                }));
            } else {
                buttons.add(ClickableItem.empty(is));
            }
                    
        }
        
        Quest parent;
        for (Quest q : Quest.values()) {
           if (lp.questAccept.contains(q) || lp.questDone.contains(q)) continue;
            
            final ItemStack is;
            
                parent = Quest.byName(q.parent);
                if (parent!=null) {
                    
                    is = new ItemBuilder(Material.FIRE_CHARGE)
                    .name(q.displayName)
                    .lore(Quest.getLore(q))
                    .lore("§6Предстоит")
                    .lore("§7Откроется после выполнения")
                    .lore(parent.displayName)
                    .build();
                    buttons.add(ClickableItem.empty(is));
                    
                } else {
                    
                    final LCuboid lc = AreaManager.getCuboid(q.parent);
                    if (lc!=null) {
                        is = new ItemBuilder(Material.FIRE_CHARGE)
                            .name(q.displayName)
                            .lore(Quest.getLore(q))
                            .lore("§6Предстоит")
                            .lore("§7Откроется при изучении локации")
                            .lore(lc.displayName)
                            .lore(builder ? "§b*Отладка: §eЛКМ-расшарить" : "")
                            .build();
                        
                        if (builder) {
                            buttons.add(ClickableItem.of(is, e-> {
                                if (e.isLeftClick()) {
                                    QuestManager.onNewAreaDiscover(p, lp, lc);
                                    reopen(p, content);
                                }
                            }));
                        } else {
                            buttons.add(ClickableItem.empty(is));
                        }
                        
                    } else {
                        
                        is = new ItemBuilder(Material.FIRE_CHARGE)
                           .name(q.displayName)
                           .lore(Quest.getLore(q))
                           .lore("§6Предстоит")
                           .build();
                        buttons.add(ClickableItem.empty(is));
                        
                    }
                }
                 
           // }
            
            
            
                //buttons.add(ClickableItem.empty(is));
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
