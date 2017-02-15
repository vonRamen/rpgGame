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
public abstract class AI {

    public enum MobState {
        IDLING, DEFAULT, FLEEING, ATTACKING, ALERTED;
    }

    Entity entity;
    PathFinder pathfinder;
    private Random random;
    private MobState currentState = MobState.DEFAULT;

    public AI() {
        random = new Random();
    }

    public void update(double deltaTime) {
        if (entity != null) {
            this.updateOnAll(deltaTime);
            switch (currentState) {
                case ALERTED:
                    updateOnAlerted(deltaTime);
                    break;
                case ATTACKING:
                    break;
                case DEFAULT:
                    updateOnDefault(deltaTime);
                    break;
                case FLEEING:
                    break;
                case IDLING:
                    updateOnIdle(deltaTime);
                    break;

                default:
                    break;
            }
        }
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    Point getRandomPointDistance(float distance) {
        int x = (int) this.entity.x;
        int y = (int) this.entity.y;

        float angle = getRandom().nextFloat() * 360;
        float direction_x = (float) Math.cos(angle);
        float direction_y = (float) Math.sin(angle);

        Point point = new Point(x + direction_x * distance, y + direction_y * distance);
        return point;
    }

    /**
     * @return the random
     */
    public Random getRandom() {
        return random;
    }

    public void setState(MobState state) {
        this.currentState = state;
    }

    public MobState getState() {
        return this.currentState;
    }

    /**
     * The default ai, that is undisturbed.
     *
     * @param deltaTime
     */
    abstract protected void updateOnDefault(double deltaTime);

    abstract protected void updateOnAlerted(double deltaTime);

    abstract protected void updateOnIdle(double deltaTime);

    protected void updateOnAll(double deltaTime) {
        if (pathfinder != null) {
            pathfinder.update();
        }
    }
}
