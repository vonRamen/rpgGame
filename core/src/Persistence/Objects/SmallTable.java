/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.GameObject;
import com.mygdx.game.Game;

/**
 *
 * @author Kristian
 */
public class SmallTable extends GameObject {
    public SmallTable() {
        this.id = 4;
        this.sprite = Game.textureHandler.generateRegion("world.png", 8*32, 0, 32, 32);
        this.isBuilt = true;
        this.requiredConstructionLevel = 1;
        this.name = "small table";
    }
}
