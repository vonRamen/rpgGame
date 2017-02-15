/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.DropItem;
import Persistence.GameEntity;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class Mob extends Entity {
    
    private AI ai;
    private GameEntity entity;
    private int damage;
    private ArrayList<DropItem> dropsOnDeath;
    
    public Mob(int id, int x, int y) {
        entity = GameEntity.get(id);
        if(entity==null) {
            System.out.println("Error!: entity doesn't exist!");
        }
        this.x = x;
        this.y = y;
        try {
            this.ai = (AI) Class.forName("com.mygdx.game."+entity.getAi()).newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ai.setEntity(this);
        this.hp = entity.getHp();
        this.damage = entity.getDamage();
        this.state = EntityState.IDLE;
        this.setAnimation(entity.getAnimationGroup(), 0, state);
        this.speed = entity.getSpeed();
        this.bounds = new Rectangle(x, y, entity.getBoundsWidth(), entity.getBoundsHeight());
        this.z = entity.getStartingZ();
        this.alpha = 0f;
    }
    
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        this.ai.update(deltaTime);
    }

    @Override
    public ArrayList<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
