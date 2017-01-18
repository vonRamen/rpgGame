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
    private float scale = 55.2f;

    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
        this.level = 1;
        this.exp = 0;
    }

    public Skill() {

    }

    public void addExp(int exp) {
        this.exp += exp;
        checkLevel();
    }

    private void checkLevel() {
        int totalNeededExp = 0;
        for (int lvl = 99; lvl > 0; lvl--) {
            totalNeededExp += (int) (scale * Math.sqrt(lvl));
        }
        System.out.println("Prototype Exp needed: " + calculateExpNeeded(99));
        //Calculate level.
        if (totalNeededExp < exp) {
            level++;
        }
        System.out.println("Exp: " + exp + " Level: " + level);
    }

    private float calculateExpNeeded(int level) {
        if (level == 0) {
            return (float) (scale * Math.sqrt(level));
        }
        return calculateExpNeeded(level-1) + (float) (scale * Math.sqrt(level));
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
