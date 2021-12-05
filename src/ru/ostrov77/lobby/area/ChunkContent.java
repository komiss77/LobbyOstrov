package ru.ostrov77.lobby.area;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ChunkContent {
    
    private Set<Integer>cuboids;

    
    
    
    
    
    
    
    
    
    
    
    
    public boolean hasCuboids() {
        return cuboids!=null && !cuboids.isEmpty();
    }

    public Set<Integer> getCuboidsIds() {
        return cuboids==null ? Collections.EMPTY_SET : cuboids;
    }

    public void deleteCuboidID(final int cuboidId) {
        cuboids.remove(cuboidId);
        if (cuboids.isEmpty()) {
            cuboids = null;
        }
    }
    
    public void addCuboidID(final int cuboidId) {
        if (cuboids==null) {
            cuboids = new HashSet<>();
        }
        cuboids.add(cuboidId);
    }
    
}
