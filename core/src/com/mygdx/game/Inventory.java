/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

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
}
