/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.Random;

/**
 *
 * @author kristian
 */
public class BirdAI extends AI {

    private float maxWaitTime;
    private float waitTime;
    private float maxDistance;

    public BirdAI() {
        super();
        maxWaitTime = 5;
        maxDistance = 100;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void updateOnDefault(double deltaTime) {
        if (waitTime > 0) {
            waitTime -= deltaTime;
        } else {
            Point point = getRandomPointDistance(maxDistance * this.getRandom().nextFloat());
            this.pathfinder = new PathFinder(this.entity, this.entity.getX(), this.entity.getY(), point.getX(), point.getY());
            waitTime = this.getRandom().nextFloat() * maxWaitTime;
            if (entity.checkView().size() > 0) {
                setState(MobState.ALERTED);
            }
        }
        if (entity.z > 0) {
            entity.z -= deltaTime * 100;
            entity.state = EntityState.get(3);
        } else {
            entity.z = 0;
        }
    }

    @Override
    protected void updateOnAlerted(double deltaTime) {
        this.pathfinder = null;
        entity.state = EntityState.get(3);
        entity.z += deltaTime * 200;
        entity.flagRemoval();
        entity.bounds = null;
    }

    @Override
    protected void updateOnIdle(double deltaTime) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
