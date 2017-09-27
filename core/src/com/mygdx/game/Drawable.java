/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public interface Drawable {
    
    public boolean isFlaggedForRemoval();
    public void draw();
    public void drawShadow();
    public float getY();
    public float getX();
    public float getZ();
    public ArrayList<Action> getActions();
    public ArrayList<Action> getActions(String uId);
}
