package ru.ostrov77.lobby.newbie;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;



public class OsComMenu implements InventoryProvider {
    
    
    public OsComMenu() {
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //contents.fillRect(0,0, 2,3, ClickableItem.empty(fill1));
        

        

        
        
        
    /*      
        if (NewBie.tasks.get(p.getName()).migrationInfo.isEmpty()) {

        contents.set(0, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.PAPER)
                .name("§fМиграционная карта")
                .lore("§7")
                .lore("§7Нужно указать,")
                .lore("§7откуда Вы прибыли")
                .lore("§7(как узнали про Остров -")
                .lore("§7лаунчер, ютуб, сайт и т.д.)")
                .lore("§7")
                .lore("§7ЛКМ - ввести данные")
                .build(), "откуда прибыли?", msg -> {
                    //msg = msg.replaceAll("&k", "").replaceAll("&", "§");
                    
                    if(msg.length()>64 ) {
                        p.sendMessage("§cЛимит 64 символа!");
                        return;
                    }
                    NewBie.tasks.get(p.getName()).migrationInfo = msg;
                    NewBie.tasks.get(p.getName()).score.getSideBar().updateLine(3, "§7§mЗаполнить миграционную.");
                    NewBie.tasks.get(p.getName()).score.getSideBar().updateLine(2, "§7§mкарту.");
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0.6f);
                    p.sendMessage("§aМиграционная карта заполнена!");
                    p.sendMessage("§6Следуйте на посадочный модуль!");
                    //reopen(p, contents);
                   // return;
                }));  
        }
        


        
        
      contents.set(2, ClickableItem.of(new ItemBuilder(Material.HONEYCOMB)
            .name("§fИнициализация модуля")
            .lore("§7")
            .lore("§7Активируйте этот пункт,")
            .lore("§7когда поднимитесь")
            .lore("§7на посадочный модуль.")
            .lore("§7")
            .lore("§7ЛКМ - §bначать отстыковку")
            .lore("§7")
            .lore("")
            .build(), e -> {
                
                
                if (Timer.has(p, "go")) {
                    return;
                }
                Timer.add(p, "go", 3);
                p.closeInventory();
                
p.sendMessage("§cВы не на модуле!");
if (1==1) return;

                if (GM.this_server_name.length()==3) {
                    ApiOstrov.sendMessage(p, Operation.AUTH_PLAYER_DATA, p.getName(), rawData);
                } else {
                    p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }
            }
        ));*/
        
        
        
        
        
        
        contents.set(4, ClickableItem.of(new ItemBuilder(Material.REDSTONE)
            .name("§cПропустить интро")
            .lore("§7ЛКМ - пропустить")
            .lore("")
            .build(), e -> {
                p.closeInventory();
                NewBie.stop(p);

            }
        ));
        
        

   

  

        

        


        
        

    }
    
    
    
    
    
    
    
    
    
    
}
