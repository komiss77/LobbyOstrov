package ru.ostrov77.lobby.area;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import ru.komiss77.modules.world.BVec;
import ru.komiss77.objects.IntHashMap;


public class ChunkContent {
    
    private Set<Integer>cuboids;
//    private Map <Integer,String> nearlyPortalData;
    private IntHashMap <BVec> plate;
    
    
 /*
        public void setStructureData(final Location loc) { //при создании
        //structureData = structure.code<<24 | (loc.getBlockX()&0xF)<<16 | (loc.getBlockY()&0xFF)<<8 | (loc.getBlockZ()&0xF);
        strX = loc.getBlockX()&0xF;
        strY = loc.getBlockY();
        strZ = loc.getBlockZ()&0xF;
        //f.structures.put(structure, cLoc);
    }
    public void setStructureData(final int raw) {  //при загрузке
// System.out.println("setStructureData11 raw="+raw);
        if (raw==0) return;
        str = Structure.fromCode(raw>>24);
        strX = (raw>>16)&0xF;
        strY = (raw>>8)&0xFF;
        strZ = raw & 0xF;
    }
    public int getStructureData() { //для сохранения в БД
        return str.code<<24 | strX<<16 | strY<<8 | strZ;//structureData;
    }
    */   
        
    public boolean hasPlate() {
        return plate!=null && !plate.isEmpty();
    }
    
    public void addPlate(final BVec firstPlateXYZ, final BVec secondPlateXYZ) {
        if (plate==null) {
            plate = new IntHashMap<>();
        }
        final int ccLoc = getCCloc(firstPlateXYZ.x, firstPlateXYZ.y, firstPlateXYZ.z);//
        plate.put( ccLoc, secondPlateXYZ);
//System.out.println("++addPlate ccloc="+ccLoc+" second="+secondPlateXYZ);
    }

    public BVec getPlate(final Location loc) {
        if (!hasPlate()) return null;
        final int ccLoc = getCCloc(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());//(loc.getBlockX()&0xF)<<16 | loc.getBlockY()<<8 | (loc.getBlockZ()&0xF);
//System.out.println("++getPlate ccloc="+ccLoc+" plate.get="+plate.get(ccLoc));
        return plate.get(ccLoc);
    }    

    public void delPlate(final Location loc) {
        if (!hasPlate()) return;
        final int ccLoc = getCCloc(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());//(loc.getBlockX()&0xF)<<16 | loc.getBlockY()<<8 | (loc.getBlockZ()&0xF);
//System.out.println("++delPlate ccloc="+ccLoc+" plate.get="+plate.get(ccLoc));
        plate.remove(ccLoc);
        if (plate.isEmpty()) {
            plate=null;
        }
    }    
    
    public static int getCCloc(final int x, final int y, final int z) {
        return (x&0xF)<<16 | y<<8 | (y&0xF);
    }
    
    
    
    public boolean isEmpty() { //условия для удаления
        return !hasCuboids() && !hasPlate();
    }    
    
    
    
    
    
    
    public boolean hasCuboids() {
        return cuboids!=null && !cuboids.isEmpty();
    }

    public Set<Integer> getCuboidsIds() {
        return cuboids==null ? Collections.emptySet() : cuboids;
    }

    public boolean deleteCuboidID(final int cuboidId) {
        boolean result = cuboids.remove(cuboidId);
        if (cuboids.isEmpty()) {
            cuboids = null;
        }
        return result;
    }
    
    public void addCuboidID(final int cuboidId) {
        if (cuboids==null) {
            cuboids = new HashSet<>();
        }
        cuboids.add(cuboidId);
    }

    
    
    
    /*public boolean hasServerPortal() {
        return nearlyPortalData!=null;
    }

    //return  hasStructure() && Math.abs(strX-(loc.getBlockX()&0xF))<=1 && Math.abs(strY-loc.getBlockY())<=1 && Math.abs(strZ-(loc.getBlockZ()&0xF))<=1 ; //на y не нужен Math, всегда +!!

    public String getServerPortalNearly(final Location entryLoc) {
        if (nearlyPortalData==null) return null;
        final int nearlyLoc = entryLoc.getBlockX() | entryLoc.getBlockZ() | entryLoc.getBlockY();
        return nearlyPortalData.get(nearlyLoc);
    }*/




    


    
}
