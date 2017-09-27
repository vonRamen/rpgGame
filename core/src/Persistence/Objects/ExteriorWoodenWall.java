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
public class ExteriorWoodenWall extends GameObject {

    public ExteriorWoodenWall() {
        this.id = 7;
        this.sprite = Game.textureHandler.generateRegion("world.png", 0, 4*32, 32, 3*32);
        this.drawLessSprite = Game.textureHandler.generateRegion("world.png", 0, 4*32, 32, 32);
        this.spriteIcon = new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 0, 5*32, 32, 32));
        this.isBuilt = true;
        this.requiredConstructionLevel = 1;
        this.hideWhenNear = true;
        this.name = "exterior wooden wall";
        this.setObjectType(ObjectType.WALL);
        this.makeBuildAbleObject();
    }
}
