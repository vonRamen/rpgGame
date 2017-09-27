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
public class StrawRoof extends GameObject {

    public StrawRoof() {
        this.id = 10;
        this.sprite = Game.textureHandler.generateRegion("world.png", 3 * 32, 4 * 32, 32, 2 * 32);
        this.sprite = Game.textureHandler.generateRegion("world.png", 0, 0, 0, 0);
        this.spriteIcon = new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 3 * 32, 4*32, 32, 32));
        this.zIndex = 5;
        this.hideWhenNear = true;
        this.isBuilt = true;
        this.name = "straw roof";
        this.rectangle = null;
        this.setObjectType(ObjectType.ROOF);
        this.makeBuildAbleObject();
    }
}
