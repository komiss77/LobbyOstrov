package ru.ostrov77.lobby.area;


import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import ru.komiss77.modules.items.ItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.MoveUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.lobby.LobbyPlayer;




public class AreaViewMenu implements InventoryProvider {



    //private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(ItemType.BLUE_STAINED_GLASS_PANE).name("§8.").build());


    private static final ItemStack[] bIts = crtBaseInv();

    private static ItemStack[] crtBaseInv() {
        final ItemStack[] its = new ItemStack[54];
        for (int i = 0; i < 54; i++) {
            if (i % 9 == 0 || i % 9 == 8) {
                its[i] = ItemType.TWISTING_VINES.createItemStack();
            } else {
                switch (i) {
                    case 12:
                    case 13:
                    case 14:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 39:
                    case 40:
                    case 41:
                        its[i] = ItemType.LIGHT_BLUE_STAINED_GLASS_PANE.createItemStack();
                        break;
                    default:
                        its[i] = ItemType.CYAN_STAINED_GLASS_PANE.createItemStack();
                        break;
                }
            }
        }

        return its;
    }

    public AreaViewMenu() {

    }

    //@Override
    // public void onClose(final Player p, final InventoryContent content) {
    // }




    @Override
    public void init(final Player p, final InventoryContent content) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        content.getInventory().setContents(bIts);

        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);


        //final Pagination pagination = content.pagination();


        //final List<ClickableItem> areas = new ArrayList<>();
        //final List<ClickableItem> unknow = new ArrayList<>();


        for (final LCuboid lc : AreaManager.getCuboids()) {

            final CuboidInfo ci = lc.getInfo();

            if (ci==CuboidInfo.DEFAULT || !ci.canTp) continue;
            //if (lc.getName().equals("newbie")) continue; //спавн новичка не показываем
            if (lp.isAreaDiscovered(lc.id)) {

                final ItemStack is = new ItemBuilder(ci.icon)
                    .name(lc.displayName)
                    .lore("§7Открыта")
                    .lore("§7ЛКМ - тп")
                    .build();

                content.set(ci.slot, ClickableItem.of(is, e->
                    MoveUtil.safeTP(p, lc.spawnPoint)));

                //if (lp.hasFlag(LobbyFlag.NewBieDone)) QuestManager.tryCompleteQuest(p, lp, Quest.Navigation);

            } else {

                final ItemStack is = new ItemBuilder(lp.target == lc.getInfo() ? ItemType.CLAY_BALL :  ItemType.GRAY_DYE)
                    .name(lc.displayName)
                    .lore("§7Не изучена")
                    .lore(lp.target == lc.getInfo() ? "§bЦель для компаса" : "§7ЛКМ - §aнавести компас")
                    .lore(lp.target == lc.getInfo() ? "§7ПКМ - §6сброс компаса" : "")
                    .build();

                content.set(ci.slot, ClickableItem.of(is, e-> {

                    if (e.isLeftClick()  && lp.target != lc.getInfo()) {

                        p.setCompassTarget(lc.spawnPoint);
                        lp.target = lc.getInfo();
                        AreaManager.setCompassTarget(p, lp, lc);
                        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
                        reopen(p, content);
                        return;

                    } else if (e.isRightClick() && lp.target == lc.getInfo()) {

                        p.setCompassTarget(p.getLocation());
                        lp.target = CuboidInfo.DEFAULT;
                        AreaManager.resetCompassTarget(p, lp);
                        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
                        reopen(p, content);
                        return;

                    }
                    PM.soundDeny(p);

                }));

            }




        }

        
        
        
        
        
        
        /*areas.addAll(unknow);
        
        
        pagination.setItems(areas.toArray(new ClickableItem[areas.size()]));
        pagination.setItemsPerPage(21);    
        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 1)).allowOverride(false));


        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (!pagination.isLast()) {
            content.set(4, 8, ClickableItem.of(ItemUtil.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }

        if (!pagination.isFirst()) {
            content.set(4, 0, ClickableItem.of(ItemUtil.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }*/



    }












}
