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
public class VaseWithFlowers extends GameObject {
    public VaseWithFlowers() {
        this.id = 5;
        this.sprite = Game.textureHandler.generateRegion("world.png", 9*32, 0, 32, 32);
        this.name = "vase with flowers";
        this.zIndex = 1;
    }
}
