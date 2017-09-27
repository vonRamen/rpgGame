/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.GameObject;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Game;

/**
 *
 * @author Kristian
 */
public class WoodenFloor extends GameObject {

    public WoodenFloor() {
        this.id = 6;
        this.sprite = Game.textureHandler.generateRegion("world.png", 0, 3 * 32, 32, 32);
        this.spriteIcon = new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 0, 3 * 32, 32, 32));
        this.zIndex = -1;
        this.isBuilt = true;
        this.name = "wooden floor";
        this.rectangle = null;
        this.setObjectType(ObjectType.FLOOR);
        this.makeBuildAbleObject();
    }
}
