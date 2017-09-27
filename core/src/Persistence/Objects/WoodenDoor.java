/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.Action;
import Persistence.GameObject;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Game;

/**
 *
 * @author Kristian
 */
public class WoodenDoor extends GameObject {
    public WoodenDoor() {
        this.id = 9;
        this.sprite = Game.textureHandler.generateRegion("world.png", 32, 4*32, 32, 3*32);
        this.spriteIcon = new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 32, 5*32, 32, 32));
        this.isBuilt = true;
        this.requiredConstructionLevel = 1;
        this.name = "wooden door";
        Action open = new Action();
        open.setName("open");
        open.setTransformIntoId(8);
        this.actions.add(open);
        this.setObjectType(ObjectType.DOOR);
        this.makeBuildAbleObject();
    }
}
