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
public enum Direction {
    DOWN(0), LEFT(1), RIGHT(2), UP(3);
    
    private final int value;
    
    Direction(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
