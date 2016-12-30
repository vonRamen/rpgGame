/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.GameItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class DroppedItem implements Drawable {

    protected int x, y;
    protected float xFlow, yFlow; //The maplestory flow
    protected float yTop, yBottom;
    protected float ySpeed;
    protected int yDirection; //1 = up, -1 = down
    protected float yTime; //for the interpolation
    protected Interpolation interpolation; //Used for item bounce
    protected int id;
    protected String name;
    protected String description;
    protected Persistence.GameItem gameItem;

    public DroppedItem(int id, int x, int y) {
        this.x = x;
        this.y = y;
        yTop = 5;
        yBottom = 16;
        yDirection = 1;
        ySpeed = 0.3f;
        yTime = 0;
        interpolation = Interpolation.bounceIn;
        if (GameItem.get(id) == null) {
            System.out.println("Warning! GameItem doesn't exist!");
            gameItem = GameItem.get(0);
            return;
        }
        gameItem = GameItem.get(id);
    }
    
    private double lerp(double point0, double point1, double time) {
        return point0 + time*(point1-point0);
    }

    public void update(double deltaTime) {
        yTime += ySpeed * deltaTime;
        lerp(yFlow, 16, yTime);
        if(yDirection == 1) {
            yFlow = interpolation.apply(yFlow, yTop, yTime);
        } else {
            yFlow = interpolation.apply(yFlow, 0, yTime);
        }
        if(yFlow > yTop-0.1) {
            if(yDirection != -1) {
                yTime = 0;
            }
            yDirection = -1;
        } else if(yFlow < 0+0.1) {
            if(yDirection != 1) {
                yTime = 0;
            }
            yDirection = 1;
        }
    }

    @Override
    public void draw() {
        //Draw shadow
        Game.batch.setColor(0, 0, 0, 0.4f);
        Game.batch.draw(gameItem.getTexture(), x + xFlow, y-4 - yFlow);
        Game.batch.setColor(Color.WHITE);
        //Draw item
        gameItem.draw(x + (int) xFlow, y + (int) yFlow);
    }

    public float getY() {
        return y;
    }

    @Override
    public ArrayList<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getX() {
        return x;
    }

}
