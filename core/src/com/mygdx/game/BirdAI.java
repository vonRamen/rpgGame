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
    Random random;

    public BirdAI() {
        super();
        random = new Random();
        maxWaitTime = 5;
        maxDistance = 100;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (waitTime > 0) {
            waitTime -= deltaTime;
        } else {
            Point point = getRandomPointDistance(maxDistance * random.nextFloat());
            this.pathfinder = new PathFinder(this.entity, this.entity.getX(), this.entity.getY(), point.getX(), point.getY());
            waitTime = random.nextFloat() * maxWaitTime;
        }
        if(entity.z > 0) {
            entity.z -= deltaTime*100;
            entity.state = EntityState.get(3);
        }
    }

    Point getRandomPointDistance(float distance) {
        int x = (int) this.entity.x;
        int y = (int) this.entity.y;

        float angle = random.nextFloat() * 360;
        float direction_x = (float) Math.cos(angle);
        float direction_y = (float) Math.sin(angle);

        Point point = new Point(x + direction_x * distance, y + direction_y * distance);
        return point;
    }
}
