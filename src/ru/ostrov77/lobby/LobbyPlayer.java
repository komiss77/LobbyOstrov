package ru.ostrov77.lobby;

import java.util.EnumMap;
import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.komiss77.LocalDB;
import ru.komiss77.Timer;
import ru.komiss77.enums.Data;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.game.Parkur;
import ru.ostrov77.lobby.quest.Quest;


public class LobbyPlayer {
    
    public final String name;
    private int flags; //флаги
    private int openedArea; //открытые локации
    public final EnumSet<Quest> questDone = EnumSet.noneOf(Quest.class); //завершенные задания
    public final EnumSet<Quest> questAccept = EnumSet.noneOf(Quest.class); //текущие задания
    private final EnumMap<Quest,Integer> progressCache = new EnumMap(Quest.class);
    
    //служебные
    public int lastCuboidId; //для playerMoveTask
    public int cuboidEntryTime = Timer.getTime(); //при входе равно текущему времени - может сразу появиться в кубоиде
    public int raceTime = -1; //таймер гонки
    private EnumSet<Material> foundBlocks; //блоки для 50 блок. задания
    public Parkur pkrist;
    public CuboidInfo compasstarget = CuboidInfo.DEFAULT; //ИД кубоида цели для компаса
    
    public boolean toSave = false;
    public boolean updAdv = false;
    public final boolean isGuest;
    
    
    public LobbyPlayer(final String name) {
        this.name = name;
        isGuest = name.startsWith("guest_");
    }
    
    
    
    
    
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    
    public void setAreaDiscovered(final int areaId) {
        openedArea =(openedArea | (1 << areaId));
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `openedArea` = '"+openedArea+"' WHERE `name` = '"+name+"';");
    }
    
    public int getOpenAreaCount () {
        int x = openedArea;
        // Collapsing partial parallel sums method
        // Collapse 32x1 bit counts to 16x2 bit counts, mask 01010101
        x = (x >>> 1 & 0x55555555) + (x & 0x55555555);
        // Collapse 16x2 bit counts to 8x4 bit counts, mask 00110011
        x = (x >>> 2 & 0x33333333) + (x & 0x33333333);
        // Collapse 8x4 bit counts to 4x8 bit counts, mask 00001111
        x = (x >>> 4 & 0x0F0F0F0F) + (x & 0x0F0F0F0F);
        // Collapse 4x8 bit counts to 2x16 bit counts
        x = (x >>> 8 & 0x00FF00FF) + (x & 0x00FF00FF);
        // Collapse 2x16 bit counts to 1x32 bit count
        return (x >>> 16) + (x & 0x0000FFFF);
    }     
    
    
    public boolean hasFlag(final LobbyFlag flag) {
        return (flags & (1 << flag.tag)) == (1 << flag.tag);//return LobbyFlag.hasFlag(flags, flag);
    }
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `flags` = '"+flags+"' WHERE `name` = '"+name+"';");
    }

    
    
    //отдельным методом, т.к. могут добавлять и НПС
    public boolean addQuest(final Quest quest) {
        if (!questDone.contains(quest) && questAccept.add(quest)) { //это задание ранее не выполнено и уже не было получено ранее
            if (!AreaManager.hasCuboid(quest.name())) Main.advance.sendToast(getPlayer(), this, quest); //для заданий открыть кубоид без помпезностей
            toSave = true;
            if (quest==Quest.FindBlock) {
                foundBlocks = EnumSet.noneOf(Material.class);
            }
            return true;
        }
        return false;
    }
    
    
    //только сохранение! все обработчики - в QuestManager
    public boolean questDone(final Quest quest) {
        //boolean change = questAccept.remove(quest); //сохранять только если что-то реально изменилось!
        if (questAccept.remove(quest) && questDone.add(quest)) {
            final Oplayer op = PM.getOplayer(name);
            if (quest.pay>0) {
                op.setData(Data.RIL, op.getDataInt(Data.RIL)+quest.pay);
            }
            progressCache.remove(quest);
            toSave = true;
            if (quest==Quest.FindBlock) {
                foundBlocks = null;
            }
            return true;
        }
        return false;
        //if (change) {
        //    saveQuest();
        //}
    }
    
    
    public void saveQuest() {
        if (toSave) {
            toSave = false;
            updAdv = true;
            final StringBuilder sbDone = new StringBuilder();
            for (Quest q:questDone) {
                sbDone.append(q.code);
            }
            final StringBuilder sbAccept = new StringBuilder();
            for (Quest q:questAccept) {
                sbAccept.append(q.code);
            }
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `questDone` = '"+sbDone.toString()+"', `questAccept` = '"+sbAccept.toString()+"' WHERE `name` = '"+name+"';");
        }
        if (updAdv) {
            updAdv = false;
            Main.advance.updVisib(getPlayer());
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setOpenedArea(int openedArea) {
        this.openedArea = openedArea;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    public LCuboid getCuboid() {
        return AreaManager.getCuboid(lastCuboidId);
    }

    
    public int getProgress(final Quest q) {
        return progressCache.containsKey(q) ? progressCache.get(q) : 0;
    }

    public void setProgress(Quest q, int progress) {
        if (progress>0) {
            progressCache.put(q, progress);
        } else {
            progressCache.remove(q);
        }
        
    }

    public boolean hasQuest(final Quest quest) {
        return questAccept.contains(quest) && !questDone.contains(quest);
    }

    public boolean foundBlockAdd(final Material type) {
        if (foundBlocks==null) foundBlocks = EnumSet.noneOf(Material.class);
        
        if (foundBlocks.add(type)) {
            progressCache.put(Quest.FindBlock, foundBlocks.size());
            return true;
        }
        return false;
    }


    
    
    

    
}
