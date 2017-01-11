/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;

/**
 *
 * @author kristian
 */
public class EntityAction {

    private Human human;
    private Drawable object;
    private Action action;
    private boolean done;
    
    public EntityAction(Action action, Human human, Drawable object) {
        this.human = human;
        this.action = action;
        this.object = object;
    }

    public void update(double deltaTime) {
        human.setActionDuration(human.getActionDuration() - (float) deltaTime);
        if (human.getActionDuration() < 0) {
            done = true;
            action.executeAction(human, object);
        }
    }

    public boolean isDone() {
        return done;
    }
}
