/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameItem;

/**
 *
 * @author kristian
 */
public class Inventory {

    private int[] count;
    private int[] id;

    public Inventory(int size) {
        count = new int[size];
        id = new int[size];
    }

    public Inventory() {

    }

    public void drop(int id, int count) {

    }

    public void add(int id, int count) {

    }

    public int getCount(int index) {
        return count[index];
    }

    public int getId(int index) {
        return id[index];
    }

    public int getSize() {
        return id.length;
    }

    public boolean hasItem(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns count left after add
     *
     * @param id
     * @param count
     * @return
     */
    public int addItem(int id, int count) {
        int maxCount = GameItem.get(id).getStackSize();
        int canMaxHoldOnSlot;
        int newCount = 0;

        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == id) {
                if(count <= 0) {
                    break;
                }
                if(this.count[i] == maxCount) {
                    break;
                }
                if(count > this.count[i] + maxCount) {
                    newCount = maxCount;
                    count -= (maxCount-this.count[i]);
                } else {
                    newCount = this.count[i] + count;
                    count = 0;
                }
                this.count[i] = newCount;
                System.out.println("Count: "+count);
            }
        }
        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == 0) {
                if(count <= 0) {
                    break;
                }
                this.id[i] = id;
                if(count > this.count[i] + maxCount) {
                    newCount = maxCount;
                    count -= (maxCount-this.count[i]);
                } else {
                    newCount = this.count[i] + count;
                    count = 0;
                }
                this.count[i] = newCount;
                if(count <= 0) {
                    break;
                }
            }
        }
        return count;
    }
}
