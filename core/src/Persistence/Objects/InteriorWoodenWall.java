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
public class InteriorWoodenWall extends GameObject {

    public InteriorWoodenWall() {
        this.id = 1;
        this.sprite = Game.textureHandler.generateRegion("world.png", 0, 0, 32, 3 * 32);
        this.drawLessSprite = Game.textureHandler.generateRegion("world.png", 0, 0, 32, 32);
        this.spriteIcon = new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 0, 32, 32, 32));
        this.isBuilt = true;
        this.requiredConstructionLevel = 1;
        this.hideWhenNear = true;
        this.name = "interior wooden wall";
        this.setObjectType(ObjectType.WALL);
        this.makeBuildAbleObject();
    }
}
