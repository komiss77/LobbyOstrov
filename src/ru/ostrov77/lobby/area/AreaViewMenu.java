package ru.ostrov77.lobby.area;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;





public class AreaViewMenu implements InventoryProvider {
    
    
    
    //private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("§8.").build());
    
    private static final List<String>compasslore = Arrays.asList("§6ЛКМ§e - задачи ","§2ПКМ§a - локации");
    
    private static final ItemStack[] bIts = crtBaseInv();
    
    private static ItemStack[] crtBaseInv() {
    	final ItemStack[] its = new ItemStack[54];
    	
    	for (int i = 0; i < 54; i++) {
    		if (i % 9 == 0 || i % 9 == 8) {
    			its[i] = new ItemStack(Material.TWISTING_VINES);
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
	    			its[i] = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
					break;
				default:
	    			its[i] = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
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
        
        final LobbyPlayer lp = Main.getLobbyPlayer(p);

        
        //final Pagination pagination = content.pagination();

        
        //final List<ClickableItem> areas = new ArrayList<>();
        //final List<ClickableItem> unknow = new ArrayList<>();
        
        
        for (final LCuboid lc : AreaManager.getCuboids()) {
        	
        	final CuboidInfo ci = lc.getInfo();
            
        	if (ci==CuboidInfo.DEFAULT || !ci.canTp) continue;
                
                if (lp.isAreaDiscovered(lc.id)) {
                    
                    final ItemStack is = new ItemBuilder(ci.icon)
                            .name(lc.displayName)
                            .lore("§7Открыта")
                            .lore(ci.canTp ? "§7ЛКМ - тп" : "")
                            .build();

                    content.set(ci.slot, ClickableItem.of(is, ci.canTp ? e-> 
                            ApiOstrov.teleportSave(p, lc.spawnPoint, false)
                        : e->{}));
                        
                } else {
                    
                    final ItemStack is = new ItemBuilder(lp.compasstarget == lc.id ? Material.CLAY_BALL :  Material.GRAY_DYE)
                            .name(lc.displayName)
                            .lore("§7Не изучена")
                            .lore(lp.compasstarget == lc.id ? "§bЦель для компаса" : "§7ЛКМ - §aнавести компас")
                            .lore(lp.compasstarget == lc.id ? "§7ПКМ - §6сброс компаса" : "")
                            .build();

                    content.set(ci.slot, ClickableItem.of(is, e-> {
                            
                            if (e.isLeftClick()  && lp.compasstarget != lc.id) {
                                
                                p.setCompassTarget(lc.spawnPoint);
                                lp.compasstarget = lc.id;
                                setCompassTarget(p, lc);
                                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
                                reopen(p, content);
                                return;
                                
                            } else if (e.isRightClick() && lp.compasstarget == lc.id) {
                                
                                p.setCompassTarget(p.getLocation());
                                lp.compasstarget = 0;
                                resetCompassTarget(p);
                                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
                                reopen(p, content);
                                return;
                                
                            }
                            PM.soundDeny(p);
                            
                        }));
                    
                }
        	
            if (lc.getName().equals("newbie")) continue; //спавн новичка не показываем
            
            
        }

        
        
        
        
        
        
        /*areas.addAll(unknow);
        
        
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
        }*/

        

    }

    
    
    public static void setCompassTarget(final Player p, final LCuboid lc) {
        final ItemStack compass = p.getInventory().getItem(0);
        if (compass!=null && compass.getType()==Material.COMPASS && compass.hasItemMeta()) {
            final ItemMeta im = compass.getItemMeta();
            if (im.hasLore()) {
                final List<Component> lore = new ArrayList<Component>();
                for (final String s : compasslore) {
                	lore.add(Component.text(s));
                }
                lore.add(Component.text(""));
                lore.add(Component.text("§fНастроен на локацию:"));
                lore.add(Component.text(lc.displayName));
                im.lore(lore);
                im.addEnchant(Enchantment.LUCK, 1, true);
                compass.setItemMeta(im);
                p.getInventory().setItem(0, compass);
                //p.updateInventory();
            }
        }
    }

    public static void resetCompassTarget(final Player p) {
        final ItemStack compass = p.getInventory().getItem(0);//.clone();
        if (compass!=null && compass.getType()==Material.COMPASS && compass.hasItemMeta()) {
            final ItemMeta im = compass.getItemMeta();
            if (im.hasLore()) {
                final List<Component> lore = new ArrayList<Component>();
                for (final String s : compasslore) {
                	lore.add(Component.text(s));
                }
                im.lore(lore);
                im.removeEnchant(Enchantment.LUCK);
                compass.setItemMeta(im);
                p.getInventory().setItem(0, compass);
            }
        }
    }

    
    
    
    
    
    
    
    
    
}
