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

    private Entity entity;
    private int[] count;
    private int[] id;

    public Inventory(int size) {
        count = new int[size];
        id = new int[size];
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Inventory() {

    }

    public void drop(int id, int count) {

    }

    public void dropOnSlot(int slotId) {
        int x = (int) entity.getX();
        int y = (int) entity.getY();

        GameWorld world = entity.getWorld();

        world.spawnItem(this.id[slotId], this.count[slotId], x, y);
        this.id[slotId] = 0;
        this.count[slotId] = 0;
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

    public boolean hasItem(int id, int count) {
        int newCount = 0;
        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == id) {
                newCount += this.count[i];
                if (newCount >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeItem(int id, int count) {
        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == id) {
                if (this.count[i] >= count) {
                    this.count[i] -= count;
                    if (this.count[i] == 0) {
                        this.id[i] = 0;
                    }
                    return;
                } else {
                    count -= this.count[i];
                    this.id[i] = 0;
                    this.count[i] = 0;
                }
            }
        }
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
        int newCount = 0;
        System.out.println("id " + id);

        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == id) {
                if (count <= 0) {
                    break;
                }
                if (count > maxCount - this.count[i]) {
                    newCount = maxCount;
                    count -= (maxCount - this.count[i]);
                } else if (this.count[i] == maxCount) {
                    newCount = maxCount;
                } else {
                    newCount = this.count[i] + count;
                    count = 0;
                }
                this.count[i] = newCount;
                System.out.println("Count: " + count);
            }
        }
        for (int i = 0; i < this.id.length; i++) {
            if (this.id[i] == 0) {
                if (count <= 0) {
                    break;
                }
                if (count > maxCount - this.count[i]) {
                    newCount = maxCount;
                    count -= (maxCount - this.count[i]);
                } else {
                    newCount = this.count[i] + count;
                    count = 0;
                }
                this.id[i] = id;
                this.count[i] = newCount;
            }
        }
        return count;
    }

    public void changePosition(int pos1, int pos2) {
        int holdId = id[pos1];
        int holdCount = count[pos1];

        id[pos1] = id[pos2];
        count[pos1] = count[pos2];

        id[pos2] = holdId;
        count[pos2] = holdCount;
    }

    public void prepareSend() {
        this.entity = null;
    }
}
