/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public enum EntityState {
    WALKING, IDLE, SWIMMING, MINING, CHOPPING, SLEEPING, UNCONSCIOUS, DEAD, SITTING, BUILDING;
    private int value;
    
    EntityState() {
    }
    
    public static EntityState get(int index) {
        int count = 0;
        for(EntityState state : EntityState.values()) {
            if(count == index) {
                return state;
            }
            count++;
        }
        return null;
    }
    
    public static int getIndex(EntityState currentState) {
        int count = 0;
        for(EntityState state : EntityState.values()) {
            if(currentState==state) {
                return count;
            }
            count++;
        }
        return -1;
    }
}
