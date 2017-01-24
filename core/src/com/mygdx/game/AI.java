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
public class AI {

    Entity entity;
    PathFinder pathfinder;
    
    public AI() {
    }
    
    public void update(double deltaTime) {
        if(entity!=null) {
            if(pathfinder!=null) {
                pathfinder.update();
            }
        }
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
