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
public class Stump extends GameObject {
    public Stump() {
        this.id = 3;
        this.sprite = Game.textureHandler.generateRegion("world.png", 8*32, 5*32, 32*3, 32);
        this.offsetX=-32;
        this.isRespawnObject = true;
        this.respawnTime = 45;
        this.isRespawningInto = 0;
    }
}
