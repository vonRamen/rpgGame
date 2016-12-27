/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

/**
 *
 * @author kristian
 */
public class Skill {

    private String name;
    private String description;
    private Texture icon;
    private int level;
    private int exp;
    
    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
        this.level = 1;
        this.exp = 0;
    }
    
    public Skill() {
        
    }
    
    public void addExp(int exp) {
        this.exp+=exp;
        checkLevel();
    }
    
    private void checkLevel() {
        throw new UnsupportedOperationException();
    }
    
    public void setIcon(Texture texture) {
        this.icon = texture;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExp() {
        return exp;
    }
}
