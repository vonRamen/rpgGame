/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.Action;
import Persistence.GameObject;
import com.mygdx.game.Game;

/**
 *
 * @author Kristian
 */
public class WoodenDoorOpen extends GameObject {

    public WoodenDoorOpen() {
        this.id = 8;
        this.sprite = Game.textureHandler.generateRegion("world.png", 2 * 32, 4 * 32, 32 * 1, 3 * 32);
        Action open = new Action();
        open.setName("close");
        open.setTransformIntoId(9);
        open.setPermissionLevel(1);
        this.actions.add(open);
        this.name = "wooden door";
        rectangle = null;
    }
}
