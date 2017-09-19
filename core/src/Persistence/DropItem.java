/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

/**
 * Item dropped by a GameObject
 * @author kristian
 */
public class DropItem {

    private int id;
    private int count;
    private float chance;

    public DropItem() {
    }
    
    public DropItem(int id, int count, float chance) {
        this.id = id;
        this.count = count;
        this.chance = chance;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
    
    public float getChance() {
        return chance;
    }
}
